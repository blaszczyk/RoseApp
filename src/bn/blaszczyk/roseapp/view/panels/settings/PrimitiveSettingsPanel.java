package bn.blaszczyk.roseapp.view.panels.settings;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.TitleButtonsPanel;
import bn.blaszczyk.roseapp.view.panels.input.*;
import bn.blaszczyk.rosecommon.RoseException;
import bn.blaszczyk.rosecommon.tools.Preference;

import static bn.blaszczyk.rosecommon.tools.Preferences.*;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class PrimitiveSettingsPanel extends AbstractRosePanel {
	
	private static final long serialVersionUID = -2243667159771643857L;

	public static RosePanel createWithTitleButton(String title, Iterable<PrimitiveSetting> settings)
	{
		return dressWithTitleButton( title, new PrimitiveSettingsPanel(settings));
	}

	public static RosePanel createWithTitleButton(String title, PrimitiveSetting[] settings)
	{
		return dressWithTitleButton(title, new PrimitiveSettingsPanel(settings));
	}

	private static RosePanel dressWithTitleButton(String title, PrimitiveSettingsPanel panel)
	{
		return TitleButtonsPanel.withBorder(title, panel);
	}

	private final Map<PrimitiveSetting,InputPanel<?>> panelMap = new LinkedHashMap<>();
	private int height = V_SPACING;
	
	public PrimitiveSettingsPanel(Iterable<PrimitiveSetting> settings)
	{
		for( PrimitiveSetting setting : settings)
			initializeSettingPanel(setting);
	}
	
	public PrimitiveSettingsPanel(PrimitiveSetting[] settings)
	{
		for( PrimitiveSetting setting : settings)
			initializeSettingPanel(setting);
	}
	
	private void initializeSettingPanel(PrimitiveSetting setting)
	{
		final Preference preference = setting.getPreference();
		final InputPanel<?> panel;
		final String name = preference.getKey();
		if(setting.isPassword())
		{
			String value = getStringValue(preference);
			panel = new StringInputPanel(name, value, true);
		}
		else
		{
			switch (setting.getPreference().getType())
			{
			case BOOLEAN:
				Boolean booleanValue = getBooleanValue(preference);
				panel = new BooleanInputPanel(name,booleanValue);
				break;
			case INT:
				Integer intValue = getIntegerValue(preference);
				panel = new IntegerInputPanel(name, intValue);
				break;
			case NUMERIC:
				BigDecimal numericValue = getBigDecimalValue(preference);
				panel = new BigDecimalInputPanel(name,numericValue, 10, 2);
				break;
			case STRING:
				String stringValue = getStringValue(preference);
				panel = new StringInputPanel(name, stringValue, 100, setting.getRegex());
				break;
			default:
				throw new IllegalArgumentException("Unknown Setting type: " + preference.getType());
			}
		}
		panel.setRoseListener(this);
		panelMap.put( setting, panel );
		JPanel jPanel = panel.getPanel();
		jPanel.setBounds( H_SPACING, height, BASIC_WIDTH, LBL_HEIGHT );
		add(jPanel);
		height += LBL_HEIGHT + V_SPACING;
	}
	
	private void saveSetting(PrimitiveSetting setting)
	{
		final InputPanel<?> panel = panelMap.get(setting);
		Object value = panel.getValue();
		putValue(setting.getPreference(), value);
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
	public void save() throws RoseException
	{
		super.save();
		for(PrimitiveSetting setting : panelMap.keySet())
			saveSetting(setting);
	}

	public static class PrimitiveSetting
	{
		private final Preference preference;
		private final String regex;
		private final boolean password;
		
		public PrimitiveSetting(final Preference preference, String regex)
		{
			this.preference = preference;
			this.regex = regex;
			this.password = false;
		}

		public PrimitiveSetting(final Preference preference)
		{
			this(preference, ".*");
		}

		public PrimitiveSetting(final Preference preference, final boolean password)
		{
			this.preference = preference;
			this.regex = ".*";
			this.password = password;
		}
		
		Preference getPreference()
		{
			return preference;
		}

		Class<?> getType()
		{
			return preference.getType().getType();
		}

		String getPrefsKey()
		{
			return preference.getKey();
		}
		
		String getRegex()
		{
			return regex;
		}
		
		boolean isPassword()
		{
			return password;
		}
	}
	
}
