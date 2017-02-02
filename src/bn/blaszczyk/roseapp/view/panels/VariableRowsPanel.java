package bn.blaszczyk.roseapp.view.panels;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;

import bn.blaszczyk.roseapp.view.factories.ButtonFactory;

@SuppressWarnings("serial")
public class VariableRowsPanel extends AbstractPanelContainer<RosePanel> {

	public interface EntityPanelCreator
	{
		public RosePanel newInstance();
	}
	
	public interface Indexable extends RosePanel
	{
		public void setIndex(int index);
	}
	
	private final List<RosePanel> panels = new ArrayList<>();
	private final EntityPanelCreator creator;
	
	private int width = 0;
	private int height = 0;
	
	public VariableRowsPanel( Iterable<? extends RosePanel> panels, EntityPanelCreator creator )
	{
		super(null);
		this.creator=creator;
		int count = 0;
		for(RosePanel panel : panels)
		{
			if(panel instanceof Indexable)
				((Indexable)panel).setIndex(count++);
			this.panels.add(panel);
		}
		setBackground(FULL_PNL_BACKGROUND);
		registerRoseListener();	
		realign();
	}
	
	public void addRow(RosePanel panel)
	{
		panel.addRoseListener(this);
		panels.add(panel);
	}
	
	private void realign()
	{
		removeAll();
		height = V_SPACING;
		width = 0;
		for(int i = 0; i < panels.size(); i++)
		{
			final int ii = i;
			JButton button = ButtonFactory.createIconButton("delete.png", e -> removeRow(ii,e));
			button.setBounds(H_SPACING , height, TBL_BTN_WIDTH, LBL_HEIGHT);
			add(button);
			
			RosePanel panel = panels.get(i);
			panel.getPanel().setBounds(2 * H_SPACING + TBL_BTN_WIDTH, height, panel.getFixWidth(), panel.getFixHeight());
			if(panel instanceof Indexable)
				((Indexable)panel).setIndex(i);
			add(panel.getPanel());
			
			height += panel.getFixHeight() + V_SPACING;
			width = Math.max(width, panel.getFixWidth());
		}
		JButton button = ButtonFactory.createIconButton("add.png", e -> addNewRow(e));
		button.setBounds(H_SPACING , height, TBL_BTN_WIDTH, LBL_HEIGHT);
		add(button);
		width += 3 * H_SPACING + TBL_BTN_WIDTH;
		height += LBL_HEIGHT + V_SPACING;
		refresh();
	}

	private void addNewRow(ActionEvent e)
	{
		addRow(creator.newInstance());
		realign();
		notify(false,e);
	}

	private void removeRow(int i, ActionEvent e)
	{
		panels.remove(i);
		realign();
		notify(false,e);
	}

	@Override
	public Iterator<RosePanel> iterator()
	{
		return panels.iterator();
	}

	@Override
	public int getPanelCount()
	{
		return panels.size();
	}

	@Override
	public RosePanel getPanel(int index)
	{
		return panels.get(index);
	}

	@Override
	public int getFixWidth()
	{
		return width;
	}

	@Override
	public int getFixHeight()
	{
		return height;
	}
	
	

}

