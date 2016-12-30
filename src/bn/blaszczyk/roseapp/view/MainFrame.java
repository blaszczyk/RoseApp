package bn.blaszczyk.roseapp.view;

import java.awt.*;
import javax.swing.*;

import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.view.factories.IconFactory;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;
import bn.blaszczyk.roseapp.view.panels.EntityPanel;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

import java.util.List;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements RoseListener{

	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final List<ActionPack> actionPacks;
	
	public MainFrame(GUIController guiController, String title, List<ActionPack> actionPacks)
	{
		super(title);
		this.actionPacks = actionPacks;
		setLayout(new BorderLayout());
		add(new ToolBar(actionPacks),BorderLayout.PAGE_START);
			
		tabbedPane.addChangeListener( e -> notifyActions() );
		add(tabbedPane,BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void showFrame()
	{
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setVisible(true);	
	}
	
	private void notifyActions()
	{
		RoseEvent e = new RoseEvent(tabbedPane);
		for(ActionPack a : actionPacks)
			a.notify(e);
	}

	public int addTab( EntityPanel panel, String name, String iconFile)
	{
		panel.addRoseListener(this);
		JPanel jPanel = panel.getPanel();
		jPanel.setPreferredSize(new Dimension(panel.getFixWidth(), panel.getFixHeight()));
		tabbedPane.addTab(name, new JScrollPane(jPanel));
		int index = tabbedPane.getTabCount() - 1;
		JLabel tabLabel = LabelFactory.createLabel(name, IconFactory.create(iconFile), TAB_FONT );
		tabLabel.setBounds(0, 0, 70, 20);
		tabbedPane.setTabComponentAt(index,tabLabel);
		tabbedPane.setSelectedIndex(index);
		return index;
	}
	
	public void replaceTab( int index, EntityPanel panel, String name, String iconFile )
	{
		panel.addRoseListener(this);
		JPanel jPanel = panel.getPanel();
		jPanel.setPreferredSize(new Dimension(panel.getFixWidth(), panel.getFixHeight()));
		tabbedPane.setComponentAt(index, new JScrollPane(jPanel));
		JLabel tabLabel = LabelFactory.createLabel(name, IconFactory.create(iconFile), TAB_FONT);
		tabLabel.setBounds(0, 0, 70, 20);
		tabbedPane.setTabComponentAt(index, tabLabel);
		tabbedPane.setSelectedIndex(index);
		notifyActions();
	}
	
	public EntityPanel getPanel(int index)
	{
		if(index < 0)
			return null;
		return (EntityPanel)((JScrollPane)tabbedPane.getComponentAt(index)).getViewport().getView();
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

	public boolean hasChanged()
	{
		for(int i = 0; i < getPanelCount(); i++)
			if(getPanel(i) != null && getPanel(i).hasChanged() )
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

	@Override
	public void notify(RoseEvent e)
	{
		for(int i = 0; i < getPanelCount(); i++)
		{
			Color color = getPanel(i).hasChanged() ? Color.RED : Color.BLACK;
			tabbedPane.getTabComponentAt(i).setForeground( color );
		}		
	}	
	
}
