package bn.blaszczyk.roseapp.view.settings;

import static bn.blaszczyk.roseapp.view.ThemeConstants.BASIC_WIDTH;
import static bn.blaszczyk.roseapp.view.ThemeConstants.FULL_PNL_BACKGROUND;
import static bn.blaszczyk.roseapp.view.ThemeConstants.H_SPACING;
import static bn.blaszczyk.roseapp.view.ThemeConstants.LBL_HEIGHT;
import static bn.blaszczyk.roseapp.view.ThemeConstants.PROPERTY_FG;
import static bn.blaszczyk.roseapp.view.ThemeConstants.PROPERTY_FONT;
import static bn.blaszczyk.roseapp.view.ThemeConstants.PROPERTY_WIDTH;
import static bn.blaszczyk.roseapp.view.ThemeConstants.VALUE_FG;
import static bn.blaszczyk.roseapp.view.ThemeConstants.VALUE_FONT;
import static bn.blaszczyk.roseapp.view.ThemeConstants.V_SPACING;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import bn.blaszczyk.rose.model.Entity;
import bn.blaszczyk.roseapp.controller.ModelController;
import bn.blaszczyk.roseapp.tools.Preferences;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.panels.AbstractEntityPanel;

@SuppressWarnings("serial")
public class EntityTableColumnSettingPanel extends AbstractEntityPanel {

	public final static String COLUMN_WIDTH = "columnWidth";
	public final static String COLUMN_CONTENT = "columnContent";
	public final static String COLUMN_COUNT = "columnCount";
	
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
	}
	
	private void addComponents()
	{
		int columnCount = Preferences.getIntegerEntityValue(type, COLUMN_COUNT, entity.getFields().size());
		for(int i = 0; i < columnCount; i++)
		{
			String columnContent = Preferences.getStringEntityValue(type, COLUMN_CONTENT + i, "f" + i);
			int columnWidth = Preferences.getIntegerEntityValue(type, COLUMN_WIDTH, BASIC_WIDTH / columnCount );
			
			JLabel label = new JLabel("Column " + i + ": ",SwingConstants.RIGHT);
			label.setFont(PROPERTY_FONT);
			label.setForeground(PROPERTY_FG);
			label.setBounds(H_SPACING, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			add(label);
			
			JTextField contentField = new JTextField(columnContent);
			contentField.setFont(VALUE_FONT);
			contentField.setForeground(VALUE_FG);
			contentField.addActionListener(changeListener);
			contentField.setBounds(2 * H_SPACING + PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			contentFields.add(contentField);
			add(contentField);
			
			label = new JLabel("Width: " ,SwingConstants.RIGHT);
			label.setFont(PROPERTY_FONT);
			label.setForeground(PROPERTY_FG);
			label.setBounds(3 * H_SPACING + 2 * PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			add(label);
			
			JTextField widthField = new JTextField("" + columnWidth);
			widthField.setFont(VALUE_FONT);
			widthField.setForeground(VALUE_FG);
			widthField.addActionListener(changeListener);
			widthField.setBounds(4 * H_SPACING + 3 * PROPERTY_WIDTH, V_SPACING + i * (LBL_HEIGHT + V_SPACING), PROPERTY_WIDTH, LBL_HEIGHT);
			widthFields.add(widthField);
			add(widthField);
		}
		
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

	@Override
	public boolean hasChanged()
	{
		// TODO Auto-generated method stub
		return true
				;
	}
	
	
	
	
}

