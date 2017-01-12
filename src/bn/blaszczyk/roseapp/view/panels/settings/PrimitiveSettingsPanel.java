package bn.blaszczyk.roseapp.view.panels.settings;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

import bn.blaszczyk.roseapp.controller.ModelController;
import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.TitleButtonsPanel;
import bn.blaszczyk.roseapp.view.panels.input.*;

import static bn.blaszczyk.roseapp.tools.Preferences.*;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class PrimitiveSettingsPanel extends AbstractRosePanel {

	public static RosePanel createWithTitleButton(String title, Iterable<PrimitiveSetting<?>> settings)
	{
		return dressWithTitleButton( title, new PrimitiveSettingsPanel(settings));
	}

	public static RosePanel createWithTitleButton(String title, PrimitiveSetting<?>[] settings)
	{
		return dressWithTitleButton(title, new PrimitiveSettingsPanel(settings));
	}
	
	private static RosePanel dressWithTitleButton(String title, PrimitiveSettingsPanel panel)
	{
		TitleButtonsPanel tbPanel = new TitleButtonsPanel(title, panel, false);
		tbPanel.addButton("default", null, e -> panel.setDefault());
		return tbPanel;
	}

	private final Map<PrimitiveSetting<?>,InputPanel<?>> panelMap = new LinkedHashMap<>();
	private int height = V_SPACING;
	
	public PrimitiveSettingsPanel(Iterable<PrimitiveSetting<?>> settings)
	{
		for( PrimitiveSetting<?> setting : settings)
			initializeSettingPanel(setting);
	}
	
	public PrimitiveSettingsPanel(PrimitiveSetting<?>[] settings)
	{
		for( PrimitiveSetting<?> setting : settings)
			initializeSettingPanel(setting);
	}
	
	private void initializeSettingPanel(PrimitiveSetting<?> setting)
	{
		String key = setting.getPrefsKey();
		InputPanel<?> panel = null;
		if(setting.getType().equals(String.class))
		{
			String def = setting.getDefValue().toString();
			String value = getStringValue(key, def);
			panel = new StringInputPanel(key, value, 100, setting.getRegex());
		}
		else if(setting.getType().equals(Integer.class))
		{
			Integer def = (Integer) setting.getDefValue();
			Integer value = getIntegerValue(key, def);
			panel = new IntegerInputPanel( key, value);
		}
		else if(setting.getType().equals(Boolean.class))
		{
			Boolean def = (Boolean) setting.getDefValue();
			Boolean value = getBooleanValue( key, def);
			panel = new BooleanInputPanel(key,value);
		}
		else if(setting.getType().equals(BigDecimal.class))
		{
			BigDecimal def = (BigDecimal) setting.getDefValue();
			BigDecimal value = getBigDecimalValue(key, def);
			panel = new BigDecimalInputPanel(key,value, 10, 2);
		}
		else
			return;
		panel.setRoseListener(this);
		panelMap.put( setting, panel );
		JPanel jPanel = panel.getPanel();
		jPanel.setBounds( H_SPACING, height, BASIC_WIDTH, LBL_HEIGHT );
		add(jPanel);
		height += LBL_HEIGHT + V_SPACING;
	}
	
	private void saveSetting(PrimitiveSetting<?> setting)
	{
		InputPanel<?> panel = panelMap.get(setting);
		String key = setting.getPrefsKey();
		Object value = panel.getValue();
		if(setting.getType().equals(String.class))
			putStringValue(key, value.toString());
		else if(setting.getType().equals(Integer.class))
			putIntegerValue(key, (Integer)value);
		else if(setting.getType().equals(Boolean.class))
			putBooleanValue(key, (Boolean)value);
		else if(setting.getType().equals(BigDecimal.class))
			putBigDecimalValue(key, (BigDecimal)value);
	}

	@SuppressWarnings("unchecked")
	private void setDefault()
	{
		for(PrimitiveSetting<?> setting : panelMap.keySet())
		{
			InputPanel<?> panel = panelMap.get(setting);
			if(setting.getType().equals(String.class))
				((InputPanel<String>)panel).setValue(setting.getDefValue().toString());
			else if(setting.getType().equals(Integer.class))
				((InputPanel<Integer>)panel).setValue((Integer)setting.getDefValue());
			else if(setting.getType().equals(Boolean.class))
				((InputPanel<Boolean>)panel).setValue((Boolean)setting.getDefValue());
			else if(setting.getType().equals(BigDecimal.class))
				((InputPanel<BigDecimal>)panel).setValue((BigDecimal)setting.getDefValue());
		}
	}
	
	@Override
	public int getFixWidth()
	{
		return BASIC_WIDTH + 2 * H_SPACING;
	}
	
	@Override
	public int getFixHeight()
	{
		return height;
	}
	
	@Override
	public void save(ModelController controller)
	{
		super.save(controller);
		for(PrimitiveSetting<?> setting : panelMap.keySet())
			saveSetting(setting);
	}

	public static class PrimitiveSetting<T>
	{
		private final Class<?> type;
		private final String prefsKey;
		private final T defValue;
		private final String regex;
		
		public PrimitiveSetting(Class<T> type, String prefsKey, T defValue, String regex)
		{
			this.type = type;
			this.prefsKey = prefsKey;
			this.defValue = defValue;
			this.regex = regex;
		}

		public PrimitiveSetting(String prefsKey, T defValue, String regex)
		{
			if(defValue == null)
				throw new IllegalArgumentException("primitive setting default value must not be null for this constructor");
			this.type = defValue.getClass();
			this.prefsKey = prefsKey;
			this.defValue = defValue;
			this.regex = regex;
		}
		
		public PrimitiveSetting(String prefsKey, T defValue)
		{
			this(prefsKey, defValue, ".*");
		}

		Class<?> getType()
		{
			return type;
		}

		String getPrefsKey()
		{
			return prefsKey;
		}
		
		T getDefValue()
		{
			return defValue;
		}
		
		String getRegex()
		{
			return regex;
		}
	}
	
}
