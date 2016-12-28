package bn.blaszczyk.roseapp.controller;


import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.tools.Preferences;
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
	
	public void addActionPack( ActionPack actionPack)
	{
		this.actionPacks.add(actionPack);
	}

	public void editCurrent()
	{
		openEntityTab( ((Readable) mainFrame.getSelectedPanel().getShownObject()), true );
	}
	
	public void viewCurrent()
	{
		openEntityTab( ((Readable) mainFrame.getSelectedPanel().getShownObject()), false );
	}
	
	public MainFrame getMainFrame()
	{
		return mainFrame;
	}
	
	public void createMainFrame(String title)
	{
		mainFrame = new MainFrame(this, title, actionPacks);
		mainFrame.showFrame();
		openStartTab();
	}
	
	public void openStartTab()
	{
		for(int i = 0; i < mainFrame.getPanelCount(); i++)
		{
			EntityPanel c = mainFrame.getPanel(i);
			if( c instanceof StartPanel )
			{
				mainFrame.setSelectedIndex(i);
				return;
			}
		}
		mainFrame.addTab(new StartPanel(this), "Start", "start.png");
	}
	
	public void openFullListTab( Class<?> type )
	{
		for(int i = 0; i < mainFrame.getPanelCount(); i++)
		{
			EntityPanel c = mainFrame.getPanel(i);
			if( c instanceof EntityPanel && ((EntityPanel)c).getShownObject().equals(type) )
			{
				mainFrame.setSelectedIndex(i);
				return;
			}
		}
		String title = type.getSimpleName() + "s";
		mainFrame.addTab( new FullListPanel(modelController, this, type), title , "applist.png" );
	}
	
	public void openEntityTab(Readable entity, boolean edit )
	{
		String iconFile = edit ? "edit.png" : "view.png";
		String title = entity.getId() > 0 ? entity.getEntityName() + " " + entity.getId() : "new " + entity.getEntityName();
		int index = getObjectsTabIndex(entity);
		if(index > 0 && ( mainFrame.getPanel(index) instanceof FullViewPanel ^ edit  ) )
			mainFrame.setSelectedIndex(index);
		else
			if(edit)
				addTab( new FullEditPanel((Writable) entity, modelController, this, true, true) , title, iconFile );
			else
				addTab( new FullViewPanel(entity, this, true) , title, iconFile );
	}
	
	public void openSettingsTab()
	{
		int index = getObjectsTabIndex(Preferences.class);
		if(index >= 0)
			mainFrame.setSelectedIndex(index);
		else
			addTab(new SettingsPanel(), "Settings", "settings.png");
	}
	
	public void openDialog(EntityPanel panel, String title)
	{
		JDialog dialog = new JDialog(mainFrame, title, true);
		dialog.setSize(panel.getFixWidth(), panel.getFixHeight());
		dialog.add(panel.getPanel());
		dialog.setLocationRelativeTo(mainFrame);
		dialog.setVisible(true);
	}
	
	public void addTab( EntityPanel panel, String name, String iconFile)
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
			EntityPanel panel = mainFrame.getPanel(i);
			if( panel != null && panel.getShownObject().equals(object) )
				return i;
		}
		return -1;
	}
	
	public void saveCurrent()
	{
		EntityPanel panel = mainFrame.getSelectedPanel();
		panel.save(modelController);
		if(panel instanceof FullEditPanel)
		{
			openEntityTab((Readable) panel.getShownObject(),false);
			modelController.commit();
		}
	}
	
	public void closeCurrent()
	{
		mainFrame.closeCurrent();
	}

	public void deleteCurrent()
	{
		EntityPanel c = mainFrame.getSelectedPanel();
		if( c instanceof EntityPanel && ((EntityPanel)c).getShownObject() instanceof Writable )
			delete( ((Writable) ((EntityPanel)c).getShownObject()) );
	}
	
	public void copyCurrent()
	{
		EntityPanel c = mainFrame.getSelectedPanel();
		if( c instanceof EntityPanel && ((EntityPanel)c).getShownObject() instanceof Writable )
			openEntityTab( modelController.createCopy( (Writable) ((EntityPanel)c).getShownObject() ), true );
	}

	@SuppressWarnings("unchecked")
	public void openNew()
	{
		EntityPanel c = mainFrame.getSelectedPanel();
		if( !(c instanceof EntityPanel) )
			return;
		Object o = ((EntityPanel)c).getShownObject();
		Class<?> type;
		if( o instanceof Class<?>)
			type = (Class<?>) o;
		else if( o instanceof Writable )
			type = o.getClass();
		else
			return;
		openEntityTab( modelController.createNew( (Class<Writable>) type ), true );
	}

	public void addNew(Writable aEntity, int index)
	{
		@SuppressWarnings("unchecked")
		Writable entity = modelController.createNew( (Class<Writable>) aEntity.getEntityClass(index) );
		modelController.addEntityField(aEntity, index, entity);
		openEntityTab( entity, true);
	}

	public void saveAll()
	{
		int current = mainFrame.getSelectedIndex();
		for(int i = 0; i < mainFrame.getPanelCount(); i++)
			{
			EntityPanel panel = mainFrame.getPanel(i);
			panel.save(modelController);
			if( mainFrame.getPanel(i) instanceof FullEditPanel )
				openEntityTab((Readable) panel.getShownObject(),false);
			}
		modelController.commit();
		mainFrame.setSelectedIndex(current);
	}

	public void delete(Writable entity)
	{
		for(int i = 0; i < entity.getEntityCount(); i++)
		{
			switch(entity.getRelationType(i))
			{
			case MANYTOMANY:
				break;
			case MANYTOONE:
				break;
			case ONETOMANY:
//				Set<?> set = (Set<?>) entity.getEntityValue(i);
//				for(Object o : set.toArray())
//					delete((Writable) o);
				break;
			case ONETOONE:
//				delete((Writable) entity.getEntityValue(i));
				break;
			}
		}		
		for(int i = 0; i < mainFrame.getPanelCount(); i++)
			if(mainFrame.getPanel(i) instanceof EntityPanel && ((EntityPanel)mainFrame.getPanel(i)).getShownObject() instanceof Readable )
				if( ((Readable) ((EntityPanel)mainFrame.getPanel(i)).getShownObject()).equals(entity))
					mainFrame.removePanel(i);
		modelController.delete(entity);
	}

	public void closeAll()
	{
		while(mainFrame.getSelectedPanel() != null)
			closeCurrent();
	}

	public ModelController getModelController()
	{
		return modelController;
	}
}
