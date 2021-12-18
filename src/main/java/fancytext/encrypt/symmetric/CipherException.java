package fancytext.encrypt.symmetric;

import java.security.GeneralSecurityException;

import fancytext.Main;
import fancytext.encrypt.symmetric.cipher.AbstractCipher;

public class CipherException extends GeneralSecurityException
{
	public CipherExceptionType type;
	private AbstractCipher cipher;

	public CipherException(final CipherExceptionType type)
	{
		super(type.description);
		this.type = type;
		cipher = null;
	}

	public CipherException(final CipherExceptionType type, final Throwable thrown)
	{
		super(type.description + ": " + thrown.getMessage(), thrown);
		this.type = type;
		cipher = null;
	}

	public CipherException(final CipherExceptionType type, final AbstractCipher cipher, final Throwable thrown)
	{
		super(type.description + ": " + thrown.getMessage(), thrown);
		this.type = type;
		this.cipher = cipher;
	}

	public CipherException(final CipherExceptionType type, final String message)
	{
		super(type.description + ": " + message);
		this.type = type;
		cipher = null;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(64);
		builder.append(super.toString()).append(Main.lineSeparator);

		builder.append("* Error code: ").append(type.ordinal()).append("(").append(type.name()).append(") ").append(type.description).append(Main.lineSeparator);

		if (getCause() != null)
			builder.append("* Cause: ").append(getCause()).append(Main.lineSeparator);

		if (cipher != null)
			builder.append(cipher.dumpInformations());

		builder.append(Main.lineSeparator);

		return builder.toString();
	}
}
