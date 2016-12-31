package bn.blaszczyk.roseapp.view.panels.input;

import java.text.ParseException;
import java.util.Date;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class DateInputPanel extends AbstractInputPanel<Date> {
	
//	private final static DateFormat DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	public DateInputPanel( String name, Date defvalue )
	{
		super(name,defvalue);
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
		return defValue.getTime() != getValue().getTime() ;
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
