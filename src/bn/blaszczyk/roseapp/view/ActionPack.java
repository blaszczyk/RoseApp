package bn.blaszczyk.roseapp.view;

import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;

public interface ActionPack extends RoseListener, Iterable<Action> {
	public List<JMenu> getMenus();
	
}
