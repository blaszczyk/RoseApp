package bn.blaszczyk.roseapp.view.panels;

import javax.swing.JPanel;

public interface MyPanel {
	public Object getShownObject();
	public int getFixWidth();
	public int getFixHeight();
	public JPanel getPanel();
	public boolean hasChanged();
	public void refresh();
}
