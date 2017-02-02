package bn.blaszczyk.roseapp.view.panels.settings;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import bn.blaszczyk.roseapp.controller.ModelController;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.factories.ButtonFactory;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;
import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.roseapp.tools.Preferences.*;

import java.io.File;


@SuppressWarnings("serial")
public class OtherSettingsPanel extends AbstractRosePanel {

	private static final String[] LOG_LEVELS = new String[]{"ALL","DEBUG","INFO","WARN","ERROR","OFF"};
	
	private File baseDirectory;
	private final JLabel lblBaseDirectoryValue;
	private final JComboBox<String> cbxLogLevel;

	public OtherSettingsPanel()
	{
		super(null);
		
		baseDirectory = new File(getStringValue(BASE_DIRECTORY, "C:/temp"));
		if(!baseDirectory.exists() || !baseDirectory.isDirectory())
		{
			baseDirectory = new File("C:/temp");
			baseDirectory.mkdirs();
		}
		JLabel lblBaseDirectory = LabelFactory.createLabel("Base Directory:");
		lblBaseDirectory.setBounds(H_SPACING, V_SPACING, PROPERTY_WIDTH, LBL_HEIGHT);
		add(lblBaseDirectory);
		
		lblBaseDirectoryValue = LabelFactory.createLabel(baseDirectory.getAbsolutePath());
		lblBaseDirectoryValue.setBounds(2 * H_SPACING + PROPERTY_WIDTH, V_SPACING, VALUE_WIDTH, LBL_HEIGHT);
		add(lblBaseDirectoryValue);
		
		JButton btnBaseDirectory = ButtonFactory.createIconButton("open.png", e -> selectBaseDirectory());
		btnBaseDirectory.setBounds(3 * H_SPACING + PROPERTY_WIDTH + VALUE_WIDTH, V_SPACING, TBL_BTN_WIDTH, LBL_HEIGHT);
		add(btnBaseDirectory);
		
		JLabel lblLogLevel = LabelFactory.createLabel("Log Level");
		lblLogLevel.setBounds( H_SPACING, 2 * V_SPACING + LBL_HEIGHT, PROPERTY_WIDTH, LBL_HEIGHT);
		add(lblLogLevel);
		
		String loglevel = getStringValue(LOG_LEVEL, "INFO");
		cbxLogLevel = new JComboBox<>(LOG_LEVELS);
		cbxLogLevel.setFont(VALUE_FONT);
		cbxLogLevel.setSelectedItem(loglevel);
		cbxLogLevel.setBounds(2 * H_SPACING + PROPERTY_WIDTH, 2 * V_SPACING + LBL_HEIGHT, VALUE_WIDTH, LBL_HEIGHT);
		cbxLogLevel.addActionListener(e -> notify(true,e));
		add(cbxLogLevel);
		
	}
	
	private void selectBaseDirectory()
	{
		JFileChooser chooser = new JFileChooser(baseDirectory);
		chooser.setDialogTitle(Messages.get("Choose Base Directory"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setCurrentDirectory(baseDirectory);
		if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
		{
			baseDirectory = chooser.getSelectedFile();
			lblBaseDirectoryValue.setText(baseDirectory.getAbsolutePath());
			notify(true,baseDirectory);
		}
	}

	@Override
	public void save(ModelController controller)
	{
		putStringValue( BASE_DIRECTORY, baseDirectory.getAbsolutePath());
		String loglevel = String.valueOf( cbxLogLevel.getSelectedItem() );
		putStringValue( LOG_LEVEL, loglevel );
		Logger.getRootLogger().setLevel(Level.toLevel(loglevel));
		super.save(controller);
	}
	
}
