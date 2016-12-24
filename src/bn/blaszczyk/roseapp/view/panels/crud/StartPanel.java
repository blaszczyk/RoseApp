package bn.blaszczyk.roseapp.view.panels.crud;

import javax.swing.JButton;

import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.panels.AbstractEntityPanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.roseapp.tools.Preferences.*;

@SuppressWarnings("serial")
public class StartPanel extends AbstractEntityPanel {
	
	
	
	
	public StartPanel( GUIController guiController)
	{
		setLayout(null);
		int heigth = START_V_SPACING;
		for(int i = 0; i < getIntegerValue(START_BUTTON_COUNT, 0); i++)
		{
			String entityName = getStringValue(START_BUTTON + i, "UNDEFINED");
			JButton button = new JButton(entityName);
			button.setBounds(START_H_SPACING, heigth, START_BTN_WIDTH, START_BTN_HEIGHT);
			button.setFont(START_BTN_FONT);
			button.addActionListener( e -> guiController.openFullListTab(TypeManager.getClass(entityName)));
			add(button);
			heigth += START_V_SPACING + START_BTN_HEIGHT;
		}
	}

	@Override
	public Object getShownObject()
	{
		return this;
	}
	
}
