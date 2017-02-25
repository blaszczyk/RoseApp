package bn.blaszczyk.roseapp.view.panels.input;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.RoseListener;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class EnumInputPanel extends JPanel implements InputPanel<Object> {
	
	private static final long serialVersionUID = -8633950334241795735L;
	
	private final JLabel label;
	private final JComboBox<Object> comboBox;
	private Object defValue;
	
	public EnumInputPanel( String name, Class<?> enumType, Object defValue )
	{
		this.defValue = defValue;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		
		label =  LabelFactory.createLabel(name + ": ", PROPERTY_FONT, PROPERTY_FG, SwingConstants.RIGHT); 
		label.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
		add(label);

		comboBox = new JComboBox<>(enumType.getEnumConstants());
		comboBox.setFont(VALUE_FONT);
		comboBox.setForeground(VALUE_FG);
		comboBox.setBounds( PROPERTY_WIDTH + H_SPACING , 0, VALUE_WIDTH , LBL_HEIGHT);
		setValue(defValue);
		add(comboBox);
	}
		
	@Override
	public String getName()
	{
		return label.getText();
	}
	
	@Override
	public JPanel getPanel()
	{
		return this;
	}

	@Override
	public boolean hasChanged()
	{
		return !getValue().equals(defValue);
	}

	@Override
	public boolean isInputValid()
	{
		return true;
	}

	@Override
	public Enum<?> getValue()
	{
		return (Enum<?>) comboBox.getSelectedItem();
	}

	@Override
	public void setValue(Object value)
	{
		comboBox.setSelectedItem(value);
	}

	@Override
	public void setRoseListener(RoseListener l)
	{
		comboBox.addItemListener( e -> l.notify(new RoseEvent(this,true,e)));
	}

	@Override
	public void resetDefValue()
	{
		this.defValue = getValue();
	}
	
	@Override
	public String toString()
	{
		return "EnumInputPanel value = " + getValue();
	}
	
}
