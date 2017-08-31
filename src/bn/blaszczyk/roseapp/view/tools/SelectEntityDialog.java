package bn.blaszczyk.roseapp.view.tools;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.rose.model.EntityModel;
import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.rosecommon.controller.ModelController;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

import java.util.Collections;
import java.util.List;

public class SelectEntityDialog extends JDialog {

	private static final long serialVersionUID = 1656535348089642568L;

	private final JFrame owner;
	
	private final EntityComboBox<Readable> selectBox;

	
	public SelectEntityDialog(JFrame owner, String title, ModelController modelController, Class<? extends Readable> type)
	{
		super(owner, title, true);
		this.owner = owner;
		setLayout(null);
		setBackground(FULL_PNL_BACKGROUND);
		setSize( SEL_DIAL_WIDTH, SEL_DIAL_HEIGTH );

		List<? extends Readable> entitiesList;
		try
		{ 
			entitiesList = modelController.getEntities(type);
		}
		catch (RoseException e) 
		{
			entitiesList = Collections.emptyList();
		}
		Readable[] entities = new Readable[entitiesList.size()];
		selectBox = new EntityComboBox<>(entities, BASIC_WIDTH, true);
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
	
	public EntityModel getSelectedEntity()
	{
		return (EntityModel) selectBox.getSelectedItem();
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
