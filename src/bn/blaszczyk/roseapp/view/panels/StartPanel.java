package bn.blaszczyk.roseapp.view.panels;

import javax.swing.JButton;

import bn.blaszczyk.roseapp.controller.GUIController;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class StartPanel extends AbstractEntityPanel {
	
	
	
	
	public StartPanel( GUIController guiController, Class<?>... types)
	{
		setLayout(null);
		int heigth = START_V_SPACING;
		for(Class<?> type : types)
		{
			JButton button = new JButton(type.getSimpleName() + "s");
			button.setBounds(START_H_SPACING, heigth, START_BTN_WIDTH, START_BTN_HEIGHT);
			button.setFont(START_BTN_FONT);
			button.addActionListener( e -> guiController.openFullListTab(type));
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
