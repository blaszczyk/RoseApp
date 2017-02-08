package bn.blaszczyk.roseapp.view.panels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import bn.blaszczyk.roseapp.view.factories.LabelFactory;
import bn.blaszczyk.roseapp.view.panels.AbstractPanelContainer;
import bn.blaszczyk.roseapp.view.panels.RosePanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class TabbedPanel extends AbstractPanelContainer<RosePanel> {

	private static final long serialVersionUID = 8283760563797839473L;
	
	private final JTabbedPane tabbedPane = new JTabbedPane();
	

	public TabbedPanel()
	{
		setLayout(new BorderLayout());
		tabbedPane.setFont(HEADER_FONT);
		add(tabbedPane,BorderLayout.CENTER);
	}
	
	public void addTab(String name, RosePanel panel)
	{
		tabbedPane.add(name, panel.getPanel());
		panel.addRoseListener(this);
		JLabel tabLabel =  LabelFactory.createLabel(name, TAB_FONT);
		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabLabel);		
	}

	@Override
	public int getPanelCount()
	{
		return tabbedPane.getTabCount();
	}

	@Override
	public RosePanel getPanel(int index)
	{
		return (RosePanel) tabbedPane.getComponentAt(index);
	}
	
}
