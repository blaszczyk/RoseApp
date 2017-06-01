package bn.blaszczyk.roseapp.view.panels.crud;

import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.RoseAppLauncher;
import bn.blaszczyk.roseapp.controller.GUIController;
import bn.blaszczyk.roseapp.model.StringFieldType;
import bn.blaszczyk.roseapp.tools.AppPreference;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;
import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;
import bn.blaszczyk.roseapp.view.panels.input.FileInputPanel;
import bn.blaszczyk.roseapp.view.panels.input.ServerFileInputPanel;
import bn.blaszczyk.rosecommon.tools.Preferences;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class BasicViewPanel extends AbstractRosePanel {

	private static final long serialVersionUID = -7368178251620381041L;
	
	private static final int WIDTH = 2 * H_SPACING + BASIC_WIDTH;
	private int height = V_SPACING;

	private final Readable entity;
	
	public BasicViewPanel( Readable entity, final GUIController controller )
	{
		this.entity = entity;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		for(int i = 0; i < entity.getFieldCount(); i++)
		{
			String regex = controller.getBehaviour().fieldType(entity, entity.getFieldName(i));
			if(StringFieldType.fromRegex(regex).equals(StringFieldType.FILE))
				addFile(entity.getFieldName(i), entity.getFieldValue(i).toString());
			else
				addValue( entity.getFieldName(i), entity.getFieldValue(i) );
		}
	}

	private void addValue(String property, Object value)
	{
		
		JLabel lblProperty = LabelFactory.createLabel(property + ": ", SwingConstants.RIGHT);
		lblProperty.setBounds( H_SPACING, height, PROPERTY_WIDTH, LBL_HEIGHT );
		add(lblProperty);
		
		JLabel lblValue = LabelFactory.createLabel( (value instanceof Date) ? DATE_FORMAT.format(value) : " " + value);
		lblValue.setBounds( 2 * H_SPACING + PROPERTY_WIDTH , height, VALUE_WIDTH, LBL_HEIGHT);
		add(lblValue);
			
		height += LBL_HEIGHT + V_SPACING;
	}	
	
	private void addFile(String property, String value)
	{
		final boolean serviceMode = Preferences.getStringValue(AppPreference.ACCESS_MODE).equals(RoseAppLauncher.ACCESS_SERVICE);
		final JPanel panel;
		if(serviceMode)
			panel = ServerFileInputPanel.view(property, value);
		else
			panel = FileInputPanel.view(property, value);
		panel.setBounds( H_SPACING , height, BASIC_WIDTH, LBL_HEIGHT );
		add(panel);
			
		height += LBL_HEIGHT + V_SPACING;
	}

	@Override
	public int getFixWidth()
	{
		return WIDTH;
	}

	@Override
	public int getFixHeight()
	{
		return height;
	}

	@Override
	public Object getShownObject()
	{
		return entity;
	}
	
}
