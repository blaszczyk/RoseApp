package bn.blaszczyk.roseapp.view.panels.settings;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JList;

import org.apache.log4j.Logger;

import bn.blaszczyk.roseapp.RoseAppLauncher;
import bn.blaszczyk.roseapp.tools.AppPreference;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.panels.AbstractPanelContainer;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.settings.PrimitiveSettingsPanel.PrimitiveSetting;
import bn.blaszczyk.rosecommon.RoseException;
import bn.blaszczyk.rosecommon.client.ServiceConfigClient;
import bn.blaszczyk.rosecommon.dto.PreferenceDto;
import bn.blaszczyk.rosecommon.tools.CommonPreference;
import bn.blaszczyk.rosecommon.tools.Preferences;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class SettingsPanel extends AbstractPanelContainer<RosePanel> {

	private static final long serialVersionUID = -4023057905189539219L;

	public static interface SubPanelLoader{
		public void loadSubpanels( SettingsPanel panel);
	}
	
	private static SubPanelLoader loader = p -> {};	
	
	private final Map<String,RosePanel> subPanels = new LinkedHashMap<>();
	private	final JList<String> settingsList;
	private final boolean serviceMode;
	private PreferenceDto dto = null;
	private RosePanel currentPanel = null;
	
	
	public SettingsPanel()
	{
		serviceMode = Preferences.getStringValue(AppPreference.ACCESS_MODE).equals(RoseAppLauncher.ACCESS_SERVICE);
		if(serviceMode)
		{
			try
			{
				dto = ServiceConfigClient.getInstance().getPreferences();
			}
			catch (RoseException e)
			{
				final String message = "Unable to fetch preferences from Server";
				Logger.getLogger(this.getClass()).error(message, e);
				error(e, message);
			}
		}
		setLayout(new BorderLayout());
		addDefaultPanels();
		loader.loadSubpanels(this);
		registerRoseListener();		
		
		String[] settingsNames = new String[subPanels.keySet().size()];
		subPanels.keySet().toArray(settingsNames);
		settingsList = new JList<>(settingsNames);
		settingsList.addListSelectionListener(e -> showSelectedSubPanel());
		settingsList.setFont(PROPERTY_FONT);
		add(settingsList, BorderLayout.LINE_START);
	}
	
	public static void setSubPanelLoader( SubPanelLoader loader)
	{
		SettingsPanel.loader = loader;
	}
	
	public void addSubPanel( String name, RosePanel panel)
	{
		subPanels.put(Messages.get(name), panel);
	}
	
	
	/**
	 * <h2>invoke like this:</h2>
	 * addPrimitivesPanel( "Test", new PrimitiveSetting<?>[]{<br>
	 *		new PrimitiveSetting<String>( "testString", "defString"),<br>
	 *		new PrimitiveSetting<Integer>( "testInteger", 1337),<br>
	 *		new PrimitiveSetting<Boolean>("testBoolean", true),<br>
	 *		new PrimitiveSetting<BigDecimal>("testBigDecimal",  new BigDecimal("53180.08") )<br>
	 *	});
	 * 
	 * @param name
	 * @param settings
	 */
	public void addPrimitivesPanel( String name, PrimitiveSetting[] settings, boolean serviceSettingIfPossible)
	{
		final RosePanel panel;
		if(serviceSettingIfPossible && serviceMode)
			if(dto != null)
				panel = PrimitiveSettingsPanel.createForServiceWithTitleButton(name, settings, dto);
			else
				panel = null;
		else
			panel = PrimitiveSettingsPanel.createForAppWithTitleButton(name, settings);
		if(panel != null)
			addSubPanel(name, panel);
	}
	
	@Override
	public Iterator<RosePanel> iterator()
	{
		return subPanels.values().iterator();
	}

	private void showSelectedSubPanel()
	{
		if(currentPanel != null)
			remove(currentPanel.getPanel());
		currentPanel = subPanels.get( settingsList.getSelectedValue() );
		add( currentPanel.getPanel(), BorderLayout.CENTER );
		refresh();
	}
	
	private void addDefaultPanels()
	{
		addSubPanel("Access", new AccessSettingsPanel());
		addSubPanel("Start", StartSettingPanelFactory.create());
		addSubPanel("Table Columns", new EntityTableColumnSettingPanel());
//		addSubPanel("Field Types", new FieldTypesSettingPanel());
		
		addSubPanel("Other", new OtherSettingsPanel());
		if(serviceMode)
		{
			final PrimitiveSetting[] settings = new PrimitiveSetting[]{
					new PrimitiveSetting(CommonPreference.DB_HOST),
					new PrimitiveSetting(CommonPreference.DB_PORT,"\\d{1,5}"),
					new PrimitiveSetting(CommonPreference.DB_NAME),
					new PrimitiveSetting(CommonPreference.DB_USER),
					new PrimitiveSetting(CommonPreference.DB_PASSWORD,true)
			};
			addPrimitivesPanel("Server - DataBase", settings, true);
		}
	}

	@Override
	public Object getShownObject()
	{
		return Preferences.class;
	}

	@Override
	public int getFixWidth()
	{
		return FULL_TABLE_WIDTH;
	}

	@Override
	public int getFixHeight()
	{
		return PANEL_HEIGHT;
	}
	
}
