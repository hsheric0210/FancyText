package fancytext.utils.encoding;

import java.util.Base64;

import at.favre.lib.bytes.Bytes;

public class Base64Encoder implements IEncoder
{
	public static int BASE64_URLSAFE = 0b1;
	public static int BASE64_MIME = 0b10;
	public static int BASE64_DO_PADDING = 0b100;

	@Override
	public String encode(final byte[] bytes, final Object parameters, final int flags)
	{
		if ((flags & BASE64_MIME) != 0)
		{
			final String b = Base64.getMimeEncoder().encodeToString(bytes);

			if ((flags & BASE64_DO_PADDING) != 0)
				return b;

			// Strip trailing padding characters
			int i = b.length() - 1;
			while (i > 0)
			{
				if (b.charAt(i - 1) != '=')
					break;
				i--;
			}

			return b.substring(0, i);
		}

		return Bytes.from(bytes).encodeBase64((flags & BASE64_URLSAFE) != 0, (flags & BASE64_DO_PADDING) != 0);
	}

	@Override
	public byte[] decode(final String string)
	{
		return Bytes.parseBase64(string).array();
	}
}
