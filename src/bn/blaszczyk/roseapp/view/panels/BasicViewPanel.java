package bn.blaszczyk.roseapp.view.panels;

import java.util.Date;

import javax.swing.JLabel;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.view.inputpanels.FileInputPanel;
import bn.blaszczyk.roseapp.view.tools.LabelFactory;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class BasicViewPanel extends AbstractEntityPanel {


	
	private int width = 3 * H_SPACING + PROPERTY_WIDTH + VALUE_WIDTH;
	private int height = V_SPACING;

	private Readable entity;
	
	public BasicViewPanel( Readable entity )
	{
		this.entity = entity;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		for(int i = 0; i < entity.getFieldCount(); i++)
			if( FileInputPanel.isFileName(entity.getFieldValue(i).toString() ) )
				addFile( entity.getFieldName(i), entity.getFieldValue(i).toString() );
			else
				addValue( entity.getFieldName(i), entity.getFieldValue(i) );
	}

	private void addValue(String property, Object value)
	{
		JLabel lblProperty = LabelFactory.createLabel(property + ": ");
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
