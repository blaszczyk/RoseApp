package bn.blaszczyk.roseapp.view.panels.settings;

import bn.blaszczyk.roseapp.view.factories.TextFieldFactory;
import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;

import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.TitleButtonsPanel;
import bn.blaszczyk.roseapp.view.panels.VariableRowsPanel;
import bn.blaszczyk.rosecommon.RoseException;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.rosecommon.tools.Preferences.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

public class StringSeriesPanel extends VariableRowsPanel {

	private static final long serialVersionUID = 4388787458446817253L;

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
	
	public static RosePanel newInstanceWithTitle(String title, String count_key, String value_key)
	{
		return TitleButtonsPanel.withBorder(title, newInstance(count_key, value_key));
	}
	
	private final String countKey;
	private final String valueKey;
	
	private StringSeriesPanel(Iterable<? extends RosePanel> panels, EntityPanelCreator creator, String countKey, String valueKey)
	{
		super(panels, creator);
		this.countKey = countKey;
		this.valueKey = valueKey;
	}
	
	@Override
	public void save() throws RoseException
	{
		super.save();
		putIntegerValue(countKey, getPanelCount());
		for(int i = 0; i < getPanelCount(); i++)
			putStringValue(valueKey + i, getPanel(i).getShownObject().toString());
	}



	private static class ValuePanel extends AbstractRosePanel implements KeyListener
	{
		private static final long serialVersionUID = -3336505661793977885L;
		
		private final JTextField textField;

		public ValuePanel(String fach)
		{
			super(null);
			textField = TextFieldFactory.createTextField(fach, e -> notify(true,e));
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
			notify(true,e);
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
