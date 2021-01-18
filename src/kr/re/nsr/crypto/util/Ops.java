package kr.re.nsr.crypto.util;

import java.util.Objects;

public final class Ops
{

	private Ops()
	{
		throw new AssertionError("Can't create an instance of class Ops");
	}

	/**
	 * lhs ^= rhs
	 * 
	 * @param lhs
	 *            [out]
	 * @param rhs
	 *            [in]
	 */
	public static void XOR(final byte[] lhs, final byte[] rhs)
	{
		Objects.requireNonNull(lhs, "lhs");
		Objects.requireNonNull(rhs, "rhs");

		if (lhs.length != rhs.length)
			throw new IllegalArgumentException("the length of two arrays should be same");

		for (int i = 0, j = lhs.length; i < j; ++i)
			lhs[i] ^= rhs[i];
	}

	/**
	 * 
	 * @param lhs
	 * @param lhsOff
	 * @param rhs
	 * @param rhsOff
	 * @param len
	 */
	public static void XOR(final byte[] lhs, final int lhsOff, final byte[] rhs, final int rhsOff, final int len)
	{
		Objects.requireNonNull(lhs, "lhs");
		Objects.requireNonNull(rhs, "rhs");

		if (lhs.length < lhsOff + len)
			throw new ArrayIndexOutOfBoundsException("lhs.length < lhsOff + len");

		if (rhs.length < rhsOff + len)
			throw new ArrayIndexOutOfBoundsException("rhs.length < rhsOff + len");

		for (int i = 0; i < len; ++i)
			lhs[lhsOff + i] ^= rhs[rhsOff + i];
	}

	/**
	 * lhs = rhs1 ^ rhs2
	 * 
	 * @param lhs
	 *             [out]
	 * @param rhs1
	 *             [in]
	 * @param rhs2
	 *             [in]
	 */
	public static void XOR(final byte[] lhs, final byte[] rhs1, final byte[] rhs2)
	{
		Objects.requireNonNull(lhs, "lhs");
		Objects.requireNonNull(rhs1, "rhs1");
		Objects.requireNonNull(rhs2, "rhs2");

		if (lhs.length != rhs1.length || lhs.length != rhs2.length)
			throw new IllegalArgumentException("the length of arrays should be same");

		for (int i = 0, j = lhs.length; i < j; ++i)
			lhs[i] = (byte) (rhs1[i] ^ rhs2[i]);
	}

	/**
	 * 
	 * @param out
	 * @param outOff
	 * @param len
	 * @param lhs
	 * @param lhs1Off
	 * @param rhs
	 * @param rhsOff
	 */
	public static void XOR(final byte[] out, final int outOff, final byte[] lhs, final int lhs1Off, final byte[] rhs, final int rhsOff, final int len)
	{
		Objects.requireNonNull(out, "out");
		Objects.requireNonNull(lhs, "lhs");
		Objects.requireNonNull(rhs, "rhs");

		if (out.length < outOff + len)
			throw new ArrayIndexOutOfBoundsException("out.length < outOff + len");

		if (lhs.length < lhs1Off + len)
			throw new ArrayIndexOutOfBoundsException("lhs.length < lhs1Off + len");

		if (rhs.length < rhsOff + len)
			throw new ArrayIndexOutOfBoundsException("rhs.length < rhsOff + len");

		for (int i = 0; i < len; ++i)
			out[outOff + i] = (byte) (lhs[lhs1Off + i] ^ rhs[rhsOff + i]);
	}

	/**
	 * lhs ^= rhs
	 * 
	 * @param lhs
	 *            [out]
	 * @param rhs
	 *            [in]
	 */
	public static void XOR(final int[] lhs, final int[] rhs)
	{
		Objects.requireNonNull(lhs, "lhs");
		Objects.requireNonNull(rhs, "rhs");

		if (lhs.length != rhs.length)
			throw new IllegalArgumentException("the length of two arrays should be same");

		for (int i = 0, j = lhs.length; i < j; ++i)
			lhs[i] ^= rhs[i];
	}

