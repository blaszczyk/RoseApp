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
	
	public Iterable<RosePanel> getPanels()
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
	
	protected void registerRoseListener()
	{
		for(RosePanel panel : getPanels())
			panel.addRoseListener(changeListener);
	}

//	@Override
//	public boolean hasChanged()
//	{
//		for(RosePanel panel : getPanels())
//			if(panel.hasChanged())
//				return true;
//		return false;
//	}

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
