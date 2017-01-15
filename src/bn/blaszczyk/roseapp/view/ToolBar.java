package bn.blaszczyk.roseapp.view;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

import java.awt.Dimension;

@SuppressWarnings("serial")
public class ToolBar extends JPanel {

	private static final Dimension GAP_DIMENSION = new Dimension(H_SPACING, V_SPACING);

	public ToolBar(Iterable<ActionPack> actions)
	{
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		for(ActionPack actionPack : actions)
		{
			for(Action action : actionPack)
				addButton(action);
			add(Box.createRigidArea(GAP_DIMENSION));
		}
	}
	
	private void addButton(Action action)
	{
		JButton button = new JButton(action);
//		button.setFont(TOOL_FONT);
		button.setText(null);
		add(button);
	}

}
