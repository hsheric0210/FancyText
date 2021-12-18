package fancytext.encrypt.symmetric;

import java.util.Arrays;

public final class CipherHelper
{
	private CipherHelper()
	{

	}

	private static byte[] pad(final byte[] bytes, final int requiredLength, final byte paddingByte)
	{
		final byte[] padding = new byte[requiredLength];
		Arrays.fill(padding, paddingByte);
		System.arraycopy(bytes, 0, padding, 0, bytes.length);
		return padding;
	}

	public static byte[] pad(final byte[] bytes, final int minLength, final int maxLength, final byte paddingByte)
	{
		final int length = bytes.length;

		// Truncation
		if (maxLength > 0 && length > maxLength)
			return Arrays.copyOf(bytes, maxLength);

		// Pad
		if (minLength > 0 && length < minLength)
			return pad(bytes, minLength, paddingByte);

		// Nothing to do
		return bytes;
	}

	public static byte[] padMultipleOf(final byte[] bytes, final int n, final byte paddingByte)
	{
		if (bytes.length % n == 0)
			return bytes;

		final int bytesLength = bytes.length;
		return pad(bytes, n * ((bytesLength - bytesLength % n) / n + 1), paddingByte);
	}
}
