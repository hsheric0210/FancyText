package fancytext.hash.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import fancytext.hash.AbstractHash;
import fancytext.hash.DigestException;
import fancytext.hash.DigestExceptionType;
import fancytext.hash.HashAlgorithm;

public class SpiBasedDigest extends AbstractHash
{
	protected final int digestSizeBits;

	protected MessageDigest messageDigest;

	public SpiBasedDigest(final HashAlgorithm algorithm, final int digestSizeBits)
	{
		super(algorithm);
		this.digestSizeBits = digestSizeBits;
	}

	public String getMessageDigestSpi()
	{
		return algorithm.getId() + (algorithm.getAvailableDigestSizes().length > 1 ? getDigestSizeBitsString() : "");
	}

	protected String getDigestSizeBitsString()
	{
		return '-' + Integer.toString(digestSizeBits);
	}

	@Override
	public void init() throws DigestException
	{
		try
		{
			messageDigest = MessageDigest.getInstance(getMessageDigestSpi(), algorithm.getProviderName());
		}
		catch (final NoSuchProviderException e)
		{
			throw new DigestException(DigestExceptionType.PROVIDER_UNAVAILABLE, e);
		}
		catch (final NoSuchAlgorithmException e)
		{
			throw new DigestException(DigestExceptionType.UNSUPPORTED_ALGORITHM, e);
		}
	}

	@Override
	public void update(final byte[] bytes)
	{
		messageDigest.update(bytes);
	}

	@Override
	public byte[] digest()
	{
		return messageDigest.digest();
	}
}
