package fancytext.utils;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import at.favre.lib.bytes.Bytes;

public class HexEncoder implements IEncoder
{
	public static int HEX_UPPERCASE = 0b1;

	@Override
	public String encode(final byte[] bytes, final Object parameters, final int flags)
	{
		final boolean upper = (flags & HEX_UPPERCASE) != 0;
		if (parameters instanceof String)
		{
			final String delimiter = (String) parameters;
			if (!delimiter.isEmpty())
				return encodeHexTokenized(bytes, delimiter, upper);
		}

		final String encode = Bytes.from(bytes).encodeHex(false);
		if (upper)
			return encode.toUpperCase(Locale.ROOT);

		return encode;
	}

	@Override
	public byte[] decode(final String string)
	{
		return Bytes.parseHex(string).array();
	}

	private static String encodeHexTokenized(final byte[] bytes, final CharSequence delimiter, final boolean upper)
	{
		return IntStream.range(0, bytes.length).mapToObj(i ->
		{
			String string = Integer.toUnsignedString(bytes[i], 16);
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

			return upper ? string.toUpperCase(Locale.ROOT) : string;
		}).collect(Collectors.joining(delimiter));
	}
}
