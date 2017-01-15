package bn.blaszczyk.roseapp.view.tools;

import java.awt.Component;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.*;
import javax.swing.tree.*;

import bn.blaszczyk.rose.model.Readable;
import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class CheckBoxTree extends JTree {
	
	private final CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
	
	public CheckBoxTree(Node root)
	{
		super(root);
		setBackground(BASIC_PNL_BACKGROUND);
		setCellRenderer(renderer);
		setCellEditor(new CheckBoxNodeEditor());
		setEditable(true);
	}
	
	private class CheckBoxNodeRenderer implements TreeCellRenderer {
		
		public CheckBoxNodeRenderer()
		{
		}
		
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus)
		{
			Object userObject = null;
			if ( value instanceof DefaultMutableTreeNode )
				userObject = ((DefaultMutableTreeNode) value).getUserObject();
			if( value instanceof DynamicUtilTreeNode)
				userObject = ((DynamicUtilTreeNode) value).getUserObject();
			if (userObject instanceof Node)
			{
				Node node = (Node) userObject;
				return node.getCheckBox();
			}			
			return new JLabel("This should not be visible.");
		}
	}
	
	class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {
		
		public CheckBoxNodeEditor()
		{
		}
		
		public Object getCellEditorValue()
		{
			Object editingValue = getEditingPath().getLastPathComponent();
			if(editingValue instanceof DynamicUtilTreeNode)
				return ((DynamicUtilTreeNode)editingValue).getUserObject();
			return null;
		}
		
		public boolean isCellEditable(EventObject event)
		{
			return true;
		}
		
		public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row)
		{			
			Component editor = renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
			if (editor instanceof JCheckBox)
				((JCheckBox)editor).addActionListener( e -> {
					if (stopCellEditing())
					{
						fireEditingStopped();
					}
					if(value instanceof DynamicUtilTreeNode)
					{
						Object userObject = ((DynamicUtilTreeNode)value).getUserObject();
						if(userObject instanceof Node)
						{
							Node node = (Node) userObject;
							boolean isSelected = node.isSelected();
							node.setSelected(isSelected, true);
							if(isSelected && node.getParent() != null)
								node.getParent().setSelected(isSelected,false);								
							repaint();
						}
					}					
				});
			return editor;
		}
	}
	
	public static class Node extends Vector<Node>  {
		private final Readable entity;
		private final JCheckBox checkbox;
		private Node parent;
		
		public Node(String name, Readable entity)
		{
			this.entity = entity;
			checkbox = new JCheckBox(name);
			checkbox.setFont(VALUE_FONT);
			checkbox.setFocusPainted(true);
			checkbox.setForeground(VALUE_FG);
			checkbox.setBackground(BASIC_PNL_BACKGROUND);
			checkbox.setSelected(true);
		}
		
		public Component getCheckBox()
		{
			return checkbox;
		}

		public Node( Readable entity )
		{
			this(entity.toString(), entity);
		}

		public Readable getEntity()
		{
			return entity;
		}
		
		public boolean isSelected()
		{
			return checkbox.isSelected();
		}

		public void setSelected(boolean selected, boolean cascade)
		{
			checkbox.setSelected(selected);
			if(cascade)
				for(Node node : this)
					node.setSelected(selected,true);
		}
		
		public Node getParent()
		{
			return parent;
		}

		private void setParent(Node parent)
		{
			this.parent = parent;
		}
		
		@Override
		public boolean add(Node e)
		{
			e.setParent(this);
			return super.add(e);
		}
		

		@Override
		public String toString()
		{
			return checkbox.getName();
		}
		
	}
}
