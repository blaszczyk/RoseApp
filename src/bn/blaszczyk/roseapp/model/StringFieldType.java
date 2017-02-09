package bn.blaszczyk.roseapp.model;

public enum StringFieldType implements NamedEnum {
	
	TIME("[0-2]\\d:[0-5]\\d"),
	EMAIL(".*@.*\\..*"),
	STRING(".*"),
	OTHER(""),
	FILE("([a-zA-Z]:)?([\\/\\\\][^\\/\\\\\\.]+)*.\\w*");
	
	public static StringFieldType fromRegex(String regex)
	{
		for(StringFieldType type : values())
			if( type.getRegex().equals(regex))
				return type;
		return OTHER;
	}

	private String name;
	private final String regex;

	private StringFieldType(String regex)
	{
		this.regex = regex;
		this.name = name();
	}

	public String getRegex()
	{
		return regex;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}
	
}
