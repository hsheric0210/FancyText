package fancytext.encrypt.symmetric;

public enum CipherExceptionType
{
	NO_SUCH_ALGORITHM_PROVIDER("It seems your Java version is not supporting SunJCE or BouncyCastle correctly"),
	UNSUPPORTED_CIPHER("Unsupported cipher algorithm"),
	CORRUPTED_AEAD_TAG("Corrupted AEAD tag"),
	BASE64_DECODE_EXCEPTION("Corrupted Base64 byte array"),
	CORRUPTED_KEY_OR_INPUT("Corrupted key or input"),
	ILLEGAL_BLOCK_SIZE("Illegal block size or invalid key"),
	BAD_PADDING("Corruped padding or invalid key"),
	INVALID_KEY("Invalid key"),
	UNAVAILABLE_PROVIDER("Provider unavailable"),
	UNSUPPORTED_ALGORITHM("Unsupported algorithm"),
	UNSUPPORTED_PADDING("Unsupported padding"),
	INVALID_CIPHERTEXT("Invalid cipher text");

	public final String description;

	CipherExceptionType(final String description)
	{
		this.description = description;
	}

	@Override
	public String toString()
	{
		return description;
	}
}