	public static void XOR(final int[] lhs, final int lhsOff, final int[] rhs, final int rhsOff, final int len)
	{
		Objects.requireNonNull(lhs, "lhs");
		Objects.requireNonNull(rhs, "rhs");

		if (lhs.length < lhsOff + len)
			throw new ArrayIndexOutOfBoundsException("lhs.length < lhsOff + len");

		if (rhs.length < rhsOff + len)
			throw new ArrayIndexOutOfBoundsException("rhs.length < rhsOff + len");

		for (int i = 0; i < len; ++i)
			lhs[lhsOff + i] ^= rhs[rhsOff + i];
	}

	/**
	 * lhs = rhs1 ^ rhs2
	 * 
	 * @param lhs
	 *             [out]
	 * @param rhs1
	 *             [in]
	 * @param rhs2
	 *             [in]
	 */
	public static void XOR(final int[] lhs, final int[] rhs1, final int[] rhs2)
	{
		Objects.requireNonNull(lhs, "lhs");
		Objects.requireNonNull(rhs1, "rhs1");
		Objects.requireNonNull(rhs2, "rhs2");

		if (lhs.length != rhs1.length || lhs.length != rhs2.length)
			throw new IllegalArgumentException("the length of arrays should be same");

		for (int i = 0, j = lhs.length; i < j; ++i)
			lhs[i] = rhs1[i] ^ rhs2[i];
	}

	/**
	 * 
	 * @param lhs
	 * @param lhsOff
	 * @param len
	 * @param rhs1
	 * @param rhs1Off
	 * @param rhs2
	 * @param rhs2Off
	 */
	public static void XOR(final int[] lhs, final int lhsOff, final int[] rhs1, final int rhs1Off, final int[] rhs2, final int rhs2Off, final int len)
	{
		Objects.requireNonNull(lhs, "lhs");
		Objects.requireNonNull(rhs1, "rhs1");
		Objects.requireNonNull(rhs2, "rhs2");

		if (lhs.length < lhsOff + len)
			throw new ArrayIndexOutOfBoundsException("lhs.length < lhsOff + len");

		if (rhs1.length < rhs1Off + len)
			throw new ArrayIndexOutOfBoundsException("rhs1.length < rhs1Off + len");

		if (rhs2.length < rhs2Off + len)
			throw new ArrayIndexOutOfBoundsException("rhs2.length < rhs2Off + len");

		for (int i = 0; i < len; ++i)
			lhs[lhsOff + i] = rhs1[rhs1Off + i] ^ rhs2[rhs2Off + i];
	}

	public static void shiftLeft(final byte[] bytes, final int shift)
	{
		Objects.requireNonNull(bytes, "input array should not be null");

		if (shift < 1 || shift > 7)
			throw new IllegalArgumentException("the allowed shift amount is 1~7");

		int tmp = bytes[0];
		for (int i = 1, j = bytes.length; i < j; ++i)
		{
			tmp = tmp << 8 | bytes[i] & 0xff;
			bytes[i - 1] = (byte) ((tmp << shift & 0xff00) >>> 8);
		}
		bytes[bytes.length - 1] <<= shift;
	}

	public static void shiftRight(final byte[] bytes, final int shift)
	{
		Objects.requireNonNull(bytes, "input array should not be null");

		if (shift < 1 || shift > 7)
			throw new IllegalArgumentException("the allowed shift amount is 1~7");

		int tmp;
		for (int i = bytes.length - 1; i > 0; --i)
		{
			tmp = bytes[i - 1] << 8 | bytes[i] & 0xff;
			bytes[i] = (byte) (tmp >> shift);
		}
		tmp = bytes[0] & 0xff;
		bytes[0] = (byte) (tmp >>> shift);
	}

