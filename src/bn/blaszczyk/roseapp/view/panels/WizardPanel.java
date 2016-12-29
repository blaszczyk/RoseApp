package bn.blaszczyk.roseapp.view.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.factories.ButtonFactory;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

import static bn.blaszczyk.roseapp.view.ThemeConstants.*;

@SuppressWarnings("serial")
public class WizardPanel extends AbstractEntityPanel {

	public static interface WizardAction
	{
		public void act();
	}
	
	public static interface StepChecker
	{
		public boolean check();
	}

	public static class WizardStep
	{
		private final String title;
		private final JComponent component;
		private final WizardAction action;
		private final StepChecker checker;
		
		public WizardStep(String title, JComponent component, WizardAction action, StepChecker checker)
		{
			this.title = title;
			this.component = component;
			this.action = action;
			this.checker = checker;
		}

		String getTitle()
		{
			return title;
		}

		JComponent getComponent()
		{
			return component;
		}

		WizardAction getAction()
		{
			return action;
		}

		StepChecker getChecker()
		{
			return checker;
		}
	}

	public static final WizardAction TRIVIAL_ACTION = () -> {};
	public static final StepChecker TRIVIAL_CHECKER = () -> true;

	private final JLabel titleLabel = LabelFactory.createLabel("", TITLE_FONT, TITLE_FG, SwingConstants.CENTER);
	private final JButton btnCancel = ButtonFactory.createButton("cancel", WIZARD_BTN_FONT, e -> close());
	private final JButton btnPrevious = ButtonFactory.createButton("previous", WIZARD_BTN_FONT, e -> previous());
	private final JButton btnNext = ButtonFactory.createButton("next", WIZARD_BTN_FONT, e -> next());
	
	private final List<WizardStep> steps = new ArrayList<>();
	
	private int current = 0;
	private WizardAction finishAction;
	
	public WizardPanel()
	{
		super(new BorderLayout());
		
		add(titleLabel,BorderLayout.PAGE_START);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(V_SPACING, H_SPACING,V_SPACING, H_SPACING));
		buttonPane.add(btnCancel);
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(btnPrevious);
		buttonPane.add(Box.createRigidArea(new Dimension(H_SPACING, V_SPACING)));
		buttonPane.add(btnNext);
		add(buttonPane,BorderLayout.PAGE_END);
	}
	
	public void addStep( WizardStep step )
	{
		steps.add(step);
		if(steps.size() == 1)
			showStep(step);
		else
			btnNext.setText(Messages.get("next"));
	}	
	
	public void setFinishAction( WizardAction finishAction)
	{
		this.finishAction = finishAction;
	}
	
	private void previous()
	{
		remove(steps.get(current).getComponent());
		showStep(steps.get(--current));
	}
	
	private void next()
	{
		if( steps.get(current).checker.check() )
		{
			if(current == steps.size() - 1)
			{
				finishAction.act();
				close();
			}
			else
			{
				remove(steps.get(current).getComponent());
				showStep(steps.get(++current));	
			}
		}
	}
	
	private void showStep(WizardStep step)
	{
		titleLabel.setText(step.getTitle());
		add(step.getComponent(),BorderLayout.CENTER);
		step.getAction().act();
		btnPrevious.setEnabled(current != 0);
		btnNext.setText(current == steps.size() - 1 ? Messages.get("finish") : Messages.get("next"));
		refresh();
	}
	
	private void close()
	{
		Component parent = getParent().getParent().getParent().getParent();
		if(parent instanceof JDialog )
			((JDialog)parent).dispose();
	}
	
	@Override
	public int getFixWidth()
	{
		return WIZARD_WIDTH;
	}

	@Override
	public int getFixHeight()
	{
		return WIZARD_HEIGTH;
	}

}

