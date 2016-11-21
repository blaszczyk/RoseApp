package bn.blaszczyk.roseapp.view.tools;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.*;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.view.ThemeConstants;
import bn.blaszczyk.roseapp.view.panels.MyPanel;

@SuppressWarnings("serial")
public class EntityTable extends JTable implements MyPanel, ThemeConstants {

	
	public interface EntityAction
	{
		public void performAction(Readable entity);
	}

	
	/*
	 * Custom Cell Renderer
	 */
	private final TableCellRenderer cellRenderer = new TableCellRenderer(){
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			String text = "";
			if(value instanceof Icon)
			{
				JLabel icon = new JLabel((Icon)value);
				icon.setBackground( row % 2 == 0 ? EVEN_BG : ODD_BG);
				return icon;
			}
			else if(value instanceof Date)
				text = DATE_FORMAT.format(value);
			else if(value instanceof Double)
				text = DOUBLE_FORMAT.format(value);
			else if(value instanceof Integer)
				text = INT_FORMAT.format(value);
			else if(value instanceof BigDecimal)
				text = DOUBLE_FORMAT.format(value);
			else  if( value == null)
				text = "";
			else
				text = String.valueOf( value );
			
			JLabel c = new JLabel( text );
			c.setOpaque(true);
			if(row < 0 )
			{
				c.setText(" " + c.getText() + " ");
				c.setFont(HEADER_FONT);
				c.setBackground(HEADER_BG);
				c.setBorder(BorderFactory.createEtchedBorder());
			}
			else
				if( (row % 2) == 1)
				{
					c.setBackground(ODD_BG);
					c.setFont( ODD_FONT );
					c.setForeground(ODD_FG);
				}
				else
				{
					c.setBackground(EVEN_BG);
					c.setFont( EVEN_FONT );
					c.setForeground(EVEN_FG);
				}
			return c;
		}
	};
	
	private EntityAction[] buttonActions;
	private EntityTableModel tableModel;
	
	private int width = FULL_TABLE_WIDTH;
	private int height = TABLE_HEIGHT;
	private final TableRowSorter<TableModel> sorter = new TableRowSorter<>();
	
	public EntityTable(EntityTableModel tableModel, int maxWidth, int height )
	{
		super(tableModel);
		this.tableModel = tableModel;
		this.height = height;
		buttonActions = new EntityAction[tableModel.getButtonCount()];

		setShowGrid(false);
		setIntercellSpacing(new Dimension(CELL_SPACING, CELL_SPACING));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getTableHeader().setFont(HEADER_FONT);
		setRowSorter(sorter);
		sorter.setModel(tableModel);
		
		addMouseListener( new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 )
				{
					int row = sorter.convertRowIndexToModel( rowAtPoint(e.getPoint()) );
					int col = columnAtPoint(e.getPoint());
					if(col < tableModel.getButtonCount() )
						buttonActions[col].performAction(tableModel.getEntity(row));						
				}
			}			
		});
		
		setRowHeight(ODD_FONT.getSize() + 10);
		
		setCellRenderer();
		setDimns(maxWidth);
	}
	
	
	public void setButtonColumn( int columnIndex, Icon icon,  EntityAction action)
	{
		if(columnIndex < 0 || columnIndex >= buttonActions.length)
			return;
		buttonActions[columnIndex] = action;
		tableModel.setButtonIcon(columnIndex, icon);
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
	
//	public void setHeight( int height )
//	{
//		this.height = height;
//	}

	
	private void setCellRenderer()
	{
		getTableHeader().setDefaultRenderer(cellRenderer);
		for(int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++)
			getColumnModel().getColumn(columnIndex).setCellRenderer( cellRenderer );
	}
	
	private void setDimns( int maxWidth )
	{
		this.width = maxWidth;
		int newWidth = 0;
		for(int i = 0 ; i < this.getColumnCount(); i++)
			newWidth += tableModel.getColumnWidth(i) + CELL_SPACING;
		for(int i = 0 ; i < this.getColumnCount(); i++)
			if( tableModel.getColumnClass(i) == Icon.class)
			{
				getColumnModel().getColumn(i).setPreferredWidth(BUTTON_WIDTH);
				getColumnModel().getColumn(i).setMinWidth(BUTTON_WIDTH);
				getColumnModel().getColumn(i).setMaxWidth(BUTTON_WIDTH);
				
			}
			else
			{
				int width =  tableModel.getColumnWidth(i) ;
				if(newWidth < maxWidth)
					width = width * maxWidth / newWidth;
				if( width >= 0 )
					getColumnModel().getColumn(i).setPreferredWidth(width);
			}
	}


	@Override
	public Object getShownObject()
	{
		return null;
	}


	@Override
	public JPanel getPanel()
	{
		return null;
	}


	@Override
	public boolean hasChanged()
	{
		return false;
	}
	
}
