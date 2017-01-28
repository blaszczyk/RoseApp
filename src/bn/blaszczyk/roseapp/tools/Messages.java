package bn.blaszczyk.roseapp.tools;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import bn.blaszczyk.roseapp.model.NamedEnum;
import bn.blaszczyk.roseapp.model.StringFieldType;

public class Messages {
	
	private static final Map<String,String> messages = new HashMap<>();
	private static final String DELIMITER = ";";
	private static final String COMMENT = "#";
	
	public static void load(InputStream stream)
	{
		try(Scanner scanner = new Scanner(stream))
		{
			while(scanner.hasNextLine())
			{
				String line = scanner.nextLine();
				if(line.startsWith(COMMENT))
					continue;
				String[] tokens = line.split(DELIMITER);
				if(tokens.length != 2)
					System.err.println("Invalid message format \"" + line + "\"");
				else
				{
					String key = tokens[0].trim();
					String value = tokens[1].trim();
					if(key.startsWith("__") && key.endsWith("__"))
						setEnumName(key,value);
					else
						messages.put(key.toLowerCase(), value);
				}
			}
		}
	}
	
	private static void setEnumName(String key, String name)
	{
		String enumValueName = key.substring(2, key.length()-2);
		Class<?>[] enumTypes = new Class<?>[]{StringFieldType.class};
		for(Class<?> enumType : enumTypes)
			for(Object enumValue : enumType.getEnumConstants())
				if(enumValue instanceof NamedEnum && enumValueName.equals(enumValue.toString()))
					((NamedEnum)enumValue).setName(name);
	}

	public static String get(String key)
	{
		if(key == null)
			return "";
		String message = messages.get(key.toLowerCase());
		if(message != null)
			return message;
		return key;
	}
	
	private Messages()
	{
	}
	
}
