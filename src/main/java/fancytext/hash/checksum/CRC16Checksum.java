package fancytext.hash.checksum;

import at.favre.lib.bytes.Bytes;
import fancytext.hash.AbstractHash;
import fancytext.hash.DigestException;
import fancytext.hash.HashAlgorithm;

/**
 * Copy of {@code sun.misc.CRC16}
 */
public class CRC16Checksum extends AbstractHash
{
	private int value;

	public CRC16Checksum(final HashAlgorithm algorithm)
	{
		super(algorithm);
	}

	@Override
	public void init() throws DigestException
	{
	}

	@Override
	public void update(final byte[] bytes)
	{
		for (byte b : bytes)
		{
			for (int i = 7; i >= 0; --i)
			{
				b <<= 1;
				final int var3 = b >>> 8 & 1;
				value = (value & '\u8000') == 0 ? (value << 1) + var3 : (value << 1) + var3 ^ 4129;
			}

			value &= 65535;
		}
	}

	@Override
	public byte[] digest()
	{
		return Bytes.from(value).array();
	}
}
