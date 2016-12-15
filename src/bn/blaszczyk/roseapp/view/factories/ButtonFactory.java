package bn.blaszczyk.roseapp.view.factories;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
public class ButtonFactory {

	public static JButton createIconButton(String text, String iconFile, Font font, Color foreground, Color background, ActionListener... listeners )
	{
		JButton button= new JButton();
		if(text != null)
			button.setText(text);
		for(ActionListener listener : listeners)
			button.addActionListener(listener);
		if(iconFile != null)
			button.setIcon( IconFactory.create(iconFile) );
		if(font != null)
			button.setFont(font);
		if(foreground != null)
			button.setForeground(foreground);
		if(background != null)
			button.setBackground(background);
		return button;
	}

	public static JButton createIconButton(String text, String iconFile, Font font, Color foregrond, ActionListener... listeners )
	{
		return createIconButton(text, iconFile, font, foregrond, null, listeners );
	}

	public static JButton createIconButton(String text, String iconFile, Font font, ActionListener... listeners )
	{
		return createIconButton(text, iconFile, font, VALUE_FG, listeners);
	}

	public static JButton createIconButton(String text, String iconFile, ActionListener... listeners )
	{
		return createIconButton(text, iconFile, VALUE_FONT, listeners);
	}

	public static JButton createIconButton(String iconFile, ActionListener... listeners )
	{
		return createIconButton(null, iconFile, listeners);
	}
	
	public static JButton createButton(String text, Font font, Color foreground, Color background, ActionListener... listeners )
	{
		return createIconButton(text, null, font, foreground, background, listeners);
	}

	public static JButton createButton(String text, Font font, Color foregrond, ActionListener... listeners )
	{
		return createButton(text, font, foregrond, null, listeners );
	}

	public static JButton createButton(String text, Font font, ActionListener... listeners )
	{
		return createButton(text, font, VALUE_FG, listeners);
	}

	public static JButton createButton(String text, ActionListener... listeners )
	{
		return createButton(text, VALUE_FONT, listeners);
	}

	
	
}
