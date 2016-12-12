package bn.blaszczyk.roseapp.view.settings;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JList;

import bn.blaszczyk.roseapp.tools.Preferences;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.panels.AbstractPanelContainer;
import bn.blaszczyk.roseapp.view.panels.EntityPanel;
import bn.blaszczyk.roseapp.view.panels.TabbedPanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class SettingsPanel extends AbstractPanelContainer {
	
	public static interface SubPanelLoader{
		public void loadSubpanels( SettingsPanel panel);
	}
	
	private static SubPanelLoader loader = p -> {};	
	private final Map<String,EntityPanel> subPanels = new HashMap<>();
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
		subPanels.put(name, panel);
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
		add( subPanels.get( settingsList.getSelectedValue() ).getPanel(), BorderLayout.CENTER );
		refresh();
	}
	
	private void addDefaultPanels()
	{
		TabbedPanel tableColumnPanel = new TabbedPanel();
		for(Class<?> type : TypeManager.getEntityClasses())
			tableColumnPanel.addTab(type.getSimpleName(), new EntityTableColumnSettingPanel(type));
		addSubPanel("Table Columns", tableColumnPanel);
	}

	@Override
	public Object getShownObject()
	{
		return Preferences.class;
	}
	
}
