package fancytext.encrypt.symmetric;

public enum CipherExceptionType
{
	EMPTY_PADDING("Padding byte is empty"),
	EMPTY_KEY("Cipher key is empty"),
	EMPTY_IV("Cipher initial vector is empty"),
	ABSENT_ARGUMENT("Argument not present"),
	PROVIDER_UNAVAILABLE("It seems your Java version is not supporting SunJCE or BouncyCastle security provider as intended"),
	UNSUPPORTED_ALGORITHM("Unsupported cipher algorithm"),
	UNSUPPORTED_MODE("Unsupported cipher mode"),
	UNSUPPORTED_PADDING("Unsupported cipher padding"),
	INVALID_KEY("Invalid key"),
	INVALID_IV("Invaild initial vector or nonce"),
	INITIALIZATION_UNSUCCESSFUL("Cipher initialization unsuccessful"),
	PROCESS_UNSUCCESSFUL("Cipher process unsuccessful");

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
