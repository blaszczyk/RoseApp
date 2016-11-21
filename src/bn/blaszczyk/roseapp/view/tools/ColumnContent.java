package bn.blaszczyk.roseapp.view.tools;

import java.text.ParseException;
import java.util.Set;

import javax.swing.Icon;

import bn.blaszczyk.rose.model.Readable;

public class ColumnContent {
	
	public static final String DELIMITER = "\\,";
	
	private Icon icon = null;
	private SubEntityPath subEntityPath = null;
	
	public ColumnContent()
	{
	}
	
	public ColumnContent(String pathAsString) throws ParseException
	{
		String[] split = pathAsString.split(DELIMITER);
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
	
	public String getName( Readable entity)
	{
		if(subEntityPath == null)
			return "";
		return getName( entity, subEntityPath);
	}

	public Class<?> getClass( Readable entity )
	{
		if(icon != null)
			return Icon.class;
		if(subEntityPath != null)
			return getClass( entity, subEntityPath );
		return Object.class;	
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

	private String getName(Readable entity, SubEntityPath subEntityPath)
	{
		int retIndex = subEntityPath.getReturnIndex();
		SubEntityPath subPath = subEntityPath.getSubPath();
		if(subPath == null && !subEntityPath.isReturnEntity())
			return entity.getFieldName(retIndex);
		if(entity.getRelationType(retIndex).isSecondMany() || subPath == null)
			return entity.getEntityName(retIndex);
		return getName((Readable) entity.getEntityValue(retIndex), subPath);
	}

	private Class<?> getClass(Readable entity, SubEntityPath subEntityPath)
	{
		int retIndex = subEntityPath.getReturnIndex();
		SubEntityPath subPath = subEntityPath.getSubPath();
		if(subPath == null && !subEntityPath.isReturnEntity())
			return entity.getFieldValue(retIndex).getClass();
		if(entity.getRelationType(retIndex).isSecondMany() )
			return Integer.class;
		if(subPath == null)
			return entity.getEntityClass(retIndex);
		return getClass((Readable) entity.getEntityValue(retIndex), subPath);
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
