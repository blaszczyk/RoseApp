package bn.blaszczyk.roseapp.view.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.view.tools.EntityTableBuilder;
import bn.blaszczyk.roseapp.view.tools.EntityComboBox;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class FullEditPanel extends AlignPanel {


	private BasicEditPanel basicPanel;
	private List<FullEditPanel> fullPanels = new ArrayList<>();
	private Map<Integer,EntityComboBox<Readable>> entityBoxes = new HashMap<>();
	
	private ModelController modelController;
	private final Writable entity;
	private final Map<Integer, Integer> panelIndices = new TreeMap<>();
	
	private boolean changed = false;

	public FullEditPanel( Writable entity, ModelController modelController, GUIController guiController, boolean showTitle, ChangeListener listener )
	{
		this(entity, modelController, guiController, showTitle,true);
		setChangeListener(listener);
	}
	private FullEditPanel( Writable entity, ModelController modelController, GUIController guiController, boolean showTitle, boolean showOneToOne )
	{
		super(guiController, showOneToOne ?  H_SPACING : -H_SPACING );
		this.modelController = modelController;
		this.entity = entity;
		if(showTitle)
			setTitle( entity.getId() > 0 ? entity.getEntityName() + " " + entity.getId() : "new " + entity.getEntityName() );
		basicPanel = addBasicPanel(entity);
		for(int i = 0; i < entity.getEntityCount(); i++)
		{
			EntityPanel panel = null;
			switch( entity.getRelationType(i))
			{
			case ONETOONE:
				if(showOneToOne)
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
		realign();
	}

	private EntityPanel addOneToOnePanel(int index)
	{
		SubEntityPanel subPanel = null;
		boolean hasEntity = entity.getEntityValue(index) != null;
		if(hasEntity)
		{
			FullEditPanel fullPanel = new FullEditPanel((Writable) entity.getEntityValue(index),modelController, guiController,false, false);
			fullPanels.add(fullPanel);
			subPanel = new SubEntityPanel(entity.getEntityName(index), fullPanel);
			subPanel.addButton("Remove", "delete.png", e -> modelController.setEntityField(entity, index, null));
		}
		else
		{
			subPanel = new SubEntityPanel(entity.getEntityName(index), null, BASIC_WIDTH, 0);
			subPanel.addButton("Add", "add.png", e -> setOneToOne(index));
		}
		return subPanel;		
	}
	private BasicEditPanel addBasicPanel( Writable entity )
	{	
		BasicEditPanel panel = new BasicEditPanel(entity); 
		super.addPanel(panel);
		return panel;
	}
	
	private EntityPanel addEntityTable( int index )
	{
		@SuppressWarnings("unchecked")
		Set<? extends Readable> set = (Set<? extends Readable>) entity.getEntityValue(index);
		JComponent component = new EntityTableBuilder()
					.type(entity.getEntityClass(index))
					.width(BASIC_WIDTH)
					.heigth(SUBTABLE_HEIGTH)
					.entities(set)
					.addButtonColumn("edit.png", e -> guiController.openEntityTab( e, true ))
					.addButtonColumn("copy.png", e -> guiController.openEntityTab( modelController.createCopy((Writable) e), true ))
					.addButtonColumn("delete.png", e -> guiController.delete((Writable) e))
					.buildInScrollPane();
		SubEntityPanel sePanel = new SubEntityPanel(entity.getEntityName(index), component, BASIC_WIDTH, SUBTABLE_HEIGTH);
		sePanel.addButton("Add", "add.png", e -> guiController.addNew( entity, index ));
		return sePanel ;
	}
	
	private EntityPanel addManyToOnePanel( int index )
	{
		JComponent component = null;
		boolean hasEntityField = entity.getEntityValue(index) != null;
		if(entityBoxes.containsKey(index))
			component = entityBoxes.get(index);
		else if(hasEntityField)		
			component = createEntityBox(index);
		SubEntityPanel subPanel = new SubEntityPanel( entity.getEntityName(index), component, BASIC_WIDTH, LBL_HEIGHT);
		if(hasEntityField)
			subPanel.addButton("Remove", "delete.png", e -> removeManyToOne(index) );
		else
			subPanel.addButton("Add", "add.png", e-> setManyToOne(index) );
		return subPanel;
	}
	
	private JComponent createEntityBox(int index)
	{
		Readable[] entities = new Readable[modelController.getAllEntites(entity.getEntityClass(index)).size()];
		modelController.getAllEntites(entity.getEntityClass(index)).toArray(entities);
		EntityComboBox<Readable> selectBox = new EntityComboBox<>(entities, BASIC_WIDTH, true);
		selectBox.setSelectedItem(entity.getEntityValue(index));
		selectBox.setFont(VALUE_FONT);
		selectBox.setForeground(VALUE_FG);
		entityBoxes.put(index, selectBox);
		return selectBox;
	}
	
	private void setManyToOne(int index)
	{
		SubEntityPanel subPanel = new SubEntityPanel( entity.getEntityName(index), createEntityBox(index), BASIC_WIDTH, LBL_HEIGHT);
		subPanel.addButton("Remove", "delete.png", e -> removeManyToOne(index) );
		setPanel(panelIndices.get(index),subPanel);
		changed = true;
		realign();
	}

	private void removeManyToOne(int index)
	{
		entityBoxes.put(index, null);
		modelController.setEntityField(entity, index, null);
		realign();
	}
	
	private void setOneToOne(int index)
	{
		Writable subEntity = modelController.createNew(entity.getEntityClass(index));
		modelController.setEntityField(entity, index, subEntity);
		setPanel( panelIndices.get(index), addOneToOnePanel(index));
		changed = true;
		realign();
	}
	
	public void save(ModelController modelController)
	{
		basicPanel.save(modelController);
		for(FullEditPanel panel : fullPanels)
			panel.save(modelController);
		for(Integer index : entityBoxes.keySet() )
			if(entityBoxes.get(index) != null)
				modelController.setEntityField(entity, index, ( (Writable)entityBoxes.get(index).getSelectedItem() ) );
	}

	@Override
	public JPanel getPanel()
	{
		return this;
	}

	@Override
	public Object getShownObject()
	{
		return entity;
	}
	

	@Override
	public boolean hasChanged()
	{
		if(changed)
			return true;
		if(basicPanel.hasChanged())
			return true;
		for(FullEditPanel panel : fullPanels)
			if(panel.hasChanged())
				return true;
		for(Integer index : entityBoxes.keySet() )
			if( entityBoxes.get(index).getSelectedItem().equals( entity.getEntityValue(index) ) )
				return true;
		return false;
	}
	
	public void setChangeListener(ChangeListener l)
	{
		basicPanel.setChangeListener(l);
		for(FullEditPanel panel : fullPanels)
			panel.setChangeListener(l);
		for(Integer index : entityBoxes.keySet() )
			entityBoxes.get(index).addItemListener(e -> l.stateChanged(new ChangeEvent(entityBoxes.get(index))));
	}
}
