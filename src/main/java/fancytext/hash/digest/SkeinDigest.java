package fancytext.hash.digest;

import fancytext.hash.DigestException;
import fancytext.hash.DigestExceptionType;
import fancytext.hash.HashAlgorithm;

public class SkeinDigest extends SpiBasedDigest
{
	private final int stateSizeBits;

	public SkeinDigest(final HashAlgorithm algorithm, final int stateSizeBits, final int digestSizeBits) throws DigestException
	{
		super(algorithm, digestSizeBits);
		if (algorithm != HashAlgorithm.Skein)
			throw new DigestException(DigestExceptionType.UNSUPPORTED_ALGORITHM, "Only supports Skein");
		this.stateSizeBits = stateSizeBits;
	}

	@Override
	protected String getDigestSizeBitsString()
	{
		return '-' + Integer.toString(stateSizeBits) + '-' + digestSizeBits;
	}
}
