package bn.blaszczyk.roseapp.view;

import bn.blaszczyk.rose.model.Writable;

import java.util.Collection;

import bn.blaszczyk.rose.model.Readable;

public interface Behaviour {
	public void setMessenger(Messenger messenger);
	
	public void checkEntity(Writable entity);
	public Readable replacePanel(Readable entity);
	public Collection<Writable> cascade(Writable entity);
}
