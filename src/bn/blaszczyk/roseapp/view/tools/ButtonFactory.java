package bn.blaszczyk.roseapp.view.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
public class ButtonFactory {
	
	
	public static JButton createIconButton(String iconFile, ActionListener... listeners )
	{	
		JButton button= new JButton();
		try
		{
			button.setIcon( new ImageIcon(ImageIO.read(ButtonFactory.class.getClassLoader().getResourceAsStream("bn/blaszczyk/roseapp/resources/" + iconFile))) );
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		for(ActionListener listener : listeners)
			button.addActionListener(listener);
		return button;
		
	}

	public static JButton createButton(String text, Font font, Color foreground, Color background, ActionListener... listeners )
	{
		JButton button= new JButton(text);
		for(ActionListener listener : listeners)
			button.addActionListener(listener);
		if(font != null)
			button.setFont(font);
		if(foreground != null)
			button.setForeground(foreground);
		if(background != null)
			button.setBackground(background);
		return button;
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
