package bn.blaszczyk.roseapp.view.table;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.*;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.controller.ModelController;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.RoseListener;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;
import bn.blaszczyk.roseapp.view.panels.EntityPanel;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class EntityTable extends JTable implements EntityPanel {

	
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
				return LabelFactory.createOpaqueLabel((Icon)value,  row % 2 == 0 ? EVEN_BG : ODD_BG);
			else if(value instanceof Date)
				text = DATE_FORMAT.format(value);
			else if(value instanceof Double)
				text = DOUBLE_FORMAT.format(value);
			else if(value instanceof Integer)
				text = INT_FORMAT.format(value);
			else if(value instanceof BigDecimal)
				text = DOUBLE_FORMAT.format(value);
			else  if( value == null)
				text = "-";
			else
				text = Messages.get(String.valueOf( value ));
			if(row < 0 )
			{
				JLabel label = LabelFactory.createOpaqueLabel(" " + text + " ", HEADER_FONT, HEADER_FG, HEADER_BG);
				label.setBorder(BorderFactory.createEtchedBorder());
				return label;
			}
			else if( (row % 2) == 1)
				return LabelFactory.createOpaqueLabel(text, ODD_FONT, ODD_FG, ODD_BG);
			else
				return LabelFactory.createOpaqueLabel(text, EVEN_FONT, EVEN_FG, EVEN_BG);
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
	public int getFixWidth()
	{
		return width;
	}

	@Override
	public int getFixHeight()
	{
		return height;
	}
	
	private void setCellRenderer()
	{
		getTableHeader().setDefaultRenderer(cellRenderer);
		for(int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++)
			getColumnModel().getColumn(columnIndex).setCellRenderer( cellRenderer );
	}
	
	private void setDimns( int maxWidth )
	{
		this.width = maxWidth;
		int newWidth = 1;
		for(int i = 0 ; i < this.getColumnCount(); i++)
			newWidth += tableModel.getColumnWidth(i) + CELL_SPACING;
		for(int i = 0 ; i < this.getColumnCount(); i++)
			if( tableModel.getColumnClass(i) == Icon.class )
			{
				getColumnModel().getColumn(i).setPreferredWidth(TBL_BTN_WIDTH);
				getColumnModel().getColumn(i).setMinWidth(TBL_BTN_WIDTH);
				getColumnModel().getColumn(i).setMaxWidth(TBL_BTN_WIDTH);
				
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


	@Override
	public void refresh()
	{
		revalidate();
	}


	@Override
	public void save(ModelController controller)
	{	
	}


	@Override
	public void addRoseListener(RoseListener listener)
	{
	}


	@Override
	public void removeRoseListener(RoseListener listener)
	{
	}
	
}
