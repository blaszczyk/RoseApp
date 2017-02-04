package bn.blaszczyk.roseapp.controller;

import java.util.List;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;

public interface ModelController {
	
	public <T extends Readable> T createNew( Class<T> type );
	public void delete( Writable entity );
	public Writable createCopy( Writable entity );
	public void update( Writable... entities );
	
	public List<? extends Readable> getAllEntites(Class<?> type);
	public void loadEntities();
	
	public void commit();
	public void closeSession();
}
