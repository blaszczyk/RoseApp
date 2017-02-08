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

import org.apache.log4j.Logger;

import bn.blaszczyk.roseapp.tools.FileConverter;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.RoseListener;
import bn.blaszczyk.roseapp.view.factories.ButtonFactory;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static javax.swing.JOptionPane.*;

public class FileInputPanel extends JPanel implements InputPanel<String> {
	
	private static final long serialVersionUID = -8724339680510275161L;
	
	private final JLabel label;
	private final JLabel lblFileName;
	private final JButton button;
	private RoseListener listener = null;
	
	private final FileConverter fileConverter = new FileConverter();
	
	private File defFile;
	private File file;
	
	public FileInputPanel( String name, String fileName, boolean edit )
	{
		
//		String fullFileName = baseDirName + fileName;
		this.file = this.defFile = fileConverter.fromPath(fileName);
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
			setValue( fileConverter.relativePath( chooser.getSelectedFile() ) );
			if(listener != null)
				listener.notify(new RoseEvent(this, true, chooser.getSelectedFile()));
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
			catch (IOException e)
			{
				Logger.getLogger(getClass()).error("Error opening file: " + file.getAbsolutePath(), e);
				showMessageDialog(this, Messages.get("Unable to open file") + ": " + file, Messages.get("Error"), ERROR_MESSAGE);
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
		return fileConverter.relativePath(file);
	}

	@Override
	public void setValue(String value)
	{
		file = fileConverter.fromPath(value);
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
	
	@Override
	public String toString()
	{
		return "FileInputPanel";
	}
}
