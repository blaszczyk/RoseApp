package bn.blaszczyk.roseapp.tools;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
					messages.put(tokens[0].trim().toLowerCase(), tokens[1].trim());				
			}
		}
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
