package bn.blaszczyk.roseapp.tools;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import bn.blaszczyk.rose.model.Entity;
import bn.blaszczyk.rose.model.EnumType;
import bn.blaszczyk.rose.model.Identifyable;
import bn.blaszczyk.rose.parser.ModelProvidingNonCreatingRoseParser;

public class TypeManager {
	
	private final static Map<String, Class<?>> CLASSES = new HashMap<>();
	private final static Map<String,Entity> ENTITIES = new HashMap<>();
	private final static Map<String,EnumType> ENUMS = new HashMap<>();
	
	private TypeManager()
	{
	}
	
	public static void parseRoseFile(InputStream stream)
	{
		ModelProvidingNonCreatingRoseParser parser = new ModelProvidingNonCreatingRoseParser(stream);
		parser.parse();
		for(Entity e : parser.getEntities())
		{
			ENTITIES.put(e.getSimpleClassName(), e);
			try
			{
				CLASSES.put(e.getSimpleClassName(), Class.forName(e.getClassName()));
			}
			catch (ClassNotFoundException e1)
			{
				System.err.println("Unable to load Class " + e.getClassName());
				e1.printStackTrace();
			}
		}
		for(EnumType e : parser.getEnums())
			ENUMS.put(e.getSimpleClassName(), e);
	}
	
//	public static void putClasses(Class<?>... types)
//	{
//		for(Class<?> type : types)
//			CLASSES.put(type.getSimpleName(), type);
//	}
	
	public static Entity getEntity(Class<?> type)
	{
		return ENTITIES.get(type.getSimpleName());
	}
	
	public static Entity getEntity( Identifyable entity )
	{
		if(entity == null)
			return null;
		return getEntity( entity.getClass() );
	}
	
	public static EnumType getEnum( Class<?> type )
	{
		return ENUMS.get(type.getSimpleName());
	}
	
	public static EnumType getEnum( Enum<?> enumOption )
	{
		if(enumOption == null)
			return null;
		return getEnum(enumOption.getClass());
	}
	
	public static Class<?> getClass( Entity entity )
	{
		return CLASSES.get(entity.getSimpleClassName());
	}
	
}
