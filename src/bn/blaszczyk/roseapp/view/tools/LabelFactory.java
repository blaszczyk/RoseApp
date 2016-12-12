package bn.blaszczyk.roseapp.view.tools;


import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class LabelFactory{

	public static JLabel createLabel(String text, Icon icon, Font font, Color foreground)
	{
		JLabel label = new JLabel(text, SwingConstants.RIGHT);
		if(icon != null)
			label.setIcon(icon);
		if(font != null)
			label.setFont(font);
		if(foreground != null)
			label.setForeground(foreground);
		return label;
	}

	public static JLabel createLabel(String text, Icon icon, Font font)
	{	
		return createLabel(text, icon, font, PROPERTY_FG);
	}
	
	public static JLabel createLabel(String text, Icon icon)
	{	
		return createLabel(text, icon, PROPERTY_FONT);
	}
	
	public static JLabel createLabel(String text)
	{	
		return createLabel(text, null);
	}
	
	
}
