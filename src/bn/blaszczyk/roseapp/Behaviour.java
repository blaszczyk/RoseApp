package bn.blaszczyk.roseapp;

import bn.blaszczyk.rose.model.Writable;

public interface Behaviour {
	public boolean checkEntity(Writable entity);
	public Readable replacePanel(Readable entity);
}
