package bn.blaszczyk.roseapp.controller;


import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import bn.blaszczyk.rose.model.Identifyable;
import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.RelationType;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.tools.EntityUtils;
import bn.blaszczyk.roseapp.tools.Preferences;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.*;
import bn.blaszczyk.roseapp.view.panels.*;
import bn.blaszczyk.roseapp.view.panels.crud.FullEditPanel;
import bn.blaszczyk.roseapp.view.panels.crud.FullListPanel;
import bn.blaszczyk.roseapp.view.panels.crud.FullViewPanel;
import bn.blaszczyk.roseapp.view.panels.crud.StartPanel;
import bn.blaszczyk.roseapp.view.panels.settings.SettingsPanel;

public class GUIController {

	private ModelController modelController;
	private MainFrame mainFrame;
	private List<ActionPack> actionPacks = new ArrayList<>();
		
	public GUIController(ModelController modelController)
	{
		this.modelController = modelController;
		actionPacks.add(new CrudActionPack(this));
	}
	
	/*
	 * General
	 */
	
	public void addActionPack( ActionPack actionPack)
	{
		this.actionPacks.add(actionPack);
	}

	public ModelController getModelController()
	{
		return modelController;
	}
	
	private void notifyListeners()
	{
		RoseEvent e = new RoseEvent(this);
		for(ActionPack pack : actionPacks)
			pack.notify(e);
		mainFrame.notify(e);
	}
	
	/*
	 * Tab controls 
	 */
	
	public MainFrame getMainFrame()
	{
		return mainFrame;
	}
	
	public void exit()
	{
		mainFrame.setVisible(false);
		mainFrame.dispose();
		modelController.closeSession();
		System.exit(0);
	}
	
	public void createMainFrame(String title)
	{
		mainFrame = new MainFrame(this, title, actionPacks);
		mainFrame.showFrame();
		openStartTab();
	}
	
	public void openStartTab()
	{
		int index = getObjectsTabIndex(StartPanel.class);
		if(index < 0)
			addTab(new StartPanel(this), "Start", "start.png");
		else
			mainFrame.setSelectedIndex(index);
	}
	
	public void openFullListTab( Class<?> type )
	{
		int index = getObjectsTabIndex(type);
		if(index < 0)
			addTab( new FullListPanel(modelController, this, type), type.getSimpleName() + "s" , "applist.png" );
		else
			mainFrame.setSelectedIndex(index);
	}
	
	public void openEntityTab(Readable entity, boolean edit )
	{
		String iconFile = edit ? "edit.png" : "view.png";
		entity = superObject(entity);
		String title = entity.getId() > 0 ? entity.getEntityName() + " " + entity.getId() : "new " + entity.getEntityName();
		int index = getObjectsTabIndex(entity);
		if(index > 0 && ( mainFrame.getPanel(index) instanceof FullViewPanel ^ edit  ) )
			mainFrame.setSelectedIndex(index);
		else
			if(edit)
				addTab( new FullEditPanel((Writable) entity, modelController, this) , title, iconFile );
			else
				addTab( new FullViewPanel(entity, this) , title, iconFile );
	}
	
	public void openSettingsTab()
	{
		int index = getObjectsTabIndex(Preferences.class);
		if(index >= 0)
			mainFrame.setSelectedIndex(index);
		else
			addTab(new SettingsPanel(), "Settings", "settings.png");
	}
	
	public void openDialog(RosePanel panel, String title)
	{
		JDialog dialog = new JDialog(mainFrame, title, true);
		dialog.setSize(panel.getFixWidth(), panel.getFixHeight());
		dialog.add(panel.getPanel());
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}
	
	public void addTab( RosePanel panel, String name, String iconFile)
	{
		for(ActionPack a : actionPacks)
			panel.addRoseListener(a);
		int index = getObjectsTabIndex(panel.getShownObject());
		if( index >= 0 )
			mainFrame.replaceTab(index, panel, name, iconFile);
		else
			mainFrame.addTab(panel, name, iconFile);
	}
	
	private int getObjectsTabIndex(Object object)
	{
		for(int i = 0; i < mainFrame.getPanelCount(); i++)
		{	
			RosePanel panel = mainFrame.getPanel(i);
			if(panel == null)
				return -1;
			Object o = panel.getShownObject();
			if(o.equals(object) )
				return i;
			if(o instanceof Identifyable &&  object instanceof Identifyable)
				if(EntityUtils.equals((Identifyable)o, (Identifyable) object))
					return i;
		}
		return -1;
	}
	
