package fancytext.encrypt.symmetric;

public enum CipherAlgorithm
{
	AES("AES", "AES: Advanced Encryption Standard", 128, 96, 128, CipherMode.SUNJCE_DEFAULT, CipherPadding.SUNJCE_DEFAULT, "SunJCE", true, false, 128, 192, 256),

	BlowFish("Blowfish", "Blowfish", 64, 8, 448, 96, 128, CipherMode.SUNJCE_DEFAULT_NO_AEAD, CipherPadding.SUNJCE_DEFAULT, "SunJCE", true, false),
	Twofish("Twofish", "Twofish", 128, 64, 256, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false),
	Threefish("Threefish", "Threefish", -1, 96, 128, CipherMode.BC_DEFAULT_NO_AEAD, new CipherPadding[]
	{
			CipherPadding.NONE
	}, "BC", true, false, 256, 512, 1024),

	DES("DES", "DES", 64, 8, 64, 96, 128, CipherMode.SUNJCE_DEFAULT_NO_AEAD, CipherPadding.SUNJCE_DEFAULT, "SunJCE", true, false, 64),
	DES_EDE("DESede", "DESede: Triple-DES", 64, 96, 128, CipherMode.SUNJCE_DEFAULT_NO_AEAD, CipherPadding.SUNJCE_DEFAULT, "SunJCE", true, false, 192),

	RC2("RC2", "RC2: Rivest cipher 2", 64, 40, 1024, 96, 128, CipherMode.SUNJCE_DEFAULT_NO_AEAD, CipherPadding.SUNJCE_DEFAULT, "SunJCE", true, false),
	ARC4("ARCFOUR", "ARC4: Alleged RC4", -1, 40, 1024, 96, 128, "SunJCE", true, false),
	RC5_32("RC5", "RC5: Rivest cipher 5 (32-bit mode)", 128, 8, 2040, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false),
	RC5_64("RC5-64", "RC5: Rivest cipher 5 (64-bit mode)", 256, 8, 2040, 96, 128, CipherMode.BC_DEFAULT_NO_AEAD, CipherPadding.BC_DEFAULT, "BC", true, false),
	RC6("RC6", "RC6: Rivest cipher 6", 128, 8, -1, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false),

	ARIA("ARIA", "ARIA", 128, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false, 128/* , 192, 256 */),

	Camellia("Camellia", "Camellia", 128, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false, 128/* , 192, 256 */),

	CAST5("CAST5", "CAST5 (CAST-128)", 128, 8, 128, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false),
	CAST6("CAST6", "CAST6 (CAST-256)", 256, 8, 256, 96, 128, CipherMode.BC_DEFAULT_NO_AEAD, CipherPadding.BC_DEFAULT, "BC", true, false),

	ChaCha("CHACHA", "ChaCha", 64, 96, 128, "BC", true, true, 128, 256),
	ChaCha7539("CHACHA7539", "ChaCha-7539", 96, 96, 128, "BC", true, true, 256),
	ChaChaP1305("CHACHA20-POLY1305", "ChaCha20-Poly1305", -1, -1, -1, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false, 256),

	DSTU7624("DSTU7624", "DSTU7624 (Kalyna)", -1, -1, -1, 96, 128, CipherMode.BC_DEFAULT_NO_AEAD, CipherPadding.BC_DEFAULT, "BC", true, false, 128, 256, 512),
	GOST3412_2015("GOST3412-2015", "GOST R 34.12-2015 (Kuznyechik)", 128, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false, 256),
	GOST28147("GOST28147", "GOST 28147 (Magma)", 64, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false, 256),

	Grain128("Grain128", "Grain-128", 96, 128, -1, 96, 128, "BC", true, true),
	Grainv1("Grainv1", "Grain-v1", 64, 80, -1, 96, 128, "BC", true, true),

	HC128("HC128", "HC-128", 128, 96, 128, "BC", true, true, 128),
	HC256("HC256", "HC-256", 256, 96, 128, "BC", true, true, 256),

	IDEA("IDEA", "IDEA: International Data Encryption Algorithm", 64, 8, -1, 96, 128, CipherMode.BC_DEFAULT_NO_AEAD, CipherPadding.BC_DEFAULT, "BC", true, false),

	Noekeon("NOEKEON", "NOEKEON", -1, 128, -1, 96, 128, new CipherMode[]
	{
			CipherMode.ECB
	}, CipherPadding.BC_DEFAULT, "BC", true, false),

	Rijndael("RIJNDAEL", "Rijndael", 128, 32, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false, 128, 160, 192, 224, 256),

	Salsa20("SALSA20", "Salsa20", 64, 96, 128, "BC", true, true, 128, 256),
	XSalsa20("XSALSA20", "XSalsa20: Salsa20 with 192-bit IV", 64, 96, 128, "BC", true, true, 256),

	SEED("SEED", "SEED", 128, 128, 1024, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false),

	Serpent("Serpent", "Serpent", 128, 64, 1024, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false),
	Tnepres("Tnepres", "Tnepres (Serpent algorithm with misimplant; Only exists for backward-compatibility.)", 128, 64, -1, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false),

	SHACAL_2("SHACAL-2", "SHACAL-2", -1, -1, -1, 96, 128, CipherMode.BC_DEFAULT_NO_AEAD, CipherPadding.BC_DEFAULT, "BC", true, false, 128, 192, 256, 320, 384, 448, 512),

