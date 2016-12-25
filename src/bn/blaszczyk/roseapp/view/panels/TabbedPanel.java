package bn.blaszczyk.roseapp.view.panels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import bn.blaszczyk.roseapp.view.factories.LabelFactory;
import bn.blaszczyk.roseapp.view.panels.AbstractPanelContainer;
import bn.blaszczyk.roseapp.view.panels.EntityPanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class TabbedPanel extends AbstractPanelContainer {
	
	private final JTabbedPane tabbedPane = new JTabbedPane();
	

	public TabbedPanel()
	{
		setLayout(new BorderLayout());
		tabbedPane.setFont(HEADER_FONT);
		add(tabbedPane,BorderLayout.CENTER);
	}
	
	public void addTab(String name, EntityPanel panel)
	{
		tabbedPane.add(name, panel.getPanel());
		panel.addActionListener(changeListener);
		JLabel tabLabel =  LabelFactory.createLabel(name, TAB_FONT);
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabLabel);		
	}

	@Override
	protected int getPanelCount()
	{
		return tabbedPane.getTabCount();
	}

	@Override
	protected EntityPanel getPanel(int index)
	{
		return (EntityPanel) tabbedPane.getComponentAt(index);
	}
	
}
