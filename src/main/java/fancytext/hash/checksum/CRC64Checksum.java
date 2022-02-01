package fancytext.hash.checksum;

import java.util.zip.CRC32;

import net.boeckling.crc.CRC64;

import at.favre.lib.bytes.Bytes;
import fancytext.hash.AbstractHash;
import fancytext.hash.DigestException;
import fancytext.hash.HashAlgorithm;

/**
 * Copy of {@code sun.misc.CRC16}
 */
public class CRC64Checksum extends AbstractHash
{
	private CRC64 crc64;

	public CRC64Checksum(final HashAlgorithm algorithm)
	{
		super(algorithm);
	}

	@Override
	public void init() throws DigestException
	{
		crc64 = new CRC64();
	}

	@Override
	public void update(final byte[] bytes)
	{
		crc64.update(bytes, bytes.length);
	}

	@Override
	public byte[] digest()
	{
		return Bytes.from(crc64.getValue()).array();
	}
}
