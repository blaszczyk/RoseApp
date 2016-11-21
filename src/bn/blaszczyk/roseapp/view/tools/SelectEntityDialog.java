package bn.blaszczyk.roseapp.view.tools;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import bn.blaszczyk.rose.model.Entity;
import bn.blaszczyk.roseapp.controller.FullModelController;
import bn.blaszczyk.roseapp.view.ThemeConstants;
import bn.blaszczyk.roseapp.view.inputpanels.MyComboBox;

@SuppressWarnings("serial")
public class SelectEntityDialog extends JDialog implements ThemeConstants {

	private final JFrame owner;
	
	private final MyComboBox<Entity> selectBox;

	
	public SelectEntityDialog(JFrame owner, String title, FullModelController modelController, Class<?> type)
	{
		super(owner, title, true);
		this.owner = owner;
		setLayout(null);
		setBackground(FULL_PNL_BACKGROUND);
		setSize( SEL_DIAL_WIDTH, SEL_DIAL_HEIGTH );

		Entity[] entities = new Entity[modelController.getAllEntites(type).size()];
		modelController.getAllEntites(type).toArray(entities);
		selectBox = new MyComboBox<>(entities, BASIC_WIDTH, true);
		selectBox.setFont(VALUE_FONT);
		selectBox.setForeground(VALUE_FG);
		selectBox.setBounds(H_SPACING, V_SPACING, SEL_DIAL_BOX_WIDTH, SEL_DIAL_BOX_HEIGHT);
		add(selectBox);

		JButton btnSelect = new JButton("Select");
		btnSelect.setBounds(H_SPACING, 2 * V_SPACING + SEL_DIAL_BOX_HEIGHT, SEL_DIAL_BOX_WIDTH / 2 - H_SPACING, BUTTON_HEIGHT);
		btnSelect.addActionListener( e -> select());
		add(btnSelect);
		
		JButton btnClose = new JButton("Select");
		btnClose.setBounds( SEL_DIAL_BOX_WIDTH / 2  + 2 *H_SPACING, 2 * V_SPACING + SEL_DIAL_BOX_HEIGHT, SEL_DIAL_BOX_WIDTH / 2 - H_SPACING, BUTTON_HEIGHT);
		btnClose.addActionListener( e -> close());
		add(btnClose);
				
	}
	
	public Entity getSelectedEntity()
	{
		return (Entity) selectBox.getSelectedItem();
	}
	
	public void showDialog()
	{
		setLocationRelativeTo(owner);
		setVisible(true);
	}

	private void close()
	{
	}

	private void select()
	{
	}
	
	
	
}
