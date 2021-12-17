package fancytext.encrypt.symmetric;

public enum CipherAlgorithm
{
	AES("AES", "Advanced Encryption Standard (a.k.a. AES)", 128, -1, -1, 96, 128, CipherAlgorithmMode.SUNJCE_DEFAULT, CipherAlgorithmPadding.SUNJCE_DEFAULT, "SunJCE", true, false, new int[]
	{
			128, 192, 256
	}),

	BlowFish("Blowfish", "Blowfish", 64, 8, 448, 96, 128, CipherAlgorithmMode.SUNJCE_DEFAULT_NO_AEAD, CipherAlgorithmPadding.SUNJCE_DEFAULT, "SunJCE", true, false),
	Twofish("Twofish", "Twofish", 128, 64, 256, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),
	Threefish("Threefish", "Threefish", -1, -1, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT_NO_AEAD, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, new int[]
	{
			256, 512, 1024
	}),

	DES("DES", "DES", 64, 8, 64, 96, 128, CipherAlgorithmMode.SUNJCE_DEFAULT_NO_AEAD, CipherAlgorithmPadding.SUNJCE_DEFAULT, "SunJCE", true, false, 64),
	DES_EDE("DESede", "Triple-DES (a.k.a. DESede)", 64, -1, -1, 96, 128, CipherAlgorithmMode.SUNJCE_DEFAULT_NO_AEAD, CipherAlgorithmPadding.SUNJCE_DEFAULT, "SunJCE", true, false, 192),

	RC2("RC2", "Rivest cipher 2 (a.k.a. RC2)", 64, 40, 1024, 96, 128, CipherAlgorithmMode.SUNJCE_DEFAULT_NO_AEAD, CipherAlgorithmPadding.SUNJCE_DEFAULT, "SunJCE", true, false),
	ARC4("ARCFOUR", "Alleged RC4 (a.k.a. ARC4, ARCFOUR)", -1, 40, 1024, 96, 128, "SunJCE", true, false),
	RC5_32("RC5", "Rivest cipher 5 (a.k.a. RC5) - 32-bit mode", 128, 8, 2040, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),
	RC5_64("RC5-64", "Rivest cipher 5 (a.k.a. RC5) - 64-bit mode", 256, 8, 2040, 96, 128, CipherAlgorithmMode.BC_DEFAULT_NO_AEAD, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),
	RC6("RC6", "Rivest cipher 6 (a.k.a. RC6)", 128, 8, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),

	ARIA("ARIA", "ARIA", 128, -1, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, new int[]
	{
			128/* , 192, 256 */
	}),

	Camellia("Camellia", "Camellia", 128, -1, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, new int[]
	{
			128/* , 192, 256 */
	}),

	CAST5("CAST5", "CAST-128 (a.k.a. CAST5)", 128, 8, 128, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),
	CAST6("CAST6", "CAST-256 (a.k.a. CAST6)", 256, 8, 256, 96, 128, CipherAlgorithmMode.BC_DEFAULT_NO_AEAD, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),

	ChaCha("CHACHA", "ChaCha", 64, -1, -1, 96, 128, "BC", true, true, 128, 256),
	ChaCha7539("CHACHA7539", "ChaCha-7539", 96, -1, -1, 96, 128, "BC", true, true, 256),
	ChaChaP1305("CHACHA20-POLY1305", "ChaCha-Poly1305", -1, -1, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT_NO_AEAD, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, 256),

	DSTU7624("DSTU7624", "DSTU7624 (a.k.a. Kalyna)", -1, -1, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT_NO_AEAD, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, new int[]
	{
			128, 256, 512
	}),
	GOST3412_2015("GOST3412-2015", "GOST R 34.12-2015 (a.k.a Kuznyechik)", 128, -1, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, 256),
	GOST28147("GOST28147", "GOST 28147 (a.k.a. Magma)", 64, -1, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, 256),

	Grain128("Grain128", "Grain-128", 96, 128, -1, 96, 128, "BC", true, true),
	Grainv1("Grainv1", "Grain-v1", 64, 80, -1, 96, 128, "BC", true, true),

	HC128("HC128", "HC-128", 128, -1, -1, 96, 128, "BC", true, true, 128),
	HC256("HC256", "HC-256", 256, -1, -1, 96, 128, "BC", true, true, 256),

	IDEA("IDEA", "International Data Encryption Algorithm (a.k.a. IDEA)", 64, 8, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT_NO_AEAD, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),

	Noekeon("NOEKEON", "NOEKEON", -1, 128, -1, 96, 128, new CipherAlgorithmMode[]
	{
			CipherAlgorithmMode.ECB
	}, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),

