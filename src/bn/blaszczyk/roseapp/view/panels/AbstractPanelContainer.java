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
	
	public Iterable<EntityPanel> getPanels()
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
	
	public int getPanelCount()
	{
		return 0;
	}
	
	public EntityPanel getPanel(int index)
	{
		return null;
	}
	
	protected void registerRoseListener()
	{
		for(EntityPanel panel : getPanels())
			panel.addRoseListener(changeListener);
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
		super.refresh();
	}

	@Override
	public void save(ModelController controller)
	{
		super.save(controller);
		for(EntityPanel panel : getPanels())
			panel.save(controller);
	}
	
}
