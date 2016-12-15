package bn.blaszczyk.roseapp.view.panels.crud;

import java.util.Set;

import javax.swing.JComponent;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.view.panels.AlignPanel;
import bn.blaszczyk.roseapp.view.panels.EntityPanel;
import bn.blaszczyk.roseapp.view.panels.SubEntityPanel;
import bn.blaszczyk.roseapp.view.table.EntityTableBuilder;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class FullViewPanel extends AlignPanel {
	
	private Readable entity;

	private FullViewPanel( Readable entity, GUIController guiController, boolean showTitle, Readable parent )
	{
		super( guiController, H_SPACING);
		this.entity = entity;
		if(showTitle)
			setTitle( entity.getId() > 0 ? entity.getEntityName() + " " + entity.getId() : "new " + entity.getEntityName() );
		super.addPanel( new BasicViewPanel(entity));
		for(int i = 0; i < entity.getEntityCount(); i++)
		{
			if( entity.getEntityValue(i) == null || entity.getEntityValue(i) == parent)
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
		refresh();
	}
	
	public FullViewPanel( Readable entity, GUIController guiController, boolean showTitle )
	{
		this(entity, guiController, showTitle, null);
	}
	

	private void addBasicPanel( String title, Readable entity )
	{
		EntityPanel subPanel = null;
		if(entity != null)
			subPanel = new BasicViewPanel(entity);
		SubEntityPanel sePanel = new SubEntityPanel(title, subPanel );
		if(entity != null)
			sePanel.addButton("View", "view.png", e -> guiController.openEntityTab( entity , false));
		super.addPanel( sePanel );
	}
	
	private void addFullPanel( String title, Readable entity )
	{
		EntityPanel subPanel = null;
		if(entity != null)
			subPanel = new FullViewPanel(entity,guiController,false,this.entity);
		SubEntityPanel sePanel = new SubEntityPanel(title, subPanel );
		if(entity != null)
			sePanel.addButton("View", "view.png", e -> guiController.openEntityTab( entity , false));
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
	public Object getShownObject()
	{
		return entity;
	}
	
}
