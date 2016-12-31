package bn.blaszczyk.roseapp.view.panels.input;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.RoseListener;
import bn.blaszczyk.roseapp.view.factories.IconFactory;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class FileInputPanel extends JPanel implements InputPanel<String> {
	
	private final JLabel label;
	private final JLabel lblFileName;
	private final JButton button = new JButton();
	private RoseListener listener = null;
	
	private String defFileName;
	private String fileName;
	
	public FileInputPanel( String name, String fileName, boolean edit )
	{
		this.defFileName = fileName;
		setBackground(BASIC_PNL_BACKGROUND);
		setValue(fileName);
		
		setLayout(null);
		label = LabelFactory.createLabel(name + ": ", PROPERTY_FONT, PROPERTY_FG, SwingConstants.RIGHT);
		label.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
		add(label);
		
		lblFileName = LabelFactory.createOpaqueLabel("", VALUE_FONT, VALUE_FG, VALUE_BG);
		lblFileName.setBounds( PROPERTY_WIDTH + H_SPACING , 0, VALUE_WIDTH - LBL_HEIGHT - H_SPACING, LBL_HEIGHT);
		add(lblFileName);
		
		String iconFile = edit ? "open.png" : "view.png";
		button.setIcon( IconFactory.create(iconFile) );
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
							listener.notify(new RoseEvent(this));
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
	public void setRoseListener(RoseListener l)
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

	@Override
	public void resetDefValue()
	{
		this.defFileName = getValue();
	}
}
