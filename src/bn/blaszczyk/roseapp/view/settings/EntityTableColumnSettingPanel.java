package bn.blaszczyk.roseapp.view.settings;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bn.blaszczyk.rose.model.Entity;
import bn.blaszczyk.rose.model.EntityField;
import bn.blaszczyk.rose.model.Field;
import bn.blaszczyk.roseapp.controller.ModelController;
import bn.blaszczyk.roseapp.tools.Preferences;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.panels.AbstractEntityPanel;
import bn.blaszczyk.roseapp.view.tools.ButtonFactory;
import bn.blaszczyk.roseapp.view.tools.LabelFactory;
import bn.blaszczyk.roseapp.view.tools.TextFieldFactory;

@SuppressWarnings("serial")
public class EntityTableColumnSettingPanel extends AbstractEntityPanel {

	public final static String COLUMN_WIDTH = "columnwidth";
	public final static String COLUMN_CONTENT = "columncontent";
	public final static String COLUMN_COUNT = "columncount";
	
	private final static Map<Entity, Field[]> fieldsMap = new HashMap<>();
	
	private final Class<?> type;
	private final Entity entity;
	private final List<PathBox> contentBoxes = new ArrayList<>();
	private final List<JTextField> widthFields = new ArrayList<>();
	
	public EntityTableColumnSettingPanel( Class<?> type )
	{
		super(null);
		this.type = type;
		this.entity = TypeManager.getEntity(type);
		
		setBackground(FULL_PNL_BACKGROUND);
		addComponents();
		realign();
	}
	
	private void addComponents()
	{
		int columnCount = Preferences.getIntegerEntityValue(type, COLUMN_COUNT, entity.getFields().size());
		for(int i = 0; i < columnCount; i++)
		{
			String columnContent = Preferences.getStringEntityValue(type, COLUMN_CONTENT + i, 
					i < entity.getFields().size() ? entity.getFields().get(i).getName() : "" );
			int columnWidth = Preferences.getIntegerEntityValue(type, COLUMN_WIDTH, BASIC_WIDTH / columnCount );
			addColumn(columnContent, columnWidth);
		}
	}
	
	private void addColumn(String columnContent, int columnWidth)
	{
		contentBoxes.add(new PathBox(e-> realign() ,entity, columnContent));
		widthFields.add(TextFieldFactory.createIntegerField(columnWidth, changeListener));
	}
	
	private void realign()
	{
		removeAll();
		for(int i = 0; i < widthFields.size(); i++)
		{
			JLabel label = LabelFactory.createLabel("Width : ");
			label.setBounds(H_SPACING, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			add(label);
			
			label = LabelFactory.createLabel("Content : " );
			label.setBounds(4 * H_SPACING + 3 * PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			add(label);	
			
			JTextField textField = widthFields.get(i);
			textField.setBounds(2 * H_SPACING + 1 * PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			add(textField);
			
			final int ii = i;
			JButton button = ButtonFactory.createButton("delete", e -> removeColumn(ii), changeListener);
			button.setBounds(3 * H_SPACING + 2 * PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			add(button);

			PathBox box = contentBoxes.get(i);
			box.setLocation(5 * H_SPACING + 4 * PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING));
			add(box);
		}
		
		JButton button = ButtonFactory.createButton("add Column", e -> addNewColumn(), changeListener);
		button.setBounds(5 * H_SPACING + 4 * PROPERTY_WIDTH, V_SPACING + widthFields.size() * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
		add(button);
		refresh();
	}

	private void addNewColumn()
	{
		addColumn("", 0);
		realign();
	}

	private void removeColumn(int i)
	{
		contentBoxes.remove(i);
		widthFields.remove(i);
		realign();
	}

	@Override
	public void save(ModelController controller)
	{
		super.save(controller);
		int count = 0;
		for(PathBox box : contentBoxes)
			Preferences.putStringEntityValue(type, COLUMN_CONTENT + count++, box.toString().toLowerCase() );
		count = 0;
		for(JTextField textField : widthFields)
			Preferences.putIntegerEntityValue(type, COLUMN_WIDTH + count++, Integer.parseInt(textField.getText()));
		Preferences.putIntegerEntityValue(type, COLUMN_COUNT, widthFields.size());
	}
	
	private static Field[] createLeafs( Entity entity)
	{
		if(!fieldsMap.containsKey(entity))
		{
			List<Field> contentList = new ArrayList<>();
			contentList.addAll(entity.getFields());
			for(EntityField entityField : entity.getEntityFields())
				if(!entityField.getType().isSecondMany())
					contentList.add(entityField);
			Field[] contents = new Field[contentList.size()];
			contentList.toArray(contents);
			fieldsMap.put(entity, contents);
		}
		return(fieldsMap.get(entity));
	}

	private class PathBox extends JPanel implements ActionListener
	{
		private final JComboBox<Field> nodeBox;
		private JComponent component = null;
		private final ActionListener externalListener;
		
		public PathBox(ActionListener externalListener, Entity entity, String path)
		{
			setLayout(null);
			setBackground(FULL_PNL_BACKGROUND);
			String[] split = path.split("\\.", 2);
			this.externalListener = externalListener;
			nodeBox = new JComboBox<>(createLeafs(entity));
			nodeBox.setFont(PROPERTY_FONT);
			nodeBox.setForeground(PROPERTY_FG);
			for(int i = 0; i < nodeBox.getItemCount(); i++)
			{
				if(nodeBox.getItemAt(i).toString().equalsIgnoreCase(split[0]))
					nodeBox.setSelectedIndex(i);
			}
			nodeBox.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
			nodeBox.addActionListener(this);
			add(nodeBox);
			setLeaf(split.length > 1 ? split[1] : null);
			setSize(PROPERTY_WIDTH, LBL_HEIGHT);
		}
		
		private void setLeaf(String path)
		{
			if(nodeBox.getSelectedItem() instanceof EntityField)
			{
				Entity subEntity = ((EntityField)nodeBox.getSelectedItem()).getEntity();
				if(path != null)
					setNode(path,subEntity);
				else
					setEntityLeaf(subEntity);
			}
		}
		
		private void setEntityLeaf(Entity entity)
		{
			component = ButtonFactory.createButton("Add Leaf", this);
			component.setBounds(PROPERTY_WIDTH, 0, PROPERTY_WIDTH, LBL_HEIGHT);
			setSize(2 * PROPERTY_WIDTH, LBL_HEIGHT);
			add(component);
		}
		
		private void setNode(String path, Entity entity)
		{
			component = new PathBox(externalListener,entity,path);
			component.setLocation(PROPERTY_WIDTH, 0);
			setSize(PROPERTY_WIDTH + component.getWidth(), LBL_HEIGHT);
			add(component);
		}
		
		@Override
		public String toString()
		{
			return nodeBox.getSelectedItem().toString() + ( (component instanceof PathBox) ? ("." + component) : "" );
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String leaf = null;
			if(e.getSource() instanceof JButton)
				leaf = nodeBox.getSelectedItem().toString();
			if(component != null)
				remove(component);
			setLeaf(leaf);
			repaint();
			revalidate();
			externalListener.actionPerformed(e);
		}
		
	}
	
}

