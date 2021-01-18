package kr.re.nsr.crypto.util;

public final class Pack
{
	private Pack()
	{
		throw new AssertionError("Can't create an instance of class Pack");
	}

	private static int bigEndianToInt(final byte[] bs, int off)
	{
		int n = bs[off] << 24;
		n |= (bs[++off] & 0xff) << 16;
		n |= (bs[++off] & 0xff) << 8;
		n |= bs[++off] & 0xff;
		return n;
	}

	public static void bigEndianToInt(final byte[] bs, int off, final int[] ns)
	{
		for (int i = 0, j = ns.length; i < j; ++i)
		{
			ns[i] = bigEndianToInt(bs, off);
			off += 4;
		}
	}

	public static byte[] intToBigEndian(final int n)
	{
		final byte[] bs = new byte[4];
		intToBigEndian(n, bs, 0);
		return bs;
	}

	public static void intToBigEndian(final int n, final byte[] bs, int off)
	{
		bs[off] = (byte) (n >>> 24);
		bs[++off] = (byte) (n >>> 16);
		bs[++off] = (byte) (n >>> 8);
		bs[++off] = (byte) n;
	}

	public static byte[] intToBigEndian(final int[] ns)
	{
		final byte[] bs = new byte[4 * ns.length];
		intToBigEndian(ns, bs, 0);
		return bs;
	}

	private static void intToBigEndian(final int[] ns, final byte[] bs, int off)
	{
		for (int n : ns)
		{
			intToBigEndian(n, bs, off);
			off += 4;
		}
	}

	private static long bigEndianToLong(final byte[] bs, final int off)
	{
		final int hi = bigEndianToInt(bs, off);
		final int lo = bigEndianToInt(bs, off + 4);
		return (hi & 0xffffffffL) << 32 | lo & 0xffffffffL;
	}

	public static void bigEndianToLong(final byte[] bs, int off, final long[] ns)
	{
		for (int i = 0, j = ns.length; i < j; ++i)
		{
			ns[i] = bigEndianToLong(bs, off);
			off += 8;
		}
	}

	public static byte[] longToBigEndian(final long n)
	{
		final byte[] bs = new byte[8];
		longToBigEndian(n, bs, 0);
		return bs;
	}

	public static void longToBigEndian(final long n, final byte[] bs, final int off)
	{
		intToBigEndian((int) (n >>> 32), bs, off);
		intToBigEndian((int) (n & 0xffffffffL), bs, off + 4);
	}

	public static byte[] longToBigEndian(final long[] ns)
	{
		final byte[] bs = new byte[8 * ns.length];
		longToBigEndian(ns, bs, 0);
		return bs;
	}

	private static void longToBigEndian(final long[] ns, final byte[] bs, int off)
	{
		for (long n : ns)
		{
			longToBigEndian(n, bs, off);
			off += 8;
		}
	}

	private static int littleEndianToInt(final byte[] bs, int off)
	{
		int n = bs[off] & 0xff;
		n |= (bs[++off] & 0xff) << 8;
		n |= (bs[++off] & 0xff) << 16;
		n |= bs[++off] << 24;
		return n;
	}

	public static void littleEndianToInt(final byte[] bs, int off, final int[] ns)
	{
		for (int i = 0, j = ns.length; i < j; ++i)
		{
			ns[i] = littleEndianToInt(bs, off);
			off += 4;
		}
	}

	public static void littleEndianToInt(final byte[] bs, int bOff, final int[] ns, final int nOff, final int count)
	{
		for (int i = 0; i < count; ++i)
		{
			ns[nOff + i] = littleEndianToInt(bs, bOff);
			bOff += 4;
		}
	}

	public static byte[] intToLittleEndian(final int n)
	{
		final byte[] bs = new byte[4];
		intToLittleEndian(n, bs, 0);
		return bs;
	}

	private static void intToLittleEndian(final int n, final byte[] bs, int off)
	{
		bs[off] = (byte) n;
		bs[++off] = (byte) (n >>> 8);
		bs[++off] = (byte) (n >>> 16);
		bs[++off] = (byte) (n >>> 24);
	}

	public static byte[] intToLittleEndian(final int[] ns)
	{
		final byte[] bs = new byte[4 * ns.length];
		intToLittleEndian(ns, bs, 0);
		return bs;
	}

	private static void intToLittleEndian(final int[] ns, final byte[] bs, int off)
	{
		for (final int n : ns)
		{
			intToLittleEndian(n, bs, off);
			off += 4;
		}
	}

	private static long littleEndianToLong(final byte[] bs, final int off)
	{
		final int lo = littleEndianToInt(bs, off);
		final int hi = littleEndianToInt(bs, off + 4);
		return (hi & 0xffffffffL) << 32 | lo & 0xffffffffL;
	}

	public static void littleEndianToLong(final byte[] bs, int off, final long[] ns)
	{
		for (int i = 0, j = ns.length; i < j; ++i)
		{
			ns[i] = littleEndianToLong(bs, off);
			off += 8;
		}
	}

	public static byte[] longToLittleEndian(final long n)
	{
		final byte[] bs = new byte[8];
		longToLittleEndian(n, bs, 0);
		return bs;
	}

	private static void longToLittleEndian(final long n, final byte[] bs, final int off)
	{
		intToLittleEndian((int) (n & 0xffffffffL), bs, off);
		intToLittleEndian((int) (n >>> 32), bs, off + 4);
	}

	public static byte[] longToLittleEndian(final long[] ns)
	{
		final byte[] bs = new byte[8 * ns.length];
		longToLittleEndian(ns, bs, 0);
		return bs;
	}

	private static void longToLittleEndian(final long[] ns, final byte[] bs, int off)
	{
		for (long n : ns)
		{
			longToLittleEndian(n, bs, off);
			off += 8;
		}
	}
}
