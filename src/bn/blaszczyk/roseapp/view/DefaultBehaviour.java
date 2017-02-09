package bn.blaszczyk.roseapp.view;

import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.rose.model.Readable;

public class DefaultBehaviour implements Behaviour {
	
	protected Messenger messenger;
	
	public DefaultBehaviour()
	{
	}

	@Override
	public boolean checkEntity(Writable entity)
	{
		return true;
	}

	@Override
	public Readable replacePanel(Readable entity)
	{
		return entity;
	}

	@Override
	public void setMessenger(Messenger messenger)
	{
		this.messenger = messenger;
	}
	
}
