package bn.blaszczyk.roseapp.view;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class ToolBar extends JPanel {


	public ToolBar(Actions actions)
	{
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		addButton("Start", "start.png", actions.getActnStart());
		addButton("New", "new.png", actions.getActnNew());
		addButton("Edit", "edit.png", actions.getActnEdit() );
		addButton("View", "view.png", actions.getActnView() );
		addButton("Save", "save.png", actions.getActnSave());
		addButton("Save All", "saveall.png", actions.getActnSaveAll());
		addButton("Copy", "copy.png", actions.getActnCopy());
		addButton("Delete", "delete.png", actions.getActnDelete());
		addButton("Close", "close.png", actions.getActnClose() );
		addButton("Close All", "closeall.png", actions.getActnCloseAll() );
		addButton("Settings", "settings.png", actions.getActnSettings() );
	}
	
	private JButton addButton(String text, String iconFile, Action action)
	{
		JButton button = new JButton();
		button.setFont(TOOL_FONT);
		button.setAction(action);
		button.setText(text);
		try
		{
			button.setIcon( new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("bn/blaszczyk/roseapp/resources/" + iconFile))) );
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		add(button);
		return button;
	}

}
