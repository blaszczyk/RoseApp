package bn.blaszczyk.roseapp.view.panels.crud;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bn.blaszczyk.rose.model.EnumField;
import bn.blaszczyk.rose.model.Field;
import bn.blaszczyk.rose.model.PrimitiveField;
import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.model.StringFieldType;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;
import bn.blaszczyk.roseapp.view.panels.input.*;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.roseapp.tools.Preferences.*;

public class BasicEditPanel extends AbstractRosePanel {

	private static final long serialVersionUID = 1819433648925673419L;
	
	private int width = 2 * H_SPACING + BASIC_WIDTH;
	private int height = V_SPACING;

	private final Writable entity;
	private final List<InputPanel<?>> panels = new ArrayList<>();
	private final ModelController modelController;
	
	public BasicEditPanel( Writable entity, ModelController modelController )
	{
		this.entity = entity;
		this.modelController = modelController;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		for(int i = 0; i < entity.getFieldCount(); i++)
			addInputPanel( i );
	}

	private void addInputPanel(int index)
	{
		InputPanel<?> panel = null;
		Field field = TypeManager.getEntity(entity).getFields().get(index);
		String name = field.getName();
		Object value = entity.getFieldValue(index);
		if(field instanceof EnumField)
			panel = new EnumInputPanel(name, (Enum<?>) value);
		else if(field instanceof PrimitiveField)
		{
			PrimitiveField pField = (PrimitiveField) field;
			switch(pField.getType())
			{
			case BOOLEAN:
				panel = new BooleanInputPanel( name, (Boolean) value );
				break;
			case CHAR:
			case VARCHAR:
				String regex = getStringEntityValue(entity, FIELD_TYPE + field.getCapitalName(), null);
				StringFieldType stringFieldType = StringFieldType.fromRegex(regex);
				if(stringFieldType.equals(StringFieldType.FILE))
					panel = new FileInputPanel(field.getCapitalName(), entity.getFieldValue(index).toString(), true);
				else
					panel = new StringInputPanel( name, (String) value,  pField.getLength1(), regex );
				break;
			case DATE:
				panel = new DateInputPanel( name, (Date) value );
				break;
			case INT:
				panel = new IntegerInputPanel( name, (Integer) value );
				break;
			case NUMERIC:
				panel = new BigDecimalInputPanel( name, (BigDecimal) value, pField.getLength1(), pField.getLength2() );
				break;
			}
		}
		panel.getPanel().setBounds( H_SPACING, height, BASIC_WIDTH, LBL_HEIGHT );
		panel.setRoseListener(this);
		panels.add(panel);
		add(panel.getPanel());
		height += LBL_HEIGHT + V_SPACING;
	}
	
	@Override
	public void save()
	{
		int i;
		for(i = 0 ; i < entity.getFieldCount(); i++ )
		{
			InputPanel<?> panel = panels.get(i);
			entity.setField( i, panel.getValue() );
			modelController.update(entity);
			panel.resetDefValue();
		}
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
	

	@Override
	public boolean hasChanged()
	{
		for(InputPanel<?> panel : panels)
			if(panel.hasChanged())
				return true;
		return false;
	}
	
}
