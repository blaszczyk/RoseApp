package bn.blaszczyk.roseapp.view.panels.settings;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import bn.blaszczyk.rose.model.*;
import bn.blaszczyk.roseapp.controller.ModelController;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.panels.*;
import bn.blaszczyk.roseapp.view.panels.VariableRowsPanel.Indexable;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.roseapp.tools.Preferences.*;

@SuppressWarnings("serial")
public class StartSettingPanelFactory{

	public StartSettingPanelFactory()
	{
	}
	
	public static EntityPanel create()
	{	
		String[] contentOptions = getContentOptions();
		final List<ButtonConfigPanel> panels = new ArrayList<>();
		final int columnCount = getIntegerValue( START_BUTTON_COUNT, 1);
		for(int index = 0; index < columnCount; index++)
		{
			String buttonOption = getStringValue(START_BUTTON + index, "" );
			panels.add(new ButtonConfigPanel( contentOptions, buttonOption ));
		}
		VariableRowsPanel varPanel = new VariableRowsPanel(panels, () -> new ButtonConfigPanel(contentOptions, "") ){

			@Override
			public void save(ModelController controller)
			{
				super.save(controller);
				putIntegerValue(START_BUTTON_COUNT, getPanelCount());
			}
			
		};
		TitleButtonsPanel tbPanel = new TitleButtonsPanel("Start Buttons", varPanel,false);
		return tbPanel;
	}

	private static String[] getContentOptions()
	{
		String[] contentOptions = new String[TypeManager.getEntityCount()];
		int count = 0;
		for(Entity entity : TypeManager.getEntites())
			contentOptions[count++] = entity.getObjectName().toLowerCase();		
		return contentOptions;
	}
	

	private static class ButtonConfigPanel extends AbstractEntityPanel implements Indexable{
		private final JComboBox<String> contentBox;
		private int index=-1;

		public ButtonConfigPanel( String[] contentOptions, String columnContent )
		{
			super(null);
			setBackground(FULL_PNL_BACKGROUND);		
			contentBox = new JComboBox<>(contentOptions);
			contentBox.setFont(VALUE_FONT);
			contentBox.setBackground(Color.WHITE);
			contentBox.setBounds(0,0, VALUE_WIDTH, LBL_HEIGHT);
			contentBox.setSelectedItem(columnContent);
			contentBox.addActionListener( e -> selected( e ));
			add(contentBox);
		}
		
		private void selected(ActionEvent e)
		{
			changeListener.notify(new RoseEvent(this));
		}

		@Override
		public void setIndex(int index)
		{
			this.index = index;
		}

		@Override
		public void save(ModelController controller)
		{
			super.save(controller);
			putStringValue( START_BUTTON + index, contentBox.getSelectedItem().toString().toLowerCase() );
		}

		@Override
		public int getFixWidth()
		{
			return VALUE_WIDTH;
		}

		@Override
		public int getFixHeight()
		{
			return LBL_HEIGHT;
		}
		
	}
	
}
