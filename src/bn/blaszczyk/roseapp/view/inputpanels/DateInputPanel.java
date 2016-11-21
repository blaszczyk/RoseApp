package bn.blaszczyk.roseapp.view.inputpanels;

import java.text.ParseException;
import java.util.Date;

@SuppressWarnings("serial")
public class DateInputPanel extends AbstractInputPanel<Date> {
	
	private final Date defvalue;
//	private final static DateFormat DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	public DateInputPanel( String name, Date defvalue )
	{
		super(name);
		this.defvalue = defvalue;
		setValue(defvalue);
	}
	
	@Override
	public Date getValue()
	{
		try
		{
			return DATE_FORMAT.parse(textField.getText());
		}
		catch (ParseException e)
		{
			return null;
		}
	}
	
	@Override
	public void setValue(Date value)
	{	
		textField.setText(DATE_FORMAT.format(value));
	}

	@Override
	public boolean hasChanged()
	{
		return defvalue.getTime() != getValue().getTime() ;
	}

	@Override
	public boolean isInputValid()
	{
		try
		{
			DATE_FORMAT.parse(textField.getText());
			return true;
		}
		catch (ParseException e)
		{
			return false;
		}
	}
	
}
