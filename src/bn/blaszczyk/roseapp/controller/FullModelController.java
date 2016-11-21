package bn.blaszczyk.roseapp.controller;

import java.util.List;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;

public interface FullModelController {

	public void setField( Writable entity, int index, Object value);
	public void setEntityField( Writable entity, int index, Writable value);
	public void addEntityField( Writable entity, int index, Writable value);
	public void removeEntityField( Writable entity, int index, Writable value);
	
	public Writable createNew( Class<Writable> type );
	public void delete( Writable entity );
	public Writable createCopy( Writable e );
	
	public List<Readable> getAllEntites(Class<?> type);
	public void loadEntities(Class<?>[] types);
	
	public void commit();
}
