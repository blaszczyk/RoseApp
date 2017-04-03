package bn.blaszczyk.roseapp.view.panels;

import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bn.blaszczyk.roseapp.view.factories.ButtonFactory;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class TitleButtonsPanel extends AbstractRosePanel {

	private static final long serialVersionUID = 298455444782285400L;

	public static TitleButtonsPanel withBorder( String title, JComponent component, int cWidth, int cHeight )
	{
		return new TitleButtonsPanel(title, component, cWidth, cHeight, false);
	}
	
	public static TitleButtonsPanel noBorder( String title, JComponent component, int cWidth, int cHeight )
	{
		return new TitleButtonsPanel(title, component, cWidth, cHeight, true);
	}
	
	public static TitleButtonsPanel withBorder( String title, RosePanel panel )
	{
		return new TitleButtonsPanel(title, panel, false);
	}
	
	public static TitleButtonsPanel noBorder( String title, RosePanel panel )
	{
		return new TitleButtonsPanel(title, panel, true);
	}
	
	private int height;
	private int width;
	
	private final boolean noBorder;
	
	private RosePanel panel = null;
	
	private int buttonCount = 0;

	private TitleButtonsPanel( String title, JComponent component, int cWidth, int cHeight, boolean noBorder ) // TODO: adjust widths
	{
		this.noBorder = noBorder;
		this.height = 2 * V_SPACING + SUBTITLE_HEIGHT + (component == null ? 0 :  V_SPACING + cHeight);
		this.width =  ( noBorder ? 0 : 2 * H_SPACING ) + cWidth;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		setSubTitle(title);
		setComponent(component, cWidth, cHeight);
	}
	
	private TitleButtonsPanel( String title, RosePanel panel, boolean noBorder)
	{
		this(title, panel.getPanel(), panel.getFixWidth(), panel.getFixHeight(), noBorder);
		this.panel = panel;
		panel.addRoseListener(this);
	}
	
	public RosePanel getSubPanel()
	{
		return panel;
	}


	private void setComponent(JComponent component, int cWidth, int cHeight)
	{
		if(component != null)
		{
			component.setBounds( noBorder ? 0 : H_SPACING, 2 * V_SPACING + SUBTITLE_HEIGHT, cWidth, cHeight);
			add(component);
		}
	}


	public void addButton(String name, String iconFile, ActionListener listener)
	{
		JButton button = ButtonFactory.createIconButton(name, iconFile, listener);
		button.setBounds( 2 * H_SPACING + SUBTITLE_WIDTH + (H_SPACING + SUBTLTBTN_WIDTH) * buttonCount++ , V_SPACING, SUBTLTBTN_WIDTH, SUBTITLE_HEIGHT);
		add(button);
	}

	protected void setSubTitle( String title )
	{
		JLabel lblTitle = LabelFactory.createLabel(title, null, SUBTITLE_FONT, TITLE_FG);
		lblTitle.setBackground(TITLE_BG);
		lblTitle.setBounds(H_SPACING, V_SPACING, SUBTITLE_WIDTH, SUBTITLE_HEIGHT);
		lblTitle.setOpaque(true);
		add(lblTitle);
	}

	@Override
	public int getFixWidth()
	{
		return width;
	}

	@Override
	public int getFixHeight()
	{
		return height;
	}

	@Override
	public void refresh()
	{
		if(panel != null)
		{
			JPanel jPanel = panel.getPanel();
			remove(jPanel);
			jPanel.setBounds(H_SPACING, 2 * V_SPACING + SUBTITLE_HEIGHT, panel.getFixWidth(), panel.getFixHeight());
			this.height = 2 * V_SPACING + SUBTITLE_HEIGHT + panel.getFixHeight();
			this.width =  ( noBorder ? 0 : 2 * H_SPACING ) + panel.getFixWidth();
			add(jPanel);
		}
		super.refresh();
	}

	@Override
	public Object getShownObject()
	{
		if(panel != null)
			return panel.getShownObject();
		return super.getShownObject();
	}

	@Override
	public void save()
	{
		super.save();
		if(panel != null)
			panel.save();
	}
	
}
