package bn.blaszczyk.roseapp.view.panels.input;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

public interface InputPanel<T> {
	
	public T getValue();
	public void setValue( T value );
	public String getName();
	public JPanel getPanel();
	public boolean hasChanged();
	public boolean isInputValid();
	public void setChangeListener( ChangeListener l);
	
}
