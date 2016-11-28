package bn.blaszczyk.roseapp.view.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import bn.blaszczyk.roseapp.controller.*;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public abstract class AlignPanel extends JPanel implements EntityPanel {


	private final List<EntityPanel> subPanels = new ArrayList<>();
	private String title = null;
	
	private int width = 0;
	private int height = 0;
	private int h_offset = 0;
	private int v_offset = 0;
	
	private final int boundaryWidth;
	
	protected GUIController guiController;

	public AlignPanel( GUIController guiController, int boundaryWidth)
	{
		this.boundaryWidth = boundaryWidth;
		this.guiController = guiController;
		h_offset = boundaryWidth;
		setLayout(null);
		setBackground(FULL_PNL_BACKGROUND);		
	}

	public void realign()
	{
		h_offset= boundaryWidth;
		v_offset = 0;
		width = 0;
		height = 0;
		removeAll();
		if(title != null)
			addTitle();
		for(EntityPanel panel : subPanels)
			drawSubPanel(panel);
		super.revalidate();
		super.repaint();
	}

	protected void setTitle( String text )
	{
		this.title = text;
	}
	
	private void addTitle()
	{
		JLabel lblTitle = new JLabel( title );
		lblTitle.setFont(TITLE_FONT);
		lblTitle.setForeground(TITLE_FG);
		lblTitle.setBackground(TITLE_BG);
		lblTitle.setBounds(h_offset, v_offset, TITLE_WIDTH, TITLE_HEIGHT);
		lblTitle.setOpaque(true);
		add(lblTitle);		
		computeDimensions(TITLE_HEIGHT, TITLE_WIDTH);		
	}

	protected int addPanel( EntityPanel panel )
	{
		if(panel == null)
			return -1;
		subPanels.add(panel);
		return subPanels.size() - 1;
	}
	
	protected void setPanel( int index, EntityPanel panel )
	{
		subPanels.set(index, panel);
	}
	
	private void drawSubPanel(EntityPanel panel)
	{
		v_offset += V_SPACING;
		if( v_offset + panel.getFixHeight() > PANEL_HEIGHT ) 
		{
			v_offset = 2 * V_SPACING + TITLE_HEIGHT;
			h_offset = this.width + 2 * boundaryWidth;
		}
		panel.getPanel().setBounds(h_offset, v_offset, panel.getFixWidth() , panel.getFixHeight());
		add(panel.getPanel());
		computeDimensions(panel.getFixHeight(), panel.getFixWidth());
	}
	
	private void computeDimensions( int height, int width )
	{
		this.v_offset += V_SPACING + height;
		this.width = Math.max(this.width, h_offset + boundaryWidth + width);
		this.height = Math.max(this.height, v_offset + V_SPACING);
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
	
	
}
