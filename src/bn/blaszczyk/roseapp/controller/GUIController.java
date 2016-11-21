package bn.blaszczyk.roseapp.controller;

import java.awt.Component;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.view.*;
import bn.blaszczyk.roseapp.view.panels.*;

public class GUIController {

	private FullModelController modelController;
	private Class<?>[] types;
	private MainFrame mainFrame;
		
	public GUIController(FullModelController modelController)
	{
		this.modelController = modelController;
	}

	public void editCurrent()
	{
		openEntityTab( ((Readable) ((MyPanel)mainFrame.getTabbedPane().getSelectedComponent()).getShownObject()), true );
	}
	
	public void viewCurrent()
	{
		openEntityTab( ((Readable) ((MyPanel)mainFrame.getTabbedPane().getSelectedComponent()).getShownObject()), false );
	}
	
	public void createMainFrame(Class<?>[] types, String title)
	{
		mainFrame = new MainFrame ( this, title);
		this.types = types;
		openStartTab();
	}
	
	public void openStartTab()
	{
		for(int i = 0; i < mainFrame.getTabbedPane().getTabCount(); i++)
		{
			Component c = mainFrame.getTabbedPane().getComponentAt(i);
			if( c instanceof StartPanel )
			{
				mainFrame.getTabbedPane().setSelectedIndex(i);
				return;
			}
		}
		mainFrame.addTab(new StartPanel(this, types), "Start", "start.png");
	}
	
	public void openFullListTab( Class<?> type )
	{
		for(int i = 0; i < mainFrame.getTabbedPane().getTabCount(); i++)
		{
			Component c = mainFrame.getTabbedPane().getComponentAt(i);
			if( c instanceof MyPanel && ((MyPanel)c).getShownObject().equals(type) )
			{
				mainFrame.getTabbedPane().setSelectedIndex(i);
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
		for(int i = 0; i < mainFrame.getTabbedPane().getTabCount(); i++)
		{
			Component c = mainFrame.getTabbedPane().getComponentAt(i);
			if( c instanceof MyPanel && ((MyPanel)c).getShownObject().equals(entity) )
			{
				if( edit ^ c instanceof FullViewPanel )
					mainFrame.getTabbedPane().setSelectedIndex(i);
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
		Component c = mainFrame.getTabbedPane().getSelectedComponent();
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
		Component c = mainFrame.getTabbedPane().getSelectedComponent();
			mainFrame.getTabbedPane().remove(c);
	}

	public void deleteCurrent()
	{
		Component c = mainFrame.getTabbedPane().getSelectedComponent();
		if( c instanceof MyPanel && ((MyPanel)c).getShownObject() instanceof Writable )
			delete( ((Writable) ((MyPanel)c).getShownObject()) );
	}
	
	public void copyCurrent()
	{
		Component c = mainFrame.getTabbedPane().getSelectedComponent();
		if( c instanceof MyPanel && ((MyPanel)c).getShownObject() instanceof Writable )
			openEntityTab( modelController.createCopy( (Writable) ((MyPanel)c).getShownObject() ), true );
	}

	@SuppressWarnings("unchecked")
	public void openNew()
	{
		Component c = mainFrame.getTabbedPane().getSelectedComponent();
		if( !(c instanceof MyPanel) )
			return;
		Object o = ((MyPanel)c).getShownObject();
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
		int current = mainFrame.getTabbedPane().getSelectedIndex();
		for(Component c : mainFrame.getTabbedPane().getComponents())
			if( c instanceof FullEditPanel )
			{
				FullEditPanel panel = (FullEditPanel) c;
				if(panel.hasChanged())
					panel.save(modelController);
				openEntityTab((Readable) panel.getShownObject(),false);
			}
		modelController.commit();
		mainFrame.getTabbedPane().setSelectedIndex(current);
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
		for(Component c : mainFrame.getTabbedPane().getComponents() )
			if(c instanceof MyPanel && ((MyPanel)c).getShownObject() instanceof Readable )
				if( ((Readable) ((MyPanel)c).getShownObject()).equals(entity))
					mainFrame.getTabbedPane().remove(c);
		modelController.delete(entity);
	}

	public void closeAll()
	{
		while(mainFrame.getTabbedPane().getSelectedComponent() != null)
			closeCurrent();
	}
}
