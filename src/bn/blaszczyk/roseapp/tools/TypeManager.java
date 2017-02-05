
package bn.blaszczyk.roseapp.tools;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import bn.blaszczyk.rose.model.*;
import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.parser.ModelProvidingNonCreatingRoseParser;

public class TypeManager {
	
	private static final Logger LOGGER = Logger.getLogger(TypeManager.class);
	
	private final static Map<String, Class<? extends Readable>> classes = new HashMap<>();
	private final static Map<String,Entity> entites = new HashMap<>();
	private final static Map<String,EnumType> enums = new HashMap<>();
	
	private static Class<?> mainClass;
	
	private TypeManager()
	{
	}
	
	public static void parseRoseFile(InputStream stream)
	{
		
		ModelProvidingNonCreatingRoseParser parser = new ModelProvidingNonCreatingRoseParser(stream);
		parser.parse();
		try
		{
			mainClass = Class.forName(parser.getMainClassAsString());
			Preferences.setMainClass( mainClass );
			LOGGER.info( "load main class " + mainClass.getName());
		}
		catch (ClassNotFoundException e1)
		{
			LOGGER.error("unable to load main class " + parser.getMainClassAsString(),e1);
			e1.printStackTrace();
		}
		for(Entity e : parser.getEntities())
		{
			entites.put(e.getSimpleClassName(), e);
			try
			{
				classes.put(e.getSimpleClassName().toLowerCase(), Class.forName(e.getClassName()).asSubclass(Readable.class));
				LOGGER.info( "load entity class " + e.getClassName());
			}
			catch (ClassNotFoundException e1)
			{
				LOGGER.error("unable to load entity class " + e.getClassName(), e1);
				e1.printStackTrace();
			}
		}
		for(EnumType e : parser.getEnums())
			enums.put(e.getSimpleClassName(), e);
	}
	
	public static Entity getEntity(Class<?> type)
	{
		return entites.get(convertType(type).getSimpleName());
	}
	
	public static Entity getEntity( Identifyable entity )
	{
		if(entity == null)
			return null;
		return getEntity( entity.getClass() );
	}
	
	public static EnumType getEnum( Class<?> type )
	{
		return enums.get(convertType(type).getSimpleName());
	}
	
	public static EnumType getEnum( Enum<?> enumOption )
	{
		if(enumOption == null)
			return null;
		return getEnum(enumOption.getClass());
	}
	
	public static Class<? extends Readable> getClass( Entity entity )
	{
		return classes.get(entity.getSimpleClassName().toLowerCase());
	}
	
	public static Collection<Class<? extends Readable>> getEntityClasses()
	{
		return classes.values();
	}
	
	public static Class<?> getMainClass()
	{
		return mainClass;
	}

	public static Collection<Entity> getEntites()
	{
		return entites.values();
	}

	public static int getEntityCount()
	{
		return entites.size();
	}

	public static Class<?> getClass(String entityName)
	{
		return classes.get(entityName.toLowerCase());
	}

	public static Class<?> convertType(Class<?> type)
	{
		for(Class<?> t : classes.values())
			if(t.isAssignableFrom(type))
				return t;
		LOGGER.error("unknown type: " + type.getName());
		return type;
	}
}
