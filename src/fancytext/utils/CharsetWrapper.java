package fancytext.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A few more detailed {@code StandardCharsets}
 */
public enum CharsetWrapper
{

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
	private final Charset charset;

	CharsetWrapper(final String displayName, final Charset charset)
	{
		this.displayName = displayName;
		this.charset = charset;
	}

	@Override
	public String toString()
	{
		return displayName;
	}

	public Charset getCharset()
	{
		return charset;
	}
}
