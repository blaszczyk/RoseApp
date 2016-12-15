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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.tools.Preferences;
import bn.blaszczyk.roseapp.view.panels.crud.*;
import bn.blaszczyk.roseapp.view.factories.IconFactory;
import bn.blaszczyk.roseapp.view.panels.EntityPanel;

public class Actions implements ChangeListener, Iterable<Action>{
	
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
	private final Action actnSettings;
	
	
	
	private final Map<Action, EnabledChecker> checkers = new HashMap<>();
	private final List<Action> actions = new LinkedList<>();
	
	public Actions( MainFrame mainFrame, GUIController guiController)
	{	
		this.mainFrame = mainFrame;
		actnStart    = createAction( "Start"   , "start.png"   , e -> guiController.openStartTab()   , p -> true );
		actnNew      = createAction( "New"     , "new.png"     , e -> guiController.openNew( )       , p -> p.getShownObject() instanceof Writable || p.getShownObject() instanceof Class<?> );
		actnEdit     = createAction( "Edit"    , "edit.png"    , e -> guiController.editCurrent()    , p -> p instanceof FullViewPanel );
		actnView     = createAction( "View"    , "view.png"    , e -> guiController.viewCurrent()    , p -> p instanceof FullEditPanel );
		actnSave     = createAction( "Save"    , "save.png"    , e -> guiController.saveCurrent()    , p -> p.hasChanged() );
		actnSaveAll  = createAction( "SaveAll" , "saveall.png" , e -> guiController.saveAll()        , p -> mainFrame.hasChanged() );
		actnCopy     = createAction( "Copy"    , "copy.png"    , e -> guiController.copyCurrent()    , p -> false );
		actnDelete   = createAction( "Delete"  , "delete.png"  , e -> guiController.deleteCurrent()  , p -> false );
		actnClose    = createAction( "Close"   , "close.png"   , e -> guiController.closeCurrent()   , p -> true );
		actnCloseAll = createAction( "CloseAll", "closeall.png", e -> guiController.closeAll()       , p -> true );
		actnSettings = createAction( "Settings", "settings.png", e -> guiController.openSettingsTab(), p -> ! p.getShownObject().equals(Preferences.class));
	}

	private Action createAction(String text, String iconFile, ActionListener l, EnabledChecker c)
	{
		@SuppressWarnings("serial")
		Action action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				l.actionPerformed(e);
			}
		};
		action.putValue(Action.NAME, text);
		action.putValue(Action.SMALL_ICON, IconFactory.create(iconFile));
		checkers.put(action, c);
		actions.add(action);
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
	
	public Action getActnSettings()
	{
		return actnSettings;
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if( mainFrame.getSelectedPanel() != null)
		{
			EntityPanel panel = mainFrame.getSelectedPanel();
			for( Action a : checkers.keySet())
				a.setEnabled( checkers.get(a).checkEnabled( panel ) );
		}
		else
			for( Action a : checkers.keySet())
				a.setEnabled( a == actnStart );
	}
	
	private static interface EnabledChecker
	{
		public boolean checkEnabled(EntityPanel panel);
	}

	@Override
	public Iterator<Action> iterator()
	{
		return actions.iterator();
	}
}
