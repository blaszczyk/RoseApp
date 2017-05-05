package bn.blaszczyk.roseapp;

import static bn.blaszczyk.rosecommon.tools.Preferences.getBooleanValue;

import javax.swing.SwingUtilities;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.tools.Messages;
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
		boolean fetchOnStart = getBooleanValue(CommonPreference.FETCH_ON_START);
		if(fetchOnStart)
		{
			ProgressDialog dialog = new ProgressDialog(controller.getMainFrame(),TypeManager.getEntityClasses().size(),Messages.get("Load Entities"),"load.png", true);
			SwingUtilities.invokeLater(() -> dialog.showDialog());
			new Thread( () -> cacheAllEntities(dialog,controller.getModelController()) ).start();
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
