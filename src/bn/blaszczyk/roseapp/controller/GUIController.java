package bn.blaszczyk.roseapp.controller;


import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.view.*;
import bn.blaszczyk.roseapp.view.panels.*;

public class GUIController {

	private ModelController modelController;
	private Class<?>[] types;
	private MainFrame mainFrame;
		
	public GUIController(ModelController modelController)
	{
		this.modelController = modelController;
	}

	public void editCurrent()
	{
		openEntityTab( ((Readable) mainFrame.getSelectedPanel().getShownObject()), true );
	}
	
	public void viewCurrent()
	{
		openEntityTab( ((Readable) mainFrame.getSelectedPanel().getShownObject()), false );
	}
	
	public void createMainFrame(Class<?>[] types, String title)
	{
		mainFrame = new MainFrame ( this, title);
		this.types = types;
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
		mainFrame.addTab(new StartPanel(this, types), "Start", "start.png");
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
		for(int i = 0; i < mainFrame.getPanelCount(); i++)
		{
			EntityPanel c = mainFrame.getPanel(i);
			if( c instanceof EntityPanel && ((EntityPanel)c).getShownObject().equals(entity) )
			{
				if( edit ^ c instanceof FullViewPanel )
					mainFrame.setSelectedIndex(i);
				else
				{
					if(edit)
						mainFrame.replaceTab(i, new FullEditPanel((Writable) entity, modelController, this, true, mainFrame.getActions()) , title, iconFile );
					else
						mainFrame.replaceTab(i, new FullViewPanel(entity, this, true) , title, iconFile );
				}
				return;
			}
		}
		if(edit)
			mainFrame.addTab( new FullEditPanel((Writable) entity, modelController, this, true, mainFrame.getActions()) , title, iconFile );
		else
			mainFrame.addTab( new FullViewPanel(entity, this, true) , title, iconFile );
	}
	
	public void saveCurrent()
	{
		EntityPanel c = mainFrame.getSelectedPanel();
		if( c instanceof FullEditPanel)
		{
			FullEditPanel panel = (FullEditPanel) c;
			panel.save(modelController);
			modelController.commit();
			openEntityTab((Readable) panel.getShownObject(),false);
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
			if( mainFrame.getPanel(i) instanceof FullEditPanel )
			{
				FullEditPanel panel = (FullEditPanel) mainFrame.getPanel(i);
				if(panel.hasChanged())
					panel.save(modelController);
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
}
