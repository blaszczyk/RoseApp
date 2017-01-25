package bn.blaszczyk.roseapp.view.table;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import bn.blaszczyk.rose.model.Entity;
import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

import static bn.blaszczyk.roseapp.tools.Preferences.*;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class EntityTable extends JTable{

	private EntityAction[] buttonActions;
	private EntityTableModel tableModel;	
	private final TableRowSorter<TableModel> sorter = new TableRowSorter<>();
	private final Entity entity;
	
	private boolean columnWidthsAdjusted = false;
	
	public EntityTable(EntityTableModel tableModel, Entity entity)
	{
		super(tableModel);
		this.tableModel = tableModel;
		this.entity = entity;
		buttonActions = new EntityAction[tableModel.getButtonCount()];

		setShowGrid(false);
		setIntercellSpacing(new Dimension(CELL_SPACING, CELL_SPACING));
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getTableHeader().setFont(HEADER_FONT);
		setRowSorter(sorter);
		sorter.setModel(tableModel);

		setRowHeight(ODD_FONT.getSize() + 10);
		setCellRenderer();
		setColumnWidths();

		OmniListener listener = new OmniListener();
		addMouseListener(listener);
		getTableHeader().addMouseListener(listener);
		getColumnModel().addColumnModelListener(listener);
	}
	
	
	public void setButtonColumn( int columnIndex, Icon icon,  EntityAction action)
	{
		if(columnIndex < 0 || columnIndex >= buttonActions.length)
			return;
		buttonActions[columnIndex] = action;
		tableModel.setButtonIcon(columnIndex, icon);
	}

	private void setCellRenderer()
	{
		TableCellRenderer cellRenderer = new EntityTableCellRenderer();
		getTableHeader().setDefaultRenderer(cellRenderer);
		for(int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++)
			getColumnModel().getColumn(columnIndex).setCellRenderer( cellRenderer );
	}
	
	private void setColumnWidths()
	{
		final int buttonCount = tableModel.getButtonCount();
		for(int i = 0 ; i < this.getColumnCount(); i++)
		{
			TableColumn col = getColumnModel().getColumn(i);
			if( tableModel.getColumnClass(i) == Icon.class )
			{
				col.setPreferredWidth(TBL_BTN_WIDTH);
				col.setMinWidth(TBL_BTN_WIDTH);
				col.setMaxWidth(TBL_BTN_WIDTH);
				
			}
			else
			{
				int width = getIntegerEntityValue(entity, COLUMN_WIDTH + (i - buttonCount), 40);
				col.setPreferredWidth(width);
			}
		}
	}

	public void filter(String text)
	{
		sorter.setRowFilter(RowFilter.regexFilter(text));
	}
	
	public void resetSource()
	{
		tableModel.resetSource();
	}
	
	public interface EntityAction
	{
		public void performAction(Readable entity);
	}

	/*
	 * Custom Cell Renderer
	 */
	private final class EntityTableCellRenderer implements TableCellRenderer
	{
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
	
	private final class OmniListener implements MouseListener, TableColumnModelListener
	{

		@Override
		public void columnAdded(TableColumnModelEvent e)
		{
		}

		@Override
		public void columnRemoved(TableColumnModelEvent e)
		{
		}

		@Override
		public void columnMoved(TableColumnModelEvent e)
		{
		}

		@Override
		public void columnMarginChanged(ChangeEvent e)
		{
			if(!columnWidthsAdjusted && getTableHeader().getResizingColumn() != null)
				columnWidthsAdjusted = true;
		}

		@Override
		public void columnSelectionChanged(ListSelectionEvent e)
		{
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.getSource().equals(EntityTable.this))
				if(e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1 )
				{
					int row = sorter.convertRowIndexToModel( rowAtPoint(e.getPoint()) );
					int col = columnAtPoint(e.getPoint());
					if(col < tableModel.getButtonCount() )
						buttonActions[col].performAction(tableModel.getEntity(row));
				}
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if(e.getSource() == getTableHeader() && columnWidthsAdjusted)
			{
				TableColumnModel columnModel = getColumnModel();
				int buttonCount = tableModel.getButtonCount();
				for(int i = 0; i < columnModel.getColumnCount() - buttonCount; i++)
				{
					int width = columnModel.getColumn(i + buttonCount).getWidth();
					putIntegerEntityValue(entity, COLUMN_WIDTH + i, width);
				}
				columnWidthsAdjusted = false;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
		}
		
	}
	
}

