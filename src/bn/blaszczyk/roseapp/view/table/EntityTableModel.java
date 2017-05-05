package bn.blaszczyk.roseapp.view.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import bn.blaszczyk.rose.model.Entity;
import bn.blaszczyk.rose.model.Readable;

import static bn.blaszczyk.rosecommon.tools.Preferences.*;
import static bn.blaszczyk.roseapp.tools.AppPreference.*;

public class EntityTableModel implements TableModel {
	
	private final Collection<? extends Readable> source;
	private List<? extends Readable> entities;
	private final Entity entity;
	private final int buttonCount;
	private final int columnCount;
	private final List<ColumnContent> colContents = new ArrayList<>();

	public EntityTableModel(Collection<? extends Readable> entities, int buttonCount, Entity entity)
	{
		this.source = entities;
		this.entity = entity;
		this.columnCount = getIntegerEntityValue(entity, COLUMN_COUNT);
		for( int i = 0; i < buttonCount; i++)
			colContents.add(new ColumnContent());
		for( int i = 0; i < columnCount; i++)
			colContents.add( new ColumnContent(entity, getStringEntityValue(entity, COLUMN_CONTENT.append(i)) ) );
		this.buttonCount = buttonCount > 0 ? buttonCount : 0;
		resetSource();
	}

	public ColumnContent getColumnContent(int column)
	{
		return colContents.get(column);
	}

	public Readable getEntity(int row)
	{
		return entities.get(row);
	}
	
	public void setButtonIcon(int columnIndex, Icon icon)
	{
		colContents.get(columnIndex).setIcon(icon);
	}

	public int getButtonCount()
	{
		return buttonCount;
	}
	
	public void resetSource()
	{
		if(source instanceof List)
			entities = (List<? extends Readable>) source;
		else
			entities = new ArrayList<>(source);
	}
	
	@Override
	public int getRowCount()
	{
		return entities.size();
	}
	
	@Override
	public int getColumnCount()
	{
		return colContents.size();
	}
	
	@Override
	public String getColumnName(int columnIndex)
	{
		return colContents.get(columnIndex).getName(entity);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return colContents.get(columnIndex).getClass(entity);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return colContents.get(columnIndex).getContent(getEntity(rowIndex));
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
	}
	
	@Override
	public void addTableModelListener(TableModelListener l)
	{
	}
	
	@Override
	public void removeTableModelListener(TableModelListener l)
	{
	}
	
}
