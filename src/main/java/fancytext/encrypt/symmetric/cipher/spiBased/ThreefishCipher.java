package fancytext.encrypt.symmetric.cipher.spiBased;

import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherAlgorithmMode;
import fancytext.encrypt.symmetric.CipherAlgorithmPadding;
import fancytext.encrypt.symmetric.cipher.CipherException;

public class ThreefishCipher extends SpiBasedCipher
{
	private int keyLength;

	public ThreefishCipher(final CipherAlgorithm algorithm, final CipherAlgorithmMode mode, final CipherAlgorithmPadding padding, final int unitBytes, final int keyLength) throws CipherException
	{
		super(algorithm, mode, padding, unitBytes);
		this.keyLength = keyLength;
	}

	@Override
	protected String getAlgorithmID()
	{
		if (padding == CipherAlgorithmPadding.NONE)
			return algorithm.getId() + "-" + (keyLength << 3);
		return super.getAlgorithmID();
	}
}