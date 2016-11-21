package bn.blaszczyk.roseapp.view.panels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.view.ThemeConstants;
import bn.blaszczyk.roseapp.view.inputpanels.*;

@SuppressWarnings("serial")
public class BasicEditPanel extends JPanel implements MyPanel, ThemeConstants {
	
	private int width = 3 * H_SPACING + PROPERTY_WIDTH + VALUE_WIDTH;
	private int height = V_SPACING;

	private Writable entity;
	private List<InputPanel<?>> panels = new ArrayList<>();
	
	public BasicEditPanel( Writable entity )
	{
		this.entity = entity;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		for(int i = 0; i < entity.getFieldCount(); i++)
			if(entity.getFieldValue(i) instanceof Enum)
				addEnumPanel( i );
			else
				addInputPanel( i );
	}

	private void addInputPanel(int index)
	{
		InputPanel<?> panel = null;
		String name = entity.getFieldName(index);
		Object value = entity.getFieldValue(index);
		if( value instanceof String )
			if( FileInputPanel.isFileName(entity.getFieldValue(index).toString() ) )
				panel = new FileInputPanel(entity.getFieldName(index), entity.getFieldValue(index).toString(), true);
			else
				panel = new StringInputPanel( name, (String) value, entity.getLength1(index) );
		else if( value instanceof Boolean )
			panel = new BooleanInputPanel( name, (Boolean) value );
		else if( value instanceof Integer)
			panel = new IntegerInputPanel( name, (Integer) value );
		else if( value instanceof Date)
			panel = new DateInputPanel( name, (Date) value );
		else if( value instanceof BigDecimal)
			panel = new BigDecimalInputPanel( name, (BigDecimal) value, entity.getLength1(index), entity.getLength2(index) );
		else
		{
			System.out.printf( "Unknown type %s \n", value);
			return;
		}
		panel.getPanel().setBounds( H_SPACING, height, PROPERTY_WIDTH + VALUE_WIDTH + H_SPACING, LBL_HEIGHT );
		panels.add(panel);
		add(panel.getPanel());
		height += LBL_HEIGHT + V_SPACING;
	}
	private void addEnumPanel(int index)
	{
		InputPanel<?> panel = new EnumInputPanel(entity.getFieldName(index), (Enum<?>) entity.getFieldValue(index));
		panel.getPanel().setBounds( H_SPACING, height, PROPERTY_WIDTH + VALUE_WIDTH + H_SPACING, LBL_HEIGHT );
		panels.add(panel);
		add(panel.getPanel());
		height += LBL_HEIGHT + V_SPACING;
	}
	
	public void save(FullModelController controller)
	{
		int i;
		for(i = 0 ; i < entity.getFieldCount(); i++ )
			controller.setField(entity, i, panels.get(i).getValue() );
	}
	
	public void setChangeListener(ChangeListener l)
	{
		for(InputPanel<?> panel : panels)
			panel.setChangeListener(l);
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
		for(InputPanel<?> panel : panels)
			if(panel.hasChanged())
				return true;
		return false;
	}
	
}
