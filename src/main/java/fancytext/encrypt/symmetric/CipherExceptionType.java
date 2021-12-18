package fancytext.encrypt.symmetric;

public enum CipherExceptionType
{
	EMPTY_INPUT("Input is empty"),
	EMPTY_PADDING("Padding byte is empty"),
	EMPTY_KEY("Key is empty"),
	EMPTY_IV("Initial vector is empty"),
	EMPTY_RESPONSE("Cipher returned null"),
	ABSENT_ARGUMENT("Argument not present"),
	PROVIDER_UNAVAILABLE("It seems your Java version is not supporting SunJCE or BouncyCastle correctly"),
	UNSUPPORTED_ALGORITHM("Unsupported cipher algorithm"),
	UNSUPPORTED_MODE("Unsupported cipher mode"),
	UNSUPPORTED_PADDING("Unsupported cipher padding"),
	INVALID_KEY("Invalid key"),
	INVALID_IV("Invaild initial vector or nonce"),
	BASE64_DECODE_EXCEPTION("Corrupted Base64 byte array"),
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
