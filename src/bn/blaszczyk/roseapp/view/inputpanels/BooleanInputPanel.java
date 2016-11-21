package bn.blaszczyk.roseapp.view.inputpanels;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
public class BooleanInputPanel extends JPanel implements InputPanel<Boolean>
{

	private final JCheckBox checkBox = new JCheckBox();
	private final boolean defValue;
	
	public BooleanInputPanel(String name, Boolean defValue)
	{
		this.defValue = defValue;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		checkBox.setSelected(defValue);
		checkBox.setText(name);
		checkBox.setFont(PROPERTY_FONT);
		checkBox.setForeground(PROPERTY_FG);
		checkBox.setBackground(PROPERTY_BG);
		checkBox.setOpaque(true);
		checkBox.setBounds(PROPERTY_WIDTH + H_SPACING, 0, VALUE_WIDTH, LBL_HEIGHT);
		add(checkBox);
	}
	
	@Override
	public Boolean getValue()
	{
		return checkBox.isSelected();
	}
	
	@Override
	public void setValue(Boolean value)
	{
		checkBox.setSelected(value);
	}

	@Override
	public String getName()
	{
		return checkBox.getText();
	}

	@Override
	public JPanel getPanel()
	{
		return this;
	}

	@Override
	public boolean hasChanged()
	{
		return defValue ^ checkBox.isSelected();
	}

	@Override
	public boolean isInputValid()
	{
		return true;
	}

	@Override
	public void setChangeListener(ChangeListener l)
	{
		checkBox.addChangeListener(l);
	}
	
}
