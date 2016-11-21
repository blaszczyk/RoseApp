package bn.blaszczyk.roseapp.view.inputpanels;

@SuppressWarnings("serial")
public class IntegerInputPanel extends AbstractInputPanel<Integer> {
	
	private final Integer defvalue;
	
	public IntegerInputPanel( String name, Integer defvalue )
	{
		super(name);
		this.defvalue = defvalue;
		setValue(defvalue);
	}
	
	@Override
	public Integer getValue()
	{
		String text = textField.getText();
		if(text == null || text == "")
			return 0;
		return Integer.parseInt(textField.getText());
	}
	
	@Override
	public void setValue(Integer value)
	{	
		textField.setText(value.toString());
	}

	@Override
	public boolean hasChanged()
	{
		return !defvalue.equals(getValue()) ;
	}

	@Override
	public boolean isInputValid()
	{
		try{
			getValue();
			return true;
		}
		catch( NumberFormatException e)
		{
			return false;
		}
	}
	
}
