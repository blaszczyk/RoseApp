package bn.blaszczyk.roseapp.view;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;

import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.factories.IconFactory;
import bn.blaszczyk.roseapp.view.panels.EntityPanel;

public abstract class AbstractActionPack implements ActionPack{

	public static interface EnabledChecker
	{
		public boolean checkEnabled(EntityPanel panel);
	}
	
	private final GUIController controller;	
	private final Map<Action, EnabledChecker> checkers = new HashMap<>();
	private final List<Action> actions = new LinkedList<>();
	
	public AbstractActionPack( GUIController controller )
	{	
		this.controller = controller;
	}
	
	protected GUIController getController()
	{
		return controller;
	}

	protected Action createAction(String text, String iconFile, RoseListener l, EnabledChecker c)
	{
		@SuppressWarnings("serial")
		Action action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				l.notify(new RoseEvent(this,true));
			}
		};
		action.putValue(Action.NAME, Messages.get(text));
		action.putValue(Action.SMALL_ICON, IconFactory.create(iconFile));
		checkers.put(action, c);
		actions.add(action);
		return action;
	}

	@Override
	public void notify(RoseEvent e)
	{
		MainFrame mainFrame = controller.getMainFrame();
		if( mainFrame.getSelectedPanel() != null)
		{
			EntityPanel panel = mainFrame.getSelectedPanel();
			for( Action a : checkers.keySet())
				a.setEnabled( checkers.get(a).checkEnabled( panel ) );
		}
	}

	@Override
	public Iterator<Action> iterator()
	{
		return actions.iterator();
	}
}
