package bn.blaszczyk.roseapp.view.panels.crud;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import bn.blaszczyk.roseapp.controller.ModelController;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;
import bn.blaszczyk.roseapp.view.table.EntityTable;
import bn.blaszczyk.roseapp.view.table.EntityTableBuilder;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class FullListPanel extends AbstractRosePanel {
	
	private Class<?> type;
	private EntityTable table;
	
	public FullListPanel(ModelController modelController, GUIController guiController, Class<?> type)
	{
		this.type = type;
		setLayout(null);
		EntityTableBuilder builder = new EntityTableBuilder();
		JPanel scrollPane = builder
				.type(type)
				.entities(modelController.getEntites(type))
				.selectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
				.addButtonColumn("view.png", e -> guiController.openEntityTab( e, false ))
				.addButtonColumn("edit.png", e -> guiController.openEntityTab( e, true ))
//				.addButtonColumn("copy.png", e -> guiController.openEntityTab( modelController.createCopy((Writable) e), true ))
				.buildWithFilterInScrollPane();
		this.table = builder.getTable();
		scrollPane.setBounds(H_SPACING, V_SPACING, FULL_TABLE_WIDTH, PANEL_HEIGHT);
		add(scrollPane);
	}
	
	public EntityTable getTable()
	{
		return table;
	}

	@Override
	public Object getShownObject()
	{
		return type;
	}

	@Override
	public int getFixWidth()
	{
		return FULL_TABLE_WIDTH;
	}

	@Override
	public int getFixHeight()
	{
		return PANEL_HEIGHT;
	}
	
	@Override
	public void refresh()
	{
		table.resetSource();
		table.revalidate();
		table.repaint();
		super.refresh();
	}
	
	
}
