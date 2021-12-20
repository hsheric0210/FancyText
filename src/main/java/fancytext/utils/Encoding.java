package fancytext.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import at.favre.lib.bytes.Bytes;

/**
 * A few more detailed {@code StandardCharsets}
 */
public enum Encoding
{
	/**
	 * Binary (aka "1" and "0") representation
	 */
	BINARY("Binary", StandardCharsets.US_ASCII, new IEncoder()
	{
		@Override
		public String encode(final byte[] bytes)
		{
			return Bytes.from(bytes).encodeBinary();
		}

		@Override
		public byte[] decode(final String string)
		{
			return Bytes.parseBinary(string).array();
		}
	}),

	/**
	 * Octal (0-7) representation
	 */
	OCTAL("Octal", StandardCharsets.US_ASCII, new IEncoder()
	{
		@Override
		public String encode(final byte[] bytes)
		{
			return Bytes.from(bytes).encodeOctal();
		}

		@Override
		public byte[] decode(final String string)
		{
			return Bytes.parseOctal(string).array();
		}
	}),

	/**
	 * Decimal (0-9) representation
	 */
	DECIMAL("Decimal", StandardCharsets.US_ASCII, new IEncoder()
	{
		@Override
		public String encode(final byte[] bytes)
		{
			return Bytes.from(bytes).encodeDec();
		}

		@Override
		public byte[] decode(final String string)
		{
			return Bytes.parseDec(string).array();
		}
	}),

	/**
	 * Base16 or Hex representation
	 */
	HEXADECIMAL("Hexadecimal (Base16)", StandardCharsets.US_ASCII, new IEncoder()
	{
		@Override
		public String encode(final byte[] bytes)
		{
			return Bytes.from(bytes).encodeHex(false);
		}

		@Override
		public byte[] decode(final String string)
		{
			return Bytes.parseHex(string).array();
		}
	}),

	/**
	 * Base32
	 */
	BASE32("Base32", StandardCharsets.US_ASCII, new IEncoder()
	{
		@Override
		public String encode(final byte[] bytes)
		{
			return Bytes.from(bytes).encodeBase32();
		}

		@Override
		public byte[] decode(final String string)
		{
			return Bytes.parseBase32(string).array();
		}
	}),

	/**
	 * Base64
	 */
	BASE64("Base64", StandardCharsets.US_ASCII, new IEncoder()
	{
		@Override
		public String encode(final byte[] bytes)
		{
			return Bytes.from(bytes).encodeBase64();
		}

		@Override
		public byte[] decode(final String string)
		{
			return Bytes.parseBase64(string).array();
		}
	}),

	/**
	 * Base64 with URL-safe variation substitution
	 */
	BASE64_URL("Base64 (URL-safe)", StandardCharsets.US_ASCII, new IEncoder()
	{
		@Override
		public String encode(final byte[] bytes)
		{
			return Bytes.from(bytes).encodeBase64Url();
		}

		@Override
		public byte[] decode(final String string)
		{
			return Bytes.parseBase64(string).array();
		}
	}),

	/**
	 * Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
	 */
	US_ASCII("ISO646-US (a.k.a. ASCII)", StandardCharsets.US_ASCII),

	/**
	 * ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
	 */
	ISO_8859_1("ISO-8859-1 (a.k.a. ISO-LATIN-1)", StandardCharsets.ISO_8859_1),

	/**
	 * Eight-bit UCS Transformation Format
	 */
	UTF_8("UTF-8", StandardCharsets.UTF_8),

	/**
	 * Sixteen-bit UCS Transformation Format, big-endian byte order
	 */
	UTF_16BE("UTF-16 (Big-endian)", StandardCharsets.UTF_16BE),

	/**
	 * Sixteen-bit UCS Transformation Format, little-endian byte order
	 */
	UTF_16LE("UTF-16 (Little-endian)", StandardCharsets.UTF_16LE),

	/**
	 * Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark
	 */
	UTF_16("UTF-16 (Optional-BOM)", StandardCharsets.UTF_16);

	private final String displayName;
	private final IEncoder encoder;
	private final Charset charset;

	Encoding(final String displayName, final Charset charset, final IEncoder encoder)
	{
		this.displayName = displayName;
		this.encoder = encoder;
		this.charset = charset;
	}

	Encoding(final String displayName, final Charset charset)
	{
		this(displayName, charset, new IEncoder()
		{
			@Override
			public String encode(final byte[] bytes)
			{
				return new String(bytes, charset);
			}

			@Override
			public byte[] decode(final String string)
			{
				return string.getBytes(charset);
			}
		});
	}

	@Override
	public String toString()
	{
		return displayName;
	}

	public String encode(final byte[] bytes)
	{
		return encoder.encode(bytes);
	}

	public byte[] decode(final String string)
	{
		return encoder.decode(string);
	}

	public byte[] readEncoded(final File file) throws IOException
	{
		return decode(new String(Files.readAllBytes(file.toPath()), charset));
	}

	public void writeEncoded(final File file, final byte[] bytes, final OpenOption... options) throws IOException
	{
		Files.write(file.toPath(), encode(bytes).getBytes(charset), options);
	}
}
