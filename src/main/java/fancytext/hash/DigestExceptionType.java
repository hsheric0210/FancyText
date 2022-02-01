package fancytext.hash;

public enum DigestExceptionType
{
	PROVIDER_UNAVAILABLE("It seems your Java version is not supporting SunJCE or BouncyCastle security provider as intended"),
	UNSUPPORTED_ALGORITHM("Unsupported messageDigest algorithm");

	public final String description;

	DigestExceptionType(final String description)
	{
		this.description = description;
	}

	@Override
	public String toString()
	{
		return description;
	}
}
