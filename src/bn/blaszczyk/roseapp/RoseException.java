package bn.blaszczyk.roseapp;

@SuppressWarnings("serial")
public class RoseException extends Exception {
	
	public RoseException(String message)
	{
		super(message);
	}
	
	public RoseException(Throwable cause)
	{
		super(cause);
	}
	
	public RoseException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public String getFullMessage()
	{
		StringBuilder sb = new StringBuilder(getMessage());
		Throwable cause = getCause();
		while(cause != null)
		{
			sb.append("\n").append(cause.getMessage());
			cause = cause.getCause();
		}
		return sb.toString();
	}
	
}
