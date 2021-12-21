package fancytext.encrypt.symmetric;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // because of 'illegal forward reference' error
public enum CipherPadding
{
	NONE("NoPadding", "None"),
	ZERO_FILL("ZeroBytePadding", "Zero padding"),
	PKCS7("PKCS5Padding", "PKCS #5 & PKCS #7 (RFC 5652) padding"),
	ISO10126("ISO10126Padding", "ISO 10126 padding"),
	X923("X923Padding", "ANSI X9.23 padding"),
	ISO7816_4("ISO7816-4Padding", "ISO/IEC 7816-4 & ISO/IEC 9797-1 padding"),
	TBC("TBCPadding", "Trailing Bit Compliment (abbr. TBC) padding");

	private final String paddingName;
	private final String displayName;

	public static final CipherPadding[] SUNJCE_DEFAULT =
	{
			NONE, PKCS7, ISO10126
	};
	public static final CipherPadding[] BC_DEFAULT = values();

	CipherPadding(final String paddingName, final String displayName)
	{
		this.paddingName = paddingName;
		this.displayName = displayName;
	}

	public String getPaddingName()
	{
		return paddingName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
