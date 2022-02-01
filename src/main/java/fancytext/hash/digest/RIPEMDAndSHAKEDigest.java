package fancytext.hash.digest;

import fancytext.hash.DigestException;
import fancytext.hash.DigestExceptionType;
import fancytext.hash.HashAlgorithm;

public class RIPEMDAndSHAKEDigest extends SpiBasedDigest
{
	public RIPEMDAndSHAKEDigest(final HashAlgorithm algorithm, final int digestSizeBits) throws DigestException
	{
		super(algorithm, digestSizeBits);
		if (algorithm != HashAlgorithm.RIPEMD && algorithm != HashAlgorithm.SHAKE)
			throw new DigestException(DigestExceptionType.UNSUPPORTED_ALGORITHM, "Only supports RIPEMD and SHAKE");
	}

	@Override
	protected String getDigestSizeBitsString()
	{
		return Integer.toString(digestSizeBits);
	}
}
