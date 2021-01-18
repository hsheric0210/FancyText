package kr.re.nsr.crypto.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Hex
{

	private Hex()
	{
		throw new AssertionError("Can't create an instance of class Hex");
	}

	public static byte[] decodeHexString(final CharSequence hexString)
	{
		if (hexString == null)
			return null;

		final byte[] buf = new byte[hexString.length() / 2];

		for (int i = 0, j = buf.length; i < j; ++i)
		{
			buf[i] = (byte) (16 * decodeHexChar(hexString.charAt(i << 1)));
			buf[i] += decodeHexChar(hexString.charAt((i << 1) + 1));
		}

		return buf;
	}

	private static byte decodeHexChar(final char ch)
	{
		if (ch >= '0' && ch <= '9')
			return (byte) (ch - '0');

		if (ch >= 'a' && ch <= 'f')
			return (byte) (ch - 'a' + 10);

		return ch >= 'A' && ch <= 'F' ? (byte) (ch - 'A' + 10) : 0;

	}

	public static byte[] toBytes(final int value, final int len)
	{
		final byte[] buf = new byte[len];
		toBytes(value, buf, 0, len);
		return buf;
	}

	public static void toBytes(final long value, final byte[] buf, final int offset, final int len)
	{
		if (len <= 0)
			throw new IllegalArgumentException("len should be positive integer");

		for (int i = offset + len - 1, shift = 0; i >= offset; --i, shift += 8)
			buf[i] = (byte) (value >>> shift & 0xff);
	}

	/**
	 * convert byte buffer to hex string
	 * 
	 * @param  buf
	 * @return
	 */
	private static String toHexString(final byte[] buf)
	{
		return Optional.ofNullable(buf).map(bytes -> toHexString(bytes, 0, bytes.length, 0)).orElse(null);

	}

	public static String toHexString(final byte[] buf, final int indent)
	{
		return Optional.ofNullable(buf).map(bytes -> toHexString(bytes, 0, bytes.length, indent)).orElse(null);

	}

	private static String toHexString(final byte[] buf, final int offset, final int len, final int indent)
	{
		if (buf == null)
			return null;

		if (buf.length < offset + len)
			throw new IllegalArgumentException("buffer length is not enough");

		final StringBuilder sb = new StringBuilder();

		int index = 0;
		for (int i = offset; i < offset + len; ++i)
		{
			sb.append(String.format("%02x", buf[i]));
			++index;

			if (index != len && indent != 0 && index % indent == 0)
				sb.append(" ");
		}

		return sb.toString();
	}

	/**
	 * convert int buffer to hex string
	 * 
	 * @param  buf
	 * @return
	 */
	private static String toHexString(final int[] buf)
	{
		if (buf == null)
			return null;

		final String sb = Arrays.stream(buf).mapToObj(ch -> String.format("%08x", ch) + "  ").collect(Collectors.joining());

		return sb;
	}

	public static String toBitString(final byte[] in)
	{
		if (in == null)
			throw new NullPointerException("input array should not be null");

		final StringBuilder sb = new StringBuilder();
		for (final byte i : in)
			sb.append(toBitString(i));

		return sb.toString();
	}

	private static String toBitString(final byte in)
	{
		final StringBuilder sb = new StringBuilder();

		for (int i = 7; i >= 0; --i)
			sb.append(in >>> i & 1);

		return sb.toString();
	}

	public static String toBitString(final int[] in)
	{
		if (in == null)
			throw new NullPointerException("input array should not be null");

		final String sb = Arrays.stream(in).mapToObj(Hex::toBitString).collect(Collectors.joining());

		return sb;
	}

	private static String toBitString(final int in)
	{
		final StringBuilder sb = new StringBuilder();

		for (int i = 31; i >= 0; --i)
			sb.append(in >>> i & 1);

		return sb.toString();
	}
}
