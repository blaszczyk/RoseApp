package bn.blaszczyk.roseapp.view.factories;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

public class IconFactory {
	
	private static final Map<String, Icon> icons = new HashMap<>();
	
	private IconFactory()
	{
	}

	public static Icon create(String resourceName)
	{
		String fullResourceName = (resourceName.contains("/") ? "" : "bn/blaszczyk/roseapp/resources/") + resourceName;
		if(!icons.containsKey(fullResourceName))
		{
			try
			{
				Icon icon = new ImageIcon(ImageIO.read(IconFactory.class.getClassLoader().getResourceAsStream(fullResourceName)));
				icons.put(fullResourceName, icon);
				return icon;
			}
			catch (IOException | IllegalArgumentException e)
			{
				Logger.getLogger(IconFactory.class).error("Image not found:" + fullResourceName,e);
				return null;
			}
		}
		return icons.get(fullResourceName);
	}

	public static Icon create(File file)
	{
		String fullResourceName = file.getAbsolutePath();
		if(!icons.containsKey(fullResourceName))
		{
			try
			{
				Icon icon = new ImageIcon(ImageIO.read(file));
				icons.put(fullResourceName, icon);
				return icon;
			}
			catch (IOException | IllegalArgumentException e)
			{
				Logger.getLogger(IconFactory.class).error("Image not found:" + fullResourceName,e);
				return null;
			}
		}
		return icons.get(fullResourceName);
	}
	
}
