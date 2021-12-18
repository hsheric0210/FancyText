package fancytext.encrypt.symmetric.cipher.spiBased;

import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherMode;
import fancytext.encrypt.symmetric.CipherPadding;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.encrypt.symmetric.CipherException;

public class ThreefishCipher extends SpiBasedCipher
{
	private final int keyLength;

	public ThreefishCipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding, final int unitBytes, final int keyLength) throws CipherException
	{
		super(algorithm, mode, padding, unitBytes);
		if (algorithm != CipherAlgorithm.Threefish)
			throw new CipherException(CipherExceptionType.UNSUPPORTED_ALGORITHM, "Only supports Threefish");
		this.keyLength = keyLength;
	}

	@Override
	protected String getAlgorithmID()
	{
		if (padding == CipherPadding.NONE)
			return algorithm.getId() + "-" + (keyLength << 3);
		return super.getAlgorithmID();
	}
}
