package fancytext.hash.checksum;

import java.util.zip.CRC32;

import at.favre.lib.bytes.Bytes;
import fancytext.hash.AbstractHash;
import fancytext.hash.DigestException;
import fancytext.hash.HashAlgorithm;

/**
 * Copy of {@code sun.misc.CRC16}
 */
public class CRC32Checksum extends AbstractHash
{
	private CRC32 crc32;

	public CRC32Checksum(final HashAlgorithm algorithm)
	{
		super(algorithm);
	}

	@Override
	public void init() throws DigestException
	{
		crc32 = new CRC32();
	}

	@Override
	public void update(final byte[] bytes)
	{
		crc32.update(bytes);
	}

	@Override
	public byte[] digest()
	{
		return Bytes.from(crc32.getValue()).array();
	}
}
