package bn.blaszczyk.roseapp.view.panels;

import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.view.tools.EntityTableBuilder;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class FullViewPanel extends AlignPanel {
	
	private Readable entity;

	public FullViewPanel( Readable entity, GUIController guiController, boolean showTitle )
	{
		super( guiController);
		this.entity = entity;
		if(showTitle)
			setTitle( entity.getId() > 0 ? entity.getEntityName() + " " + entity.getId() : "new " + entity.getEntityName() );
		super.addPanel( new BasicViewPanel(entity));
		for(int i = 0; i < entity.getEntityCount(); i++)
		{
			if( entity.getEntityValue(i) == null )
				continue;
			switch( entity.getRelationType(i) )
			{
			case MANYTOMANY:
			case ONETOMANY:
				if(!((Set<?>)entity.getEntityValue(i)).isEmpty())
					addEntityTable(i);
				break;
			case MANYTOONE:
				if(entity.getEntityValue(i)!= null)
					addBasicPanel( entity.getEntityName(i), (Readable) entity.getEntityValue(i) );
				break;
			case ONETOONE:
				if(entity.getEntityValue(i)!= null)
					addFullPanel( entity.getEntityName(i), (Readable) entity.getEntityValue(i)  );
				break;
			}
		}
		realign();
	}
	

	private void addBasicPanel( String title, Readable entity )
	{
		EntityPanel subPanel = null;
		if(entity != null)
			subPanel = new BasicViewPanel(entity);
		SubEntityPanel sePanel = new SubEntityPanel(title, subPanel );
		if(entity != null)
			sePanel.addButton("View", "bn/blaszczyk/roseapp/resources/view.png", e -> guiController.openEntityTab( entity , false));
		super.addPanel( sePanel );
	}
	
	private void addFullPanel( String title, Readable entity )
	{
		EntityPanel subPanel = null;
		if(entity != null)
			subPanel = new FullViewPanel(entity,guiController,false);
		SubEntityPanel sePanel = new SubEntityPanel(title, subPanel );
		if(entity != null)
			sePanel.addButton("View", "bn/blaszczyk/roseapp/resources/view.png", e -> guiController.openEntityTab( entity , false));
		super.addPanel( sePanel );
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
					.addButtonColumn("view.png", e -> guiController.openEntityTab( e, false ))
					.buildInScrollPane();
		SubEntityPanel sePanel = new SubEntityPanel(entity.getEntityName(index), component, BASIC_WIDTH, SUBTABLE_HEIGTH);
		super.addPanel( sePanel );
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
		return false;
	}
	
	@Override
	public void refresh()
	{
		super.realign();
	}
	
	
}
