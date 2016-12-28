package bn.blaszczyk.roseapp.view;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class ToolBar extends JPanel {


	public ToolBar(Iterable<ActionPack> actions)
	{
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		for(ActionPack actionPack : actions)
			for(Action action : actionPack)
				addButton(action);
	}
	
	private void addButton(Action action)
	{
		JButton button = new JButton(action);
		button.setFont(TOOL_FONT);
		add(button);
	}

}
