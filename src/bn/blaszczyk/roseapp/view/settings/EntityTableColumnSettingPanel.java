package bn.blaszczyk.roseapp.view.settings;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import bn.blaszczyk.rose.model.Entity;
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
	private final List<JTextField> contentFields = new ArrayList<>();
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
			String columnContent = Preferences.getStringEntityValue(type, COLUMN_CONTENT + i, entity.getFields().get(i).getName() );
			int columnWidth = Preferences.getIntegerEntityValue(type, COLUMN_WIDTH, BASIC_WIDTH / columnCount );
			addColumn(columnContent, columnWidth);
		}
	}
	
	private void addColumn(String columnContent, int columnWidth)
	{	
		contentFields.add(TextFieldFactory.createTextField(columnContent, changeListener));
		widthFields.add(TextFieldFactory.createIntegerField(columnWidth, changeListener));
	}
	
	private void realign()
	{
		removeAll();
		for(int i = 0; i < contentFields.size(); i++)
		{
			JLabel label = LabelFactory.createLabel("Column " + i + ": ");
			label.setBounds(H_SPACING, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			add(label);

			JTextField textField = contentFields.get(i);
			textField.setBounds(2 * H_SPACING + PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			add(textField);
			
			label = LabelFactory.createLabel("Width: " );
			label.setBounds(3 * H_SPACING + 2 * PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			add(label);	
			
			textField = widthFields.get(i);
			textField.setBounds(4 * H_SPACING + 3 * PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			add(textField);
			
			final int ii = i;
			JButton button = ButtonFactory.createButton("delete", e -> removeColumn(ii), changeListener);
			button.setBounds(5 * H_SPACING + 4 * PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			add(button);
		}
		
		JButton button = ButtonFactory.createButton("add Column", e -> addNewColumn(), changeListener);
		button.setBounds(5 * H_SPACING + 4 * PROPERTY_WIDTH, V_SPACING + contentFields.size() * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
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
		contentFields.remove(i);
		widthFields.remove(i);
		realign();
	}

	@Override
	public void save(ModelController controller)
	{
		super.save(controller);
		int count = 0;
		for(JTextField textField : contentFields)
			Preferences.putStringEntityValue(type, COLUMN_CONTENT + count++, textField.getText());
		count = 0;
		for(JTextField textField : widthFields)
			Preferences.putIntegerEntityValue(type, COLUMN_WIDTH + count++, Integer.parseInt(textField.getText()));
		Preferences.putIntegerEntityValue(type, COLUMN_COUNT, contentFields.size());
	}	
	
}

