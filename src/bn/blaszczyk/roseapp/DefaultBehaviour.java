package bn.blaszczyk.roseapp;

import bn.blaszczyk.rose.model.Writable;

public class DefaultBehaviour implements Behaviour {
	
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
	
}
