package bn.blaszczyk.roseapp;

import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.view.Messenger;
import bn.blaszczyk.roseapp.view.table.ColumnContent;

import java.util.Collection;
import java.util.Comparator;

import bn.blaszczyk.rose.model.EntityModel;
import bn.blaszczyk.rose.model.Readable;

public interface Behaviour {
	public void setMessenger(Messenger messenger);
	
	public void checkEntity(Writable entity);
	public Readable replacePanel(Readable entity);
	public Collection<Writable> cascade(Writable entity);
	public boolean creatable(Class<? extends Writable> type);
	public Comparator<?> comparator(EntityModel entityModel, ColumnContent content);

	public String fieldType(Readable entity, String fieldName);
}
