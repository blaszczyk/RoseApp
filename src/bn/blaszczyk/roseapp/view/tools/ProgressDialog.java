package bn.blaszczyk.roseapp.view.tools;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class ProgressDialog extends JDialog implements ActionListener {


	/*
	 * Components
	 */
	private final JTextArea taInfo = new JTextArea();
	private final JProgressBar progressBar = new JProgressBar();
	private final JLabel lblTimeLeft = new JLabel("geschätzte Restzeit: unbekannt");
	private final JButton btnCancel = new JButton("Abbrechen");

	/*
	 * Variables
	 */
	private final JDialog owner;
	private int secsLeft = 0;
	private int maxValue;
	private long initTimeStamp = System.currentTimeMillis();
	
	private boolean cancelRequest = false;
	private boolean selfClosable = false;

	private int value = 0;
	private int lastValue = 0;
	
	private Timer timerDots = new Timer(100, e -> appendInfo("."));
	private Timer timerSecs = new Timer(1000, e -> setSecsLeft());


	/*
	 * Constructors
	 */
	public ProgressDialog(JDialog owner, String title, Image icon, boolean showButton)
	{
		this(owner,1,title,icon,showButton);
	}
	
	public ProgressDialog(JDialog owner, int maxValue, String title, Image icon, boolean showButton)
	{
		super(owner, title, owner != null);
		this.owner = owner;
		this.maxValue = maxValue;
		
		setLayout(null);
		setSize(606, 340);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		if(icon != null)
			setIconImage( icon );

		taInfo.setEditable(false);
		taInfo.setLineWrap(true);

		JScrollPane infoPane = new JScrollPane(taInfo);
		infoPane.setBounds(10, 10, 580, 200);
		add(infoPane);
		
		progressBar.setMinimum(0);
		progressBar.setMaximum(maxValue);
		progressBar.setStringPainted(true);
		progressBar.setBounds(10, 210, 580, 40);
		add(progressBar);
		
		lblTimeLeft.setBounds(10, 260, 300, 30);
		add(lblTimeLeft);
		if(showButton)
		{
			btnCancel.setBounds(440, 260, 150, 30);
			btnCancel.addActionListener(this);
			btnCancel.setMnemonic('r');
			add(btnCancel);
		}
	}

	/*
	 * show on Screen
	 */
	public void showDialog()
	{
		timerDots.start();
		timerSecs.start();
		setLocationRelativeTo(owner);
		setVisible(true);	
	}

	/*
	 * Getters, Setters
	 */
	public void setMaxValue(int maxValue)
	{
		this.maxValue = maxValue;
		progressBar.setMaximum(maxValue);
	}
	
	public void setValue(int value)
	{
		this.value = value;
		progressBar.setValue(value);
	}
	
	public void incrementValue()
	{
		value++;
		progressBar.setValue(value);
	}
	
	public void appendInfo(String info)
	{
		taInfo.append(info);
		taInfo.setCaretPosition(taInfo.getDocument().getLength());
	}
	
	public void appendException(Throwable t)
	{
		while(t != null)
		{
			appendInfo("\n>>" + t.getMessage() );
			t = t.getCause();
		}
	}
	
	public boolean hasCancelRequest()
	{
		return cancelRequest;
	}

	public void undoCancelRequest()
	{
		cancelRequest = false;
		btnCancel.setEnabled(true);
	}
	
	public void setFinished()
	{
		selfClosable = true;
		btnCancel.setEnabled(true);
		btnCancel.setText("Schließen");
		timerDots.stop();
		timerSecs.stop();
		lblTimeLeft.setText("Gesamtdauer: " + (System.currentTimeMillis() - initTimeStamp) / 1000 + " Sekunden");
	}
	
	/*
	 * dispose 
	 */
	public void disposeDialog()
	{
		timerDots.stop();
		timerSecs.stop();
		dispose();
	}
	
	/*
	 * Internal Method
	 */
	private void setSecsLeft()
	{
		if(value == lastValue && secsLeft > 0)
				secsLeft--;
		else
		{
			if(value != 0)
				secsLeft = (int)( (System.currentTimeMillis() - initTimeStamp) * (maxValue - value) / value )/1000;
			lastValue = value;
		}
		lblTimeLeft.setText( String.format( "geschätzte Restzeit: %2d Sekunde%s",secsLeft, secsLeft == 1 ? "" : "n") );
	}
	
	/*
	 * ActionListener Method
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(selfClosable)
			disposeDialog();
		else
		{
			cancelRequest = true;
			appendInfo("\nBitte warten");
			btnCancel.setEnabled(false);
		}
	}

	
}
