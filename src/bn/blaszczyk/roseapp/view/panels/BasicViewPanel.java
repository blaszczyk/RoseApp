package bn.blaszczyk.roseapp.view.panels;

import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.view.ThemeConstants;
import bn.blaszczyk.roseapp.view.inputpanels.FileInputPanel;

@SuppressWarnings("serial")
public class BasicViewPanel extends JPanel implements MyPanel, ThemeConstants {


	
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
		JLabel lblProperty = new JLabel(property + ": ", SwingConstants.RIGHT);
		lblProperty.setBounds( H_SPACING, height, PROPERTY_WIDTH, LBL_HEIGHT );
		lblProperty.setFont(PROPERTY_FONT);
		lblProperty.setOpaque(true);
		lblProperty.setForeground(PROPERTY_FG);
		lblProperty.setBackground(PROPERTY_BG);
		add(lblProperty);
		
		JLabel lblValue = new JLabel( " " + value);
		if(value instanceof Date)
			lblValue.setText(" " +  DATE_FORMAT.format(value));
		lblValue.setBounds( 2 * H_SPACING + PROPERTY_WIDTH , height, VALUE_WIDTH, LBL_HEIGHT);
		lblValue.setFont(VALUE_FONT);
		lblValue.setOpaque(true);
		lblValue.setForeground(VALUE_FG);
		lblValue.setBackground(VALUE_BG);
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
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public JPanel getPanel()
	{
		return this;
	}


	@Override
	public Object getShownObject()
	{
		return entity;
	}

	@Override
	public boolean hasChanged()
	{
		return false;
	}
	
}
