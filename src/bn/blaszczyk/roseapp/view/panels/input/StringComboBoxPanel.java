package bn.blaszczyk.roseapp.view.panels.input;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.RoseListener;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;
import bn.blaszczyk.roseapp.view.tools.EntityComboBox;

@SuppressWarnings("serial")
public class StringComboBoxPanel extends JPanel implements InputPanel<String> {
	

	private final JLabel label;
	private final JComboBox<String> comboBox;
	private String defValue;
	
	public StringComboBoxPanel( String name, String defValue, String[] values )
	{
		this.defValue = defValue;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		
		label =  LabelFactory.createLabel(name + ": ", PROPERTY_FONT, PROPERTY_FG, SwingConstants.RIGHT); 
		label.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
		add(label);

		comboBox = new EntityComboBox<>(values, VALUE_WIDTH, true);
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
	public String getValue()
	{
		return comboBox.getSelectedItem().toString();
	}
	
	public int getSelectedIndex()
	{
		return comboBox.getSelectedIndex();
	}

	@Override
	public void setValue(String value)
	{
		if(value != null)
			comboBox.setSelectedItem(value);
	}

	@Override
	public void setRoseListener(RoseListener l)
	{
		comboBox.addItemListener( e -> l.notify(new RoseEvent(this)));
	}

	@Override
	public void resetDefValue()
	{
		this.defValue = getValue();
	}
	
}
