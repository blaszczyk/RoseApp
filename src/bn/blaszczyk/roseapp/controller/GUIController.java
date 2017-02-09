package bn.blaszczyk.roseapp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import bn.blaszczyk.rose.model.Identifyable;
import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.RoseException;
import bn.blaszczyk.roseapp.tools.EntityUtils;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.tools.Preferences;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.*;
import bn.blaszczyk.roseapp.view.panels.*;
import bn.blaszczyk.roseapp.view.panels.crud.FullEditPanel;
import bn.blaszczyk.roseapp.view.panels.crud.FullListPanel;
import bn.blaszczyk.roseapp.view.panels.crud.FullViewPanel;
import bn.blaszczyk.roseapp.view.panels.crud.StartPanel;
import bn.blaszczyk.roseapp.view.panels.settings.SettingsPanel;

public class GUIController implements Messenger {
	
	private static final Logger LOGGER = Logger.getLogger(GUIController.class);
	
	private Behaviour behaviour = new DefaultBehaviour();

	private ModelController modelController;
	private MainFrame mainFrame;
	private List<ActionPack> actionPacks = new ArrayList<>();
		
	public GUIController(ModelController modelController)
	{
		this.modelController = modelController;
		modelController.setMessenger(this);
		actionPacks.add(new CrudActionPack(this));
	}
	
	/*
	 * General
	 */
	public void setBehaviour(Behaviour behaviour)
	{
		this.behaviour = behaviour;
		behaviour.setMessenger(this);
	}
	
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
		entity = behaviour.replacePanel(entity);
		String iconFile = edit ? "edit.png" : "view.png";
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
		LOGGER.info("open tab: \"" + name + "\" : " + panel );
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
		RosePanel panel = mainFrame.getSelectedPanel();
		if(panel.hasChanged())
			if(!confirm("Discard changes?", "There are unsaved changes"))
				return;
		openEntityTab( ((Readable) panel.getShownObject()), false );
	}
	
	public void saveCurrent()
	{
		save(mainFrame.getSelectedPanel());
		notifyListeners();
	}
	
	private void save(RosePanel panel)
	{
		if(panel.getShownObject() instanceof Writable)
			if(!behaviour.checkEntity((Writable) panel.getShownObject()))
				return;
		panel.save();
		if(panel instanceof FullEditPanel)
		{
//			openEntityTab((Readable) panel.getShownObject(),false);
			try
			{
				modelController.commit();
			}
			catch (RoseException e)
			{
				LOGGER.error("Error saving " + panel.getShownObject(), e);
				error(e, "Save Error");
			}
		}
	}
	
	public void closeCurrent()
	{
		if(mainFrame.getSelectedPanel().hasChanged())
		{
			int option = questionYesNoCancel("Save before closing?", "There are unsaved changes");
			if(option == JOptionPane.CANCEL_OPTION)
				return;
			if(option == JOptionPane.YES_OPTION)
				saveCurrent();
		}
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
			try
			{
				openEntityTab( modelController.createCopy( (Writable) ((RosePanel)c).getShownObject() ), true );
			}
			catch (RoseException e)
			{
				LOGGER.error("Thou shalt not copy", e);
				error(e, "Error");
			}
		notifyListeners();
	}

	public <T extends Writable> void openNew(Class<T> type)
	{
		try
		{
			openEntityTab( modelController.createNew( type ), true );
			notifyListeners();
		}
		catch (RoseException e)
		{
			LOGGER.error(Messages.get("Error creating new") + " " + type.getName(), e);
			error(e, "Error");
		}
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

	public void addNew(Writable entity, int index)
	{
		if(entity == null)
			return;
		try
		{
			Writable subEntity = modelController.createNew( entity.getEntityClass(index).asSubclass(Writable.class) );
			entity.addEntity(index, subEntity);
			modelController.update(entity,subEntity);
			openEntityTab( subEntity, true);
			notifyListeners();
		}
		catch (RoseException e)
		{
			LOGGER.error("Error adding new entity field at index " + index + " to\r\n" + EntityUtils.toStringFull(entity) , e);
			error(e, "Error");
		}
	}

	public void saveAll()
	{
		int current = mainFrame.getSelectedIndex();
		for(int i = 0; i < mainFrame.getPanelCount(); i++)
			save(mainFrame.getPanel(i));
		mainFrame.setSelectedIndex(current);
		notifyListeners();
	}

	public void delete(Writable entity)
	{
		if(entity == null)
			return;
		if(! confirm(Messages.get("Really Delete") + " " + entity + "?", "Confirm Delete"))
			return;
		try
		{
			modelController.delete(entity);
			for(RosePanel panel : mainFrame)
			{
				if(panel.getShownObject() instanceof Readable )
					if( ((Readable)panel.getShownObject()).equals(entity))
						mainFrame.removePanel(panel);
				if(panel.getShownObject().equals(entity.getClass()))
					panel.refresh();
			}
			notifyListeners();
		}
		catch (RoseException e)
		{
			LOGGER.error("Error deleting " + EntityUtils.toStringFull(entity), e);
			error(e, Messages.get("Error deleting") + " " + entity);
		}
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
			for(Readable entity : modelController.getEntites(type))
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
			}
	}
	
	/*
	 * messages
	 */
	@Override
	public int questionYesNoCancel(String message, String title)
	{
		return JOptionPane.showConfirmDialog(mainFrame, message, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	}

	@Override
	public boolean questionYesNo(String message, String title)
	{
		return JOptionPane.showConfirmDialog(mainFrame, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
				== JOptionPane.YES_OPTION;
	}

	@Override
	public void error(Exception e, String title)
	{
		String message = ( e instanceof RoseException) ? ((RoseException)e).getFullMessage() : e.getMessage();
		JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void warning(String message, String title)
	{
		JOptionPane.showMessageDialog(mainFrame, message, title, JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public boolean confirm(String message, String title)
	{
		return JOptionPane.showConfirmDialog(mainFrame, message, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)
				== JOptionPane.OK_OPTION;
	}

	@Override
	public void info(String message, String title)
	{
		if(mainFrame != null)
			mainFrame.showInfo(message,title);
	}
}
