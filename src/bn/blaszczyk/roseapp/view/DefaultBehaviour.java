package bn.blaszczyk.roseapp.view;

import bn.blaszczyk.rose.model.Writable;
import bn.blaszczyk.roseapp.tools.TypeManager;

import static bn.blaszczyk.roseapp.tools.Preferences.FIELD_TYPE;
import static bn.blaszczyk.roseapp.tools.Preferences.getStringEntityValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
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
				String regex =  getStringEntityValue(entity, FIELD_TYPE + entitee.getFields().get(i).getCapitalName(), ".*");
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
	
}
