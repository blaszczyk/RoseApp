package bn.blaszczyk.roseapp.view.panels.crud;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JComponent;

import org.apache.logging.log4j.*;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.view.panels.AlignPanel;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.TitleButtonsPanel;
import bn.blaszczyk.roseapp.view.table.EntityTableBuilder;
import bn.blaszczyk.roseapp.view.tools.EntityComboBox;
import bn.blaszczyk.rosecommon.RoseException;
import bn.blaszczyk.rosecommon.tools.EntityUtils;
import bn.blaszczyk.rosecommon.controller.ModelController;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class FullEditPanel extends AlignPanel {
	
	private static final long serialVersionUID = 7493040089382377039L;

	private static final Logger LOGGER = LogManager.getLogger(FullEditPanel.class);

	private List<MediumEditPanel> mediumPanels = new ArrayList<>();
	private Map<Integer,EntityComboBox<Readable>> entityBoxes = new HashMap<>();
	
	private final ModelController modelController;
	private final GUIController guiController;
	private final Writable entity;
	private final Map<Integer, Integer> panelIndices = new TreeMap<>();

	public FullEditPanel( Writable entity, GUIController guiController )
	{
		super( H_SPACING );
		this.guiController = guiController;
		this.modelController = guiController.getModelController();
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
			MediumEditPanel fullPanel = new MediumEditPanel((Writable) entity.getEntityValueOne(index), guiController);
			mediumPanels.add(fullPanel);
			subPanel = TitleButtonsPanel.noBorder(entity.getEntityName(index), fullPanel);
			subPanel.addButton("Remove", "delete.png", e -> removeOneToOne(index,e));
		}
		else
		{
			subPanel = TitleButtonsPanel.withBorder(entity.getEntityName(index), null, BASIC_WIDTH, 0);
			subPanel.addButton("Add", "add.png", e -> setOneToOne(index,e));
		}
		return subPanel;		
	}
	private BasicEditPanel addBasicPanel( Writable entity )
	{	
		BasicEditPanel panel = new BasicEditPanel(entity, guiController);
		super.addPanel(panel);
		return panel;
	}
	
	private RosePanel addEntityTable( int index )
	{
		Set<? extends Readable> set = entity.getEntityValueMany(index);
		JComponent component = new EntityTableBuilder()
					.type(entity.getEntityClass(index))
					.entities(set)
					.behaviour(guiController.getBehaviour())
					.addButtonColumn("edit.png", e -> guiController.openEntityTab( e, true ))
//					.addButtonColumn("copy.png", e -> guiController.openEntityTab( modelController.createCopy((Writable) e), true ))
//					.addButtonColumn("delete.png", e -> guiController.delete((Writable) e))
					.buildWithFilterInScrollPane();
		TitleButtonsPanel sePanel = TitleButtonsPanel.withBorder(entity.getEntityName(index), component, BASIC_WIDTH, SUBTABLE_HEIGTH);
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
		TitleButtonsPanel subPanel = TitleButtonsPanel.withBorder( entity.getEntityName(index), component, BASIC_WIDTH, LBL_HEIGHT);
		if(hasEntityField)
			subPanel.addButton("Remove", "delete.png", e -> removeManyToOne(index,e) );
		else
			subPanel.addButton("Add", "add.png", e-> setManyToOne(index,e) );
		subPanel.addButton("New", "new.png", e -> addManyToOne(index,e));
		return subPanel;
	}
	
	private JComponent createEntityBox(int index)
	{
		List<? extends Readable> entitiesList;
		try
		{ 
			entitiesList = modelController.getEntities(entity.getEntityClass(index));
		}
		catch (RoseException e) 
		{
			String error = "Error filling dropdown box for " + entity.getEntityName(index);
			error(e, error);
			LOGGER.error(error, e);
			entitiesList = Collections.emptyList();
		}
		Readable[] entities = entitiesList.toArray(new Readable[entitiesList.size()]);
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
		TitleButtonsPanel subPanel = TitleButtonsPanel.withBorder( entity.getEntityName(index), createEntityBox(index), BASIC_WIDTH, LBL_HEIGHT);
		subPanel.addButton("Remove", "delete.png", ee -> removeManyToOne(index,ee) );
		setPanel(panelIndices.get(index),subPanel);
		notify(false,e);
	}

	private void removeManyToOne(int index, ActionEvent e)
	{
		LOGGER.debug("remove index " + index + " from:\r\n" + EntityUtils.toStringFull(entity));
		entityBoxes.put(index, null);
		try
		{
			modelController.update(entity,(Writable)entity.getEntityValueOne(index));
		}
		catch (RoseException re)
		{
			String error = "Error removing " + entity.getEntityName(index);
			error(re, error);
			LOGGER.error(error, re);
		}
		entity.setEntity(index, null);
		notify(false,e);
	}
	
	private void addManyToOne(int index, ActionEvent e)
	{
		try
		{
			Writable subEntity = (Writable) modelController.createNew(entity.getEntityClass(index));
			entity.setEntity(index, subEntity);
			modelController.update(entity,subEntity);
			TitleButtonsPanel subPanel = TitleButtonsPanel.withBorder( entity.getEntityName(index), createEntityBox(index), BASIC_WIDTH, LBL_HEIGHT);
			subPanel.addButton("Remove", "delete.png", ee -> removeManyToOne(index,ee) );
			setPanel(panelIndices.get(index),subPanel);
			guiController.openEntityTab(subEntity, true);
			notify(false,e);
		}
		catch (RoseException re) 
		{
			LOGGER.error("Unable to set entity at index " + index + " for entity:" + EntityUtils.toStringFull(entity), re);
			error(re,"Unable to set entity");
		}
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
			error(re,"Unable to set entity");
		}
	}
	
	private void removeOneToOne(int index, ActionEvent e)
	{
		LOGGER.debug("remove index " + index + " from:\r\n" + EntityUtils.toStringFull(entity));
		try
		{
			modelController.update(entity,(Writable)entity.getEntityValueOne(index));
		}
		catch (RoseException re)
		{
			String error = "Error removing " + entity.getEntityName(index);
			error(re, error);
			LOGGER.error(error, re);
		}
		entity.setEntity( index, null);
		setPanel( panelIndices.get(index), addOneToOnePanel(index));
		notify(false,e);
	}
	
	@Override
	public void save() throws RoseException
	{
		super.save();
		LOGGER.debug("saving entity:\r\n" + EntityUtils.toStringFull(entity));
		modelController.update(entity);
		for(Integer index : entityBoxes.keySet() )
			if(entityBoxes.get(index) != null)
			{
				Writable subEntity = (Writable)entityBoxes.get(index).getSelectedItem();
				entity.setEntity( index, subEntity );
				modelController.update(subEntity);
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
		{
			final Object selectedObject = entityBoxes.get(index).getSelectedItem();
			if(selectedObject instanceof Readable)
				if( EntityUtils.equals( (Readable)selectedObject, entity.getEntityValueOne(index) ) )
					return true;
		}
		return super.hasChanged();
	}
	
}
