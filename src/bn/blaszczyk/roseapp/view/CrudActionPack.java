package bn.blaszczyk.roseapp.view;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.crud.*;
import bn.blaszczyk.rosecommon.tools.Preferences;
import bn.blaszczyk.rosecommon.tools.TypeManager;

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
		actnStart    = createAction( "Start"   , "start_32.png"   , e -> guiController.openStartTab()   , p -> true );
		actnNew      = createAction( "New"     , "new_32.png"     , e -> guiController.openNew( )       , p -> enableNew(p) );
		actnEdit     = createAction( "Edit"    , "edit_32.png"    , e -> guiController.editCurrent()    , p -> p instanceof FullViewPanel );
		actnView     = createAction( "View"    , "view_32.png"    , e -> guiController.viewCurrent()    , p -> p instanceof FullEditPanel );
		actnSave     = createAction( "Save"    , "save_32.png"    , e -> guiController.saveCurrent()    , p -> p.hasChanged() );
		actnSaveAll  = createAction( "SaveAll" , "saveall_32.png" , e -> guiController.saveAll()        , p -> guiController.getMainFrame().hasChanged() );
//		actnCopy     = createAction( "Copy"    , "copy.png"    , e -> guiController.copyCurrent()    , p -> false );
		actnDelete   = createAction( "Delete"  , "delete_32.png"  , e -> guiController.deleteCurrent()  , p -> p.getShownObject() instanceof Writable );
		actnClose    = createAction( "Close"   , "close_32.png"   , e -> guiController.closeCurrent()   , p -> true );
		actnCloseAll = createAction( "CloseAll", "closeall_32.png", e -> guiController.closeAll()       , p -> true );
		actnSettings = createAction( "Settings", "settings_32.png", e -> guiController.openSettingsTab(), p -> ! p.getShownObject().equals(Preferences.class));
		
		JMenu menuFile = new JMenu(Messages.get("File"));
		menuFile.add(new JMenuItem(actnStart));
		menuFile.add(menuItem("Synchronize", e -> getController().synchronize()));
		menuFile.add(createListsMenu());
		menuFile.add(new JMenuItem(actnClose));
		menuFile.add(new JMenuItem(actnCloseAll));
		menuFile.addSeparator();
		menuFile.add(new JMenuItem(actnSettings));
		menuFile.addSeparator();
		menuFile.add(menuItem("Exit", e -> getController().exit()));
		addMenu(menuFile);
		
		JMenu menuEntities = new JMenu(Messages.get("Entities"));
		menuEntities.add(createNewMenu());
		menuEntities.add(new JMenuItem(actnView));
		menuEntities.add(new JMenuItem(actnEdit));
		menuEntities.add(new JMenuItem(actnDelete));
		menuEntities.add(new JMenuItem(actnSave));
		menuEntities.add(new JMenuItem(actnSaveAll));
//		menuEntities.addSeparator();
//		menuEntities.add(menuItem("Delete Orphans", e -> getController().deleteOrphans()));
		addMenu(menuEntities);
	}
	
	private JMenu createNewMenu()
	{
		JMenu newMenu = new JMenu(Messages.get("new"));
		for(Class<? extends Readable> type : TypeManager.getEntityClasses())
			if(getController().getBehaviour().creatable(type.asSubclass(Writable.class)))
			{
				JMenuItem item = new JMenuItem(Messages.get(type.getSimpleName()));
				item.addActionListener(e -> getController().openNew(type.asSubclass(Writable.class)));
				newMenu.add(item);
			}
		return newMenu;
	}
	
	private JMenu createListsMenu()
	{
		JMenu newMenu = new JMenu(Messages.get("List"));
		for(Class<? extends Readable> type : TypeManager.getEntityClasses())
		{
			JMenuItem item = new JMenuItem(Messages.get(type.getSimpleName()));
			item.addActionListener(e -> getController().openFullListTab(type.asSubclass(Writable.class)));
			newMenu.add(item);
		}
		return newMenu;
	}
	
	private boolean enableNew(RosePanel panel)
	{
		Object o = panel.getShownObject();
		if( o instanceof Writable)
			return getController().getBehaviour().creatable( ((Writable)o).getClass() );
		if( o instanceof Class && Writable.class.isAssignableFrom((Class<?>) o))
			return getController().getBehaviour().creatable( ((Class<?>) o).asSubclass(Writable.class) );
		return false;
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
	
	@Override
	public String toString()
	{
		return "CRUD ActionPack";
	}
}
