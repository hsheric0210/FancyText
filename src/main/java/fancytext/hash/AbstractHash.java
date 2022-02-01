package fancytext.hash;

import java.util.StringJoiner;

import fancytext.Main;

public abstract class AbstractHash
{
	protected HashAlgorithm algorithm;

	public AbstractHash(final HashAlgorithm algorithm)
	{
		this.algorithm = algorithm;
	}

	protected void dumpAdditionalInformations(final StringBuilder builder)
	{
	}

	protected void serializeAdditionalInformations(final StringJoiner builder)
	{
	}

	public final String dumpInformations()
	{
		final StringBuilder builder = new StringBuilder();

		builder.append("Digest algorithm: ").append(algorithm.getId()).append("(").append(algorithm).append(")").append(Main.lineSeparator);
		dumpAdditionalInformations(builder);

		return builder.toString();
	}

	@Override
	public final String toString()
	{
		final StringJoiner joiner = new StringJoiner(" / ");
		joiner.add("Algorithm=" + algorithm.getId() + "(" + algorithm + ") - (Provider=" + algorithm.getProviderName() + ")");
		serializeAdditionalInformations(joiner);
		return joiner.toString();
	}

	public abstract void init() throws DigestException;

	public abstract void update(final byte[] bytes) throws DigestException;

	public abstract byte[] digest() throws DigestException;
}