	Rijndael("RIJNDAEL", "Rijndael", 128, -1, -1, 32, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, new int[]
	{
			128, 160, 192, 224, 256
	}),

	Salsa20("SALSA20", "Salsa20", 64, -1, -1, 96, 128, "BC", true, true, 128, 256),
	XSalsa20("XSALSA20", "Salsa20 with 192-bit IV (a.k.a. XSalsa20)", 64, -1, -1, 96, 128, "BC", true, true, 256),

	SEED("SEED", "SEED", 128, 128, 1024, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),

	Serpent("Serpent", "Serpent", 128, 64, 1024, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),
	Tnepres("Tnepres", "Tnepres (Serpent algorithm with several mistakes. Only exists for backward-compatibility.)", 128, 64, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),

	SHACAL_2("SHACAL-2", "SHACAL-2", -1, -1, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT_NO_AEAD, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, new int[]
	{
			128, 192, 256, 320, 384, 448, 512
	}),

	Skipjack("SKIPJACK", "Skipjack", 64, 80, 1024, 96, 128, CipherAlgorithmMode.BC_DEFAULT_NO_AEAD, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false),

	SM4("SM4", "SM4 (formerly SMS4)", 128, -1, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, 128),

	TEA("TEA", "Tiny Encryption Algorithm (a.k.a. TEA)", 128, -1, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT_NO_AEAD, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, 128),
	XTEA("XTEA", "eXtended Tiny Encryption Algorithm (a.k.a. XTEA)", 128, -1, -1, 96, 128, CipherAlgorithmMode.BC_DEFAULT_NO_AEAD, CipherAlgorithmPadding.BC_DEFAULT, "BC", true, false, 128),

	VMPC("VMPC", "Variably Modified Permutation Composition (a.k.a. VMPC)", 128, 8, -1, 96, 128, "BC", true, true),
	VMPCKSA3("VMPC-KSA3", "Variably Modified Permutation Composition (a.k.a. VMPC) with Key Scheduling Algorithm 3 (a.k.a. KSA3)", 128, 8, -1, 96, 128, "BC", true, true),

	Zuc128("Zuc-128", "Zuc-128", 128, -1, -1, 96, 128, "BC", true, true, 128),
	Zuc256("Zuc-256", "Zuc-256", 200, -1, -1, 96, 128, "BC", true, true, 256),

	LEA("LEA", "Lightweight Encryption Algorithm (a.k.a. LEA)", 128, -1, -1, 8, 16, new CipherAlgorithmMode[]
	{
			CipherAlgorithmMode.ECB, CipherAlgorithmMode.CBC, CipherAlgorithmMode.CFB, CipherAlgorithmMode.OFB, CipherAlgorithmMode.CTS, CipherAlgorithmMode.CCM, CipherAlgorithmMode.GCM
	}, new CipherAlgorithmPadding[]
	{
			CipherAlgorithmPadding.NONE, CipherAlgorithmPadding.PKCS5
	}, null, true, true, new int[]
	{
			128, 256
	});

