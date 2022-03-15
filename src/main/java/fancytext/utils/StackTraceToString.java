package fancytext.utils;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class StackTraceToString
{
	private StackTraceToString()
	{

	}

	public static String convert(final Throwable t)
	{
		try(final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw))
		{
			t.printStackTrace(pw);
			return sw.toString();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		return "Error while converting stack-trace to string";
	}
}
