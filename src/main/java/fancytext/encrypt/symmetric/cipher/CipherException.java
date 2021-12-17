package fancytext.encrypt.symmetric.cipher;

import java.security.GeneralSecurityException;

import fancytext.encrypt.symmetric.CipherExceptionType;

public class CipherException extends GeneralSecurityException
{
	public CipherExceptionType type;

	public CipherException(final CipherExceptionType type, final Throwable thrown)
	{
		super(type.description, thrown);
		this.type = type;
	}
}
