package bn.blaszczyk.roseapp.view.panels.settings;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JList;

import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.tools.Preferences;
import bn.blaszczyk.roseapp.view.panels.AbstractPanelContainer;
import bn.blaszczyk.roseapp.view.panels.EntityPanel;
import bn.blaszczyk.roseapp.view.panels.settings.PrimitiveSettingsPanel.PrimitiveSetting;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class SettingsPanel extends AbstractPanelContainer {
	
	public static interface SubPanelLoader{
		public void loadSubpanels( SettingsPanel panel);
	}
	
	private static SubPanelLoader loader = p -> {};	
	private final Map<String,EntityPanel> subPanels = new LinkedHashMap<>();
	private	final JList<String> settingsList; 
	private EntityPanel currentPanel = null;
	
	public SettingsPanel()
	{
		setLayout(new BorderLayout());
		addDefaultPanels();
		loader.loadSubpanels(this);
		registerActionListener();		
		
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
	
	public void addSubPanel( String name, EntityPanel panel)
	{
		subPanels.put(Messages.get(name), panel);
	}	
	
	public void addPrimitivesPanel( String name, PrimitiveSetting<?>[] settings)
	{
		addSubPanel(name, PrimitiveSettingsPanel.createWithTitleButton(name, settings));
	}
	
	@Override
	protected Iterable<EntityPanel> getPanels()
	{
		return subPanels.values();
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
		addSubPanel("Start", StartSettingPanelFactory.create());
		addSubPanel("Table Columns", new EntityTableColumnSettingPanel());
//		addPrimitivesPanel( "Test", new PrimitiveSetting<?>[]{
//			new PrimitiveSetting<String>( "testString", "defString"),
//			new PrimitiveSetting<Integer>( "testInteger", 1337),
//			new PrimitiveSetting<Boolean>("testBoolean", true),
//			new PrimitiveSetting<BigDecimal>("testBigDecimal",  new BigDecimal("53180.08") )
//		});
	}

	@Override
	public Object getShownObject()
	{
		return Preferences.class;
	}
	
}
