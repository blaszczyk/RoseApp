package bn.blaszczyk.roseapp.view.panels;

import javax.swing.JPanel;

public interface MyPanel {
	public Object getShownObject();
	public int getWidth();
	public int getHeight();
	public JPanel getPanel();
	public boolean hasChanged();
}
