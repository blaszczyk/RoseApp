package bn.blaszczyk.roseapp.view.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public FullEditPanel( Writable entity, ModelController modelController, GUIController guiController, boolean showTitle, ChangeListener listener )
	{
		this(entity, modelController, guiController, showTitle);
		setChangeListener(listener);		
	}
	public FullEditPanel( Writable entity, ModelController modelController, GUIController guiController, boolean showTitle )
	{
		super(guiController);
		this.modelController = modelController;
		this.entity = entity;
		if(showTitle)
			setTitle( entity.getId() > 0 ? entity.getEntityName() + " " + entity.getId() : "new " + entity.getEntityName() );
		basicPanel = addBasicPanel(entity);
		for(int i = 0; i < entity.getEntityCount(); i++)
		{
			switch( entity.getRelationType(i))
			{
			case ONETOONE:
				if(	entity.getEntityValue(i) instanceof Writable )
					fullPanels.add( addFullPanel( entity.getEntityName(i), (Writable) entity.getEntityValue(i) ) );
				break;
			case MANYTOMANY:
			case ONETOMANY:
				addEntityTable( i );
				break;
			case MANYTOONE:
				addSelectionBox( i );
				break;
			}	
		}		
		realign();
	}

	private BasicEditPanel addBasicPanel( Writable entity )
	{	
		BasicEditPanel panel = new BasicEditPanel(entity); 
		super.addPanel(panel);
		return panel;
	}

	private FullEditPanel addFullPanel( String title, Writable entity )
	{	
		FullEditPanel subPanel = null;
		if(entity != null)
			subPanel = new FullEditPanel(entity,modelController, guiController,false);
		SubEntityPanel sePanel = new SubEntityPanel(title, subPanel );
		if(entity != null)
			sePanel.addButton("View", "bn/blaszczyk/roseapp/resources/view.png", e -> guiController.openEntityTab( entity , false));
		super.addPanel( sePanel );
		return subPanel;
	}
	
	private void addEntityTable( int index )
	{
		@SuppressWarnings("unchecked")
		Set<? extends Readable> set = (Set<? extends Readable>) entity.getEntityValue(index);
		JComponent component = null;
		if(set != null && !set.isEmpty())
			component = new EntityTableBuilder()
					.type(entity.getEntityClass(index))
					.width(BASIC_WIDTH)
					.heigth(SUBTABLE_HEIGTH)
					.entities(set)
					.addButtonColumn("edit.png", e -> guiController.openEntityTab( e, true ))
					.addButtonColumn("copy.png", e -> guiController.openEntityTab( modelController.createCopy((Writable) e), true ))
					.addButtonColumn("delete.png", e -> guiController.delete((Writable) e))
					.buildInScrollPane();
		SubEntityPanel sePanel = new SubEntityPanel(entity.getEntityName(index), component, BASIC_WIDTH, SUBTABLE_HEIGTH);
		sePanel.addButton("Add", "bn/blaszczyk/roseapp/resources/add.png", e -> guiController.addNew( entity, index ));
		super.addPanel( sePanel );
	}
	
	private void addSelectionBox( int index )
	{
		Readable[] entities = new Readable[modelController.getAllEntites(entity.getEntityClass(index)).size()];
		modelController.getAllEntites(entity.getEntityClass(index)).toArray(entities);
		EntityComboBox<Readable> selectBox = new EntityComboBox<>(entities, BASIC_WIDTH, true);
		if(entity.getEntityValue(index) != null)
			selectBox.setSelectedItem(entity.getEntityValue(index));
		selectBox.setFont(VALUE_FONT);
		selectBox.setForeground(VALUE_FG);
		entityBoxes.put(index, selectBox);
		
		SubEntityPanel subPanel = new SubEntityPanel( entity.getEntityName(index), selectBox, BASIC_WIDTH, LBL_HEIGHT);
		super.addPanel( subPanel);
	}
	
	public void save(ModelController modelController)
	{
		basicPanel.save(modelController);
		for(FullEditPanel panel : fullPanels)
			panel.save(modelController);
		for(Integer index : entityBoxes.keySet() )
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
