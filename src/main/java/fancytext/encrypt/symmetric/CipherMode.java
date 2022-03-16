package fancytext.encrypt.symmetric;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // because of 'illegal forward reference' error
public enum CipherMode
{
	EMPTY,
	ECB(false, false, false, false),
	CBC(true, false, false, false),
	CFB(true, false, false, true),
	OFB(true, false, false, true),
	PCBC(true, false, false, false),
	CTR(true, false, false, false),
	CTS(true, false, false, false),
	CCM(true, true, true, false),
	GCM(true, true, true, false);

	private final boolean isUsingIV;
	private final boolean isUsingNonce;
	private final boolean isAEADMode;
	private final boolean isUsingUnitBytes;

	public static final CipherMode[] SUNJCE_DEFAULT =
	{
			ECB, CBC, CFB, OFB, PCBC, CTR, CTS, GCM
	};

	public static final CipherMode[] SUNJCE_DEFAULT_NO_AEAD =
	{
			ECB, CBC, CFB, OFB, PCBC, CTR, CTS
	};

	public static final CipherMode[] BC_DEFAULT =
	{
			ECB, CBC, CFB, OFB, CTR, CTS, CCM, GCM
	};

	public static final CipherMode[] BC_DEFAULT_NO_AEAD =
	{
			ECB, CBC, CFB, OFB, CTR, CTS/* , CCM, GCM */
	};

	public static final CipherMode[] NO_MODE_SUPPORT =
	{
			EMPTY
	};

	CipherMode()
	{
		this(false, false, false, false);
	}

	CipherMode(final boolean usingIV, final boolean usingNonce, final boolean AEAD, final boolean usingUnitBytes)
	{
		isUsingIV = usingIV;
		isUsingNonce = usingNonce;
		isAEADMode = AEAD;
		isUsingUnitBytes = usingUnitBytes;
	}

	public boolean isUsingIV()
	{
		return isUsingIV;
	}

	public boolean isUsingNonce()
	{
		return isUsingNonce;
	}

	public boolean isAEADMode()
	{
		return isAEADMode;
	}

	public boolean isUsingUnitBytes()
	{
		return isUsingUnitBytes;
	}

	@Override
	public String toString()
	{
		if (this == EMPTY)
			return "";
		return name();
	}
}
