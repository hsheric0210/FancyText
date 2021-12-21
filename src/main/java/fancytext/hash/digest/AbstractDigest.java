package fancytext.hash.digest;

import fancytext.hash.HashAlgorithm;

public abstract class AbstractDigest
{
	protected HashAlgorithm algorithm;

	public AbstractDigest(final HashAlgorithm algorithm)
	{
		this.algorithm = algorithm;
	}

	public abstract void update(final byte[] bytes);

	public abstract byte[] digest();
}
