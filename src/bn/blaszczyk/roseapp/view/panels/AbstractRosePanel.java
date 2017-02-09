package bn.blaszczyk.roseapp.view.panels;

import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import bn.blaszczyk.roseapp.RoseException;
import bn.blaszczyk.roseapp.view.RoseEvent;
import bn.blaszczyk.roseapp.view.RoseListener;

public abstract class AbstractRosePanel extends JPanel implements RosePanel, RoseListener {
	
	private static final long serialVersionUID = -6684299166521819588L;
	
	private boolean changed = false;
	private final List<RoseListener> listeners = new ArrayList<>();
	
	public AbstractRosePanel()
	{
		this(null);
	}

	public AbstractRosePanel( LayoutManager layout )
	{
		super.setLayout(layout);
	}
	
	protected void setChanged()
	{
		changed = true;
	}
	
	@Override
	public void info(String message, String title)
	{
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public int questionYesNoCancel(String message, String title)
	{
		return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	}

	@Override
	public boolean questionYesNo(String message, String title)
	{
		return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
				== JOptionPane.YES_OPTION;
	}

	@Override
	public void error(Exception e, String title)
	{
		String message = ( e instanceof RoseException) ? ((RoseException)e).getFullMessage() : e.getMessage();
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void warning(String message, String title)
	{
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public boolean confirm(String message, String title)
	{
		return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)
				== JOptionPane.OK_OPTION;
	}
	
	@Override
	public int getFixWidth()
	{
		return 0;
	}
	
	@Override
	public int getFixHeight()
	{
		return 0;
	}
	
	@Override
	public JPanel getPanel()
	{
		return this;
	}
	
	@Override
	public boolean hasChanged()
	{
		return changed;
	}
	
	@Override
	public void refresh()
	{
		revalidate();
		repaint();
	}
	
	@Override
	public Object getShownObject()
	{
		return this;
	}

	@Override
	public void save()
	{
		changed = false;
	}
	
	@Override
	public void addRoseListener(RoseListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void removeRoseListener(RoseListener listener)
	{
		listeners.remove(listener);
	}
	
	private void notifyListeners(RoseEvent e)
	{
		for(RoseListener l : listeners)
			l.notify(e);
	}
	
	protected void notify(boolean noRefresh, Object origin)
	{
		notify(new RoseEvent(this, noRefresh,origin));
	}

	@Override
	public void notify(RoseEvent e)
	{
		changed = true;
		if(!e.isNoRefresh())
			refresh();
		notifyListeners(e);
	}
	
	@Override
	public String toString()
	{
		if(getShownObject() == this)
			return getClass().getSimpleName();
		return getClass().getSimpleName() + " object = " + getShownObject();
	}
	
}
