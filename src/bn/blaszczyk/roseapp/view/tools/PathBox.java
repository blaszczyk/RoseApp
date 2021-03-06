package bn.blaszczyk.roseapp.view.tools;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import bn.blaszczyk.rose.model.EntityModel;
import bn.blaszczyk.rose.model.EntityField;
import bn.blaszczyk.rose.model.Field;
import bn.blaszczyk.roseapp.view.factories.ButtonFactory;

public class PathBox extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 5345696536465236151L;

	private final static Map<EntityModel, Field[]> fieldsMap = new HashMap<>();
	
	private final JComboBox<Field> nodeBox;
	private JComponent component = null;
	private final ActionListener externalListener;
	
	public PathBox(ActionListener externalListener, EntityModel entityModel, String path)
	{
		setLayout(null);
		setBackground(FULL_PNL_BACKGROUND);
		String[] split = path.split("\\.", 2);
		this.externalListener = externalListener;
		nodeBox = new JComboBox<>(createLeafs(entityModel));
		nodeBox.setFont(PROPERTY_FONT);
		nodeBox.setForeground(PROPERTY_FG);
		for(int i = 0; i < nodeBox.getItemCount(); i++)
		{
			if(nodeBox.getItemAt(i).toString().equalsIgnoreCase(split[0]))
				nodeBox.setSelectedIndex(i);
		}
		nodeBox.setBounds(0, 0, PROPERTY_WIDTH, LBL_HEIGHT);
		nodeBox.addActionListener(this);
		add(nodeBox);
		setLeaf(split.length > 1 ? split[1] : null);
		setSize(PROPERTY_WIDTH, LBL_HEIGHT);
	}
	
	private void setLeaf(String path)
	{
		if(nodeBox.getSelectedItem() instanceof EntityField)
		{
			EntityModel subEntityModel = ((EntityField)nodeBox.getSelectedItem()).getEntityModel();
			if(path != null)
				setNode(path,subEntityModel);
			else
				setEntityLeaf(subEntityModel);
		}
	}
	
	private void setEntityLeaf(EntityModel entityModel)
	{
		component = ButtonFactory.createButton("Add Leaf", this);
		component.setBounds(PROPERTY_WIDTH, 0, PROPERTY_WIDTH, LBL_HEIGHT);
		setSize(2 * PROPERTY_WIDTH, LBL_HEIGHT);
		add(component);
	}
	
	private void setNode(String path, EntityModel entityModel)
	{
		component = new PathBox(externalListener,entityModel,path);
		component.setLocation(PROPERTY_WIDTH, 0);
		setSize(PROPERTY_WIDTH + component.getWidth(), LBL_HEIGHT);
		add(component);
	}
	

	private static Field[] createLeafs( EntityModel entityModel)
	{
		if(!fieldsMap.containsKey(entityModel))
		{
			List<Field> contentList = new ArrayList<>();
			contentList.addAll(entityModel.getFields());
			for(EntityField entityField : entityModel.getEntityFields())
				if(!entityField.getType().isSecondMany())
					contentList.add(entityField);
			Field[] contents = new Field[contentList.size()];
			contentList.toArray(contents);
			fieldsMap.put(entityModel, contents);
		}
		return(fieldsMap.get(entityModel));
	}
	
	@Override
	public String toString()
	{
		return nodeBox.getSelectedItem().toString() + ( (component instanceof PathBox) ? ("." + component) : "" );
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String leaf = null;
		if(e.getSource() instanceof JButton)
			leaf = nodeBox.getSelectedItem().toString();
		if(component != null)
			remove(component);
		setLeaf(leaf);
		repaint();
		revalidate();
		externalListener.actionPerformed(e);
	}
	
}
