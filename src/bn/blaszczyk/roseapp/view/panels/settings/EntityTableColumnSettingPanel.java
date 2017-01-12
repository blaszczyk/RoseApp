package bn.blaszczyk.roseapp.view.panels.settings;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import bn.blaszczyk.rose.model.*;
import bn.blaszczyk.roseapp.controller.ModelController;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.factories.*;
import bn.blaszczyk.roseapp.view.panels.*;
import bn.blaszczyk.roseapp.view.panels.VariableRowsPanel.Indexable;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.roseapp.tools.Preferences.*;

@SuppressWarnings("serial")
public class EntityTableColumnSettingPanel extends TabbedPanel{

	
	
	public EntityTableColumnSettingPanel()
	{
		for(Class<?> type : TypeManager.getEntityClasses())
		{
			String[] contentOptions = getContentOptions(type);
			final List<SingleRowPanel> panels = new ArrayList<>();
			final int columnCount = getIntegerEntityValue(type, COLUMN_COUNT, 1);
			for(int index = 0; index < columnCount; index++)
			{
				String columnContent = getStringEntityValue(type, COLUMN_CONTENT + index, "" );
				int columnWidth = getIntegerEntityValue(type, COLUMN_WIDTH + index, 0 );
				panels.add(new SingleRowPanel(type, contentOptions, columnWidth, columnContent));
			}
			VariableRowsPanel varPanel = new VariableRowsPanel(panels, () -> new SingleRowPanel(type,  contentOptions, 0, "") ){
				@Override
				public void save(ModelController controller)
				{
					super.save(controller);
					putIntegerEntityValue(type, COLUMN_COUNT, getPanelCount());
				}
			};
			TitleButtonsPanel tbPanel = new TitleButtonsPanel("Columns", varPanel,false);
			tbPanel.addButton("Generate", null, e -> {
				int width = BASIC_WIDTH / panels.size();
				for(SingleRowPanel panel : panels)
					panel.setColumnWidth(width);
				
			});
			addTab(type.getSimpleName(), tbPanel);
		}	
	}

	private static String[] getContentOptions(Class<?>type)
	{
		Entity entity = TypeManager.getEntity(type);
		List<String> options = new ArrayList<>();
		options.add("id");
		for(Field field : entity.getFields())
			options.add(field.getName());
		for(EntityField field : entity.getEntityFields())
		{
			options.add(field.getName());
			if(!field.getType().isSecondMany())
			{
				options.add(field.getName() + ".id");
				for(Field sfield : field.getEntity().getFields())
					options.add(field.getName() + "." + sfield.getName());
				for(EntityField sfield : field.getEntity().getEntityFields())
					options.add(field.getName() + "." + sfield.getName());
			}
		}			
		String[] contentOptions = new String[options.size()];
		options.toArray(contentOptions);
		return contentOptions;
	}
	

	private class SingleRowPanel extends AbstractRosePanel implements Indexable{
		private final Class<?> type;
		private final JComboBox<String> contentBox;
		private final JTextField widthField;
		private int index=-1;

		public SingleRowPanel( Class<?> type, String[] contentOptions, int columnWidth, String columnContent )
		{
			super(null);
			this.type = type;
			setBackground(FULL_PNL_BACKGROUND);
			
			JLabel label = LabelFactory.createLabel( Messages.get("content") +  " : ", SwingConstants.RIGHT);
			label.setBounds(0,0,PROPERTY_WIDTH, LBL_HEIGHT);
			add(label);
			
			contentBox = new JComboBox<>(contentOptions);
			contentBox.setFont(VALUE_FONT);
			contentBox.setBackground(Color.WHITE);
			contentBox.setBounds(PROPERTY_WIDTH,0, VALUE_WIDTH, LBL_HEIGHT);
			contentBox.setSelectedItem(columnContent);
			contentBox.addActionListener(e -> notify(e));
			add(contentBox);
			
			label = LabelFactory.createLabel( Messages.get("width") + " : ", SwingConstants.RIGHT );
			label.setBounds(PROPERTY_WIDTH + VALUE_WIDTH,0,PROPERTY_WIDTH, LBL_HEIGHT);
			add(label);	
			
			widthField = TextFieldFactory.createIntegerField(columnWidth, e -> notify(e));
			widthField.setBounds(2 *PROPERTY_WIDTH + VALUE_WIDTH,0, PROPERTY_WIDTH, LBL_HEIGHT);
			add(widthField);
		}
		
		private void notify(ActionEvent e)
		{
			changeListener.notify(new RoseEvent(this));
		}

		public void setColumnWidth(int width)
		{
			widthField.setText(String.valueOf(width));
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
			putStringEntityValue(type, COLUMN_CONTENT + index, contentBox.getSelectedItem().toString().toLowerCase() );
			putIntegerEntityValue(type, COLUMN_WIDTH + index, Integer.parseInt(widthField.getText()));
		}

		@Override
		public int getFixWidth()
		{
			return 3 * PROPERTY_WIDTH + VALUE_WIDTH;
		}

		@Override
		public int getFixHeight()
		{
			return LBL_HEIGHT;
		}
		
	}
	

//	private static String generateColContents(Entity entity)
//	{
//		boolean first = true;
//		StringBuilder builder = new StringBuilder();
//		for(Field field : entity.getFields())
//		{
//			if(first)
//				first = false;
//			else
//				builder.append(";");
//			builder.append( field.getName() );
//		}
//		return builder.toString();
//	}
//
//	private static String generateColWidths(Entity entity)
//	{
//		boolean first = true;
//		StringBuilder builder = new StringBuilder();
//		for(Field field : entity.getFields())
//		{
//			if(first)
//				first = false;
//			else
//				builder.append(";");
//			int width = 0;
//			if(field instanceof EnumField)
//				width = ((EnumField)field).getEnumName().length() * 2;
//			else if(field instanceof PrimitiveField)
//			{
//				PrimitiveField pField = (PrimitiveField) field;
//				switch(pField.getType())
//				{
//				case CHAR:
//				case VARCHAR:
//				case NUMERIC:
//					width = pField.getLength1() * 2;
//					break;
//				case DATE:
//				case BOOLEAN:
//				case INT:
//					width = 50;
//					break;
//				}
//			}
//			builder.append( width );
//		}
//		return builder.toString();
//	}

	
}
