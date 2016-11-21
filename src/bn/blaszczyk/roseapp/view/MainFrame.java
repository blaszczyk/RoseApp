package bn.blaszczyk.roseapp.view;

import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.view.panels.MyPanel;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ThemeConstants {

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
//		setSize( MF_WIDTH, MF_HEIGTH );
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);	
	}
	
	public int addTab( Component component, String name, String iconFile)
	{
		tabbedPane.addTab(name, component);
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
		tabLabel.setFont(new Font("Arial",Font.PLAIN, 18));
		tabLabel.setBounds(0, 0, 70, 20);
		tabbedPane.setTabComponentAt(index,tabLabel);
		tabbedPane.setSelectedIndex(index);
		return index;
	}
	
	public void replaceTab( int index, Component component, String name, String iconFile )
	{
		tabbedPane.setComponentAt(index, component);
		JLabel tabLabel = new JLabel(name,  new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("../resources/" + iconFile))), SwingConstants.LEFT);
		tabLabel.setFont(new Font("Arial",Font.PLAIN, 18));
		tabLabel.setBounds(0, 0, 70, 20);
		tabbedPane.setTabComponentAt(index, tabLabel);
		tabbedPane.setSelectedIndex(index);
		actions.stateChanged(new ChangeEvent(tabbedPane));
	}
	
	public JTabbedPane getTabbedPane()
	{
		return tabbedPane;
	}

	public Actions getActions()
	{
		return actions;
	}

	public boolean hasChanged()
	{
		for( Component c : tabbedPane.getComponents())
			if( c instanceof MyPanel )
				if( ((MyPanel)c).hasChanged() )
					return true;
		return false;
	}
	
	
}
