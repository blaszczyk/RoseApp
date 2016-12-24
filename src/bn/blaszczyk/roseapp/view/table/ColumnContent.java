package bn.blaszczyk.roseapp.view.table;

import java.util.Set;

import javax.swing.Icon;

import bn.blaszczyk.rose.model.Entity;
import bn.blaszczyk.rose.model.EntityField;
import bn.blaszczyk.rose.model.EnumField;
import bn.blaszczyk.rose.model.Field;
import bn.blaszczyk.rose.model.PrimitiveField;
import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.tools.TypeManager;

public class ColumnContent {
	
	public static final String DELIMITER = "\\,";
	
	private Icon icon = null;
	private SubEntityPath subEntityPath = null;
	
	public ColumnContent()
	{
	}
	
	public ColumnContent(Entity entity, String pathAsString)
	{
		String[] split = tagEntityName(entity, pathAsString).split(DELIMITER);
		String leafAsString = split[split.length - 1];
		subEntityPath = new SubEntityPath( leafAsString.substring(0, 1).equalsIgnoreCase("e") , Integer.parseInt(leafAsString.substring(1)));
		for(int i = split.length - 2; i >= 0; i--)
			subEntityPath = new SubEntityPath(subEntityPath, Integer.parseInt(split[i].substring(1)));
	}
	
	public void setIcon( Icon icon)
	{
		this.icon = icon;
		this.subEntityPath = null;
	}
	
	public Object getContent( Readable entity )
	{
		if(icon != null)
			return icon;
		if(subEntityPath != null)
			return getContent( entity, subEntityPath );
		return entity;	
	}
	
	public String getName( Entity entity )
	{
		if(subEntityPath == null)
			return "";
		return getName( entity, subEntityPath);
	}

	public Class<?> getClass( Entity entity )
	{
		if(subEntityPath != null)
			return getClass( entity, subEntityPath );
		return Icon.class;	
	}
	
	private Object getContent(Readable entity, SubEntityPath subEntityPath)
	{
		if(entity == null)
			return null;
		int retIndex = subEntityPath.getReturnIndex();
		SubEntityPath subPath = subEntityPath.getSubPath();
		if(subPath == null && !subEntityPath.isReturnEntity())
			return entity.getFieldValue(retIndex);
		if(entity.getRelationType(retIndex).isSecondMany() )
			return ((Set<?>)entity.getEntityValue(retIndex)).size();
		if(subPath == null)
			return entity.getEntityValue(retIndex);
		return getContent((Readable) entity.getEntityValue(retIndex), subPath);
	}

	private String getName(Entity entity, SubEntityPath subEntityPath)
	{
		int retIndex = subEntityPath.getReturnIndex();
		SubEntityPath subPath = subEntityPath.getSubPath();
		if(subPath == null && !subEntityPath.isReturnEntity())
			return entity.getFields().get(retIndex).getCapitalName(); 
		if(entity.getEntityFields().get(retIndex).getType().isSecondMany() || subPath == null)
			return entity.getEntityFields().get(retIndex).getCapitalName();
		return getName( entity.getEntityFields().get(retIndex).getEntity(), subPath);
	}

	private Class<?> getClass(Entity entity, SubEntityPath subEntityPath)
	{
		int retIndex = subEntityPath.getReturnIndex();
		SubEntityPath subPath = subEntityPath.getSubPath();
		if(subPath == null && !subEntityPath.isReturnEntity())
		{
			Field field = entity.getFields().get(retIndex);
			if(field instanceof PrimitiveField)
				return ((PrimitiveField) field).getType().getJavaType();
			else if( field instanceof EnumField)
				return Enum.class;
		}
		EntityField subEntity = entity.getEntityFields().get(retIndex);
		if(subEntity.getType().isSecondMany() )
			return Integer.class;
		if(subPath == null)
			return TypeManager.getClass( subEntity.getEntity() );
		return getClass( subEntity.getEntity(), subPath);
	}
	

	private static String tagEntityName(Entity entity, final String ccString)
	{
		String[] split = ccString.split("\\.|\\,", 2 );
		try
		{
			for(int i = 0; i < entity.getFields().size(); i++)
				if(split[0].trim().equalsIgnoreCase( entity.getFields().get(i).getName() ))
					return new StringBuilder().append("f").append(i).toString();
			for(int i = 0; i < entity.getEntityFields().size(); i++)
				if(split[0].trim().equalsIgnoreCase( entity.getEntityFields().get(i).getName() ))
				{
					StringBuilder builder = new StringBuilder();
					builder.append("e").append(i);
					if(split.length == 2)
						builder.append(",").append( tagEntityName(entity.getEntityFields().get(i).getEntity(), split[1]) );
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

	private static class SubEntityPath
	{
		private SubEntityPath subPath = null;
		private boolean returnEntity = true;
		private int returnIndex;
		
		public SubEntityPath(boolean returnsPrimitive, int returnIndex)
		{
			this.returnEntity = returnsPrimitive;
			this.returnIndex = returnIndex;
		}

		public SubEntityPath(SubEntityPath subPath, int returnIndex)
		{
			this.subPath = subPath;
			this.returnIndex = returnIndex;
		}

		public SubEntityPath getSubPath()
		{
			return subPath;
		}

		public boolean isReturnEntity()
		{
			return returnEntity;
		}

		public int getReturnIndex()
		{
			return returnIndex;
		}			
	}
}