package bn.blaszczyk.roseapp.view.panels.settings;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
	private final JRadioButton rbFetchLazy;
	private final JRadioButton rbFetchOnStart;

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
		
		ButtonGroup fetchGroup = new ButtonGroup();
		boolean fetchOnStart = getBooleanValue(FETCH_ON_START, false);

		rbFetchLazy = new JRadioButton(Messages.get("fetch entities on request"));
		rbFetchLazy.setFont(PROPERTY_FONT);
		rbFetchLazy.setSelected(!fetchOnStart);
		rbFetchLazy.setBounds(2 * H_SPACING + PROPERTY_WIDTH, 3 * V_SPACING + 2 * LBL_HEIGHT, VALUE_WIDTH, LBL_HEIGHT);
		rbFetchLazy.addActionListener(e -> notify(true, e));
		fetchGroup.add(rbFetchLazy);
		add(rbFetchLazy);
		
		rbFetchOnStart = new JRadioButton(Messages.get("fetch entities on startup"));
		rbFetchOnStart.setFont(PROPERTY_FONT);
		rbFetchOnStart.setSelected(fetchOnStart);
		rbFetchOnStart.setBounds(2 * H_SPACING + PROPERTY_WIDTH, 4 * V_SPACING + 3 * LBL_HEIGHT, VALUE_WIDTH, LBL_HEIGHT);
		rbFetchOnStart.addActionListener(e -> notify(true, e));
		fetchGroup.add(rbFetchOnStart);
		add(rbFetchOnStart);
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
	public void save()
	{
		putStringValue( BASE_DIRECTORY, baseDirectory.getAbsolutePath());
		String loglevel = String.valueOf( cbxLogLevel.getSelectedItem() );
		putStringValue( LOG_LEVEL, loglevel );
		Logger.getRootLogger().setLevel(Level.toLevel(loglevel));
		putBooleanValue(FETCH_ON_START, rbFetchOnStart.isSelected());
		super.save();
	}
	
}
