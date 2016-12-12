package bn.blaszczyk.roseapp.view.panels;

import java.awt.LayoutManager;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import bn.blaszczyk.roseapp.controller.ModelController;

@SuppressWarnings("serial")
public abstract class AbstractEntityPanel extends JPanel implements EntityPanel {
	
	private boolean changed = false;
	
	protected ActionListener changeListener = e -> {changed = true;};
	
	public AbstractEntityPanel()
	{
	}
	
	public AbstractEntityPanel( LayoutManager layout )
	{
		super.setLayout(layout);
	}
	
	protected void setChanged()
	{
		changed = true;
	}
	
	@Override
	public int getFixWidth()
	{
		return 0;
	}
	
	@Override
	public int getFixHeight()
	{
		return 0;
	}
	
	@Override
	public JPanel getPanel()
	{
		return this;
	}
	
	@Override
	public boolean hasChanged()
	{
		return changed;
	}
	
	@Override
	public void refresh()
	{
		revalidate();
		repaint();
	}
	
	@Override
	public Object getShownObject()
	{
		return this;
	}

	@Override
	public void save(ModelController controller)
	{
		changed = false;
	}
	
}
