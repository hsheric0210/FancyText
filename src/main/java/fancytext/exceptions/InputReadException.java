package fancytext.exceptions;

public class InputReadException extends Exception
{
	public InputReadException(final Throwable t)
	{
		super("Failed to read input due exception: " + t.toString(), t);
	}

	public InputReadException(final String target, final Throwable t)
	{
		super("Failed to read " + target + " due exception: " + t.toString(), t);
	}
}
