package fancytext.utils;

/**
 * Copy of {@code sun.misc.CRC16}.
 */
public final class CRC16
{
	private int value;

	public void update(final byte b)
	{
		int var2 = b;

		for (int i = 7; i >= 0; --i)
		{
			var2 <<= 1;
			final int var3 = var2 >>> 8 & 1;
			value = (value & '\u8000') == 0 ? (value << 1) + var3 : (value << 1) + var3 ^ 4129;
		}

		value &= 65535;
	}

	public void reset()
	{
		value = 0;
	}

	public int getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return String.valueOf(value);
	}
}
