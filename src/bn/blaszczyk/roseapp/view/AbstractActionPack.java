package bn.blaszczyk.roseapp.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.factories.IconFactory;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public abstract class AbstractActionPack implements ActionPack{

	static{
		UIManager.put("Menu.font", PROPERTY_FONT);
		UIManager.put("MenuBar.font", PROPERTY_FONT);
		UIManager.put("MenuItem.font", PROPERTY_FONT);
	}
	
	public static interface EnabledChecker
	{
		public boolean checkEnabled(RosePanel panel);
	}
	
	private final GUIController controller;	
	private final Map<Action, EnabledChecker> checkers = new HashMap<>();
	private final List<Action> actions = new LinkedList<>();
	private final List<JMenu> menus = new LinkedList<>();
	
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
		Action action = new AbstractAction() {
			private static final long serialVersionUID = -6150110792873006921L;
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					l.notify(new RoseEvent(this,true,e));
				}
				catch(Exception ex)
				{
					final String message = "Unexpected error";
					Logger.getLogger(this.getClass()).error(message, ex);
				}
			}
		};
		action.putValue(Action.NAME, Messages.get(text));
		action.putValue(Action.SHORT_DESCRIPTION, Messages.get(text));
//		action.putValue(Action.SMALL_ICON, IconFactory.create(iconFile));
		action.putValue(Action.LARGE_ICON_KEY, IconFactory.create(iconFile));
		checkers.put(action, c);
		actions.add(action);
		return action;
	}
	
	protected void addMenu( JMenu menu)
	{
		menus.add(menu);
	}
	
	protected JMenuItem menuItem(String text, ActionListener l)
	{
		JMenuItem mi = new JMenuItem(Messages.get(text));
		mi.addActionListener(l);
		return mi;
	}
	
	@Override
	public List<JMenu> getMenus()
	{
		return menus;
	}

	@Override
	public void notify(RoseEvent e)
	{
		MainFrame mainFrame = controller.getMainFrame();
		if( mainFrame.getSelectedPanel() != null)
		{
			RosePanel panel = mainFrame.getSelectedPanel();
			for( Action a : checkers.keySet())
				a.setEnabled( checkers.get(a).checkEnabled( panel ) );
		}
	}

	@Override
	public Iterator<Action> iterator()
	{
		return actions.iterator();
	}
	
	@Override
	public String toString()
	{
		return getClass().toString();
	}
}
