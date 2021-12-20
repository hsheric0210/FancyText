package fancytext.utils;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import at.favre.lib.bytes.Bytes;

public final class DecimalToString
{
	public static final int TOSTRING_UPPERCASE = 0b1;
	public static final int TOSTRING_HEXPREFIX = 0b10;

	private DecimalToString()
	{

	}

	public static String applyUpperCase(final String string, final int flags)
	{
		return (flags & TOSTRING_UPPERCASE) == 0 ? string : string.toUpperCase(Locale.ENGLISH);
	}

	public static String applyHexPrefix(final String string, final int flags)
	{
		return (flags & TOSTRING_HEXPREFIX) == 0 ? string : "0x" + string;
	}

	public static String toString(final long value, final int radix, final int flags)
	{
		return applyHexPrefix(applyUpperCase(Long.toString(value, radix), flags), flags);
	}

	public static String toString(final byte[] bytes, final int radix, final String delimiter, final int flags)
	{
		final int length = bytes.length;

		if (length <= 0)
			return "";

		final String converted = IntStream.range(0, length).mapToObj(i ->
		{
			String string = Integer.toUnsignedString(bytes[i], radix);
			final int convertedLength = string.length();

			if (convertedLength > 2)
				// Truncate start
				string = string.substring(convertedLength - 2);
			else if (convertedLength < 2)
			{
				// Pad start
				final char[] chars = new char[2];
				Arrays.fill(chars, '0');
				string.getChars(0, convertedLength, chars, 2 - convertedLength);
				string = new String(chars);
			}

			return applyUpperCase(string, flags);
		}).collect(Collectors.joining(delimiter));

		return applyHexPrefix(converted, flags);
	}
}
