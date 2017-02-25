package bn.blaszczyk.roseapp;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bn.blaszczyk.rose.Rose;
import bn.blaszczyk.roseapp.tools.Messages;
import bn.blaszczyk.roseapp.view.factories.LabelFactory;

public class VersionInfo {
	
	public static final String VERSION_ID = "0.71";
	
	private static JPanel infoPanel = null;
	
	public static JPanel getInfoPanel()
	{
		if(infoPanel == null)
		{
			infoPanel = new JPanel(null);
			infoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			infoPanel.setSize(300, 170);
			
			JLabel lblRoseApp = LabelFactory.createLabel(Messages.get("based on") + " RoseApp v." + VERSION_ID);
			lblRoseApp.setBounds(10, 10, 280, 30);
			infoPanel.add(lblRoseApp);
			
			JLabel lblRose = LabelFactory.createLabel(Messages.get("using") + " Rose v." + Rose.VERSION_ID);
			lblRose.setBounds(10, 50, 280, 30);
			infoPanel.add(lblRose);
			
			JLabel lblGitHub = LabelFactory.createLabel("https://github.com/blaszczyk");
			lblGitHub.setBounds(10, 90, 280, 30);
			infoPanel.add(lblGitHub);
			
			JLabel lblEmail = LabelFactory.createLabel("michael.i.blaszczyk@gmail.com");
			lblEmail.setBounds(10, 130, 280, 30);
			infoPanel.add(lblEmail);
		}
		return infoPanel;
	}
}