	private final String id;
	private final String displayName;
	private final int blocksize;
	private final int minKeySize;
	private final int maxKeySize;
	private final CipherAlgorithmMode[] supportMode;
	private final CipherAlgorithmPadding[] supportPadding;
	private final String providerName;
	private final int[] availableKeySizes;
	private final boolean paddedInputRequired;
	private final boolean streamCipher;
	private final int minAEADTagLength;
	private final int maxAEADTagLength;

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minKeySize, final int maxKeySize, final int minAEADTagLength, final int maxAEADTagLength, final String providerName, final boolean paddedInputRequired, final boolean streamCipher)
	{
		this(id, displayName, blocksize, minKeySize, maxKeySize, minAEADTagLength, maxAEADTagLength, new CipherAlgorithmMode[]
		{
				CipherAlgorithmMode.ECB
		}, new CipherAlgorithmPadding[]
		{
				CipherAlgorithmPadding.NONE
		}, providerName, paddedInputRequired, streamCipher, null);
	}

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minKeySize, final int maxKeySize, final int minAEADTagLength, final int maxAEADTagLength, final String providerName, final boolean paddedInputRequired, final boolean streamCipher, final int availableKeySizes)
	{
		this(id, displayName, blocksize, minKeySize, maxKeySize, minAEADTagLength, maxAEADTagLength, new CipherAlgorithmMode[]
		{
				CipherAlgorithmMode.ECB
		}, new CipherAlgorithmPadding[]
		{
				CipherAlgorithmPadding.NONE
		}, providerName, paddedInputRequired, streamCipher, availableKeySizes);
	}

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minKeySize, final int maxKeySize, final int minAEADTagLength, final int maxAEADTagLength, final String providerName, final boolean paddedInputRequired, final boolean streamCipher, final int... availableKeySizes)
	{
		this(id, displayName, blocksize, minKeySize, maxKeySize, minAEADTagLength, maxAEADTagLength, new CipherAlgorithmMode[]
		{
				CipherAlgorithmMode.ECB
		}, new CipherAlgorithmPadding[]
		{
				CipherAlgorithmPadding.NONE
		}, providerName, paddedInputRequired, streamCipher, availableKeySizes);
	}

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minKeySize, final int maxKeySize, final int minAEADTagLength, final int maxAEADTagLength, final CipherAlgorithmMode[] supportedModes, final CipherAlgorithmPadding[] supportedPaddings, final String providerName, final boolean paddedInputRequired, final boolean streamCipher, final int availableKeySizes)
	{
		this(id, displayName, blocksize, minKeySize, maxKeySize, minAEADTagLength, maxAEADTagLength, supportedModes, supportedPaddings, providerName, paddedInputRequired, streamCipher, new int[]
		{
				availableKeySizes
		});
	}

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minKeySize, final int maxKeySize, final int minAEADTagLength, final int maxAEADTagLength, final CipherAlgorithmMode[] supportedModes, final CipherAlgorithmPadding[] supportedPaddings, final String providerName, final boolean paddedInputRequired, final boolean streamCipher)
	{
		this(id, displayName, blocksize, minKeySize, maxKeySize, minAEADTagLength, maxAEADTagLength, supportedModes, supportedPaddings, providerName, paddedInputRequired, streamCipher, null);
	}

	// CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minKeySize, final int maxKeySize, final int minAEADTagLength, final int maxAEADTagLength, final CipherAlgorithmMode[] supportedModes, final CipherAlgorithmPadding[] supportedPaddings, final String providerName, final boolean paddedInputRequired, final boolean streamCipher, final Function<?, int[]> availableKeySizes)
	// {
	// this(id, displayName, blocksize, minKeySize, maxKeySize, minAEADTagLength, maxAEADTagLength, supportedModes, supportedPaddings, providerName, paddedInputRequired, streamCipher, Optional.ofNullable(availableKeySizes).map(keySizes -> keySizes.apply(null)).orElse(null));
	// }

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minKeySize, final int maxKeySize, final int minAEADTagLength, final int maxAEADTagLength, final CipherAlgorithmMode[] supportedModes, final CipherAlgorithmPadding[] supportedPaddings, final String providerName, final boolean paddedInputRequired, final boolean streamCipher, final int[] availableKeySizes)
	{
		this.id = id;
		this.displayName = displayName;
		this.blocksize = blocksize;
		this.minKeySize = minKeySize;
		this.maxKeySize = maxKeySize;
		supportMode = supportedModes;
		supportPadding = supportedPaddings;
		this.providerName = providerName;
		this.availableKeySizes = availableKeySizes;
		this.paddedInputRequired = paddedInputRequired;
		this.streamCipher = streamCipher; // Which always require IV
		this.minAEADTagLength = minAEADTagLength;
		this.maxAEADTagLength = maxAEADTagLength;
	}

	public String getId()
	{
		return id;
	}

	public int getBlocksize()
	{
		return blocksize;
	}

	public int getMinKeySize()
	{
		return minKeySize;
	}

	public int getMaxKeySize()
	{
		return maxKeySize;
	}

	public CipherAlgorithmMode[] getSupportedModes()
	{
		return supportMode;
	}

	public CipherAlgorithmPadding[] getSupportedPaddings()
	{
		return supportPadding;
	}

	public String getProviderName()
	{
		return providerName;
	}

	public int[] getAvailableKeySizes()
	{
		return availableKeySizes;
	}

	public Integer[] getAvailableKeySizesBoxed()
	{
		final Integer[] keySizes = new Integer[availableKeySizes.length];
		int j = 0;
		for (final int i : availableKeySizes)
			keySizes[j++] = i;

		return keySizes;
	}

	public boolean isPaddedInputRequired()
	{
		return paddedInputRequired;
	}

	public boolean isStreamCipher()
	{
		return streamCipher;
	}

	@Override
	public String toString()
	{
		return displayName;
	}

	public int getMinAEADTagLength()
	{
		return minAEADTagLength;
	}

	public int getMaxAEADTagLength()
	{
		return maxAEADTagLength;
	}
}
