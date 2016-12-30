package bn.blaszczyk.roseapp.view.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public abstract class AlignPanel extends AbstractPanelContainer {


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

	protected void setTitle( String text )
	{
		this.title = text;
	}
	
	private void addTitle()
	{
		JLabel lblTitle =  LabelFactory.createOpaqueLabel(title, TITLE_FONT, TITLE_FG, TITLE_BG);
		lblTitle.setBounds(h_offset, v_offset, TITLE_WIDTH, TITLE_HEIGHT);
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
		panel.addRoseListener(changeListener);
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
	public Iterable<EntityPanel> getPanels()
	{
		return subPanels;
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
		h_offset= boundaryWidth;
		v_offset = 0;
		width = 0;
		height = 0;
		removeAll();
		if(title != null)
			addTitle();
		for(EntityPanel panel : subPanels)
			drawSubPanel(panel);
		super.refresh();
	}	
	
}
