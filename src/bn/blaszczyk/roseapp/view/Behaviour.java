package bn.blaszczyk.roseapp.view;

import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.rose.model.Readable;

public interface Behaviour {
	public void setMessenger(Messenger messenger);
	
	public boolean checkEntity(Writable entity);
	public Readable replacePanel(Readable entity);
}
