package bn.blaszczyk.roseapp.view;

import org.apache.logging.log4j.*;

public class RoseEvent {
	
	private static final Logger LOGGER = LogManager.getLogger(RoseEvent.class);
	
	private final Object source;
	private final boolean noRefresh;

	public RoseEvent(Object source, boolean noRefresh, Object origin)
	{
		this.source = source;
		this.noRefresh = noRefresh;
		LOGGER.debug("source = " + source);
		if(origin != null)
			LOGGER.debug("origin = " + origin);
	}

	public RoseEvent(Object source, boolean noRefresh)
	{
		this(source, noRefresh, null);
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
