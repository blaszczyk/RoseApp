package bn.blaszczyk.roseapp.view.factories;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class IconFactory {
	
	private static final Map<String, Icon> icons = new HashMap<>();
	
	private IconFactory()
	{
	}
	
	public static Icon create(String fileName)
	{
		String fullFileName = (fileName.contains("/") ? "" : "bn/blaszczyk/roseapp/resources/") + fileName;
		if(!icons.containsKey(fullFileName))
		{
			try
			{
				Icon icon = new ImageIcon(ImageIO.read(IconFactory.class.getClassLoader().getResourceAsStream(fullFileName)));
				icons.put(fullFileName, icon);
				return icon;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return icons.get(fullFileName);
	}
	
}
