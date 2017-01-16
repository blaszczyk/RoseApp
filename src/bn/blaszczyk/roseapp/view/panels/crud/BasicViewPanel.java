package bn.blaszczyk.roseapp.view.panels.crud;

import java.util.Date;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.model.StringFieldType;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;
import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;
import bn.blaszczyk.roseapp.view.panels.input.FileInputPanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.roseapp.tools.Preferences.*;

@SuppressWarnings("serial")
public class BasicViewPanel extends AbstractRosePanel {


	
	private int width = 3 * H_SPACING + PROPERTY_WIDTH + VALUE_WIDTH;
	private int height = V_SPACING;

	private Readable entity;
	
	public BasicViewPanel( Readable entity )
	{
		this.entity = entity;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		for(int i = 0; i < entity.getFieldCount(); i++)
		{
			String regex = getStringEntityValue(entity, FIELD_TYPE + entity.getFieldName(i), "");
			if(StringFieldType.fromRegex(regex).equals(StringFieldType.FILE))
				addFile(entity.getFieldName(i), entity.getFieldValue(i).toString());
			else
				addValue( entity.getFieldName(i), entity.getFieldValue(i) );
		}
	}

	private void addValue(String property, Object value)
	{
		
		JLabel lblProperty = LabelFactory.createLabel(property + ": ", SwingConstants.RIGHT);
		lblProperty.setBounds( H_SPACING, height, PROPERTY_WIDTH, LBL_HEIGHT );
		add(lblProperty);
		
		JLabel lblValue = LabelFactory.createLabel( (value instanceof Date) ? DATE_FORMAT.format(value) : " " + value);
		lblValue.setBounds( 2 * H_SPACING + PROPERTY_WIDTH , height, VALUE_WIDTH, LBL_HEIGHT);
		add(lblValue);
			
		height += LBL_HEIGHT + V_SPACING;
	}	
	
	private void addFile(String property, String value)
	{
		FileInputPanel panel = new FileInputPanel(property, value, false);
		panel.setBounds( H_SPACING , height, PROPERTY_WIDTH + H_SPACING + VALUE_WIDTH, LBL_HEIGHT );
		add(panel);
			
		height += LBL_HEIGHT + V_SPACING;
	}

	@Override
	public int getFixWidth()
	{
		return width;
	}

	@Override
	public int getFixHeight()
	{
		return height;
	}

	@Override
	public Object getShownObject()
	{
		return entity;
	}
	
}
