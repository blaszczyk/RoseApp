package bn.blaszczyk.roseapp.controller;

import java.util.List;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;

public interface ModelController {

	public void setField( Writable entity, int index, Object value);
	public void setEntityField( Writable entity, int index, Writable value);
	public void addEntityField( Writable entity, int index, Writable value);
	public void removeEntityField( Writable entity, int index, Writable value);
	
	public Writable createNew( Class<?> class1 );
	public void delete( Writable entity );
	public Writable createCopy( Writable e );
	
	public List<Readable> getAllEntites(Class<?> type);
	public void loadEntities();
	
	public void commit();
}
