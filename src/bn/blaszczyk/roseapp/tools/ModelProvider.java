package bn.blaszczyk.roseapp.tools;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bn.blaszczyk.rose.model.Entity;
import bn.blaszczyk.rose.model.EnumType;
import bn.blaszczyk.rose.model.Identifyable;
import bn.blaszczyk.rose.parser.ModelProvidingNonCreatingRoseParser;

public class ModelProvider {
	
	private static List<Entity> entites;
	private static List<EnumType> enums;
	
	private final static Map<String, Class<?>> CLASSES = new HashMap<>();
	
	private ModelProvider()
	{
	}
	
	public static void parseRoseFile(InputStream stream)
	{
		ModelProvidingNonCreatingRoseParser parser = new ModelProvidingNonCreatingRoseParser(stream);
		parser.parse();
		entites = parser.getEntities();
		enums = parser.getEnums();
	}
	
	public static void putClasses(Class<?>... types)
	{
		for(Class<?> type : types)
			CLASSES.put(type.getSimpleName(), type);
	}
	
	public static Entity getEntity(Class<?> type)
	{
		for(Entity entity : entites)
			if(entity.getClassName().equals(type.getName()))
				return entity;
		return null;
	}
	
	public static Entity getEntity( Identifyable entity )
	{
		if(entity == null)
			return null;
		return getEntity( entity.getClass() );
	}
	
	public static EnumType getEnum( Class<?> type )
	{
		for(EnumType enumType : enums)
			if(enumType.getClassName().equals(type.getName()))
				return enumType;
		return null;
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
