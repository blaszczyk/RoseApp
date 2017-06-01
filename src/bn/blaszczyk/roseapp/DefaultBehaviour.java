package bn.blaszczyk.roseapp;

import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.model.StringFieldType;
import bn.blaszczyk.roseapp.view.Messenger;
import bn.blaszczyk.roseapp.view.table.ColumnContent;

import bn.blaszczyk.rosecommon.tools.TypeManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;

import bn.blaszczyk.rose.model.Entity;
import bn.blaszczyk.rose.model.PrimitiveField;
import bn.blaszczyk.rose.model.Readable;

public class DefaultBehaviour implements Behaviour {
	
	protected Messenger messenger;
	
	public DefaultBehaviour()
	{
	}

	@Override
	public void checkEntity(Writable entity)
	{
		Entity entitee = TypeManager.getEntity(entity);
		for(int i = 0; i < entity.getFieldCount(); i++)
		{
			Object o = entity.getFieldValue(i);
			if(o == null)
				continue;
			if(o instanceof String)
			{
				String value = (String)o;
				int maxLength = ((PrimitiveField)entitee.getFields().get(i)).getLength1();
				if(value.length() > maxLength)
				{
					entity.setField(i, value.substring(0, maxLength));
					messenger.warning( "\"" + value + "\" will be cut to " + maxLength + " characters.", "String value too long");
				}
				String regex = fieldType(entity, entitee.getFields().get(i).getCapitalName());
				if(! Pattern.matches(regex, value) )
					messenger.warning("Value \"" + value + "\" does not match pattern \"" + regex + "\"", "Warning: invalid input.");
			}
			if(o instanceof BigDecimal)
			{
				BigDecimal value = (BigDecimal) o;
				int length1 = ((PrimitiveField)entitee.getFields().get(i)).getLength1();
				int length2 = ((PrimitiveField)entitee.getFields().get(i)).getLength2();
				if(value.scale() > length2)
				{
					messenger.warning( "\"" + value + "\" will be rounded to " + length2 + " digits.", "Numeric value too precise.");
				}
				value = value.setScale(length2, RoundingMode.HALF_DOWN);
				entity.setField(i, value);
				if(value.precision() > length1)
				{
					messenger.warning( "\"" + value + "\" will be cut to " + length1 + " digits.", "Numeric value too big.");
					value = value.remainder(BigDecimal.TEN.pow(length1-length2));
					entity.setField(i, value);
				}
			}

		}
	}

	@Override
	public Readable replacePanel(Readable entity)
	{
		return entity;
	}

	@Override
	public void setMessenger(Messenger messenger)
	{
		this.messenger = messenger;
	}

	@Override
	public Collection<Writable> cascade(Writable entity)
	{
		return Collections.emptyList();
	}

	@Override
	public boolean creatable(Class<? extends Writable> type)
	{
		return true;
	}

	@Override
	public Comparator<?> comparator(Entity entity, ColumnContent content)
	{
		Class<?> type = content.getClass(entity);
		if(type == Integer.class)
			return (i1,i2) -> Integer.compare((Integer)i1, (Integer)i2);
		if(type == Date.class)
			return (d1,d2) -> ((Date)d2).compareTo((Date)d1);
		if(type == BigDecimal.class)
			return (n1,n2) -> ((BigDecimal)n1).compareTo((BigDecimal)n2);
		return (o1,o2) -> String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2));
	}
	
	@Override
	public String fieldType(Readable entity, String fieldName)
	{
//		return  getStringEntityValue(entity, FIELD_TYPE.append(fieldName));
		return StringFieldType.STRING.getRegex();
	}
	
}
