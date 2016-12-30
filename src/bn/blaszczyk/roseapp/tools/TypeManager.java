package bn.blaszczyk.roseapp.tools;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import bn.blaszczyk.rose.model.*;
import bn.blaszczyk.rose.parser.ModelProvidingNonCreatingRoseParser;

public class TypeManager {
	
	private final static Map<String, Class<?>> classes = new HashMap<>();
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
		for(Entity e : parser.getEntities())
		{
			entites.put(e.getSimpleClassName(), e);
			try
			{
				classes.put(e.getSimpleClassName().toLowerCase(), Class.forName(e.getClassName()));
			}
			catch (ClassNotFoundException e1)
			{
				System.err.println("Unable to load Class " + e.getClassName());
				e1.printStackTrace();
			}
		}
		for(EnumType e : parser.getEnums())
			enums.put(e.getSimpleClassName(), e);
		try
		{
			mainClass = Class.forName(parser.getMainClassAsString());
			Preferences.setMainClass( mainClass );
		}
		catch (ClassNotFoundException e1)
		{
			System.err.println("Unable to load Class " + parser.getMainClassAsString());
			e1.printStackTrace();
		}
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
	
	public static Class<?> getClass( Entity entity )
	{
		return classes.get(entity.getSimpleClassName().toLowerCase());
	}
	
	public static Collection<Class<?>> getEntityClasses()
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
		System.err.println("Unknown Type: " + type);
		return type;
	}
}
