package bn.blaszczyk.roseapp.view;

public interface Messenger {
	public void info(String message, String title);
	public boolean confirm(String message, String title);
	public boolean questionYesNo(String message, String title);
	public int questionYesNoCancel(String message, String title);
	public void warning(String message, String title);
	public void error(Exception e, String title);
}
