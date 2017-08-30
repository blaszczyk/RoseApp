package bn.blaszczyk.roseapp.view.panels.settings;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import bn.blaszczyk.rose.RoseException;
import bn.blaszczyk.roseapp.RoseAppLauncher;
import bn.blaszczyk.roseapp.tools.AppPreference;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.panels.AbstractRosePanel;
import bn.blaszczyk.roseapp.view.panels.AlignPanel;
import bn.blaszczyk.roseapp.view.panels.RosePanel;
import bn.blaszczyk.roseapp.view.panels.settings.PrimitiveSettingsPanel.PrimitiveSetting;
import bn.blaszczyk.rosecommon.tools.Preference;
import bn.blaszczyk.rosecommon.tools.Preferences;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.rosecommon.tools.CommonPreference.*;

public class AccessSettingsPanel extends AlignPanel {

	private static final long serialVersionUID = -5421708668639520992L;

	private static final List<PrimitiveSetting> DB_ACCESS_SETTINGS = Arrays.asList( 
			new PrimitiveSetting( DB_HOST ),
			new PrimitiveSetting( DB_PORT, "\\d{1,5}" ),
			new PrimitiveSetting( DB_NAME ),
			new PrimitiveSetting( DB_USER ),
			new PrimitiveSetting( DB_PASSWORD, true));
	
	private static final List<PrimitiveSetting> SERVICE_ACCESS_SETTINGS = Arrays.asList( 
			new PrimitiveSetting( SERVICE_HOST ),
			new PrimitiveSetting( SERVICE_PORT ));
	
	private final RosePanel dbAccessPanel = PrimitiveSettingsPanel.createForApp(DB_ACCESS_SETTINGS);
	private final RosePanel serviceAccessPanel = PrimitiveSettingsPanel.createForApp(SERVICE_ACCESS_SETTINGS);

	private final JRadioButton dbAccessButton = new JRadioButton(Messages.get("Database"));
	private final JRadioButton serviceAccessButton = new JRadioButton(Messages.get("Service"));
	
	public AccessSettingsPanel()
	{
		super(H_SPACING);
		setTitle("Access Mode");
		
		final ButtonGroup accessGroup = new ButtonGroup();
		accessGroup.add(serviceAccessButton);
		accessGroup.add(dbAccessButton);
		
		addPanel(new RadioButtonWrapper(serviceAccessButton, 200, 40, AppPreference.ACCESS_MODE, RoseAppLauncher.ACCESS_SERVICE));
		addPanel(serviceAccessPanel);
		addPanel(new RadioButtonWrapper(dbAccessButton, 200, 40, AppPreference.ACCESS_MODE, RoseAppLauncher.ACCESS_DATABASE));
		addPanel(dbAccessPanel);
				
		enablePanels();
		refresh();
		addRoseListener(e -> enablePanels());
	}
	
	private void enablePanels()
	{
		final boolean serviceEnabled = serviceAccessButton.isSelected();
		serviceAccessPanel.getPanel().setEnabled(serviceEnabled);
		setEnabledAll(serviceAccessPanel, serviceEnabled);
		final boolean dbEnabled = dbAccessButton.isSelected();
		dbAccessPanel.getPanel().setEnabled(dbEnabled);
		setEnabledAll(dbAccessPanel, dbEnabled);
	}
	
	private void setEnabledAll(final RosePanel panel, final boolean enabled)
	{
		Arrays.stream(panel.getPanel().getComponents())
			.forEach(c -> c.setEnabled(enabled));
	}
	
	@Override
	public void save() throws RoseException
	{
		if(hasChanged())
			warning("Restart required for changes to apply.", "Access Mode Changes");
		super.save();
	}
	
	private static class RadioButtonWrapper extends AbstractRosePanel
	{
		private static final long serialVersionUID = 274477394299645446L;
		
		private final JRadioButton button;
		private final int width;
		private final int height;
		private final Preference preference;
		private final Object value;
		
		public RadioButtonWrapper(final JRadioButton button, final int width, final int height, final Preference preference, final Object value )
		{
			super(new GridLayout());
			this.button = button;
			this.width = width;
			this.height = height;
			this.preference = preference;
			this.value = value;
			final boolean selected = value.equals(Preferences.getValue(preference));
			button.setSelected(selected);
			button.setFont(PROPERTY_FONT);
			button.addActionListener(e -> selectButton(e));
			add(button);
		}
		
		private void selectButton(final ActionEvent e)
		{
			notify(true, e);
		}
		
		@Override
		public int getFixHeight()
		{
			return height;
		}
		
		@Override
		public int getFixWidth()
		{
			return width;
		}
		
		@Override
		public void save() throws RoseException
		{
			if(button.isSelected())
				Preferences.putValue(preference, value);
			super.save();
		}
		
	}
	
}
