package bn.blaszczyk.roseapp.view.panels.input;

public class IntegerInputPanel extends AbstractInputPanel<Integer> {
	
	private static final long serialVersionUID = 4161028851889026808L;

	public IntegerInputPanel( String name, Integer defValue )
	{
		super(name,defValue);
		setValue(defValue);
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
