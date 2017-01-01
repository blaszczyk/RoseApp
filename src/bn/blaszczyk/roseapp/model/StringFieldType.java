package bn.blaszczyk.roseapp.model;

public enum StringFieldType {
	
	TIME("[0-2]\\d:[0-5]\\d"),
	EMAIL(".*@.*\\..*"),
	STRING(".*"),
	OTHER(""),
	FILE("[A-Z]:([/\\\\]\\w*).\\w*");
	
	public static StringFieldType fromRegex(String regex)
	{
		for(StringFieldType type : values())
			if( type.getRegex().equals(regex))
				return type;
		return OTHER;
	}
	
	private final String regex;

	private StringFieldType(String regex)
	{
		this.regex = regex;
	}

	public String getRegex()
	{
		return regex;
	}
	
}
