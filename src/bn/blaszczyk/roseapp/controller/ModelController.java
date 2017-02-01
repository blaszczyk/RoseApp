package bn.blaszczyk.roseapp.controller;

import java.util.List;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;

public interface ModelController {

	public void setField( Writable entity, int index, Object value);
	public void setEntityField( Writable entity, int index, Writable value);
	public void addEntityField( Writable entity, int index, Writable value);
	public void removeEntityField( Writable entity, int index, Writable value);
	
	public <T extends Readable> T createNew( Class<T> type );
	public void delete( Writable entity );
	public Writable createCopy( Writable entity );
	public void register( Writable entity );
	
	public List<? extends Readable> getAllEntites(Class<?> type);
	public void loadEntities();
	
	public void commit();
	public void closeSession();
}
