package bn.blaszczyk.roseapp.view.panels;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.controller.FullModelController;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.view.ThemeConstants;
import bn.blaszczyk.roseapp.view.tools.EntityTableBuilder;

@SuppressWarnings("serial")
public class FullListPanel extends JPanel implements ThemeConstants, MyPanel {
	
	private Class<?> type;
	
	public FullListPanel(FullModelController modelController, GUIController guiController, Class<?> type)
	{
		this.type = type;
		setLayout(null);
		JScrollPane scrollPane = new EntityTableBuilder()
				.type(type)
				.width(FULL_TABLE_WIDTH)
				.heigth(PANEL_HEIGHT)
				.entities(modelController.getAllEntites(type))
				.addButtonColumn("view.png", e -> guiController.openEntityTab( e, false ))
				.addButtonColumn("edit.png", e -> guiController.openEntityTab( e, true ))
				.addButtonColumn("copy.png", e -> guiController.openEntityTab( modelController.createCopy((Writable) e), true ))
				.buildInScrollPane();
		scrollPane.setBounds(H_SPACING, V_SPACING, FULL_TABLE_WIDTH, PANEL_HEIGHT);
		add(scrollPane);
	}

	@Override
	public Object getShownObject()
	{
		return type;
	}

	@Override
	public JPanel getPanel()
	{
		return this;
	}

	@Override
	public boolean hasChanged()
	{
		return false;
	}
}
