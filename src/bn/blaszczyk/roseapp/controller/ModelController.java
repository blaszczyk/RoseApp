package bn.blaszczyk.roseapp.controller;

import java.util.List;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.RoseException;
import bn.blaszczyk.roseapp.view.Messenger;

public interface ModelController {

	public void setMessenger(Messenger messenger);
	
	public <T extends Readable> T createNew( Class<T> type ) throws RoseException;
	public Writable createCopy( Writable entity ) throws RoseException;
	public void delete( Writable entity ) throws RoseException;
	public void update( Writable... entities );
	
	public List<? extends Readable> getEntites(Class<?> type);

	public void synchronize();
	public void commit() throws RoseException;
	public void closeSession();
	public void rollback();

}
