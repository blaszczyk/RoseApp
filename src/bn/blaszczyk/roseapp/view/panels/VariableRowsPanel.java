package bn.blaszczyk.roseapp.view.panels;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import bn.blaszczyk.roseapp.view.factories.ButtonFactory;

@SuppressWarnings("serial")
public class VariableRowsPanel extends AbstractPanelContainer {

	public interface EntityPanelCreator
	{
		public EntityPanel newInstance();
	}
	
	public interface Indexable extends EntityPanel
	{
		public void setIndex(int index);
	}
	
	private final List<EntityPanel> panels = new ArrayList<>();
	private final EntityPanelCreator creator;
	
	
	public VariableRowsPanel( Iterable<? extends EntityPanel> panels, EntityPanelCreator creator )
	{
		super(null);
		this.creator=creator;
		int count = 0;
		for(EntityPanel panel : panels)
		{
			if(panel instanceof Indexable)
				((Indexable)panel).setIndex(count++);
			this.panels.add(panel);
		}
		setBackground(FULL_PNL_BACKGROUND);
		registerActionListener();	
		realign();
	}
	
	public void addRow(EntityPanel panel)
	{
		panel.addActionListener(changeListener);
		panels.add(panel);
	}
	
	private void realign()
	{
		removeAll();
		int heigth = V_SPACING;
		for(int i = 0; i < panels.size(); i++)
		{
			final int ii = i;
			JButton button = ButtonFactory.createIconButton("delete.png", e -> removeColumn(ii,e), changeListener);
			button.setBounds(H_SPACING , heigth, TBL_BTN_WIDTH, LBL_HEIGHT);
			add(button);
			
			EntityPanel panel = panels.get(i);
			panel.getPanel().setBounds(2 * H_SPACING + TBL_BTN_WIDTH, heigth, panel.getFixWidth(), panel.getFixHeight());
			if(panel instanceof Indexable)
				((Indexable)panel).setIndex(i);
			add(panel.getPanel());
			
			heigth += panel.getFixHeight() + V_SPACING;
		}
		JButton button = ButtonFactory.createIconButton("add.png", e -> addNewRow(e));
		button.setBounds(H_SPACING , heigth, TBL_BTN_WIDTH, LBL_HEIGHT);
		add(button);
		refresh();
	}

	private void addNewRow(ActionEvent e)
	{
		addRow(creator.newInstance());
		realign();
		changeListener.actionPerformed(e);
	}

	private void removeColumn(int i, ActionEvent e)
	{
		panels.remove(i);
		realign();
		changeListener.actionPerformed(e);
	}

	@Override
	protected Iterable<EntityPanel> getPanels()
	{
		return panels;
	}

	@Override
	protected int getPanelCount()
	{
		return panels.size();
	}

	@Override
	protected EntityPanel getPanel(int index)
	{
		return panels.get(index);
	}
	
	

}

