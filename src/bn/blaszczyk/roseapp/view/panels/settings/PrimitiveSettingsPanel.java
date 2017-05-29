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
import bn.blaszczyk.rosecommon.client.ServiceConfigClient;
import bn.blaszczyk.rosecommon.dto.PreferenceDto;
import bn.blaszczyk.rosecommon.tools.Preference;

import static bn.blaszczyk.rosecommon.tools.Preferences.*;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public abstract class PrimitiveSettingsPanel extends AbstractRosePanel {
	
	private static final long serialVersionUID = -2243667159771643857L;
	private static final String SERVICE_PREFIX = "Service: ";

	public static RosePanel createForAppWithTitleButton(String title, Iterable<PrimitiveSetting> settings)
	{
		return dressWithTitleButton( title, createForApp(settings));
	}

	public static RosePanel createForAppWithTitleButton(String title, PrimitiveSetting[] settings)
	{
		return dressWithTitleButton(title, createForApp(settings));
	}

	public static RosePanel createForServiceWithTitleButton(String title, Iterable<PrimitiveSetting> settings, final PreferenceDto dto)
	{
		return dressWithTitleButton( SERVICE_PREFIX + title, createForService(settings,dto));
	}

	public static RosePanel createForServiceWithTitleButton(String title, PrimitiveSetting[] settings, final PreferenceDto dto)
	{
		return dressWithTitleButton( SERVICE_PREFIX + title, createForService(settings,dto));
	}
	
	public static RosePanel createForApp(final Iterable<PrimitiveSetting> settings)
	{
		final PrimitiveSettingsPanel panel = new PrimitiveAppSettingsPanel();
		panel.intializePanels(settings);
		return panel;
	}
	
	public static RosePanel createForApp(final PrimitiveSetting[] settings)
	{
		final PrimitiveSettingsPanel panel = new PrimitiveAppSettingsPanel();
		panel.intializePanels(settings);
		return panel;
	}
	
	public static RosePanel createForService(final Iterable<PrimitiveSetting> settings, final PreferenceDto dto)
	{
		final PrimitiveSettingsPanel panel = new PrimitiveServiceSettingsPanel(dto);
		panel.intializePanels(settings);
		return panel;
	}
	
	public static RosePanel createForService(final PrimitiveSetting[] settings, final PreferenceDto dto)
	{
		PrimitiveSettingsPanel panel = new PrimitiveServiceSettingsPanel(dto);
		panel.intializePanels(settings);
		return panel;
	}

	private static RosePanel dressWithTitleButton(String title, RosePanel panel)
	{
		return TitleButtonsPanel.withBorder(title, panel);
	}

	final Map<PrimitiveSetting,InputPanel<?>> panelMap = new LinkedHashMap<>();
	private int height = V_SPACING;
	
	private void intializePanels(PrimitiveSetting[] settings)
	{
		for( PrimitiveSetting setting : settings)
			initializeSettingPanel(setting);
	}
	
	private void intializePanels(Iterable<PrimitiveSetting> settings)
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
				Boolean booleanValue = getBooleanDefValue(preference);
				panel = new BooleanInputPanel(name,booleanValue);
				break;
			case INT:
				Integer intValue = getIntegerDefValue(preference);
				panel = new IntegerInputPanel(name, intValue);
				break;
			case NUMERIC:
				BigDecimal numericValue = getBigDecimalDefValue(preference);
				panel = new BigDecimalInputPanel(name,numericValue, 10, 2);
				break;
			case STRING:
				String stringValue = getStringDefValue(preference);
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

	abstract Boolean getBooleanDefValue(final Preference preference);
	abstract Integer getIntegerDefValue(final Preference preference);
	abstract BigDecimal getBigDecimalDefValue(final Preference preference);
	abstract String getStringDefValue(final Preference preference);
	
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
	
	private static class PrimitiveAppSettingsPanel extends PrimitiveSettingsPanel
	{
		private static final long serialVersionUID = -3429652340399250722L;

		@Override
		public void save() throws RoseException
		{
			super.save();
			for(PrimitiveSetting setting : panelMap.keySet())
				saveSetting(setting);
		}
		
		private void saveSetting(PrimitiveSetting setting)
		{
			final InputPanel<?> panel = panelMap.get(setting);
			Object value = panel.getValue();
			putValue(setting.getPreference(), value);
		}

		@Override
		Boolean getBooleanDefValue(final Preference preference)
		{
			return getBooleanValue(preference);
		}

		@Override
		Integer getIntegerDefValue(final Preference preference)
		{
			return getIntegerValue(preference);
		}

		@Override
		BigDecimal getBigDecimalDefValue(final Preference preference)
		{
			return getBigDecimalValue(preference);
		}

		@Override
		String getStringDefValue(final Preference preference)
		{
			return getStringValue(preference);
		}
		
	}
	
	private static class PrimitiveServiceSettingsPanel extends PrimitiveSettingsPanel
	{
		private static final long serialVersionUID = -3429652340399250722L;
		
		private final PreferenceDto initDto;
		
		private PrimitiveServiceSettingsPanel(final PreferenceDto dto)
		{
			initDto = dto;
		}

		@Override
		public void save() throws RoseException
		{
			super.save();
			final PreferenceDto dto = new PreferenceDto();
			for(PrimitiveSetting setting : panelMap.keySet())
			{
				final InputPanel<?> panel = panelMap.get(setting);
				final Object value = panel.getValue();
				dto.put(setting.getPreference(), value);
				
			}
			final ServiceConfigClient client = ServiceConfigClient.getInstance();
			client.putPreferences(dto);
		}

		@Override
		Boolean getBooleanDefValue(Preference preference)
		{
			return initDto.getBoolean(preference);
		}

		@Override
		Integer getIntegerDefValue(Preference preference)
		{
			return initDto.getInt(preference);
		}

		@Override
		BigDecimal getBigDecimalDefValue(Preference preference)
		{
			return initDto.getNumeric(preference);
		}

		@Override
		String getStringDefValue(Preference preference)
		{
			return initDto.getString(preference);
		}
		
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
