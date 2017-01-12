package bn.blaszczyk.roseapp.view;

public class RoseEvent {
	
	private final Object source;
	private final boolean noRefresh;
	
	public RoseEvent(Object source, boolean noRefresh)
	{
		this.source = source;
		this.noRefresh = noRefresh;
	}
	
	public RoseEvent(Object source)
	{
		this(source, false);
	}

	public Object getSource()
	{
		return source;
	}

	public boolean isNoRefresh()
	{
		return noRefresh;
	}
	
}
