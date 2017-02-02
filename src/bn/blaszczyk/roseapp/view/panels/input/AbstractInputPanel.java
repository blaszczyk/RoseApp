package bn.blaszczyk.roseapp.view.panels.input;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.RoseListener;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public abstract class AbstractInputPanel<T> extends JPanel implements InputPanel<T>, KeyListener {
	
	protected T defValue;
	
	private final JLabel label;
	protected final JTextField textField = new JTextField();
	private RoseListener listener = null;
	
	public AbstractInputPanel( String name, T defValue )
	{
		this.defValue = defValue;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		
		label = LabelFactory.createLabel( Messages.get(name) + ": ", PROPERTY_FONT, PROPERTY_FG, SwingConstants.RIGHT);
		label.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
		add(label);
		
		textField.setBounds( PROPERTY_WIDTH + H_SPACING , 0, VALUE_WIDTH, LBL_HEIGHT);
		textField.setFont(VALUE_FONT);
		textField.setOpaque(true);
		textField.setForeground(VALUE_FG);
		add(textField);
		textField.addKeyListener(this);
	}
		
	@Override
	public String getName()
	{
		return label.getText();
	}	
	
	@Override
	public void setRoseListener(RoseListener l)
	{
		this.listener = l;
	}

	@Override
	public JPanel getPanel()
	{
		return this;
	}

	@Override
	public boolean hasChanged()
	{
		return !defValue.equals(getValue()) ;
	}

	@Override
	public void resetDefValue()
	{
		defValue = getValue();
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		textField.setEnabled(enabled);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if(!isInputValid())
			textField.setForeground(Color.RED);
		else if(hasChanged())
			textField.setForeground(Color.BLACK);
		else
			textField.setForeground(Color.DARK_GRAY);
		if(listener != null)
			listener.notify(new RoseEvent(this,true,e));
		textField.requestFocusInWindow();
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " value = " + String.valueOf(getValue());
	}
	
}
