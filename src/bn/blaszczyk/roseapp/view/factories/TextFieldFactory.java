package bn.blaszczyk.roseapp.view.factories;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
public class TextFieldFactory {
	
	private static final KeyListener FORCE_INTEGER_LISTENER = new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent e)
		{
			char c = e.getKeyChar();
			if (!Character.isISOControl(c) && !Character.isDigit(c) && c!='-')
			{
				Toolkit.getDefaultToolkit().beep();
				e.consume();
			}
		}
	};

	public static JTextField createTextField(String text, ActionListener listener, Font font, Color foreground, Color background )
	{
		JTextField textField= new JTextField(text);
		if(listener != null)
			textField.addActionListener(listener);
		if(font != null)
			textField.setFont(font);
		if(foreground != null)
			textField.setForeground(foreground);
		if(background != null)
			textField.setBackground(background);
		return textField;
	}

	public static JTextField createTextField(String text, ActionListener listener, Font font, Color foregrond )
	{
		return createTextField(text, listener, font, foregrond, Color.WHITE );
	}

	public static JTextField createTextField(String text, ActionListener listener, Font font )
	{
		return createTextField(text, listener, font, VALUE_FG);
	}

	public static JTextField createTextField(String text, ActionListener listener )
	{
		return createTextField(text, listener, VALUE_FONT);
	}

	public static JTextField createTextField(String text)
	{
		return createTextField(text,null);
	}
	
	public static JTextField createIntegerField(int value, ActionListener listener, Font font, Color foreground, Color background )
	{
		JTextField textField= createTextField("" + value, listener, font, foreground, background);
		textField.addKeyListener(FORCE_INTEGER_LISTENER);
		return textField;
	}

	public static JTextField createIntegerField(int value, ActionListener listener, Font font, Color foregrond )
	{
		return createIntegerField(value, listener, font, foregrond, Color.WHITE );
	}

	public static JTextField createIntegerField(int value, ActionListener listener, Font font )
	{
		return createIntegerField(value, listener, font, VALUE_FG);
	}

	public static JTextField createIntegerField(int value, ActionListener listener )
	{
		return createIntegerField(value, listener, VALUE_FONT);
	}

	public static JTextField createIntegerField(int value)
	{
		return createIntegerField(value,null);
	}
	
	
}
