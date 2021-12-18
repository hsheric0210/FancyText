package fancytext.encrypt.symmetric.cipher.spiBased;

import javax.crypto.spec.RC2ParameterSpec;

import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.encrypt.symmetric.CipherMode;
import fancytext.encrypt.symmetric.CipherPadding;
import fancytext.encrypt.symmetric.CipherException;

public class RC2Cipher extends SpiBasedCipher
{
	public RC2Cipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding, final int unitBytes) throws CipherException
	{
		super(algorithm, mode, padding, unitBytes);
		if (algorithm != CipherAlgorithm.RC2)
			throw new CipherException(CipherExceptionType.UNSUPPORTED_ALGORITHM, "Only supports RC2");
		parameter = new RC2ParameterSpec(0);
	}

	@Override
	public void setIV(final byte[] iv, final int macSize) throws CipherException
	{
		try
		{
			parameter = new RC2ParameterSpec(0, iv);
		}
		catch (final Throwable e)
		{
			throw new CipherException(CipherExceptionType.INVALID_IV, e);
		}
	}
}
