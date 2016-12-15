package bn.blaszczyk.roseapp.view.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JScrollPane;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.factories.IconFactory;

public class EntityTableBuilder
{
	private final List<Icon> icons = new ArrayList<>();
	private final List<EntityTable.EntityAction> actions = new ArrayList<>();
	private List<? extends Readable> entities; 
	private int height;
	private int width;
	private Class<?> type;
	
	public EntityTableBuilder type(Class<?> type)
	{
		this.type = type;
		return this;
	}
	
	public EntityTableBuilder heigth(int heigth)
	{
		this.height = heigth;
		return this;
	}
	
	public EntityTableBuilder width(int width)
	{
		this.width = width;
		return this;
	}
	
	public EntityTableBuilder entities( Collection<? extends Readable> entities )
	{
		if(entities instanceof List)
			this.entities = (List<? extends Readable>) entities;
		else
		{
			List<Readable> tEntities = new ArrayList<>();
			tEntities.addAll(entities);
			this.entities = tEntities;
		}
		return this;
	}
	
	public EntityTableBuilder addButtonColumn(String iconFile, EntityTable.EntityAction action)
	{
		icons.add( IconFactory.create(iconFile) );
		actions.add(action);
		return this;
	}
	
	public EntityTable build()
	{
		EntityTableModel tableModel= new EntityTableModel(entities, actions.size(), TypeManager.getEntity(type));
		EntityTable table = new EntityTable(tableModel, width, height);
		for(int i = 0; i < actions.size(); i++)
			table.setButtonColumn(i, icons.get(i), actions.get(i));
		return table;		
	}
	
	public JScrollPane buildInScrollPane()
	{
		return new JScrollPane(build());
	}

}
