package bn.blaszczyk.roseapp.view.panels.input;

import javax.swing.JPanel;

import bn.blaszczyk.roseapp.view.RoseListener;

public interface InputPanel<T> {
	
	public T getValue();
	public void setValue( T value );
	public String getName();
	public JPanel getPanel();
	public boolean hasChanged();
	public boolean isInputValid();
	public void setRoseListener( RoseListener l);
	public void resetDefValue();
	public void setEnabled(boolean enabled);
	
}
