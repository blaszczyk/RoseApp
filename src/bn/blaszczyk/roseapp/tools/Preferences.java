package bn.blaszczyk.roseapp.tools;

public class Preferences {
	

	public final static String COLUMN_WIDTH = "columnwidth";
	public final static String COLUMN_CONTENT = "columncontent";
	public final static String COLUMN_COUNT = "columncount";
	
	public final static String START_BUTTON = "startbutton";
	public final static String START_BUTTON_COUNT = "startbuttoncount";

	private static java.util.prefs.Preferences preferences;
	
	private Preferences()
	{
	}
	
	public static void setMainClass(Class<?> type)
	{
		preferences = java.util.prefs.Preferences.userNodeForPackage(type);
	}

	public static String getStringValue(String key, String def)
	{
		return preferences.get(key, def);
	}
	
	public static void putStringValue( String key, String value)
	{
		preferences.put(key, value);
	}

	public static boolean getIntegerValue(String key, boolean def)
	{
		return preferences.getBoolean(key, def);
	}
	
	public static void putBooleanValue( String key, boolean value)
	{
		preferences.putBoolean(key, value);
	}

	public static int getIntegerValue(String key, int def)
	{
		return preferences.getInt(key, def);
	}
	
	public static void putIntegerValue( String key, int value)
	{
		preferences.putInt(key, value);
	}
	
	public static String getStringEntityValue(Class<?> type, String key, String def)
	{
		return getEntityNode(type).get(key, def);
	}
	
	public static String getStringEntityValue(Readable entity, String key, String def)
	{
		return getStringEntityValue(entity.getClass(), key, def);
	}
	
	public static void putStringEntityValue(Class<?> type, String key, String value)
	{
		getEntityNode(type).put(key, value);
	}
	
	public static void putStringEntityValue(Readable entity, String key, String value)
	{
		putStringEntityValue(entity.getClass(), key, value);
	}
	
	public static boolean getBooleanEntityValue(Class<?> type, String key, boolean def)
	{
		return getEntityNode(type).getBoolean(key, def);
	}
	
	public static boolean getBooleanEntityValue(Readable entity, String key, boolean def)
	{
		return getBooleanEntityValue(entity.getClass(), key, def);
	}
	
	public static void putBooleanEntityValue(Class<?> type, String key, boolean value)
	{
		getEntityNode(type).putBoolean(key, value);
	}
	
	public static void putBooleanEntityValue(Readable entity, String key, boolean value)
	{
		putBooleanEntityValue(entity.getClass(), key, value);
	}
	
	public static int getIntegerEntityValue(Class<?> type, String key, int def)
	{
		return getEntityNode(type).getInt(key, def);
	}
	
	public static int getIntegerEntityValue(Readable entity, String key, int def)
	{
		return getIntegerEntityValue(entity.getClass(), key, def);
	}
	
	public static void putIntegerEntityValue(Class<?> type, String key, int value)
	{
		getEntityNode(type).putInt(key, value);
	}
	
	public static void putIntegerEntityValue(Readable entity, String key, int value)
	{
		putIntegerEntityValue(entity.getClass(), key, value);
	}
	
	private static java.util.prefs.Preferences getEntityNode(Class<?> type)
	{
		return preferences.node(type.getSimpleName().toLowerCase());
	}
	
}
