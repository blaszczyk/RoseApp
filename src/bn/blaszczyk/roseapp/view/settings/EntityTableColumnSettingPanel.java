package bn.blaszczyk.roseapp.view.settings;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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
	
	private final Class<?> type;
	private final Entity entity;
	private final List<PathBox> contentBoxes = new ArrayList<>();
//	private final List<JComboBox<String>> contentBoxes = new ArrayList<>();
	private final List<JTextField> widthFields = new ArrayList<>();
	
	private final String[] contents;
	
	public EntityTableColumnSettingPanel( Class<?> type )
	{
		super(null);
		this.type = type;
		this.entity = TypeManager.getEntity(type);
		
		List<String> contents = new ArrayList<>();
		for(Field field : entity.getFields())
			contents.add(field.getName());
		for(EntityField entityField : entity.getEntityFields())
			if(!entityField.getType().isSecondMany())
				contents.add(entityField.getName());
		this.contents = new String[contents.size()];
		contents.toArray(this.contents);
		
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
		contentBoxes.add(new PathBox(e-> realign() ,columnContent));
//		JComboBox<String> box = new JComboBox<>(contents);
//		box.setSelectedItem(columnContent);
//		contentBoxes.add(box);
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

//			JComboBox<String> box = contentBoxes.get(i);
//			box.setBounds(2 * H_SPACING + PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
//			add(box);
			
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
			Preferences.putStringEntityValue(type, COLUMN_CONTENT + count++, box.toString() );
//		for(JComboBox<String> box : contentBoxes)
//			Preferences.putStringEntityValue(type, COLUMN_CONTENT + count++, box.getSelectedItem().toString() );
		count = 0;
		for(JTextField textField : widthFields)
			Preferences.putIntegerEntityValue(type, COLUMN_WIDTH + count++, Integer.parseInt(textField.getText()));
		Preferences.putIntegerEntityValue(type, COLUMN_COUNT, widthFields.size());
	}	
	
	private class PathBox extends JPanel
	{
		private final JComboBox<String> nodeBox;
		private JComponent component = null;
		private final ActionListener externalListener;
		
		public PathBox(ActionListener externalListener, String path)
		{
			setLayout(null);
			this.externalListener = externalListener;
			String[] split = path.split("\\.", 2);
			
			nodeBox = new JComboBox<>(contents);
			nodeBox.addActionListener(e -> setLeaf());
			nodeBox.setSelectedItem(split[0]);
			nodeBox.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
			add(nodeBox);
			
			if(split.length > 1)
				setNode(split[1]);
			else
				setLeaf();
		}
		
		private void setLeaf()
		{
			if(component != null)
				remove(component);
			component = ButtonFactory.createButton("Add Leaf", e -> setNode(""));
			component.setBounds(PROPERTY_WIDTH, 0, PROPERTY_WIDTH, LBL_HEIGHT);
			setSize(2 * PROPERTY_WIDTH, LBL_HEIGHT);
			add(component);
			externalListener.actionPerformed(new ActionEvent(this, 1, ""));
		}
		
		private void setNode(String path)
		{
			if(component != null)
				remove(component);
			component = new PathBox(externalListener,path);
			component.setLocation(PROPERTY_WIDTH, 0);
			setSize(PROPERTY_WIDTH + component.getWidth(), LBL_HEIGHT);
			add(component);
			externalListener.actionPerformed(new ActionEvent(this, 1, ""));
		}
		
		@Override
		public String toString()
		{
			return nodeBox.getSelectedItem().toString() + ( (component instanceof PathBox) ? ("." + component) : "" );
		}
		
	}
	
}

