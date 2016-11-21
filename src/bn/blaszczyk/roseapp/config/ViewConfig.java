package bn.blaszczyk.roseapp.config;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.view.tools.ColumnContent;

public class ViewConfig {
	
	private static final String DELIMITER = "\\;";
	
	private final static Map<Class<?>, List<ColumnContent>> COLUMN_CONTENT_MAP = new HashMap<>();
	private final static Map<Class<?>, int[]> COLUMN_WIDTH_MAP = new HashMap<>();

	
	public static List<ColumnContent> getColumnContents(Class<?> type)
	{
		return COLUMN_CONTENT_MAP.get(type);
	}
	
	/*
	 * Format is "field0;field1;entity0;entity1.field2;entity1.entity0.field4"
	 */
	public static void putColumnContentsAsString(Class<?> type, String wString) throws ParseException
	{	
		String[] split = wString.split(DELIMITER);
		List<ColumnContent> columnContents = new ArrayList<>();
		for(String ccString : split)
			columnContents.add(new ColumnContent(tagEntityName(type, ccString).trim()));
		COLUMN_CONTENT_MAP.put(type, columnContents);
	}

	public static int[] getColumnWidths(Class<?> type)
	{
		return COLUMN_WIDTH_MAP.get(type);
	}
	
	/*
	 * Format is "150;100;100;40"
	 */
	public static void putColumnWidthsAsString(Class<?> type, String ccsString) throws ParseException
	{
		String[] split = ccsString.split(DELIMITER);
		int[] widths = new int[split.length];
		for(int i = 0; i < split.length; i++)
			widths[i] = Integer.parseInt(split[i].trim());
		COLUMN_WIDTH_MAP.put(type, widths);
	}
	
	private static String tagEntityName(final Class<?> type, final String ccString)
	{
		String[] split = ccString.split("\\.|\\,", 2 );
		try
		{
			Readable entity =  (Readable) type.newInstance();
			for(int i = 0; i < entity.getFieldCount(); i++)
				if(split[0].trim().equalsIgnoreCase( entity.getFieldName(i) ))
					return new StringBuilder().append("f").append(i).toString();
			for(int i = 0; i < entity.getEntityCount(); i++)
				if(split[0].trim().equalsIgnoreCase( entity.getEntityName(i) ))
				{
					StringBuilder builder = new StringBuilder();
					builder.append("e").append(i);
					if(split.length == 2)
						builder.append(",").append( tagEntityName(entity.getEntityClass(i), split[1]) );
					return builder.toString();
				}
			return ccString;
		}
		catch (Exception e)
		{
			System.err.println("Unknown EntityFieldName in " + ccString);
			return ccString;
		}		
	}
}
