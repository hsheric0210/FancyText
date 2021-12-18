package fancytext.encrypt.symmetric;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // because of 'illegal forward reference' error
public enum CipherPadding
{
	NONE("NoPadding", "None"),
	ZERO_FILL("ZeroBytePadding", "Zero-fill padding"),
	PKCS5("PKCS5Padding", "PKCS #5 / PKCS #7 padding"),
	ISO10126("ISO10126Padding", "ISO 10126-2 padding"),
	X923("X923Padding", "x9.23 padding"),
	ISO7816_4("ISO7816-4Padding", "ISO 7816-4 / ISO 9797-1 padding"),
	TBC("TBCPadding", "Trailing-Bit-Compliment(TBC) padding");

	private final String paddingName;
	private final String displayName;

	public static final CipherPadding[] SUNJCE_DEFAULT =
	{
			NONE, PKCS5, ISO10126
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
