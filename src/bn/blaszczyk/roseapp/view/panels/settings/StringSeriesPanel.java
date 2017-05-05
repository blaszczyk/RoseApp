package bn.blaszczyk.roseapp.view.panels.settings;

import bn.blaszczyk.roseapp.view.factories.TextFieldFactory;
import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;

import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.TitleButtonsPanel;
import bn.blaszczyk.roseapp.view.panels.VariableRowsPanel;
import bn.blaszczyk.rosecommon.RoseException;
import bn.blaszczyk.rosecommon.tools.Preference;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.rosecommon.tools.Preferences.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

public class StringSeriesPanel extends VariableRowsPanel {

	private static final long serialVersionUID = 4388787458446817253L;

	public static StringSeriesPanel newInstance(final Preference countPreference, final Preference valuePreference)
	{
		int faecherCount = getIntegerValue(countPreference);
		List<ValuePanel> panels = new ArrayList<>();
		for(int i = 0; i < faecherCount; i++)
		{
			String fach = getStringValue(valuePreference.append(i));
			panels.add(new ValuePanel(fach));
		}
		return new StringSeriesPanel(panels, () -> new ValuePanel(""), countPreference, valuePreference);
	}
	
	public static RosePanel newInstanceWithTitle(final String title, final Preference countPreference, final Preference valuePreference)
	{
		return TitleButtonsPanel.withBorder(title, newInstance(countPreference, valuePreference));
	}
	
	private final Preference countPreference;
	private final Preference valuePreference;
	
	private StringSeriesPanel(Iterable<? extends RosePanel> panels, EntityPanelCreator creator, Preference countPreference, Preference valuePreference)
	{
		super(panels, creator);
		this.countPreference = countPreference;
		this.valuePreference = valuePreference;
	}
	
	@Override
	public void save() throws RoseException
	{
		super.save();
		putIntegerValue(countPreference, getPanelCount());
		for(int i = 0; i < getPanelCount(); i++)
			putStringValue(valuePreference.append(i), getPanel(i).getShownObject().toString());
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
