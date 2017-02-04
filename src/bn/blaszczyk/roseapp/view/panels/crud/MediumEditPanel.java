package bn.blaszczyk.roseapp.view.panels.crud;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JComponent;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.view.panels.AlignPanel;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.TitleButtonsPanel;
import bn.blaszczyk.roseapp.view.table.EntityTableBuilder;
import bn.blaszczyk.roseapp.view.tools.EntityComboBox;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class MediumEditPanel extends AlignPanel {

	private Map<Integer,EntityComboBox<Readable>> entityBoxes = new HashMap<>();
	
	private final ModelController modelController;
	private final Writable entity;
	private final Map<Integer, Integer> panelIndices = new TreeMap<>();

	public MediumEditPanel( Writable entity, ModelController modelController, GUIController guiController )
	{
		super(guiController, 0 );
		this.modelController = modelController;
		this.entity = entity;
		setBackground(BASIC_PNL_BACKGROUND);
		addBasicPanel(entity);
		for(int i = 0; i < entity.getEntityCount(); i++)
		{
			RosePanel panel = null;
			switch( entity.getRelationType(i))
			{
			case ONETOONE:
				break;
			case MANYTOMANY:
			case ONETOMANY:
				panel = addEntityTable( i );
				break;
			case MANYTOONE:
				panel = addManyToOnePanel( i );
				break;
			}	
			int panelIndex = super.addPanel(panel);
			panelIndices.put(i, panelIndex);
		}
		super.registerRoseListener();
		refresh();
	}
	
	private BasicEditPanel addBasicPanel( Writable entity )
	{	
		BasicEditPanel panel = new BasicEditPanel(entity,modelController);
		super.addPanel(panel);
		return panel;
	}
	
	private RosePanel addEntityTable( int index )
	{
		Set<? extends Readable> set = entity.getEntityValueMany(index);
		JComponent component = new EntityTableBuilder()
					.type(entity.getEntityClass(index))
					.entities(set)
					.addButtonColumn("edit.png", e -> guiController.openEntityTab( e, true ))
//					.addButtonColumn("copy.png", e -> guiController.openEntityTab( modelController.createCopy((Writable) e), true ))
//					.addButtonColumn("delete.png", e -> guiController.delete((Writable) e))
					.buildWithFilterInScrollPane();
		TitleButtonsPanel sePanel = new TitleButtonsPanel(entity.getEntityName(index), component, BASIC_WIDTH, SUBTABLE_HEIGTH,false);
		sePanel.addButton("Add", "add.png", e -> guiController.addNew( entity, index ));
		return sePanel ;
	}
	
	private RosePanel addManyToOnePanel( int index )
	{
		JComponent component = null;
		boolean hasEntityField = entity.getEntityValueOne(index) != null;
		if(entityBoxes.containsKey(index))
			component = entityBoxes.get(index);
		else if(hasEntityField)		
			component = createEntityBox(index);
		TitleButtonsPanel subPanel = new TitleButtonsPanel( entity.getEntityName(index), component, BASIC_WIDTH, LBL_HEIGHT,false);
		if(hasEntityField)
			subPanel.addButton("Remove", "delete.png", e -> removeManyToOne(index,e) );
		else
			subPanel.addButton("Add", "add.png", e-> setManyToOne(index,e) );
		return subPanel;
	}
	
	private JComponent createEntityBox(int index)
	{
		Readable[] entities = new Readable[modelController.getAllEntites(entity.getEntityClass(index)).size()];
		modelController.getAllEntites(entity.getEntityClass(index)).toArray(entities);
		EntityComboBox<Readable> selectBox = new EntityComboBox<>(entities, BASIC_WIDTH, true);
		selectBox.setSelectedItem(entity.getEntityValueOne(index));
		selectBox.setFont(VALUE_FONT);
		selectBox.setForeground(VALUE_FG);
		selectBox.addItemListener(ee -> notify(false,ee));
		entityBoxes.put(index, selectBox);
		return selectBox;
	}
	
	private void setManyToOne(int index, ActionEvent e)
	{
		TitleButtonsPanel subPanel = new TitleButtonsPanel( entity.getEntityName(index), createEntityBox(index), BASIC_WIDTH, LBL_HEIGHT,false);
		subPanel.addButton("Remove", "delete.png", ee -> removeManyToOne(index,ee) );
		setPanel(panelIndices.get(index),subPanel);
		notify(false,e);
	}

	private void removeManyToOne(int index, ActionEvent e)
	{
		entityBoxes.put(index, null);
		entity.setEntity(index, null);
		modelController.update(entity);
		notify(false,e);
	}
	
	@Override
	public void save()
	{
		super.save();
		for(Integer index : entityBoxes.keySet() )
			if(entityBoxes.get(index) != null)
			{
				Writable subEntity = (Writable)entityBoxes.get(index).getSelectedItem();
				entity.setEntity( index, subEntity );
				modelController.update(entity,subEntity);
			}
	}

	@Override
	public Object getShownObject()
	{
		return entity;
	}
	

	@Override
	public boolean hasChanged()
	{
		for(Integer index : entityBoxes.keySet() )
			if( entityBoxes.get(index).getSelectedItem().equals( entity.getEntityValueOne(index) ) )
				return true;
		return super.hasChanged();
	}
	
}
