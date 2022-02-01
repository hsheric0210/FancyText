package fancytext.hash.checksum;

import java.util.zip.Adler32;

import at.favre.lib.bytes.Bytes;
import fancytext.hash.AbstractHash;
import fancytext.hash.DigestException;
import fancytext.hash.HashAlgorithm;

/**
 * Copy of {@code sun.misc.CRC16}
 */
public class Adler32Checksum extends AbstractHash
{
	private Adler32 adler32;

	public Adler32Checksum(final HashAlgorithm algorithm)
	{
		super(algorithm);
	}

	@Override
	public void init() throws DigestException
	{
		adler32 = new Adler32();
	}

	@Override
	public void update(final byte[] bytes)
	{
		adler32.update(bytes);
	}

	@Override
	public byte[] digest()
	{
		return Bytes.from(adler32.getValue()).array();
	}
}
