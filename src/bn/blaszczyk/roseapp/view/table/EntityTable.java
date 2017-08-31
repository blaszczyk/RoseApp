package bn.blaszczyk.roseapp.view.table;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import javax.swing.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.*;
import javax.swing.table.*;

import bn.blaszczyk.rose.model.EntityModel;
import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.Behaviour;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

import static bn.blaszczyk.rosecommon.tools.Preferences.*;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;
import static bn.blaszczyk.roseapp.tools.AppPreference.*;

public class EntityTable extends JTable{

	private static final long serialVersionUID = 6465707416534205313L;
	
	private EntityAction[] buttonActions;
	private EntityTableModel tableModel;	
	private final TableRowSorter<TableModel> sorter = new TableRowSorter<>();
	private final EntityModel entityModel;
	
	private boolean columnWidthsAdjusted = false;
	
	public EntityTable(EntityTableModel tableModel, EntityModel entityModel, Behaviour behaviour)
	{
		super(tableModel);
		this.tableModel = tableModel;
		this.entityModel = entityModel;
		buttonActions = new EntityAction[tableModel.getButtonCount()];

		setShowGrid(false);
		setIntercellSpacing(new Dimension(CELL_SPACING, CELL_SPACING));
		getTableHeader().setFont(HEADER_FONT);
		setRowSorter(sorter);
		sorter.setModel(tableModel);
		for(int i = 0; i < tableModel.getColumnCount(); i++)
			sorter.setComparator(i, behaviour.comparator(entityModel, tableModel.getColumnContent(i)));
		int sortColumn = getIntegerEntityValue(entityModel, SORT_COLUMN) + tableModel.getButtonCount();
		String sortOrder = getStringEntityValue(entityModel, SORT_ORDER);
		SortKey sortKey = new SortKey(sortColumn, SortOrder.valueOf(sortOrder));
		sorter.setSortKeys(Collections.singletonList(sortKey));

		setRowHeight(ODD_FONT.getSize() + 10);
		setCellRenderer();
		setColumnWidths();

		OmniListener listener = new OmniListener();
		addMouseListener(listener);
		getTableHeader().addMouseListener(listener);
		getColumnModel().addColumnModelListener(listener);
		sorter.addRowSorterListener(listener);
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
				int width = getIntegerEntityValue(entityModel, COLUMN_WIDTH.append(i - buttonCount));
				col.setPreferredWidth(width);
			}
		}
	}

	public void filter(String text)
	{
		try
		{
			sorter.setRowFilter(RowFilter.regexFilter(text));
		}
		catch (PatternSyntaxException e) 
		{
			sorter.setRowFilter(RowFilter.regexFilter(".*"));
		}
	}
	
	public void resetSource()
	{
		tableModel.resetSource();
	}

	public Readable getEntity(int row)
	{
		return tableModel.getEntity( getRowSorter().convertRowIndexToModel(row) );
	}

	public EntityTableModel getRoseTableModel()
	{
		return tableModel;
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
			Color background;
			if(isSelected)
				background = row % 2 == 0 ? SELECTED_EVEN_BG : SELECTED_ODD_BG;
			else
				background =  row % 2 == 0 ? EVEN_BG : ODD_BG;
			if(value instanceof Icon)
				return LabelFactory.createOpaqueLabel((Icon)value, background);
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
				return LabelFactory.createOpaqueLabel(text, ODD_FONT, ODD_FG, background);
			else
				return LabelFactory.createOpaqueLabel(text, EVEN_FONT, EVEN_FG, background);
		}
	};
	
	private final class OmniListener implements MouseListener, TableColumnModelListener, RowSorterListener
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
			int fromIndex = e.getFromIndex() - tableModel.getButtonCount();
			int toIndex = e.getToIndex() - tableModel.getButtonCount();
			if(fromIndex < 0 || toIndex < 0)
				return;	
			if(fromIndex != toIndex)
			{
				String fromContent = getStringEntityValue(entityModel, COLUMN_CONTENT.append(fromIndex));
				int fromWidth = getIntegerEntityValue(entityModel, COLUMN_WIDTH.append(fromIndex));
				String toContent = getStringEntityValue(entityModel, COLUMN_CONTENT.append(toIndex));
				int toWidth = getIntegerEntityValue(entityModel, COLUMN_WIDTH.append(toIndex));
				putStringEntityValue(entityModel, COLUMN_CONTENT.append(toIndex), fromContent);
				putIntegerEntityValue(entityModel, COLUMN_WIDTH.append(toIndex), fromWidth);
				putStringEntityValue(entityModel, COLUMN_CONTENT.append(fromIndex), toContent);
				putIntegerEntityValue(entityModel, COLUMN_WIDTH.append(fromIndex), toWidth);
			}
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
					putIntegerEntityValue(entityModel, COLUMN_WIDTH.append(i), width);
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

		@Override
		public void sorterChanged(RowSorterEvent e)
		{
			List<? extends SortKey> sortKeys = sorter.getSortKeys();
			if(sortKeys.isEmpty())
				return;
			SortKey sortKey = sortKeys.get(0);
			int sortColumn = sortKey.getColumn() - tableModel.getButtonCount();
			if(sortColumn < 0)
				return;
			String sortOrder = sortKey.getSortOrder().name();
			putIntegerEntityValue(entityModel, SORT_COLUMN, sortColumn);
			putStringEntityValue(entityModel, SORT_ORDER, sortOrder);
		}
		
	}
	
}
