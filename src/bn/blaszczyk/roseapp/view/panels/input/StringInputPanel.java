package bn.blaszczyk.roseapp.view.panels.input;

@SuppressWarnings("serial")
public class StringInputPanel extends AbstractInputPanel<String> {
	
	private final int maxLength;
	
	public StringInputPanel( String name, String defvalue, int maxLength )
	{
		super(name, defvalue);
		this.maxLength = maxLength;
		setValue(defvalue);
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
		return getValue().length() <= maxLength;
	}
	
}
