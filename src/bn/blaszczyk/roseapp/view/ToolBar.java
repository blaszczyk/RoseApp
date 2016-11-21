package bn.blaszczyk.roseapp.view;

import java.awt.Font;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ToolBar extends JPanel implements ThemeConstants {


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
	}
	
	private JButton addButton(String text, String iconFile, Action action)
	{
		JButton button = new JButton();
		button.setFont(new Font("Arial", Font.PLAIN, 16));
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
//		button.setIcon( new ImageIcon(getClass().getResource("../resources/" + iconFile)) );
		add(button);
		return button;
	}

}