	/**
	 * byte array to int array
	 */
	public static void pack(final byte[] in, final int[] out)
	{
		Objects.requireNonNull(in, "in");
		Objects.requireNonNull(out, "out");

		if (in.length != out.length << 2)
			throw new ArrayIndexOutOfBoundsException("in.length != out.length * 2");

		int outIndex = 0;
		for (int inIndex = 0, inIndexLength = in.length; inIndex < inIndexLength; ++inIndex, ++outIndex)
		{
			out[outIndex] = in[inIndex] & 0xff;
			out[outIndex] |= (in[++inIndex] & 0xff) << 8;
			out[outIndex] |= (in[++inIndex] & 0xff) << 16;
			out[outIndex] |= (in[++inIndex] & 0xff) << 24;
		}
	}

	public static void pack(final byte[] in, final int inOff, final int[] out, final int outOff, final int inlen)
	{
		Objects.requireNonNull(in, "in");
		Objects.requireNonNull(out, "out");

		if ((inlen & 3) != 0)
			throw new IllegalArgumentException("length should be multiple of 4");

		if (in.length < inOff + inlen)
			throw new ArrayIndexOutOfBoundsException("in.length < inOff + inlen");

		if (out.length < outOff + inlen / 4)
			throw new ArrayIndexOutOfBoundsException("out.length < outOff + inlen / 4");

		int outIdx = outOff;
		final int endInIdx = inOff + inlen;
		for (int inIdx = inOff; inIdx < endInIdx; ++inIdx, ++outIdx)
		{
			out[outIdx] = in[inIdx] & 0xff;
			out[outIdx] |= (in[++inIdx] & 0xff) << 8;
			out[outIdx] |= (in[++inIdx] & 0xff) << 16;
			out[outIdx] |= (in[++inIdx] & 0xff) << 24;
		}
	}

	/**
	 * int array to byte array
	 */
	public static void unpack(final int[] in, final byte[] out)
	{
		Objects.requireNonNull(in, "in");
		Objects.requireNonNull(out, "out");

		if (in.length << 2 != out.length)
			throw new ArrayIndexOutOfBoundsException("in.length * 4 != out.length");

		int outIndex = 0;
		for (int inIndex = 0, inIndexLength = in.length; inIndex < inIndexLength; ++inIndex, ++outIndex)
		{
			out[outIndex] = (byte) in[inIndex];
			out[++outIndex] = (byte) (in[inIndex] >> 8);
			out[++outIndex] = (byte) (in[inIndex] >> 16);
			out[++outIndex] = (byte) (in[inIndex] >> 24);
		}
	}

	public static void unpack(final int[] in, final int inOff, final byte[] out, final int outOff, final int inlen)
	{
		Objects.requireNonNull(in, "in");
		Objects.requireNonNull(out, "out");

		if (in.length < inOff + inlen)
			throw new ArrayIndexOutOfBoundsException("in.length < inOff + inlen");

		if (out.length < outOff + (inlen << 2))
			throw new ArrayIndexOutOfBoundsException("out.length < outOff + inlen * 4");

		int outIdx = outOff;
		final int endInIdx = inOff + inlen;
		for (int inIdx = inOff; inIdx < endInIdx; ++inIdx, ++outIdx)
		{
			out[outIdx] = (byte) in[inIdx];
			out[++outIdx] = (byte) (in[inIdx] >> 8);
			out[++outIdx] = (byte) (in[inIdx] >> 16);
			out[++outIdx] = (byte) (in[inIdx] >> 24);
		}
	}

	public static void XOR(final long[] lhs, final int lhsOff, final long[] rhs, final int rhsOff, final int len)
	{
		Objects.requireNonNull(lhs, "lhs");
		Objects.requireNonNull(rhs, "rhs");

		if (lhs.length < lhsOff + len)
			throw new ArrayIndexOutOfBoundsException("lhs.length < lhsOff + len");

		if (rhs.length < rhsOff + len)
			throw new ArrayIndexOutOfBoundsException("rhs.length < rhsOff + len");

		for (int i = 0; i < len; ++i)
			lhs[lhsOff + i] ^= rhs[rhsOff + i];
	}
}
