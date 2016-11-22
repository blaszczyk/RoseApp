package bn.blaszczyk.roseapp.tools;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import bn.blaszczyk.rose.model.Entity;
import bn.blaszczyk.rose.model.EntityField;

public final class EntityTreeModel implements TreeModel {
	
	private final Entity root;
	
	public EntityTreeModel(Entity root)
	{
		this.root = root;
	}
	
	@Override
	public Object getRoot()
	{
		return root;
	}
	
	@Override
	public Object getChild(Object parent, int index)
	{
		if( root.equals(parent) )
			return root.getEntityFields().get(index);
		else if( parent instanceof EntityField)
			return ((EntityField)parent).getEntity().getEntityFields().get(index);			
		return null;
	}
	
	@Override
	public int getChildCount(Object parent)
	{
		if( root.equals(parent) )
			return root.getEntityFields().size();
		else if( parent instanceof EntityField)
			return ((EntityField)parent).getEntity().getEntityFields().size();
		return 0;
	}
	
	@Override
	public boolean isLeaf(Object node)
	{
		return getChildCount(node) == 0;
	}
	
	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
	}
	
	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		return 0;
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
	}
	
	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
	}
	
}
