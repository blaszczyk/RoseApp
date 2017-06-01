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

public class MediumViewPanel extends AlignPanel {
	
	private static final long serialVersionUID = -460666614494926237L;
	
	private final Readable entity;

	private final GUIController guiController;

	public MediumViewPanel( Readable entity, GUIController guiController )
	{
		super(0);
		this.guiController = guiController;
		this.entity = entity;
		setBackground(BASIC_PNL_BACKGROUND);
		super.addPanel( new BasicViewPanel(entity, guiController));
		for(int i = 0; i < entity.getEntityCount(); i++)
		{
			switch( entity.getRelationType(i) )
			{
			case MANYTOMANY:
			case ONETOMANY:
				if(!(entity.getEntityValueMany(i)).isEmpty())
					addEntityTable(i);
				break;
			case MANYTOONE:
				if(entity.getEntityValueOne(i)!= null)
					addBasicPanel( entity.getEntityName(i), (Readable) entity.getEntityValueOne(i) );
				break;
			case ONETOONE:
				break;
			}
		}
		refresh();
	}
	
	private void addBasicPanel( String title, Readable entity )
	{
		RosePanel subPanel = null;
		if(entity != null)
			subPanel = new BasicViewPanel(entity, guiController);
		TitleButtonsPanel sePanel = TitleButtonsPanel.withBorder( title, subPanel );
		if(entity != null)
			sePanel.addButton("View", "view.png", e -> guiController.openEntityTab( entity , false));
		super.addPanel( sePanel );
	}
	
	private void addEntityTable( int index )
	{
		Set<? extends Readable> set = entity.getEntityValueMany(index);
		JComponent component = null;
		if(set != null && !set.isEmpty())
			component = new EntityTableBuilder()
					.type(entity.getEntityClass(index))
					.entities(set)
					.behaviour(guiController.getBehaviour())
					.addButtonColumn("view.png", e -> guiController.openEntityTab( e, false ))
					.buildWithFilterInScrollPane();
		TitleButtonsPanel sePanel = TitleButtonsPanel.withBorder(entity.getEntityName(index), component, BASIC_WIDTH, SUBTABLE_HEIGTH);
		super.addPanel( sePanel );
	}
	
	@Override
	public Object getShownObject()
	{
		return entity;
	}
	
}
