package bn.blaszczyk.roseapp.view.panels;

import java.awt.LayoutManager;
import java.util.Iterator;

@SuppressWarnings("serial")
public abstract class AbstractPanelContainer<T extends RosePanel> extends AbstractRosePanel implements Iterable<T> {
	
	public AbstractPanelContainer()
	{
	}
	
	public AbstractPanelContainer(LayoutManager layout)
	{
		super(layout);
	}
	
	public int getPanelCount()
	{
		return 0;
	}
	
	public T getPanel(int index)
	{
		return null;
	}
	
	public void registerRoseListener()
	{
		for(RosePanel panel : this)
			panel.addRoseListener(this);
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return new Iterator<T>(){
			private int index = 0;
			@Override
			public boolean hasNext()
			{
				return index < getPanelCount();
			}
			@Override
			public T next()
			{
				return getPanel(index++);
			}
		};
	}

	@Override
	public void refresh()
	{
		for(RosePanel panel : this)
			panel.refresh();
		super.refresh();
	}

	@Override
	public void save()
	{
		super.save();
		for(RosePanel panel : this)
			panel.save();
	}
	
}
