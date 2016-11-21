package bn.blaszczyk.roseapp.view.inputpanels;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bn.blaszczyk.roseapp.view.ThemeConstants;

@SuppressWarnings("serial")
public abstract class AbstractInputPanel<T> extends JPanel implements InputPanel<T>, ThemeConstants, KeyListener {
	
	private final JLabel label;
	protected final JTextField textField = new JTextField();
	private ChangeListener listener = null;
	
	public AbstractInputPanel( String name )
	{
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		
		label =  new JLabel( name + ": ", SwingConstants.RIGHT);
		label.setFont(PROPERTY_FONT);
		label.setOpaque(true);
		label.setForeground(PROPERTY_FG);
		label.setBackground(PROPERTY_BG);
		label.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
		add(label);
		
		textField.setBounds( PROPERTY_WIDTH + H_SPACING , 0, VALUE_WIDTH, LBL_HEIGHT);
//		lblFileName.setBounds( PROPERTY_WIDTH + H_SPACING , 0, VALUE_WIDTH - LBL_HEIGHT - H_SPACING, LBL_HEIGHT);
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
	public void setChangeListener(ChangeListener l)
	{
		this.listener = l;
	}

	@Override
	public JPanel getPanel()
	{
		return this;
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
			listener.stateChanged(new ChangeEvent(this));
	}
	
	
	
}
