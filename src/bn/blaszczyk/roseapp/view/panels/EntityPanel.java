package bn.blaszczyk.roseapp.view.panels;

import javax.swing.JPanel;

import bn.blaszczyk.roseapp.controller.ModelController;

public interface EntityPanel {
	public Object getShownObject();
	public int getFixWidth();
	public int getFixHeight();
	public JPanel getPanel();
	public boolean hasChanged();
	public void refresh();
	public void save(ModelController controller);
}
