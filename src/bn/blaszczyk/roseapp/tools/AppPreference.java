package bn.blaszczyk.roseapp.tools;

import bn.blaszczyk.roseapp.model.StringFieldType;
import bn.blaszczyk.rosecommon.tools.Preference;

import static bn.blaszczyk.rosecommon.tools.Preference.Type.*;

import javax.swing.SortOrder;

public enum AppPreference implements Preference {
	
	COLUMN_WIDTH(INT,"columnwidth",80),
	COLUMN_CONTENT(STRING,"columncontent",""),
	COLUMN_COUNT(INT,"columncount",0),

	SORT_COLUMN(INT,"sortcolumn",1),
	SORT_ORDER(STRING,"sortorder",SortOrder.ASCENDING.name()),
	
	START_BUTTON(STRING,"startbutton",""),
	START_BUTTON_COUNT(INT,"startbuttoncount",0),

	FIELD_TYPE(STRING,"fieldtype",StringFieldType.STRING.name());
	
	private final Type type;
	private final String key;
	private final Object defaultValue;
	
	private AppPreference(final Type type, final String key, final Object defaultValue)
	{
		if(defaultValue != null && !type.getType().isInstance(defaultValue))
			throw new IllegalArgumentException("preference " + key + "of type " + type + " has false default value class: " + defaultValue.getClass());
		this.type = type;
		this.key = key;
		this.defaultValue = defaultValue;
	}

	@Override
	public Type getType()
	{
		return type;
	}

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public Object getDefaultValue()
	{
		return type.getType().cast(defaultValue);
	}
	
}
