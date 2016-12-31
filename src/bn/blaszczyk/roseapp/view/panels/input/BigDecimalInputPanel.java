package bn.blaszczyk.roseapp.view.panels.input;

import java.math.BigDecimal;
import java.text.ParseException;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class BigDecimalInputPanel extends AbstractInputPanel<BigDecimal> {
	
	private final int maxLength;
	private final int precision;
	
	static{
		BIG_DEC_FORMAT.setParseBigDecimal(true);
	}
	
	public BigDecimalInputPanel( String name, BigDecimal defvalue, int maxLength, int precision )
	{
		super(name,defvalue);
		this.maxLength = maxLength;
		this.precision = precision;
		setValue(defvalue);
	}
	
	@Override
	public BigDecimal getValue()
	{
		try
		{
			BigDecimal retValue = (BigDecimal) BIG_DEC_FORMAT.parse( textField.getText() );
			retValue.setScale(precision,BigDecimal.ROUND_HALF_UP);
			return retValue;
		}
		catch (ParseException e)
		{
			return null;
		}
	}
	
	@Override
	public void setValue(BigDecimal value)
	{	
		textField.setText(BIG_DEC_FORMAT.format(value));
	}

	@Override
	public boolean isInputValid()
	{
		try
		{
			BigDecimal value = (BigDecimal) BIG_DEC_FORMAT.parse( textField.getText() );
			if(	value.longValue() >= Math.pow(10, maxLength-precision) )
					return false;
			return true;
		}
		catch (ParseException e)
		{
			return false;
		}
	}
	
}
