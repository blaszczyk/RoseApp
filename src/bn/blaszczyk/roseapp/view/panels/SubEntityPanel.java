package bn.blaszczyk.roseapp.view.panels;

import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class SubEntityPanel extends JPanel implements EntityPanel {

	
	private final int height;
	private final int width;
	
	private boolean changed = false;
	
	private int buttonCount = 0;

	public SubEntityPanel( String title, JComponent component, int cWidth, int cHeight )
	{
		height = 3 * V_SPACING + SUBTITLE_HEIGHT + cHeight;
		width = 2 * H_SPACING + cWidth;
		setLayout(null);
		setBackground(BASIC_PNL_BACKGROUND);
		setSubTitle(title);
		setComponent(component, cWidth, cHeight);
	}
	
	public SubEntityPanel( String title, EntityPanel panel)
	{
		this(title, panel.getPanel(), panel.getFixWidth(), panel.getFixHeight());
	}


	private void setComponent(JComponent component, int cWidth, int cHeight)
	{
		if(component != null)
		{
			component.setBounds(H_SPACING, 2 * V_SPACING + SUBTITLE_HEIGHT, cWidth, cHeight);
			add(component);
		}
	}


	protected void addButton(String name, String iconFile, ActionListener listener)
	{
		JButton button = new JButton(name);
		try
		{
			button.setIcon( new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream(iconFile))) );
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		button.addActionListener(listener);
		button.setBounds( 2 * H_SPACING + SUBTITLE_WIDTH + (H_SPACING + SUBTLTBTN_WIDTH) * buttonCount++ , V_SPACING, SUBTLTBTN_WIDTH, SUBTITLE_HEIGHT);
		add(button);
	}
	
	public void setChanged(boolean changed)
	{
		this.changed = changed;
	}

	public void realign()
	{
		super.repaint();
	}



	protected void setSubTitle( String title )
	{
		JLabel lblTitle = new JLabel( title );
		lblTitle.setFont(SUBTITLE_FONT);
		lblTitle.setForeground(TITLE_FG);
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
	public JPanel getPanel()
	{
		return this;
	}

	@Override
	public void refresh()
	{
		realign();
	}


	@Override
	public Object getShownObject()
	{
		return null;
	}


	@Override
	public boolean hasChanged()
	{
		return changed;
	}
	
}
