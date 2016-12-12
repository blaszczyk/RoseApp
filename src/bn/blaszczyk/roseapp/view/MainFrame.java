package bn.blaszczyk.roseapp.view;

import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.view.panels.EntityPanel;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame{

	private JTabbedPane tabbedPane = new JTabbedPane();
	private Actions actions;
	
	public MainFrame(GUIController guiController, String title)
	{
		super(title);
		actions = new Actions(this, guiController);
		setLayout(new BorderLayout());
		
		ToolBar toolBar = new ToolBar(actions);
		add(toolBar,BorderLayout.PAGE_START);

		tabbedPane.addChangeListener(actions);
		add(tabbedPane,BorderLayout.CENTER);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);	
	}
	
	public int addTab( EntityPanel panel, String name, String iconFile)
	{
		tabbedPane.addTab(name, new JScrollPane(panel.getPanel()));
		int index = tabbedPane.getTabCount() - 1;
		JLabel tabLabel = new JLabel(name, SwingConstants.LEFT);
		try
		{
			tabLabel.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("bn/blaszczyk/roseapp/resources/" + iconFile))) );
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		tabLabel.setFont(TAB_FONT);
		tabLabel.setBounds(0, 0, 70, 20);
		tabbedPane.setTabComponentAt(index,tabLabel);
		tabbedPane.setSelectedIndex(index);
		return index;
	}
	
	public void replaceTab( int index, EntityPanel panel, String name, String iconFile )
	{
		tabbedPane.setComponentAt(index, new JScrollPane(panel.getPanel()));
		JLabel tabLabel = new JLabel(name,  new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("../resources/" + iconFile))), SwingConstants.LEFT);
		tabLabel.setFont(TAB_FONT);
		tabLabel.setBounds(0, 0, 70, 20);
		tabbedPane.setTabComponentAt(index, tabLabel);
		tabbedPane.setSelectedIndex(index);
		actions.stateChanged(new ChangeEvent(tabbedPane));
	}
	
	public EntityPanel getPanel(int index)
	{
		if(index < 0)
			return null;
		return (EntityPanel)((JScrollPane)tabbedPane.getComponentAt(index)).getViewport().getView();
	}
	
	public void setPanel(int index, EntityPanel panel)
	{
		tabbedPane.setComponentAt(index, new JScrollPane(panel.getPanel()));
	}
	
	public int getSelectedIndex()
	{
		return tabbedPane.getSelectedIndex();
	}
	
	public int getPanelCount()
	{
		return tabbedPane.getTabCount();
	}
	
	public void setSelectedIndex(int index)
	{
		tabbedPane.setSelectedIndex(index);
	}
	
	public EntityPanel getSelectedPanel()
	{
		return getPanel(getSelectedIndex());
	}

	public Actions getActions()
	{
		return actions;
	}

	public boolean hasChanged()
	{
		for(int i = 0; i < getPanelCount(); i++)
			if( getPanel(i).hasChanged() )
				return true;
		return false;
	}

	public void closeCurrent()
	{
		if(tabbedPane.getSelectedIndex() >= 0)
			tabbedPane.remove(tabbedPane.getSelectedIndex());
	}

	public void removePanel(int index)
	{
		tabbedPane.remove(index);
	}
	
	
}
