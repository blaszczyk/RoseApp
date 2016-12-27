package bn.blaszczyk.roseapp.view.factories;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import bn.blaszczyk.roseapp.tools.Messages;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class LabelFactory{
	
	public static JLabel createOpaqueLabel(String text, Icon icon, Font font, Color foreground, Color background, int alignment)
	{
		JLabel label = createLabel(text, icon, font, foreground, alignment);
		label.setOpaque(true);
		if(background != null)
			label.setBackground(background);
		return label;
	}
	
	public static JLabel createOpaqueLabel(String text, Font font, Color foreground, Color background, int alignment)
	{
		return createOpaqueLabel(text, null, font, foreground, background, alignment);
	}

	public static JLabel createOpaqueLabel(String text, Font font, Color foreground, Color background)
	{
		return createOpaqueLabel(text, font, foreground, background, SwingConstants.LEFT);
	}
	
	public static JLabel createOpaqueLabel(Icon icon, Color background, int alignment)
	{
		return createOpaqueLabel(null, icon, null, null, background, alignment);
	}

	public static JLabel createOpaqueLabel(Icon icon, Color background)
	{
		return createOpaqueLabel( icon, background, SwingConstants.CENTER);
	}

	public static JLabel createLabel(String text, Icon icon, Font font, Color foreground, int alignment)
	{
		JLabel label = new JLabel( Messages.get(text), alignment);
		if(icon != null)
			label.setIcon(icon);
		if(font != null)
			label.setFont(font);
		if(foreground != null)
			label.setForeground(foreground);
		return label;
	}
	
	public static JLabel createLabel(String text, Icon icon, Font font, Color foreground)
	{
		return createLabel(text, icon, font, foreground, SwingConstants.LEFT);
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
		return createLabel(text, (Icon)null);
	}

	public static JLabel createLabel(String text, Icon icon, Font font, int alignment)
	{	
		return createLabel(text, icon, font, PROPERTY_FG, alignment);
	}
	
	public static JLabel createLabel(String text, Icon icon, int alignment)
	{	
		return createLabel(text, icon, PROPERTY_FONT, alignment);
	}
	
	public static JLabel createLabel(String text, int alignment)
	{	
		return createLabel(text, null, alignment);
	}

	public static JLabel createLabel(String text, Font font)
	{
		return createLabel(text, null, font);
	}
	
}
