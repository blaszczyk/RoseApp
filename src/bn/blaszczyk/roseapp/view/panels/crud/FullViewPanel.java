package bn.blaszczyk.roseapp.view.panels.crud;

import java.util.Set;

import javax.swing.JComponent;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.view.panels.AlignPanel;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.TitleButtonsPanel;
import bn.blaszczyk.roseapp.view.table.EntityTableBuilder;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class FullViewPanel extends AlignPanel {
	
	private Readable entity;

	public FullViewPanel( Readable entity, GUIController guiController )
	{
		super( guiController, H_SPACING );
		this.entity = entity;
		setTitle( entity.getId() > 0 ? entity.getEntityName() + " " + entity.getId() : "new " + entity.getEntityName() );
		super.addPanel( new BasicViewPanel(entity));
		for(int i = 0; i < entity.getEntityCount(); i++)
		{
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
					addMediumPanel( entity.getEntityName(i), (Readable) entity.getEntityValue(i)  );
				break;
			}
		}
		refresh();
	}
	
	private void addBasicPanel( String title, Readable entity )
	{
		RosePanel subPanel = null;
		if(entity != null)
			subPanel = new BasicViewPanel(entity);
		TitleButtonsPanel sePanel = new TitleButtonsPanel(title, subPanel, false );
		if(entity != null)
			sePanel.addButton("View", "view.png", e -> guiController.openEntityTab( entity , false));
		super.addPanel( sePanel );
	}
	
	private void addMediumPanel( String title, Readable entity )
	{
		RosePanel subPanel = null;
		if(entity != null)
			subPanel = new MediumViewPanel(entity,guiController);
		TitleButtonsPanel sePanel = new TitleButtonsPanel(title, subPanel, true );
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
					.entities(set)
					.addButtonColumn("view.png", e -> guiController.openEntityTab( e, false ))
					.buildWithFilterInScrollPane();
		TitleButtonsPanel sePanel = new TitleButtonsPanel(entity.getEntityName(index), component, BASIC_WIDTH, SUBTABLE_HEIGTH,false);
		super.addPanel( sePanel );
	}
	
	@Override
	public Object getShownObject()
	{
		return entity;
	}
	
}
