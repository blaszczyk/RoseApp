package bn.blaszczyk.roseapp.view.panels.settings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.rose.model.*;
import bn.blaszczyk.roseapp.view.panels.*;
import bn.blaszczyk.roseapp.view.panels.VariableRowsPanel.Indexable;
import bn.blaszczyk.rosecommon.tools.TypeManager;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.rosecommon.tools.Preferences.*;
import static bn.blaszczyk.roseapp.tools.AppPreference.*;

public class StartSettingPanelFactory{

	public StartSettingPanelFactory()
	{
	}
	
	public static RosePanel create()
	{	
		String[] contentOptions = getContentOptions();
		final List<ButtonConfigPanel> panels = new ArrayList<>();
		final int columnCount = getIntegerValue( START_BUTTON_COUNT);
		for(int index = 0; index < columnCount; index++)
		{
			String buttonOption = getStringValue(START_BUTTON.append(index));
			panels.add(new ButtonConfigPanel( contentOptions, buttonOption ));
		}
		VariableRowsPanel varPanel = new VariableRowsPanel(panels, () -> new ButtonConfigPanel(contentOptions, "") ){
			private static final long serialVersionUID = -4398203237706957274L;
			@Override
			public void save() throws RoseException
			{
				super.save();
				putIntegerValue(START_BUTTON_COUNT, getPanelCount());
			}
			
		};
		return TitleButtonsPanel.withBorder("Start Buttons", varPanel);
	}

	private static String[] getContentOptions()
	{
		String[] contentOptions = new String[TypeManager.getEntityCount()];
		int count = 0;
		for(Entity entity : TypeManager.getEntites())
			contentOptions[count++] = entity.getObjectName().toLowerCase();		
		return contentOptions;
	}
	

	private static class ButtonConfigPanel extends AbstractRosePanel implements Indexable{
		private static final long serialVersionUID = 5561380409172085631L;
		
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
			contentBox.addActionListener( e -> notify(true,e));
			add(contentBox);
		}

		@Override
		public void setIndex(int index)
		{
			this.index = index;
		}

		@Override
		public void save() throws RoseException
		{
			super.save();
			putStringValue( START_BUTTON.append(index), contentBox.getSelectedItem().toString().toLowerCase() );
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
