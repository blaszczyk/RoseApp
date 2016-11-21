package bn.blaszczyk.roseapp.view.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bn.blaszczyk.roseapp.controller.*;
import bn.blaszczyk.roseapp.view.ThemeConstants;

@SuppressWarnings("serial")
public abstract class AlignPanel extends JPanel implements MyPanel, ThemeConstants {


	private final List<SubPanel> subPanels = new ArrayList<>();
	private String title;
	
	private int width = 2 * H_SPACING;
	private int height = V_SPACING;
	private int h_offset = H_SPACING;
	private int v_offset = V_SPACING;
	
	protected GUIController guiController;

	public AlignPanel( GUIController guiController )
	{
		this.guiController = guiController;
		setLayout(null);
		setBackground(FULL_PNL_BACKGROUND);		
	}



	public void realign()
	{
		removeAll();
		addTitle();
		for(SubPanel panel : subPanels)
			drawSubPanel(panel);
		super.repaint();
	}



	protected void setTitle( String text )
	{
		this.title = text;
	}
	
	private void addTitle()
	{
//		height += V_OFFSET;
		JLabel lblTitle = new JLabel( title );
		lblTitle.setFont(TITLE_FONT);
		lblTitle.setForeground(TITLE_FG);
		lblTitle.setBackground(TITLE_BG);
		lblTitle.setBounds(h_offset, v_offset, TITLE_WIDTH, TITLE_HEIGHT);
		lblTitle.setOpaque(true);
		add(lblTitle);		
		computeDimensions(TITLE_HEIGHT, TITLE_WIDTH);		
	}
	
	protected void addPanel( String title, JButton button,  MyPanel panel )
	{
		subPanels.add(new SubPanel(panel, button, title));
//		addPanel(title, button, panel.getPanel(), panel.getWidth(), panel.getHeight());		
	}
	
	protected void addPanel( String title, JButton button,  JComponent component, int width, int height )
	{
		subPanels.add(new SubPanel(component, width, height, button, title));	
	}//
	
	private void drawSubPanel(SubPanel panel)
	{
		v_offset += V_OFFSET;
		if( v_offset + panel.getHeight() > PANEL_HEIGHT ) 
		{
			v_offset = 2 * V_SPACING + TITLE_HEIGHT;
			h_offset = this.width + 2 * H_SPACING;
		}
		if(panel.getLabelText() != null)
		{
			JLabel lblSubTitle = new JLabel( panel.getLabelText() );
			lblSubTitle.setFont(SUBTITLE_FONT);
			lblSubTitle.setForeground(SUBTITLE_FG);
			lblSubTitle.setBackground(SUBTITLE_BG);
			lblSubTitle.setBounds(h_offset, v_offset, SUBTITLE_WIDTH, SUBTITLE_HEIGHT);
			lblSubTitle.setOpaque(true);
			add(lblSubTitle);
			
			if(panel.getButton() != null)
			{
				panel.getButton().setBounds( h_offset + H_SPACING + SUBTITLE_WIDTH, v_offset , SUBTLTBTN_WIDTH, SUBTITLE_HEIGHT);
				add(panel.getButton());
			}
			computeDimensions(SUBTITLE_HEIGHT, SUBTITLE_WIDTH);
		}		
		panel.getComponent().setBounds(h_offset, v_offset, panel.getWidth(), panel.getHeight());
		add(panel.getComponent());
		computeDimensions(panel.getHeight(), panel.getWidth());
	}
	
	private void computeDimensions( int height, int width )
	{
//		System.out.printf( "(%4d,%4d) - (%4d,%4d)- (%4d,%4d)\n",  h_offset, v_offset, this.width, this.height, width, height);	
		this.v_offset += V_SPACING + height;
		this.width = Math.max(this.width, h_offset + H_SPACING + width);
		this.height = Math.max(this.height, v_offset + V_SPACING);
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public JPanel getPanel()
	{
		return this;
	}
	

	private class SubPanel {
		private final JComponent component;
		private final int width;
		private final int height;
		private final JButton button;
		private final String labelText;
		
		public SubPanel(JComponent component, int width, int height, JButton button, String labelText)
		{
			this.component = component;
			this.width = width;
			this.height = height;
			this.button = button;
			this.labelText = labelText;
		}

		public SubPanel(MyPanel panel, JButton button, String labelText)
		{
			this.component = panel.getPanel();
			this.button = button;
			this.labelText = labelText;
			this.width = 0;
			this.height = 0;
		}

		public JComponent getComponent()
		{
			return component;
		}

		public int getWidth()
		{
			return component instanceof MyPanel ? ((MyPanel)component).getWidth() : width;
		}

		public int getHeight()
		{
			return component instanceof MyPanel ? ((MyPanel)component).getHeight() : height;
		}

		public JButton getButton()
		{
			return button;
		}

		public String getLabelText()
		{
			return labelText;
		}		
	}
	
}
