package bn.blaszczyk.roseapp.view;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.view.factories.IconFactory;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements RoseListener, Iterable<RosePanel>{

	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final List<ActionPack> actionPacks;
	
	public MainFrame(GUIController guiController, String title, List<ActionPack> actionPacks)
	{
		super(title);
		this.actionPacks = actionPacks;
		setLayout(new BorderLayout());
		add(new ToolBar(actionPacks),BorderLayout.PAGE_START);
		
		JMenuBar menuBar = new JMenuBar();
		for(ActionPack actionPack : actionPacks)
			for(JMenu menu : actionPack.getMenus())
				menuBar.add(menu);
		setJMenuBar(menuBar);
			
		tabbedPane.addChangeListener( e -> notifyActions() );
		add(tabbedPane,BorderLayout.CENTER);		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				guiController.exit();
			}
		});
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
		if(getSelectedPanel() != null)
			getSelectedPanel().refresh();
	}

	public int addTab( RosePanel panel, String name, String iconFile)
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
	
	public void replaceTab( int index, RosePanel panel, String name, String iconFile )
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
	
	public RosePanel getPanel(int index)
	{
		if(index < 0)
			return null;
		return (RosePanel)((JScrollPane)tabbedPane.getComponentAt(index)).getViewport().getView();
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
	
	public RosePanel getSelectedPanel()
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

	@Override
	public Iterator<RosePanel> iterator()
	{
		return new Iterator<RosePanel>(){
			private int index = 0;
			@Override
			public boolean hasNext()
			{
				return index < getPanelCount();
			}
			@Override
			public RosePanel next()
			{
				return getPanel(index++);
			}
		};
	}

	public void removePanel(RosePanel panel)
	{
		for(int i = 0; i < getPanelCount(); i++)
			if(getPanel(i).equals(panel))
				removePanel(i);
	}
	
}