	/*
	 * Entity controls
	 */

	public void editCurrent()
	{
		openEntityTab( ((Readable) mainFrame.getSelectedPanel().getShownObject()), true );
	}
	
	public void viewCurrent()
	{
		openEntityTab( ((Readable) mainFrame.getSelectedPanel().getShownObject()), false );
	}
	
	public void saveCurrent()
	{
		RosePanel panel = mainFrame.getSelectedPanel();
		panel.save(modelController);
		if(panel instanceof FullEditPanel)
		{
//			openEntityTab((Readable) panel.getShownObject(),false);
			modelController.commit();
		}
		notifyListeners();
	}
	
	public void closeCurrent()
	{
		mainFrame.closeCurrent();
		notifyListeners();
	}

	public void deleteCurrent()
	{
		RosePanel panel = mainFrame.getSelectedPanel();
		if( panel.getShownObject() instanceof Writable )
			delete( ((Writable)panel.getShownObject()) );
	}
	
	public void copyCurrent()
	{
		RosePanel c = mainFrame.getSelectedPanel();
		if( c instanceof RosePanel && ((RosePanel)c).getShownObject() instanceof Writable )
			openEntityTab( modelController.createCopy( (Writable) ((RosePanel)c).getShownObject() ), true );
		notifyListeners();
	}

	public <T extends Writable> void openNew(Class<T> type)
	{
		openEntityTab( modelController.createNew( type ), true );
		notifyListeners();
	}

	public void openNew()
	{
		RosePanel c = mainFrame.getSelectedPanel();
		if( !(c instanceof RosePanel) )
			return;
		Object o = ((RosePanel)c).getShownObject();
		Class<? extends Writable> type;
		if( o instanceof Class )
			type = ((Class<?>)o).asSubclass(Writable.class)  ;
		else if( o instanceof Writable )
			type = ((Writable)o).getClass();
		else
			return;
		openNew(type);
	}

	public void addNew(Writable aEntity, int index)
	{
		Writable entity = modelController.createNew( aEntity.getEntityClass(index).asSubclass(Writable.class) );
		modelController.addEntityField(aEntity, index, entity);
		openEntityTab( entity, true);
		notifyListeners();
	}

	public void saveAll()
	{
		int current = mainFrame.getSelectedIndex();
		for(int i = 0; i < mainFrame.getPanelCount(); i++)
			{
			RosePanel panel = mainFrame.getPanel(i);
			panel.save(modelController);
//			if( mainFrame.getPanel(i) instanceof FullEditPanel )
//				openEntityTab((Readable) panel.getShownObject(),false);
			}
		modelController.commit();
		mainFrame.setSelectedIndex(current);
		notifyListeners();
	}

	public void delete(Writable entity)
	{
		for(RosePanel panel : mainFrame)
		{
			if(panel.getShownObject() instanceof Readable )
				if( ((Readable)panel.getShownObject()).equals(entity))
					mainFrame.removePanel(panel);
			if(panel.getShownObject().equals(entity.getClass()))
				panel.refresh();
		}
		modelController.delete(entity);
		notifyListeners();
	}

	public void closeAll()
	{
		while(mainFrame.getSelectedPanel() != null)
			closeCurrent();
		notifyListeners();
	}

	public void deleteOrphans()
	{
		for(Class<? extends Readable> type : TypeManager.getEntityClasses())
			for(Readable entity : modelController.getAllEntites(type))
			{
				boolean orphan = true;
				for(int i = 0; i < entity.getEntityCount(); i++)
				{
					if( entity.getRelationType(i).isSecondMany() )
						orphan &= entity.getEntityValueMany(i).isEmpty();
					else
						orphan &= entity.getEntityValueOne(i) == null;
				}
				if(orphan)
					delete((Writable) entity);
				// TODO: option to delete
			}
	}
	
	private Readable superObject( Readable in )
	{
		Readable out = null;
		for(int i = 0; i < in.getEntityCount(); i++)
			if(in.getRelationType(i).equals(RelationType.ONETOONE) && in.getEntityValueOne(i) != null)
				if(out == null)
					out = in.getEntityValueOne(i);
				else
					return in;
		if(out == null)
			return in;
		if(out.getEntityCount() > in.getEntityCount())
			return out;
		return in;
	}
}
