package fancytext.digest;

public enum DigestAlgorithm
{

	CRC_16("", "CRC-16", 32),

	CRC_32("", "CRC-32", 64),

	ADLER_32("", "Adler-32", 64),

	MD2("MD2", "MD-2", "SUN", 128),

	MD4("MD4", "MD-4", "BC", 128),

	MD5("MD5", "MD-5", "SUN", 128),

	SHA1("SHA-1", "SHA-1", "SUN", 160),
	SHA2("SHA", "SHA-2", "SUN", 224, 256, 384, 512),
	SHA3("SHA3", "SHA-3", "BC", 224, 256, 384, 512),

	Keccak("KECCAK", "Keccak", "BC", 224, 256, 288, 384, 512),
	SHAKE("SHAKE", "SHAKE", "BC", 256, 512),

	BLAKE2s("BLAKE2S", "BLAKE2s (a.k.a. Successor of BLAKE-256)", "BC", 128, 160, 224, 256),
	BLAKE2b("BLAKE2B", "BLAKE2b (a.k.a. Successor of BLAKE-512)", "BC", 160, 256, 384, 512),

	DSTU7564("DSTU7564", "Kupyna (DSTU 7564:2014)", "BC", 256, 384, 512),

	GOST3411("GOST3411", "GOST 34.11-94", "BC", 256),
	GOST3411_2012("GOST3411-2012", "Streebog (GOST R 34.11-2012)", "BC", 256, 512),

	Haraka("HARAKA", "Haraka", "BC", 256, 512),

	RIPEMD("RIPEMD", "RIPE Message Digest", "BC", 128, 160, 256, 320),

	Skein("SKEIN", "Skein", "BC", 256, 512, 1024),

	SM3("SM3", "SM3", "BC"),

	Tiger("TIGER", "Tiger", "BC", 192),

	Whirlpool("WHIRLPOOL", "Whirlpool", "BC", 512);

	private final String id;
	private final String displayName;
	private final String providerName;
	private final int[] availableDigestSizes;

	DigestAlgorithm(final String id, final String displayName, final int... availableDigestSize)
	{
		this(id, displayName, null, availableDigestSize);
	}

	DigestAlgorithm(final String id, final String displayName, final String providerName, final int... availableDigestSizes)
	{
		this.id = id;
		this.displayName = displayName;
		this.providerName = providerName;
		this.availableDigestSizes = availableDigestSizes;
	}

	public String getId()
	{
		return id;
	}

	public String getProviderName()
	{
		return providerName;
	}

	public int[] getAvailableDigestSizes()
	{
		return availableDigestSizes;
	}

	public Integer[] getAvailableDigestSizesBoxed()
	{
		if (availableDigestSizes == null)
			return EMPTY_INTEGER_ARRAY;

		final Integer[] digestSizes = new Integer[availableDigestSizes.length];
		int j = 0;
		for (final int i : availableDigestSizes)
			digestSizes[j++] = i;

		return digestSizes;
	}

	@Override
	public String toString()
	{
		return displayName;
	}

	public static final Integer[] EMPTY_INTEGER_ARRAY = new Integer[0];
}
