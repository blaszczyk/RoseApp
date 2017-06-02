package bn.blaszczyk.roseapp.view.panels.input;

import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.RoseListener;
import bn.blaszczyk.roseapp.view.factories.ButtonFactory;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;
import bn.blaszczyk.rosecommon.RoseException;
import bn.blaszczyk.rosecommon.client.FileClient;
import bn.blaszczyk.rosecommon.tools.FileConverter;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class ServerFileInputPanel extends JPanel implements InputPanel<String> {
	
	private static final long serialVersionUID = -8724339680510275161L;
	
	public static ServerFileInputPanel view( String name, String fileName )
	{
		return new ServerFileInputPanel(name, fileName);
	}
	
	private final JLabel label;
	private final JLabel lblFileName;
	private final JButton serverFileButton;
	private final JButton localFileButton;
	private RoseListener listener = null;
	
	private final FileClient client = FileClient.getInstance();
	
	private final FileConverter fileConverter = new FileConverter();

	private final JPopupMenu serverPopup = new JPopupMenu("Server File");
	private final JPopupMenu localPopup = new JPopupMenu("Local File");
	
	private final File localFile;
	private final String path;
	
	private ServerFileInputPanel( String name, String fileName )
	{
		this.localFile = fileConverter.fromPath(fileName);
		this.path = fileName;
		setBackground(BASIC_PNL_BACKGROUND);
		
		setLayout(null);
		label = LabelFactory.createLabel(name + ": ", PROPERTY_FONT, PROPERTY_FG, SwingConstants.RIGHT);
		label.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
		add(label);
		
		lblFileName = LabelFactory.createLabel("");
		lblFileName.setBounds( PROPERTY_WIDTH + H_SPACING , 0, VALUE_WIDTH - 3 * LBL_HEIGHT - H_SPACING, LBL_HEIGHT);
		add(lblFileName);
		
		setValue(fileName);
		
		boolean serverFileExists = false;
		try
		{
			serverFileExists = client.exists(fileName);
		}
		catch (RoseException e)
		{
			logAndMessage(e,"Unable to check file existence on Server");
		}
		serverFileButton = ButtonFactory.createIconButton( serverFileExists ? "servercloud.png" : "servercloud-x.png", e -> serverPopup() );
		serverFileButton.setBounds( BASIC_WIDTH - 3 * LBL_HEIGHT, 0, LBL_HEIGHT, LBL_HEIGHT);
		add(serverFileButton);
		
		final boolean localFileExists = fileConverter.fromPath(fileName).exists();
		localFileButton = ButtonFactory.createIconButton( localFileExists ? "localdrive.png" : "localdrive-x.png", e -> localPopup() );
		localFileButton.setBounds( BASIC_WIDTH - LBL_HEIGHT, 0, LBL_HEIGHT, LBL_HEIGHT);
		add(localFileButton);
		
		initPopups(serverFileExists, localFileExists);
	}
	
	private void initPopups(final boolean serverFileExists, final boolean localFileExists)
	{
		if(serverFileExists)
		{
			serverPopup.add(item("download",e -> download()));
			serverPopup.add(item("open in browser", e -> openInBrowser()));
		}
		serverPopup.add(item("upload",e -> upload()));
		if(localFileExists)
			localPopup.add(item("open", e -> openLocal()));
		localPopup.add(item("copy from", e -> copyFrom()));
	}
	
	private void download()
	{
		try
		{
			client.download(path);
		}
		catch (RoseException e)
		{
			logAndMessage(e, "Error downloading " + path);
		}
	}
	
	private void upload()
	{
		try
		{
			final String extension = path.substring(path.lastIndexOf('.') + 1);
			final File file = chooseFile(extension);
			if(file != null)
				client.upload(path, file);
		}
		catch (Exception e) 
		{
        	final String message = "Error uploading " + path;
        	logAndMessage(RoseException.wrap(e, message), message);
		}
	}
	
	private void openInBrowser()
	{
		final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
		{
			try
			{
				final URL url = client.urlFor(path);
				desktop.browse(url.toURI());
			}
			catch (Exception e)
			{
				final String message = "Error opening " + path + " in default browser";
				logAndMessage(RoseException.wrap(e, message), message);
			}
		}
	}
	
	private void openLocal()
	{
		try
		{
			Desktop.getDesktop().open(localFile);
		}
		catch (IOException e)
		{
			final String message = "Error opening file: " + localFile.getAbsolutePath();
			logAndMessage(RoseException.wrap(e, message), message);
		}
	}
	
	private void copyFrom()
	{
		try
		{
			final String extension = path.substring(path.lastIndexOf('.') + 1);
			final File file = chooseFile(extension);
			if(file != null)
				Files.copy(file.toPath(), localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch (Exception e) 
		{
        	final String message = "Error copying to " + path;
        	logAndMessage(RoseException.wrap(e, message), message);
		}
	}

	private JMenuItem item(final String name, final ActionListener listener)
	{
		final JMenuItem item = new JMenuItem(name);
		item.addActionListener(listener);
		return item;
	}
	
	private void logAndMessage(final RoseException e, final String message)
	{
		Logger.getLogger(ServerFileInputPanel.class).error(message, e);
		JOptionPane.showMessageDialog(this, e.getFullMessage(), message, JOptionPane.ERROR_MESSAGE);
	}

	private void serverPopup()
	{
		serverPopup.show(serverFileButton, H_SPACING / 2, H_SPACING / 2);
	}

	private void localPopup()
	{
		localPopup.show(localFileButton, H_SPACING / 2, H_SPACING / 2);
	}

	private File chooseFile(final String extension)
	{
		JFileChooser chooser;
		if(localFile.exists())
		{
			chooser = new JFileChooser(localFile);
			chooser.setCurrentDirectory(localFile);
		}
		else
			chooser = new JFileChooser();
		chooser.setDialogTitle(Messages.get("Choose File"));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileNameExtensionFilter("Original Extension", extension));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		if( chooser.showOpenDialog(this) ==  JFileChooser.APPROVE_OPTION )
		{
			if(listener != null)
				listener.notify(new RoseEvent(this, true, chooser.getSelectedFile()));
			return chooser.getSelectedFile() ;
		}
		return null;
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
		return fileConverter.relativePath(localFile);
	}

	@Override
	public void setValue(String value)
	{
		lblFileName.setText(value);
	}

	@Override
	public boolean hasChanged()
	{
		return false;
	}

	@Override
	public boolean isInputValid()
	{
		return true;
	}

	@Override
	public void resetDefValue()
	{
	}
	
	@Override
	public String toString()
	{
		return "FileInputPanel";
	}
}
