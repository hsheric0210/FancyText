package fancytext.encrypt.symmetric.cipher.spiBased;

import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.encrypt.symmetric.CipherMode;
import fancytext.encrypt.symmetric.CipherPadding;
import fancytext.encrypt.symmetric.CipherException;

public class XSalsa20Cipher extends SpiBasedCipher
{
	public XSalsa20Cipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding, final int unitBytes) throws CipherException
	{
		super(algorithm, mode, padding, unitBytes);
		if (algorithm != CipherAlgorithm.XSalsa20)
			throw new CipherException(CipherExceptionType.UNSUPPORTED_ALGORITHM, "Only supports XSalsa20");
	}

	@Override
	public int getIVSize()
	{
		return 24;
	}
}
