package bn.blaszczyk.roseapp.view.factories;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.*;

public class IconFactory {
	
	private static final Map<String, ImageIcon> icons = new HashMap<>();
	
	private IconFactory()
	{
	}

	public static ImageIcon create(String resourceName)
	{
		if(resourceName == null)
			return null;
		String fullResourceName = (resourceName.contains("/") ? "" : "bn/blaszczyk/roseapp/resources/") + resourceName;
		if(!icons.containsKey(fullResourceName))
		{
			try
			{
				ImageIcon icon = new ImageIcon(ImageIO.read(IconFactory.class.getClassLoader().getResourceAsStream(fullResourceName)));
				icons.put(fullResourceName, icon);
				return icon;
			}
			catch (IOException | IllegalArgumentException e)
			{
				LogManager.getLogger(IconFactory.class).error("Image not found:" + fullResourceName,e);
				return null;
			}
		}
		return icons.get(fullResourceName);
	}

	public static ImageIcon create(File file)
	{
		if(file == null)
			return null;
		String fullResourceName = file.getAbsolutePath();
		if(!icons.containsKey(fullResourceName))
		{
			try
			{
				ImageIcon icon = new ImageIcon(ImageIO.read(file));
				icons.put(fullResourceName, icon);
				return icon;
			}
			catch (IOException | IllegalArgumentException e)
			{
				LogManager.getLogger(IconFactory.class).error("Image not found:" + fullResourceName,e);
				return null;
			}
		}
		return icons.get(fullResourceName);
	}
	
}