	Skipjack("SKIPJACK", "Skipjack", 64, 80, 1024, 96, 128, CipherMode.BC_DEFAULT_NO_AEAD, CipherPadding.BC_DEFAULT, "BC", true, false),

	SM4("SM4", "SM4 (formerly SMS4)", 128, 96, 128, CipherMode.BC_DEFAULT, CipherPadding.BC_DEFAULT, "BC", true, false, 128),

	TEA("TEA", "TEA: Tiny Encryption Algorithm", 128, -1, -1, 96, 128, CipherMode.BC_DEFAULT_NO_AEAD, CipherPadding.BC_DEFAULT, "BC", true, false, 128),
	XTEA("XTEA", "XTEA: eXtended Tiny Encryption Algorithm", 128, -1, -1, 96, 128, CipherMode.BC_DEFAULT_NO_AEAD, CipherPadding.BC_DEFAULT, "BC", true, false, 128),

	VMPC("VMPC", "VMPC: Variably Modified Permutation Composition", 128, 8, -1, 96, 128, "BC", true, true),
	VMPCKSA3("VMPC-KSA3", "VMPC-KSA3: Variably Modified Permutation Composition with Key Scheduling Algorithm 3", 128, 8, -1, 96, 128, "BC", true, true),

	Zuc128("Zuc-128", "Zuc-128", 128, 96, 128, "BC", true, true, 128),
	Zuc256("Zuc-256", "Zuc-256", 200, 96, 128, "BC", true, true, 256),

	LEA("LEA", "LEA: Lightweight Encryption Algorithm", 128, 8, 16, new CipherMode[]
	{
			CipherMode.ECB, CipherMode.CBC, CipherMode.CFB, CipherMode.OFB, CipherMode.CCM, CipherMode.GCM
	}, new CipherPadding[]
	{
			CipherPadding.NONE, CipherPadding.PKCS7
	}, null, true, false, 128, 256);

	private final String id;
	private final String displayName;
	private final int blocksize;
	private final int minKeySize;
	private final int maxKeySize;
	private final CipherMode[] supportMode;
	private final CipherPadding[] supportPadding;
	private final String providerName;
	private final int[] availableKeySizes;
	private final boolean paddedInputRequired;
	private final boolean streamCipher;
	private final int minAEADTagLength;
	private final int maxAEADTagLength;

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minKeySize, final int maxKeySize, final int minAEADTagLength, final int maxAEADTagLength, final String providerName, final boolean paddedInputRequired, final boolean streamCipher)
	{
		this(id, displayName, blocksize, minKeySize, maxKeySize, minAEADTagLength, maxAEADTagLength, new CipherMode[]
		{
				CipherMode.ECB
		}, new CipherPadding[]
		{
				CipherPadding.NONE
		}, providerName, paddedInputRequired, streamCipher, (int[]) null);
	}

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minAEADTagLength, final int maxAEADTagLength, final String providerName, final boolean paddedInputRequired, final boolean streamCipher, final int... availableKeySizes)
	{
		this(id, displayName, blocksize, -1, -1, minAEADTagLength, maxAEADTagLength, providerName, paddedInputRequired, streamCipher, availableKeySizes);
	}

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minKeySize, final int maxKeySize, final int minAEADTagLength, final int maxAEADTagLength, final String providerName, final boolean paddedInputRequired, final boolean streamCipher, final int... availableKeySizes)
	{
		this(id, displayName, blocksize, minKeySize, maxKeySize, minAEADTagLength, maxAEADTagLength, new CipherMode[]
		{
				CipherMode.ECB
		}, new CipherPadding[]
		{
				CipherPadding.NONE
		}, providerName, paddedInputRequired, streamCipher, availableKeySizes);
	}

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minKeySize, final int maxKeySize, final int minAEADTagLength, final int maxAEADTagLength, final CipherMode[] supportedModes, final CipherPadding[] supportedPaddings, final String providerName, final boolean paddedInputRequired, final boolean streamCipher)
	{
		this(id, displayName, blocksize, minKeySize, maxKeySize, minAEADTagLength, maxAEADTagLength, supportedModes, supportedPaddings, providerName, paddedInputRequired, streamCipher, null);
	}

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minAEADTagLength, final int maxAEADTagLength, final CipherMode[] supportedModes, final CipherPadding[] supportedPaddings, final String providerName, final boolean paddedInputRequired, final boolean streamCipher, final int... availableKeySizes)
	{
		this(id, displayName, blocksize, -1, -1, minAEADTagLength, maxAEADTagLength, supportedModes, supportedPaddings, providerName, paddedInputRequired, streamCipher, availableKeySizes);
	}

	CipherAlgorithm(final String id, final String displayName, final int blocksize, final int minKeySize, final int maxKeySize, final int minAEADTagLength, final int maxAEADTagLength, final CipherMode[] supportedModes, final CipherPadding[] supportedPaddings, final String providerName, final boolean paddedInputRequired, final boolean streamCipher, final int... availableKeySizes)
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
		this.streamCipher = streamCipher; // Stream-ciphers always requires IV
		this.minAEADTagLength = minAEADTagLength;
		this.maxAEADTagLength = maxAEADTagLength;
	}

	public String getId()
	{
		return id;
	}

	public int getBlockSize()
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

	public CipherMode[] getSupportedModes()
	{
		return supportMode;
	}

	public CipherPadding[] getSupportedPaddings()
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
