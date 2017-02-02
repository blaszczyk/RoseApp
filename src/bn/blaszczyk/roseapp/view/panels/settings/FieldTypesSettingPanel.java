package bn.blaszczyk.roseapp.view.panels.settings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import bn.blaszczyk.rose.model.*;
import bn.blaszczyk.roseapp.controller.ModelController;
import bn.blaszczyk.roseapp.model.StringFieldType;
import bn.blaszczyk.roseapp.tools.TypeManager;
import bn.blaszczyk.roseapp.view.factories.*;
import bn.blaszczyk.roseapp.view.panels.*;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.roseapp.tools.Preferences.*;

@SuppressWarnings("serial")
public class FieldTypesSettingPanel extends TabbedPanel{

	private static final Dimension GAP_DIMENSION = new Dimension( 3 * H_SPACING, 3 * V_SPACING);
	
	
	public FieldTypesSettingPanel()
	{
		for(Entity entity : TypeManager.getEntites())
		{
			final List<FieldTypePanel> panels = new ArrayList<>();
			for(Field field : entity.getFields())
				if(field instanceof PrimitiveField)
				{
					PrimitiveField pField = (PrimitiveField) field;
					if( pField.getType().equals(PrimitiveType.VARCHAR) || pField.getType().equals(PrimitiveType.CHAR) )
						panels.add(new FieldTypePanel(entity, pField));
				}
			if(panels.isEmpty())
				continue;
			AbstractPanelContainer<FieldTypePanel> container = new AbstractPanelContainer<FieldTypePanel>() {
				@Override
				public Iterator<FieldTypePanel> iterator()
				{
					return panels.iterator();
				}
			};
			container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
			container.setBorder(BorderFactory.createEmptyBorder(V_SPACING, H_SPACING, V_SPACING, H_SPACING));
			for(FieldTypePanel panel : panels )
			{
				container.add(panel);
				container.add(Box.createRigidArea(GAP_DIMENSION));
			}
			container.registerRoseListener();
			addTab(entity.getSimpleClassName(), container);
		}	
	}
	
	private static class FieldTypePanel extends AbstractRosePanel {
		
		private static final Dimension DIMENSION = new Dimension( 2 * PROPERTY_WIDTH + VALUE_WIDTH, LBL_HEIGHT);
		
		private final Entity entity;
		private final PrimitiveField field;
		private final JComboBox<StringFieldType> fieldTypeBox;
		private final JTextField regexField;

		public FieldTypePanel( Entity entity, PrimitiveField field )
		{
			super(null);
			this.field = field;
			this.entity = entity;
			setBackground(FULL_PNL_BACKGROUND);
			setSize(DIMENSION);
			setMaximumSize(DIMENSION);
			setAlignmentX(RIGHT_ALIGNMENT);
			String regex = getStringEntityValue(entity, FIELD_TYPE + field.getCapitalName(), ".*");
			
			JLabel label = LabelFactory.createLabel( field.getCapitalName() +  " : ", SwingConstants.RIGHT);
			label.setBounds(0,0,PROPERTY_WIDTH, LBL_HEIGHT);
			add(label);
			
			fieldTypeBox = new JComboBox<>(StringFieldType.values());
			fieldTypeBox.setFont(VALUE_FONT);
			fieldTypeBox.setBackground(Color.WHITE);
			fieldTypeBox.setBounds(PROPERTY_WIDTH,0, PROPERTY_WIDTH, LBL_HEIGHT);
			fieldTypeBox.setSelectedItem(StringFieldType.fromRegex(regex));
			fieldTypeBox.addActionListener(e -> checkOther(e));
			add(fieldTypeBox);
			
			regexField = TextFieldFactory.createTextField(regex, e -> notify(false,e));
			regexField.setBounds( 2 * PROPERTY_WIDTH, 0, VALUE_WIDTH, LBL_HEIGHT);
			if(isOther())
				add(regexField);
		}
		
		private void checkOther(ActionEvent e)
		{
			if(isOther())
				add(regexField);
			else
				remove(regexField);
			notify(false,e);
		}
		
		private boolean isOther()
		{
			return fieldTypeBox.getSelectedItem().equals(StringFieldType.OTHER);
		}

		@Override
		public void save(ModelController controller)
		{
			String regex = isOther() ? regexField.getText() : ((StringFieldType)fieldTypeBox.getSelectedItem()).getRegex();
			putStringEntityValue(entity, FIELD_TYPE + field.getCapitalName(), regex);
			super.save(controller);
		}
		
	}

	
}
