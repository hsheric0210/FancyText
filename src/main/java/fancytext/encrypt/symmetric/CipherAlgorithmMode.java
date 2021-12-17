package fancytext.encrypt.symmetric;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // because of 'illegal forward reference' error
public enum CipherAlgorithmMode
{
	ECB(false, false, false),
	CBC(true, false, false),
	CFB(true, false, false),
	OFB(true, false, false),
	PCBC(true, false, false),
	CTR(true, false, false),
	CTS(true, false, false),
	CCM(true, true, true),
	GCM(true, true, true);

	private final boolean isUsingIV;
	private final boolean isUsingNonce;
	private final boolean isAEADMode;

	public static final CipherAlgorithmMode[] SUNJCE_DEFAULT =
	{
			ECB, CBC, CFB, OFB, PCBC, CTR, CTS, GCM
	};

	public static final CipherAlgorithmMode[] SUNJCE_DEFAULT_NO_AEAD =
	{
			ECB, CBC, CFB, OFB, PCBC, CTR, CTS
	};

	public static final CipherAlgorithmMode[] BC_DEFAULT =
	{
			ECB, CBC, CFB, OFB, CTR, CTS, CCM, GCM
	};

	public static final CipherAlgorithmMode[] BC_DEFAULT_NO_AEAD =
	{
			ECB, CBC, CFB, OFB, CTR, CTS/* , CCM, GCM */
	};

	CipherAlgorithmMode(final boolean usingIV, final boolean usingNonce, final boolean AEAD)
	{
		isUsingIV = usingIV;
		isUsingNonce = usingNonce;
		isAEADMode = AEAD;
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
}
