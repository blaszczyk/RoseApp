package bn.blaszczyk.roseapp.view.inputpanels;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bn.blaszczyk.roseapp.view.ThemeConstants;

@SuppressWarnings("serial")
public class FileInputPanel extends JPanel implements InputPanel<String>, ThemeConstants {
	
	private final JLabel label;
	private final JLabel lblFileName = new JLabel();
	private final JButton button = new JButton();
	private ChangeListener listener = null;
	
	private final String defFileName;
	private String fileName;
	
	public FileInputPanel( String name, String fileName, boolean edit )
	{
		this.defFileName = fileName;
		setBackground(BASIC_PNL_BACKGROUND);
		setValue(fileName);
		
		setLayout(null);
		label =  new JLabel( name + ": ", SwingConstants.RIGHT);
		label.setFont(PROPERTY_FONT);
		label.setOpaque(true);
		label.setForeground(PROPERTY_FG);
		label.setBackground(PROPERTY_BG);
		label.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
		add(label);
		
		lblFileName.setBounds( PROPERTY_WIDTH + H_SPACING , 0, VALUE_WIDTH - LBL_HEIGHT - H_SPACING, LBL_HEIGHT);
		lblFileName.setFont(VALUE_FONT);
		lblFileName.setOpaque(true);
		lblFileName.setForeground(VALUE_FG);
		lblFileName.setBackground(VALUE_BG);
		add(lblFileName);
		
		String iconFile = edit ? "open.png" : "view.png";
		try
		{
			button.setIcon( new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("bn/blaszczyk/roseapp/resources/" + iconFile))) );
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		button.setBounds( PROPERTY_WIDTH + H_SPACING + VALUE_WIDTH - LBL_HEIGHT, 0, LBL_HEIGHT, LBL_HEIGHT);
		add(button);
		if(edit)
			button.addActionListener( e -> {
				try
				{
					URI uri = new URI(fileName);
					File file = new File(uri);
					JFileChooser chooser = new JFileChooser(file);
					chooser.setDialogTitle("Choose File");
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setMultiSelectionEnabled(false);
					chooser.setCurrentDirectory(file);
					switch( chooser.showOpenDialog(null) )
					{
					case JFileChooser.APPROVE_OPTION:
						String filename = chooser.getSelectedFile().toURI().toString();
						setValue( filename );
						if(listener != null)
							listener.stateChanged(new ChangeEvent(this));
						break;
					}
				}
				catch (URISyntaxException e1)
				{
					e1.printStackTrace();
				}
			});
		else
			button.addActionListener( e -> {
				try
				{
					URI uri = new URI(fileName);
					File file = new File(uri);
					Desktop.getDesktop().open(file);
				}
				catch (IOException | URISyntaxException e1)
				{
					e1.printStackTrace();
				}
			});
	}
		
	public static boolean isFileName(String fileName)
	{
		URI uri;
		try
		{
			uri = new URI(fileName);
		}
		catch (URISyntaxException e)
		{
			return false;
		}
		if(!uri.isAbsolute())
			return false;
		File file = new File(uri);
		return file.exists();		
	}
	
	@Override
	public String getName()
	{
		return label.getText();
	}
	
	@Override
	public void setChangeListener(ChangeListener l)
	{
		this.listener = l;
	}

	@Override
	public JPanel getPanel()
	{
		return this;
	}

	@Override
	public String getValue()
	{
		return fileName;
	}

	@Override
	public void setValue(String value)
	{
		fileName = value;
		lblFileName.setText(value.substring(value.lastIndexOf("/")+1));
	}

	@Override
	public boolean hasChanged()
	{
		return defFileName != fileName;
	}

	@Override
	public boolean isInputValid()
	{
		return true;
	}
}
