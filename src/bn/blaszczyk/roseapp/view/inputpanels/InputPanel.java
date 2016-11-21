package bn.blaszczyk.roseapp.view.inputpanels;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import bn.blaszczyk.roseapp.view.ThemeConstants;

public interface InputPanel<T> extends ThemeConstants{
	
	public T getValue();
	public void setValue( T value );
	public String getName();
	public JPanel getPanel();
	public boolean hasChanged();
	public boolean isInputValid();
	public void setChangeListener( ChangeListener l);
	
}
