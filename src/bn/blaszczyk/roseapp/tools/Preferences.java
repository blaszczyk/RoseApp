package bn.blaszczyk.roseapp.tools;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;

import bn.blaszczyk.rose.model.Entity;
import bn.blaszczyk.rose.model.Readable;

public class Preferences {
	
	private final static DecimalFormat DECIMAL_FORMAT =  (DecimalFormat) NumberFormat.getNumberInstance();
	static {
		DECIMAL_FORMAT.setParseBigDecimal(true);
	}

	public final static String COLUMN_WIDTH = "columnwidth";
	public final static String COLUMN_CONTENT = "columncontent";
	public final static String COLUMN_COUNT = "columncount";
	
	public final static String START_BUTTON = "startbutton";
	public final static String START_BUTTON_COUNT = "startbuttoncount";

	public final static String DB_HOST = "dbhost";
	public final static String DB_PORT = "dbport";
	public final static String DB_NAME = "dbname";
	public final static String DB_USER = "dbuser";
	public final static String DB_PASSWORD = "dbpassword";

	public static final String FIELD_TYPE = "fieldtype";
	
	public static final String BASE_DIRECTORY = "basefolder";
	public static final String LOG_LEVEL = "loglevel";

	private static java.util.prefs.Preferences preferences;
	
	private Preferences()
	{
	}

	public static void setMainClass(Class<?> type)
	{
		preferences = java.util.prefs.Preferences.userNodeForPackage(type);
		configureLogger();
	}

	public static String getStringValue(String key, String def)
	{
		return preferences.get(key, def);
	}
	
	public static void putStringValue( String key, String value)
	{
		preferences.put(key, value);
	}

	public static boolean getBooleanValue(String key, boolean def)
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

	public static BigDecimal getBigDecimalValue(String key, BigDecimal def)
	{
		String stringValue = preferences.get(key, DECIMAL_FORMAT.format(def));
		try
		{
			return (BigDecimal) DECIMAL_FORMAT.parse( stringValue );
		}
		catch (ParseException e)
		{
			System.err.println("Unable to parse BigDecimal from \"" + stringValue + "\"\n"
					+ "Using default value " + def + " for key " + key );
			return def;
		}
	}
	
	public static void putBigDecimalValue( String key, BigDecimal value)
	{
		preferences.put(key, DECIMAL_FORMAT.format(value));
	}
	
	public static String getStringEntityValue(Class<?> type, String key, String def)
	{
		return getEntityNode(type).get(key, def);
	}

	public static String getStringEntityValue(Readable entity, String key, String def)
	{
		return getStringEntityValue(entity.getClass(), key, def);
	}
	
	public static String getStringEntityValue(Entity entity, String key, String def)
	{
		return getStringEntityValue(TypeManager.getClass(entity), key, def);
	}
	
	public static void putStringEntityValue(Class<?> type, String key, String value)
	{
		getEntityNode(type).put(key, value);
	}
	
	public static void putStringEntityValue(Readable entity, String key, String value)
	{
		putStringEntityValue(entity.getClass(), key, value);
	}
	
	public static void putStringEntityValue(Entity entity, String key, String value)
	{
		putStringEntityValue(TypeManager.getClass(entity), key, value);
	}
	
	public static boolean getBooleanEntityValue(Class<?> type, String key, boolean def)
	{
		return getEntityNode(type).getBoolean(key, def);
	}
	
	public static boolean getBooleanEntityValue(Readable entity, String key, boolean def)
	{
		return getBooleanEntityValue(entity.getClass(), key, def);
	}
	
	public static boolean getBooleanEntityValue(Entity entity, String key, boolean def)
	{
		return getBooleanEntityValue(TypeManager.getClass(entity), key, def);
	}
	
	public static void putBooleanEntityValue(Class<?> type, String key, boolean value)
	{
		getEntityNode(type).putBoolean(key, value);
	}
	
	public static void putBooleanEntityValue(Readable entity, String key, boolean value)
	{
		putBooleanEntityValue(entity.getClass(), key, value);
	}
	
	public static void putBooleanEntityValue(Entity entity, String key, boolean value)
	{
		putBooleanEntityValue(TypeManager.getClass(entity), key, value);
	}
	
	public static int getIntegerEntityValue(Class<?> type, String key, int def)
	{
		return getEntityNode(type).getInt(key, def);
	}
	
	public static int getIntegerEntityValue(Readable entity, String key, int def)
	{
		return getIntegerEntityValue(entity.getClass(), key, def);
	}
	
	public static int getIntegerEntityValue(Entity entity, String key, int def)
	{
		return getIntegerEntityValue(TypeManager.getClass(entity), key, def);
	}
	
	public static void putIntegerEntityValue(Class<?> type, String key, int value)
	{
		getEntityNode(type).putInt(key, value);
	}
	
	public static void putIntegerEntityValue(Readable entity, String key, int value)
	{
		putIntegerEntityValue(entity.getClass(), key, value);
	}
	
	public static void putIntegerEntityValue(Entity entity, String key, int value)
	{
		putIntegerEntityValue(TypeManager.getClass(entity), key, value);
	}
	

	public static void configureLogger()
	{
		String baseDirectory = getStringValue(BASE_DIRECTORY, "C:");
		String loglevelName = getStringValue(LOG_LEVEL, "INFO");
		Level loglevel = Level.toLevel(loglevelName);
		Logger logger = Logger.getRootLogger();
		logger.setLevel(loglevel);
		Appender appender = logger.getAppender("rolling-file");
		if(appender instanceof RollingFileAppender)
		{
			RollingFileAppender rfAppender = (RollingFileAppender) appender;
			String fullLoggerPath = baseDirectory + "/" + rfAppender.getFile();
			File file = new File(fullLoggerPath);
			if(!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			rfAppender.setFile(fullLoggerPath);
			Logger.getLogger(Preferences.class).log(Level.INFO, "log file: " + fullLoggerPath);
		}
	}
	
	private static java.util.prefs.Preferences getEntityNode(Class<?> type)
	{
		return preferences.node(TypeManager.convertType(type).getSimpleName().toLowerCase());
	}
	
}
