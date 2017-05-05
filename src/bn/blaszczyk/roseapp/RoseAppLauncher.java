package bn.blaszczyk.roseapp;

import static bn.blaszczyk.rosecommon.tools.Preferences.*;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.tools.AppPreference;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.MainFrame;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.crud.FullEditPanel;
import bn.blaszczyk.roseapp.view.panels.crud.FullListPanel;
import bn.blaszczyk.roseapp.view.panels.crud.FullViewPanel;
import bn.blaszczyk.roseapp.view.panels.crud.StartPanel;
import bn.blaszczyk.roseapp.view.panels.settings.SettingsPanel;
import bn.blaszczyk.roseapp.view.tools.ProgressDialog;
import bn.blaszczyk.rosecommon.RoseException;
import bn.blaszczyk.rosecommon.controller.ModelController;
import bn.blaszczyk.rosecommon.tools.CommonPreference;
import bn.blaszczyk.rosecommon.tools.TypeManager;

public class RoseAppLauncher {
	
	private RoseAppLauncher()
	{
	}
	
	public static void launch(final GUIController controller, final String title)
	{
		controller.createMainFrame(title);
		fetchEntities(controller);
		loadPanels(controller);
		
	}

	private static void fetchEntities(final GUIController controller)
	{
		boolean fetchOnStart = getBooleanValue(CommonPreference.FETCH_ON_START);
		if(fetchOnStart)
		{
			ProgressDialog dialog = new ProgressDialog(controller.getMainFrame(),TypeManager.getEntityClasses().size(),Messages.get("Load Entities"),"load.png", true);
			SwingUtilities.invokeLater(() -> dialog.showDialog());
			new Thread( () -> cacheAllEntities(dialog,controller.getModelController()) ).start();
		}
	}

	private static void loadPanels(final GUIController controller)
	{
		final int startPanelCount = getIntegerValue(AppPreference.START_PANEL_COUNT);
		for(int i = 0; i < startPanelCount; i++)
		{
			final String panel = getStringValue(AppPreference.START_PANEL.append(i));
			openPanel(controller,panel);
		}
	}

	private static void openPanel(final GUIController controller, final String panel)
	{
		if(panel.equals("start"))
			controller.openStartTab();
		else if(panel.equals("settings"))
			controller.openSettingsTab();
		else if(panel.startsWith("list."))
		{
			final Class<? extends Readable> type = TypeManager.getClass(panel.substring(5));
			controller.openFullListTab(type);			
		}
		else if(panel.startsWith("view.") || panel.startsWith("edit."))
		{
			final String[] tokens = panel.split("\\.",3);
			try
			{
				final boolean edit = panel.substring(0, 4).equals("edit");
				final Class<? extends Readable> type = TypeManager.getClass(tokens[1]);
				final int id = Integer.parseInt(tokens[2]);
				final Readable entity = controller.getModelController().getEntityById(type, id);
				controller.openEntityTab(entity, edit);
			}
			catch(Exception e)
			{
				Logger.getLogger(RoseAppLauncher.class).error("Unable to open tab for " + panel, e);
			}
		}
	}
	
	public static void savePanels(final GUIController controller)
	{
		final MainFrame mainFrame = controller.getMainFrame();
		final int panelCount = mainFrame.getPanelCount();
		putIntegerValue(AppPreference.START_PANEL_COUNT, panelCount);
		for(int i = 0; i < panelCount; i++)
		{
			final RosePanel panel = mainFrame.getPanel(i);
			final String panelString;
			if(panel instanceof StartPanel)
				panelString = "start";
			else if(panel instanceof SettingsPanel)
				panelString = "settings";
			else if(panel instanceof FullListPanel)
				panelString = "list." + ((Class<?>)panel.getShownObject()).getSimpleName();
			else if(panel instanceof FullViewPanel || panel instanceof FullEditPanel)
			{
				final String prefix = panel instanceof FullEditPanel ? "edit." : "view.";
				final Readable entity = (Readable) panel.getShownObject();
				panelString = prefix + entity.getEntityName() + "." + entity.getId();
			}
			else
				panelString = "";
			putStringValue(AppPreference.START_PANEL.append(i), panelString);
		}
	}

	private static void cacheAllEntities(final ProgressDialog dialog, final ModelController modelController)
	{
		try{
			dialog.appendInfo(Messages.get("caching entities"));
			for(Class<? extends Readable> type : TypeManager.getEntityClasses())
			{
				dialog.incrementValue();
				dialog.appendInfo( String.format("\n%s %s", Messages.get("loading"), Messages.get(type.getSimpleName() + "s") ) );
				modelController.getEntities(type);
			}
			dialog.disposeDialog();
		}
		catch(RoseException e)
		{
			dialog.appendException(e);
			dialog.appendInfo("\nconnection error");
			dialog.setFinished();
		}
	}
	
}
