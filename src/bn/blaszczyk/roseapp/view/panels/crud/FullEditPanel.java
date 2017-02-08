package bn.blaszczyk.roseapp.view.panels.crud;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.RoseException;
import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.tools.EntityUtils;
import bn.blaszczyk.roseapp.view.panels.AlignPanel;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.TitleButtonsPanel;
import bn.blaszczyk.roseapp.view.table.EntityTableBuilder;
import bn.blaszczyk.roseapp.view.tools.EntityComboBox;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class FullEditPanel extends AlignPanel {
	
	private static final long serialVersionUID = 7493040089382377039L;

	private static final Logger LOGGER = Logger.getLogger(FullEditPanel.class);

	private List<MediumEditPanel> mediumPanels = new ArrayList<>();
	private Map<Integer,EntityComboBox<Readable>> entityBoxes = new HashMap<>();
	
	private final ModelController modelController;
	private final Writable entity;
	private final Map<Integer, Integer> panelIndices = new TreeMap<>();

	public FullEditPanel( Writable entity, ModelController modelController, GUIController guiController )
	{
		super(guiController, H_SPACING );
		this.modelController = modelController;
		this.entity = entity;
		setTitle( entity.getId() > 0 ? entity.getEntityName() + " " + entity.getId() : "new " + entity.getEntityName() );
		addBasicPanel(entity);
		for(int i = 0; i < entity.getEntityCount(); i++)
		{
			RosePanel panel = null;
			switch( entity.getRelationType(i))
			{
			case ONETOONE:
				panel = addOneToOnePanel(i);
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
	
	private RosePanel addOneToOnePanel(int index)
	{
		TitleButtonsPanel subPanel = null;
		boolean hasEntity = entity.getEntityValueOne(index) != null;
		if(hasEntity)
		{
			MediumEditPanel fullPanel = new MediumEditPanel((Writable) entity.getEntityValueOne(index),modelController, guiController);
			mediumPanels.add(fullPanel);
			subPanel = new TitleButtonsPanel(entity.getEntityName(index), fullPanel, true);
			subPanel.addButton("Remove", "delete.png", e -> removeOneToOne(index,e));
		}
		else
		{
			subPanel = new TitleButtonsPanel(entity.getEntityName(index), null, BASIC_WIDTH, 0, true);
			subPanel.addButton("Add", "add.png", e -> setOneToOne(index,e));
		}
		return subPanel;		
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
		Readable[] entities = new Readable[modelController.getEntites(entity.getEntityClass(index)).size()];
		modelController.getEntites(entity.getEntityClass(index)).toArray(entities);
		EntityComboBox<Readable> selectBox = new EntityComboBox<>(entities, BASIC_WIDTH, true);
		selectBox.setSelectedItem(entity.getEntityValueOne(index));
		selectBox.setFont(VALUE_FONT);
		selectBox.setForeground(VALUE_FG);
		selectBox.addItemListener(e -> notify(false,e));
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
		LOGGER.debug("remove index " + index + " from:\r\n" + EntityUtils.toStringFull(entity));
		entityBoxes.put(index, null);
		entity.setEntity(index, null);
		modelController.update(entity);
		notify(false,e);
	}
	
	private void setOneToOne(int index, ActionEvent e)
	{
		try
		{
			Writable subEntity = (Writable) modelController.createNew(entity.getEntityClass(index));
			entity.setEntity(index, subEntity);
			modelController.update(entity,subEntity);
			setPanel( panelIndices.get(index), addOneToOnePanel(index));
			notify(false,e);
		}
		catch (RoseException re) 
		{
			LOGGER.error("Unable to set entity at index " + index + " for entity:" + EntityUtils.toStringFull(entity), re);
			errorDialog(re,"Unable to set entity");
		}
	}
	
	private void removeOneToOne(int index, ActionEvent e)
	{
		LOGGER.debug("remove index " + index + " from:\r\n" + EntityUtils.toStringFull(entity));
		entity.setEntity( index, null);
		modelController.update(entity);
		setPanel( panelIndices.get(index), addOneToOnePanel(index));
		notify(false,e);
	}
	
	@Override
	public void save()
	{
		LOGGER.debug("saving entity:\r\n" + EntityUtils.toStringFull(entity));
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
	
}
