package bn.blaszczyk.roseapp.view.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
public class ButtonFactory {

	public static JButton createButton(String text, Font font, Color foreground, Color background, ActionListener... listeners )
	{
		JButton textField= new JButton(text);
		for(ActionListener listener : listeners)
			textField.addActionListener(listener);
		if(font != null)
			textField.setFont(font);
		if(foreground != null)
			textField.setForeground(foreground);
		if(background != null)
			textField.setBackground(background);
		return textField;
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
