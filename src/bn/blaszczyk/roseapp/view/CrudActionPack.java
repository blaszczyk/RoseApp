package bn.blaszczyk.roseapp.view;

import javax.swing.Action;

import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.tools.Preferences;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.crud.*;

public class CrudActionPack extends AbstractActionPack{
	
	private final Action actnStart;
	private final Action actnNew;
	private final Action actnEdit;
	private final Action actnView;
	private final Action actnSave;
	private final Action actnSaveAll;
	private final Action actnDelete;
//	private final Action actnCopy;
	private final Action actnClose;
	private final Action actnCloseAll;
	private final Action actnSettings;
	
	public CrudActionPack(GUIController guiController)
	{
		super(guiController);
		actnStart    = createAction( "Start"   , "start.png"   , e -> guiController.openStartTab()   , p -> true );
		actnNew      = createAction( "New"     , "new.png"     , e -> guiController.openNew( )       , p -> enableNew(p) );
		actnEdit     = createAction( "Edit"    , "edit.png"    , e -> guiController.editCurrent()    , p -> p instanceof FullViewPanel );
		actnView     = createAction( "View"    , "view.png"    , e -> guiController.viewCurrent()    , p -> p instanceof FullEditPanel );
		actnSave     = createAction( "Save"    , "save.png"    , e -> guiController.saveCurrent()    , p -> p.hasChanged() );
		actnSaveAll  = createAction( "SaveAll" , "saveall.png" , e -> guiController.saveAll()        , p -> guiController.getMainFrame().hasChanged() );
//		actnCopy     = createAction( "Copy"    , "copy.png"    , e -> guiController.copyCurrent()    , p -> false );
		actnDelete   = createAction( "Delete"  , "delete.png"  , e -> guiController.deleteCurrent()  , p -> p.getShownObject() instanceof Writable  );
		actnClose    = createAction( "Close"   , "close.png"   , e -> guiController.closeCurrent()   , p -> true );
		actnCloseAll = createAction( "CloseAll", "closeall.png", e -> guiController.closeAll()       , p -> true );
		actnSettings = createAction( "Settings", "settings.png", e -> guiController.openSettingsTab(), p -> ! p.getShownObject().equals(Preferences.class));
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

//	public Action getActnCopy()
//	{
//		return actnCopy;
//	}

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
	public void notify(RoseEvent e)
	{
		super.notify(e);
		if(getController().getMainFrame().getSelectedIndex() < 0)
			for( Action a : this)
				a.setEnabled( a == actnStart || a == actnSettings);
	}
	
	private static boolean enableNew(RosePanel panel)
	{
		Object o = panel.getShownObject();
		if( o instanceof Writable)
			return true;
		if( o instanceof Class )
			return Writable.class.isAssignableFrom((Class<?>) o);
		return false;
	}
}
