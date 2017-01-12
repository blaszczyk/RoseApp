package bn.blaszczyk.roseapp.view.panels.input;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.RoseListener;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;


@SuppressWarnings("serial")
public class BooleanInputPanel extends JPanel implements InputPanel<Boolean>
{

	private final JCheckBox checkBox = new JCheckBox();
	private boolean defValue;
	
	public BooleanInputPanel(String name, Boolean defValue)
	{
		this.defValue = defValue;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		checkBox.setSelected(defValue);
		checkBox.setText(Messages.get(name));
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
	public void setRoseListener(RoseListener l)
	{
		checkBox.addChangeListener( e -> l.notify(new RoseEvent(this,true)));
	}

	@Override
	public void resetDefValue()
	{
		this.defValue = getValue();
	}
	
}
