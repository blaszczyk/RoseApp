package bn.blaszczyk.roseapp.view.panels.input;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.RoseListener;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class EnumInputPanel extends JPanel implements InputPanel<Enum<?>> {
	
	private final JLabel label;
	private final JComboBox<Enum<?>> comboBox;
	private final Enum<?> defValue;
	
	public EnumInputPanel( String name, Enum<?> defValue )
	{
		this.defValue = defValue;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		
		label =  LabelFactory.createOpaqueLabel(name + ": ", PROPERTY_FONT, PROPERTY_FG, PROPERTY_BG, SwingConstants.RIGHT); 
		label.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
		add(label);

		comboBox = new JComboBox<>(defValue.getClass().getEnumConstants());
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
	public void setValue(Enum<?> value)
	{
		comboBox.setSelectedItem(value);
	}

	@Override
	public void setRoseListener(RoseListener l)
	{
		comboBox.addItemListener( e -> l.notify(new RoseEvent(this)));
	}

	
}
