package bn.blaszczyk.roseapp.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.view.panels.FullEditPanel;
import bn.blaszczyk.roseapp.view.panels.FullViewPanel;
import bn.blaszczyk.roseapp.view.panels.MyPanel;

public class Actions implements ChangeListener{
	
	private final MainFrame mainFrame;
	
	private final Action actnStart;
	private final Action actnClose;
	private final Action actnCloseAll;
	private final Action actnEdit;
	private final Action actnView;
	private final Action actnSave;
	private final Action actnSaveAll;
	private final Action actnDelete;
	private final Action actnNew;
	private final Action actnCopy;
	
	private final Map<Action, EnabledChecker> checkers = new HashMap<>();
	
	public Actions( MainFrame mainFrame, GUIController guiController)
	{	
		this.mainFrame = mainFrame;
		actnStart = createAction( e -> guiController.openStartTab(), p -> true );
		actnClose = createAction( e -> guiController.closeCurrent(), p -> true );
		actnCloseAll = createAction( e -> guiController.closeAll(), p -> true );
		actnEdit = createAction( e -> guiController.editCurrent(), p -> p instanceof FullViewPanel );
		actnView = createAction( e -> guiController.viewCurrent(), p -> p instanceof FullEditPanel );
		actnSave = createAction( e -> guiController.saveCurrent(), p -> p instanceof FullEditPanel && p.hasChanged() );
		actnSaveAll = createAction( e -> guiController.saveAll(), p -> mainFrame.hasChanged() );
		actnCopy = createAction( e -> guiController.copyCurrent(), p -> false );
		actnDelete = createAction( e -> guiController.deleteCurrent(), p -> false );
		actnNew = createAction( e -> guiController.openNew( ), p -> p.getShownObject() instanceof Writable || p.getShownObject() instanceof Class<?> );
	}

	private Action createAction(ActionListener l, EnabledChecker c)
	{
		@SuppressWarnings("serial")
		Action action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				l.actionPerformed(e);
			}
		};
		checkers.put(action, c);
		return action;
	}

	public Action getActnClose()
	{
		return actnClose;
	}

	public Action getActnCloseAll()
	{
		return actnCloseAll;
	}

	public Action getActnEdit()
	{
		return actnEdit;
	}

	public Action getActnView()
	{
		return actnView;
	}

	public Action getActnSave()
	{
		return actnSave;
	}
	
	public Action getActnSaveAll()
	{
		return actnSaveAll;
	}

	public Action getActnDelete()
	{
		return actnDelete;
	}

	public Action getActnCopy()
	{
		return actnCopy;
	}

	public Action getActnNew()
	{
		return actnNew;
	}
	
	public Action getActnStart()
	{
		return actnStart;
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if( mainFrame.getTabbedPane().getSelectedComponent() instanceof MyPanel)
		{
			MyPanel panel = (MyPanel) mainFrame.getTabbedPane().getSelectedComponent();
			for( Action a : checkers.keySet())
				a.setEnabled( checkers.get(a).checkEnabled( panel ) );
		}
		else
			for( Action a : checkers.keySet())
				a.setEnabled( a == actnStart );
	}
	
	private interface EnabledChecker
	{
		public boolean checkEnabled(MyPanel panel);
	}
}
