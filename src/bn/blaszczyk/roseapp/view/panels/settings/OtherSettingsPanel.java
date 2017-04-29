package bn.blaszczyk.roseapp.view.panels.settings;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.factories.ButtonFactory;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;
import bn.blaszczyk.roseapp.view.factories.TextFieldFactory;
import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;
import bn.blaszczyk.rosecommon.RoseException;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.rosecommon.tools.Preferences.*;

import java.awt.event.ActionEvent;
import java.io.File;


public class OtherSettingsPanel extends AbstractRosePanel {

	private static final long serialVersionUID = -5513414662598274921L;

	private static final String[] LOG_LEVELS = new String[]{"ALL","DEBUG","INFO","WARN","ERROR","OFF"};
	
	private File baseDirectory;
	private final JLabel lblBaseDirectoryValue;
	private final JComboBox<String> cbxLogLevel;
	private final JRadioButton rbFetchLazy;
	private final JRadioButton rbFetchOnStart;
	private final JCheckBox chbFetchTimeAll;
	private final JTextField tfFetchTime;

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

		JLabel lblFetchTime = LabelFactory.createLabel("fetch entities max age/days",SwingConstants.RIGHT);
		lblFetchTime.setBounds( H_SPACING, 5 * V_SPACING + 4 * LBL_HEIGHT, PROPERTY_WIDTH, LBL_HEIGHT);
		add(lblFetchTime);

		int fetchTimeSpan = getIntegerValue(FETCH_TIMESPAN, Integer.MAX_VALUE);

		tfFetchTime = TextFieldFactory.createIntegerField(fetchTimeSpan);
		tfFetchTime.setVisible(fetchTimeSpan != Integer.MAX_VALUE);
		tfFetchTime.setBounds(3 * H_SPACING + 2 * PROPERTY_WIDTH, 5 * V_SPACING + 4 * LBL_HEIGHT, PROPERTY_WIDTH, LBL_HEIGHT);
		tfFetchTime.addActionListener(e -> notify(true,e));
		add(tfFetchTime);

		chbFetchTimeAll = new JCheckBox(Messages.get("all"));
		chbFetchTimeAll.setFont(PROPERTY_FONT);
		chbFetchTimeAll.setSelected(fetchTimeSpan == Integer.MAX_VALUE);
		chbFetchTimeAll.setBounds(2 * H_SPACING + PROPERTY_WIDTH, 5 * V_SPACING + 4 * LBL_HEIGHT, PROPERTY_WIDTH, LBL_HEIGHT);
		chbFetchTimeAll.addActionListener(e -> toggleTimeSpanTextField(e));
		add(chbFetchTimeAll);
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
	
	private void toggleTimeSpanTextField(ActionEvent e)
	{
		tfFetchTime.setVisible(!chbFetchTimeAll.isSelected());
		notify(true,e);
	}

	@Override
	public void save() throws RoseException
	{
		putStringValue( BASE_DIRECTORY, baseDirectory.getAbsolutePath());
		String loglevel = String.valueOf( cbxLogLevel.getSelectedItem() );
		putStringValue( LOG_LEVEL, loglevel );
		Logger.getRootLogger().setLevel(Level.toLevel(loglevel));
		putBooleanValue(FETCH_ON_START, rbFetchOnStart.isSelected());
		int fetchTimeSpan = Integer.MAX_VALUE;
		if(!chbFetchTimeAll.isSelected())
			fetchTimeSpan = Integer.parseInt(tfFetchTime.getText());
		putIntegerValue(FETCH_TIMESPAN, fetchTimeSpan);
		super.save();
	}
	
}
