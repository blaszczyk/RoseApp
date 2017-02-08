package bn.blaszczyk.roseapp.view.panels.input;

import java.util.regex.Pattern;

public class StringInputPanel extends AbstractInputPanel<String> {
	
	private static final long serialVersionUID = -8455942582317512323L;
	
	private final int maxLength;
	private Pattern pattern = null;
	
	public StringInputPanel( String name, String defvalue, int maxLength, String regex )
	{
		super(name, defvalue);
		this.maxLength = maxLength;
		if(regex != null && regex != ".*")
			this.pattern = Pattern.compile(regex);
		setValue(defvalue);
	}
	
	public StringInputPanel( String name, String defvalue, int maxLength )
	{
		this(name, defvalue, maxLength, null);
	}
	
	@Override
	public String getValue()
	{
		return textField.getText();
	}
	
	@Override
	public void setValue(String value)
	{	
		textField.setText(value);
	}

	@Override
	public boolean isInputValid()
	{
		if(pattern != null)
			if(! pattern.matcher(getValue()).matches())
				return false;
		return getValue().length() <= maxLength;
	}
	
}
