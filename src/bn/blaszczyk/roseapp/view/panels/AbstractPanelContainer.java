package bn.blaszczyk.roseapp.view.panels;

import java.awt.LayoutManager;
import java.util.Iterator;

import bn.blaszczyk.roseapp.controller.ModelController;

@SuppressWarnings("serial")
public abstract class AbstractPanelContainer extends AbstractEntityPanel {
	
	public AbstractPanelContainer()
	{
	}
	
	public AbstractPanelContainer(LayoutManager layout)
	{
		super(layout);
	}
	
	protected Iterable<EntityPanel> getPanels()
	{
		Iterable<EntityPanel> iterable = () -> {
			return new Iterator<EntityPanel>(){
				private int index = 0;				
				@Override
				public boolean hasNext()
				{
					return index < getPanelCount();
				}

				@Override
				public EntityPanel next()
				{
					return getPanel(index++);
				}
				
			};
		};
		return iterable;
	}
	
	protected int getPanelCount()
	{
		return 0;
	}
	
	protected EntityPanel getPanel(int index)
	{
		return null;
	}

	@Override
	public boolean hasChanged()
	{
		for(EntityPanel panel : getPanels())
			if(panel.hasChanged())
				return true;
		return false;
	}

	@Override
	public void refresh()
	{
		for(EntityPanel panel : getPanels())
			panel.refresh();
	}

	@Override
	public void save(ModelController controller)
	{
		for(EntityPanel panel : getPanels())
			panel.save(controller);
	}	
	
}
