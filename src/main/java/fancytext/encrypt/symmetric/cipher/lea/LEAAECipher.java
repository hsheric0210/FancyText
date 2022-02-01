package fancytext.encrypt.symmetric.cipher.lea;

import javax.crypto.Cipher;

import fancytext.encrypt.symmetric.*;
import fancytext.encrypt.symmetric.cipher.AbstractCipher;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherModeAE;
import kr.re.nsr.crypto.symm.LEA.CCM;
import kr.re.nsr.crypto.symm.LEA.GCM;

public class LEAAECipher extends AbstractCipher
{
	private BlockCipherModeAE theCipher;
	private byte[] key;
	private byte[] nonce;
	private int aeadTagLength;

	public LEAAECipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding)
	{
		super(algorithm, mode, padding);
	}

	private BlockCipherModeAE getCipher() throws CipherException
	{
		switch (mode)
		{
			case CCM:
				return new CCM();
			case GCM:
				return new GCM();
			default:
				throw new CipherException(CipherExceptionType.UNSUPPORTED_MODE, mode.name());
		}
	}

	@Override
	public int getIVSize()
	{
		switch (mode)
		{
			case CCM:
				return 13;
			case GCM:
				return 12;
			default:
				return -1;
		}
	}

	@Override
	public void constructCipher() throws CipherException
	{
		theCipher = getCipher();
	}

	@Override
	public void setKey(final byte[] key)
	{
		this.key = key;
	}

	@Override
	public void setIV(final byte[] nonce, final int aeadTagLength)
	{
		this.nonce = nonce;
		this.aeadTagLength = aeadTagLength;
	}

	@Override
	public void initCipher(final int opMode) throws CipherException
	{
		requirePresent(key, "Key");
		requirePresent(nonce, "Nonce");

		try
		{
			final Mode mode = opMode == Cipher.ENCRYPT_MODE ? Mode.ENCRYPT : Mode.DECRYPT;
			theCipher.init(mode, key, nonce, aeadTagLength);
		}
		catch (final Throwable e)
		{
			throw new CipherException(CipherExceptionType.INITIALIZATION_UNSUCCESSFUL, e);
		}
	}

	@Override
	public byte[] doFinal(final byte[] bytes) throws CipherException
	{
		try
		{
			return theCipher.doFinal(bytes);
		}
		catch (final Throwable e)
		{
			throw new CipherException(CipherExceptionType.PROCESS_UNSUCCESSFUL, e);
		}
	}

	@Override
	public int getBlockSize()
	{
		return 16;
	}
}
