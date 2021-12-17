package fancytext.tabs.encrypt;

import java.security.GeneralSecurityException;
import java.util.Optional;

import fancytext.Main;
import fancytext.encrypt.symmetric.CipherExceptionType;

class SymmetricKeyCryptionException extends GeneralSecurityException
{
	private final CipherExceptionType type;
	private final String algorithm;
	private final String theProblem;
	private final Throwable cause;

	public SymmetricKeyCryptionException(final CipherExceptionType type, final Throwable cause)
	{
		this.type = type;
		this.cause = cause;
		algorithm = null;
		theProblem = null;
	}

	public SymmetricKeyCryptionException(final CipherExceptionType type, final String algorithm, final Throwable cause)
	{
		this.type = type;
		this.algorithm = algorithm;
		this.cause = cause;
		theProblem = null;
	}

	public SymmetricKeyCryptionException(final CipherExceptionType type, final String algorithm, final String problem, final Throwable cause)
	{
		this.type = type;
		this.algorithm = algorithm;
		theProblem = problem;
		this.cause = cause;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(64);
		builder.append(getClass().getName()).append(":").append(Main.lineSeparator);

		builder.append("* ERROR CODE ").append(type.ordinal()).append("(").append(type.name()).append(") - ").append(type.description).append(Main.lineSeparator);
		builder.append("* Caused by ").append(cause).append(Main.lineSeparator);

		Optional.ofNullable(algorithm).ifPresent(str -> builder.append("Algorithm: ").append(str).append(Main.lineSeparator));

		Optional.ofNullable(theProblem).ifPresent(str -> builder.append(str).append(Main.lineSeparator));

		builder.append(Main.lineSeparator);

		return builder.toString();
	}
}
