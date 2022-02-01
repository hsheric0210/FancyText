package fancytext.hash;

import java.security.GeneralSecurityException;

import fancytext.Main;

public class DigestException extends GeneralSecurityException
{
	public DigestExceptionType type;
	private final AbstractHash digest;

	public DigestException(final DigestExceptionType type)
	{
		super(type.description);
		this.type = type;
		digest = null;
	}

	public DigestException(final DigestExceptionType type, final Throwable thrown)
	{
		super(type.description + ": " + thrown.getMessage(), thrown);
		this.type = type;
		digest = null;
	}

	public DigestException(final DigestExceptionType type, final AbstractHash digest, final Throwable thrown)
	{
		super(type.description + ": " + thrown.getMessage(), thrown);
		this.type = type;
		this.digest = digest;
	}

	public DigestException(final DigestExceptionType type, final String message)
	{
		super(type.description + ": " + message);
		this.type = type;
		digest = null;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(64);
		builder.append(super.toString()).append(Main.lineSeparator);

		builder.append("* Error code: ").append(type.ordinal()).append("(").append(type.name()).append(") ").append(type.description).append(Main.lineSeparator);

		if (getCause() != null)
			builder.append("* Cause: ").append(getCause()).append(Main.lineSeparator);

		if (digest != null)
			builder.append(digest.dumpInformations());

		builder.append(Main.lineSeparator);

		return builder.toString();
	}
}
