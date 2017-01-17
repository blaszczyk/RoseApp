package bn.blaszczyk.roseapp.view.panels;

import java.awt.LayoutManager;
import java.util.Iterator;

import bn.blaszczyk.roseapp.controller.ModelController;

@SuppressWarnings("serial")
public abstract class AbstractPanelContainer extends AbstractRosePanel {
	
	public AbstractPanelContainer()
	{
	}
	
	public AbstractPanelContainer(LayoutManager layout)
	{
		super(layout);
	}
	
	public Iterable<? extends RosePanel> getPanels()
	{
		Iterable<RosePanel> iterable = () -> {
			return new Iterator<RosePanel>(){
				
				private int index = 0;
				
				@Override
				public boolean hasNext()
				{
					return index < getPanelCount();
				}

				@Override
				public RosePanel next()
				{
					return getPanel(index++);
				}
				
			};
		};
		return iterable;
	}
	
	public int getPanelCount()
	{
		return 0;
	}
	
	public RosePanel getPanel(int index)
	{
		return null;
	}
	
	public void registerRoseListener()
	{
		for(RosePanel panel : getPanels())
			panel.addRoseListener(this);
	}

	@Override
	public void refresh()
	{
		for(RosePanel panel : getPanels())
			panel.refresh();
		super.refresh();
	}

	@Override
	public void save(ModelController controller)
	{
		super.save(controller);
		for(RosePanel panel : getPanels())
			panel.save(controller);
	}
	
}
