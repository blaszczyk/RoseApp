package bn.blaszczyk.roseapp.view.panels.input;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.RoseListener;
import bn.blaszczyk.roseapp.view.factories.ButtonFactory;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.roseapp.tools.Preferences.*;
import static javax.swing.JOptionPane.*;

@SuppressWarnings("serial")
public class FileInputPanel extends JPanel implements InputPanel<String> {
	
	private final JLabel label;
	private final JLabel lblFileName;
	private final JButton button;
	private RoseListener listener = null;
	
	private String baseDirName = getStringValue(BASE_DIRECTORY, "C:/temp");
	private File defFile;
	private File file;
	
	public FileInputPanel( String name, String fileName, boolean edit )
	{
		
		String fullFileName = baseDirName + fileName;
		this.file = this.defFile = new File(fullFileName);
		setBackground(BASIC_PNL_BACKGROUND);
		
		setLayout(null);
		label = LabelFactory.createLabel(name + ": ", PROPERTY_FONT, PROPERTY_FG, SwingConstants.RIGHT);
		label.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
		add(label);
		
		lblFileName = LabelFactory.createLabel("");
		lblFileName.setBounds( PROPERTY_WIDTH + H_SPACING , 0, VALUE_WIDTH - LBL_HEIGHT - H_SPACING, LBL_HEIGHT);
		add(lblFileName);
		
		setValue(fileName);
		
		button = ButtonFactory.createIconButton(edit ? "open.png" : "view.png", edit ? e -> chooseFile() : e -> openFile() );
		button.setBounds( PROPERTY_WIDTH + H_SPACING + VALUE_WIDTH - LBL_HEIGHT, 0, LBL_HEIGHT, LBL_HEIGHT);
		add(button);
	}
	
	private String relativePath(File file)
	{
		String fullPath = file.getAbsolutePath();
		return fullPath.substring(baseDirName.length());
	}
	
	private String fullPath( String fileName )
	{
		return baseDirName + fileName;
	}
	
	private void chooseFile()
	{
		JFileChooser chooser;
		if(file.exists())
		{
			chooser = new JFileChooser(file);
			chooser.setCurrentDirectory(file);
		}
		else
			chooser = new JFileChooser();
		chooser.setDialogTitle(Messages.get("Choose File"));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		if( chooser.showOpenDialog(this) ==  JFileChooser.APPROVE_OPTION )
		{
			setValue( relativePath( chooser.getSelectedFile() ) );
			if(listener != null)
				listener.notify(new RoseEvent(this));
		}
	}

	private void openFile()
	{
		if(file.exists())
		{
			try
			{
				Desktop.getDesktop().open(file);
			}
			catch (IOException e1)
			{
				showMessageDialog(this, Messages.get("Unable to open file") + ": " + file, Messages.get("Error"), ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
		else
			showMessageDialog(this, Messages.get("File not found") + ": " + file, Messages.get("Error"), ERROR_MESSAGE);
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
		return relativePath(file);
	}

	@Override
	public void setValue(String value)
	{
		file = new File(fullPath(value));
		lblFileName.setText(value);
		lblFileName.setForeground( file.exists() ? Color.BLACK : Color.RED );
	}

	@Override
	public boolean hasChanged()
	{
		return defFile.getAbsolutePath().equals(file.getAbsolutePath());
	}

	@Override
	public boolean isInputValid()
	{
		return file.exists();
	}

	@Override
	public void resetDefValue()
	{
		this.defFile = file;
	}
}
