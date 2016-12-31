package bn.blaszczyk.roseapp.view.panels.crud;

import javax.swing.JScrollPane;

import bn.blaszczyk.roseapp.controller.ModelController;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;
import bn.blaszczyk.roseapp.view.table.EntityTableBuilder;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class FullListPanel extends AbstractRosePanel {
	
	private Class<?> type;
	
	public FullListPanel(ModelController modelController, GUIController guiController, Class<?> type)
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
//				.addButtonColumn("copy.png", e -> guiController.openEntityTab( modelController.createCopy((Writable) e), true ))
				.buildInScrollPane();
		scrollPane.setBounds(H_SPACING, V_SPACING, FULL_TABLE_WIDTH, PANEL_HEIGHT);
		add(scrollPane);
	}

	@Override
	public Object getShownObject()
	{
		return type;
	}
}
