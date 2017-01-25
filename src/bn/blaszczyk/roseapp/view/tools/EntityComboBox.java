package bn.blaszczyk.roseapp.view.tools;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class EntityComboBox<T> extends JComboBox<T> implements MouseWheelListener, KeyListener, FocusListener
{


	/*
	 * Variables
	 */
	private final boolean editable;
	
	private int charCounter = 0;
	private char selectChar = '.';
	
	private T[] items;
	
	private final JTextField inputField = (JTextField)getEditor().getEditorComponent();
	
	/*
	 * Constructors
	 */
	@SuppressWarnings("unchecked")
	public EntityComboBox(List<T> tList, int boxWidth, boolean editable)
	{
		this( (T[]) new Object[0], boxWidth,  editable);
		for(T t : tList)
			addItem(t);
		this.items = toArray(tList);
	}
	
	public EntityComboBox(T[] tArray, int boxWidth, boolean editable)
	{
		super(tArray);
		this.editable = editable;
		this.items = tArray;
		setMaximumSize(new Dimension(boxWidth,LBL_HEIGHT));
		setMinimumSize(new Dimension(boxWidth,LBL_HEIGHT));
		setFont(VALUE_FONT);
		inputField.setOpaque(false);
		if(!editable)
			setRenderer(new DefaultListCellRenderer(){
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value,
						int index, boolean isSelected, boolean cellHasFocus) {
					JComponent result = (JComponent)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					result.setOpaque(false);
					return result;
				}});
		addMouseWheelListener(this);
		addKeyListener(this);
		addFocusListener(this);
		setInheritsPopupMenu(true);
		setEditable(editable);
		if(editable)
			inputField.addKeyListener(this);
	}
	
	
	/*
	 * Special Methods
	 */
	public void repopulateBox(T[] items)
	{
		String input = "";
		int caret = 0;
		int selectedIndex = getSelectedIndex();
		if(editable)
		{
			input = inputField.getText();
			caret = inputField.getCaretPosition();
		}
		ActionListener[] listeners = getActionListeners();
		for(ActionListener listener : listeners)
			removeActionListener(listener);
		
		removeAllItems();
		for(T t : items)
			addItem(t);

		if(selectedIndex < getItemCount())
			setSelectedIndex(selectedIndex);
		else
			setSelectedIndex(getItemCount() - 1);
		if(editable)
		{
			inputField.setText(input);
			inputField.setCaretPosition(caret);
		}
		for(ActionListener listener : listeners)
			addActionListener(listener);
	}

	public void moveSelection(int steps)
	{
		if(getItemCount() == 0)
			return;
		int newIndex = getSelectedIndex() + steps;
		if( newIndex < 0 )
			newIndex = getItemCount()-1;
		else if( newIndex >= getItemCount())
			newIndex = 0;
		setSelectedIndex( newIndex );
		inputField.selectAll();
	}
	
	/*
	 * Internal Methods
	 */
	private void selectByChar(char c)
	{
		boolean hasChar = false;
		if( c == selectChar )
			charCounter++;
		else
		{
			charCounter = 0;
			selectChar = c;
		}
		int charCounterTmp = charCounter;
		for(int i = 0; i < getItemCount(); i++)
		{
			Object o = getItemAt(i);
			String name = o.toString();
			if(name.toLowerCase().startsWith( "" + Character.toLowerCase(c) ))
			{
				hasChar = true;
				if(charCounterTmp > 0)
				{
					charCounterTmp--;
					continue;
				}
				setSelectedIndex(i);
				return;
			}
		}
		charCounter = charCounterTmp - 1;
		if(hasChar)
			selectByChar(c);
	}
	
	@SuppressWarnings("unchecked")
	private T[] toArray(List<T> tList)
	{
		return tList.toArray((T[]) new Object[tList.size()]);
	}

	/*
	 * MouswWheelListener Method
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int steps = (int) (4 * e.getPreciseWheelRotation());
		moveSelection(steps);
	}

	/*
	 * KeyListener Methods
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
		case KeyEvent.VK_DOWN:
			moveSelection(1);
			e.consume();
			break;
		case KeyEvent.VK_UP:
			moveSelection(-1);
			e.consume();
			break;
		case KeyEvent.VK_RIGHT:
			setPopupVisible(true);
			break;
		case KeyEvent.VK_ENTER:
		case KeyEvent.VK_LEFT:
			setPopupVisible(false);
			e.consume();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		e.consume();
		char keyChar = e.getKeyChar();
		if(!editable)
			return;
		if( !Character.isISOControl(keyChar) && !Character.isDigit(keyChar) && !Character.isAlphabetic(keyChar) )
			return;		
		List<T> newItems = new ArrayList<>();
		for(T t : items)
			if(t.toString().toLowerCase().contains(inputField.getText().toLowerCase()))
				newItems.add(t);
		repopulateBox(toArray(newItems));
		setPopupVisible(true);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		char keyChar = e.getKeyChar();
		if(editable)
			return;
		if(e.getModifiers() == InputEvent.ALT_DOWN_MASK)
			return;
		if(Character.isAlphabetic(keyChar) || Character.isDigit(keyChar))
			selectByChar(Character.toLowerCase(keyChar));
		requestFocusInWindow();
	}
	
	/*
	 * Focus Listener Methods
	 */
	
	@Override
	public void focusLost(FocusEvent e)
	{
	}
	
	@Override
	public void focusGained(FocusEvent e)
	{
		inputField.selectAll();
	}
	
}
