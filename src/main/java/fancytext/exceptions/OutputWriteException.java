package fancytext.exceptions;

public class OutputWriteException extends Exception
{
	public OutputWriteException(final Throwable t)
	{
		super("Failed to write output due exception: " + t.toString(), t);
	}

	public OutputWriteException(final String target, final Throwable t)
	{
		super("Failed to write " + target + " due exception: " + t.toString(), t);
	}
}
