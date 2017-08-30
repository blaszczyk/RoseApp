package bn.blaszczyk.roseapp.view.panels;

import javax.swing.JPanel;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.roseapp.view.Messenger;
import bn.blaszczyk.roseapp.view.RoseListener;

public interface RosePanel extends Messenger {
	public Object getShownObject();
	public int getFixWidth();
	public int getFixHeight();
	public JPanel getPanel();
	public boolean hasChanged();
	public void refresh();
	public void save() throws RoseException;
	public void addRoseListener(RoseListener listener);
	public void removeRoseListener(RoseListener listener);
}
