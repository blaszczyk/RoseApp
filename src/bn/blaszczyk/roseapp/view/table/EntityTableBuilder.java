package bn.blaszczyk.roseapp.view.table;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import bn.blaszczyk.rose.model.EntityModel;
import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.Behaviour;
import bn.blaszczyk.roseapp.DefaultBehaviour;
import bn.blaszczyk.roseapp.view.factories.IconFactory;
import bn.blaszczyk.roseapp.view.factories.TextFieldFactory;
import bn.blaszczyk.rosecommon.tools.TypeManager;

public class EntityTableBuilder
{
	private final List<Icon> icons = new ArrayList<>();
	private final List<EntityTable.EntityAction> actions = new ArrayList<>();
	private Collection<? extends Readable> entities;
	private int selectionMode = ListSelectionModel.SINGLE_SELECTION;
	private Class<? extends Readable> type;
	private Behaviour behaviour = null;
	
	private EntityTable table = null;
	
	public EntityTableBuilder type(Class<? extends Readable> type)
	{
		this.type = type;
		return this;
	}
	
	public EntityTableBuilder entities( Collection<? extends Readable> entities )
	{
		this.entities = entities;
		return this;
	}
	
	public EntityTableBuilder behaviour( Behaviour behaviour )
	{
		this.behaviour = behaviour;
		return this;
	}
	
	public EntityTableBuilder selectionMode( int selectionMode)
	{
		this.selectionMode = selectionMode;
		return this;
	}
	
	public EntityTableBuilder addButtonColumn(String iconFile, EntityTable.EntityAction action)
	{
		icons.add( IconFactory.create(iconFile) );
		actions.add(action);
		return this;
	}
	
	public EntityTable build()
	{
		EntityModel entityModel = TypeManager.getEntityModel(type);
		EntityTableModel tableModel= new EntityTableModel(entities, actions.size(), entityModel);
		table = new EntityTable(tableModel, entityModel, behaviour == null ? new DefaultBehaviour() : behaviour);
		table.setSelectionMode(selectionMode);
		for(int i = 0; i < actions.size(); i++)
			table.setButtonColumn(i, icons.get(i), actions.get(i));
		return table;
	}
	
	public JScrollPane buildInScrollPane()
	{
		return new JScrollPane(build());
	}
	
	public JPanel buildWithFilterInScrollPane()
	{
		JPanel panel = new JPanel(new BorderLayout());
		final EntityTable table = build();
		JTextField textField = TextFieldFactory.createTextField("");
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e)
			{
				table.filter( "(?i)" + textField.getText() );
			}
		});
		panel.add(textField,BorderLayout.PAGE_START);
		panel.add(new JScrollPane(table),BorderLayout.CENTER);
		return panel;
	}
	
	public EntityTable getTable()
	{
		return table;
	}
	
}
