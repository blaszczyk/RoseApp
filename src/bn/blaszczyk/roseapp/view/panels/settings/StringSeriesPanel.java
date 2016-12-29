package bn.blaszczyk.roseapp.view.panels.settings;

import bn.blaszczyk.roseapp.controller.ModelController;
import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.factories.TextFieldFactory;
import bn.blaszczyk.roseapp.view.panels.AbstractEntityPanel;

import bn.blaszczyk.roseapp.view.panels.EntityPanel;
import bn.blaszczyk.roseapp.view.panels.TitleButtonsPanel;
import bn.blaszczyk.roseapp.view.panels.VariableRowsPanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.roseapp.tools.Preferences.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;


@SuppressWarnings("serial")
public class StringSeriesPanel extends VariableRowsPanel {

	public static StringSeriesPanel newInstance(String countKey, String valueKey)
	{
		int faecherCount = getIntegerValue(countKey, 0);
		List<ValuePanel> panels = new ArrayList<>();
		for(int i = 0; i < faecherCount; i++)
		{
			String fach = getStringValue(valueKey + i, "");
			panels.add(new ValuePanel(fach));
		}
		return new StringSeriesPanel(panels, () -> new ValuePanel(""), countKey, valueKey);
	}
	
	public static EntityPanel newInstanceWithTitle(String title, String count_key, String value_key)
	{
		return new TitleButtonsPanel(title, newInstance(count_key, value_key), false);
	}
	
	private final String countKey;
	private final String valueKey;
	
	private StringSeriesPanel(Iterable<? extends EntityPanel> panels, EntityPanelCreator creator, String countKey, String valueKey)
	{
		super(panels, creator);
		this.countKey = countKey;
		this.valueKey = valueKey;
	}
	
	@Override
	public void save(ModelController controller)
	{
		super.save(controller);
		putIntegerValue(countKey, getPanelCount());
		for(int i = 0; i < getPanelCount(); i++)
			putStringValue(valueKey + i, getPanel(i).getShownObject().toString());
	}



	private static class ValuePanel extends AbstractEntityPanel implements KeyListener
	{
		private final JTextField textField;

		public ValuePanel(String fach)
		{
			super(null);
			textField = TextFieldFactory.createTextField(fach, e -> changeListener.notify(new RoseEvent(this)));
			textField.addKeyListener(this);
			textField.setBounds(0, 0, VALUE_WIDTH, LBL_HEIGHT);
			add(textField);
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
		
		@Override
		public Object getShownObject()
		{
			return textField.getText();
		}

		@Override
		public void keyTyped(KeyEvent e)
		{
			changeListener.notify(new RoseEvent(this));
			textField.requestFocusInWindow();
		}

		@Override
		public void keyPressed(KeyEvent e)
		{
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
		}
		
	}
	
}
