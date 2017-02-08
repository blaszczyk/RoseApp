package bn.blaszczyk.roseapp.view.tools;

import java.awt.Component;
import java.util.Vector;

import javax.swing.*;
import javax.swing.tree.*;

import bn.blaszczyk.rose.model.Readable;
import bn.blaszczyk.roseapp.view.factories.IconFactory;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

public class CheckBoxTree extends JTree {

	private static final long serialVersionUID = -2779977606811993399L;
	
	private final CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
	
	public CheckBoxTree(Node root)
	{
		super(root);
		setBackground(BASIC_PNL_BACKGROUND);
		setCellRenderer(renderer);
		setCellEditor(new CheckBoxNodeEditor());
		setEditable(true);
	}
	
	private final class CheckBoxNodeRenderer implements TreeCellRenderer {
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, 
				boolean expanded,	boolean leaf, int row, boolean hasFocus)
		{
			if ( value instanceof DefaultMutableTreeNode )
			{
				Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
				if (userObject instanceof Node)
				{
					Node node = (Node) userObject;
					return node.getCheckBox();
				}
			}
			return new JLabel("This should not be visible.");
		}
	}
	
	private final class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {
		
		private static final long serialVersionUID = 6859606384576001150L;

		@Override
		public Object getCellEditorValue()
		{
			Object editingValue = getEditingPath().getLastPathComponent();
			if(editingValue instanceof DefaultMutableTreeNode)
				return ((DefaultMutableTreeNode)editingValue).getUserObject();
			return null;
		}
		
		@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row)
		{			
			Component editor = renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
			if (editor instanceof JCheckBox)
				((JCheckBox)editor).addActionListener( e -> {
					stopCellEditing();
					if(value instanceof DefaultMutableTreeNode)
					{
						Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
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

		private static final long serialVersionUID = -3055218823425274241L;
		
		public static final Icon ICON_CHECKED = IconFactory.create("box_check.png");
		public static final Icon ICON_UNCHECKED = IconFactory.create("box_uncheck.png");
		
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
			checkbox.setIcon(ICON_UNCHECKED);
			checkbox.setSelectedIcon(ICON_CHECKED);
		}

		public Node( Readable entity )
		{
			this(entity.toString(), entity);
		}
		
		public Component getCheckBox()
		{
			return checkbox;
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
