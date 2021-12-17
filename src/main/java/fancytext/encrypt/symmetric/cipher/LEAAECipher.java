package fancytext.encrypt.symmetric.cipher;

import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;

import org.bouncycastle.crypto.DataLengthException;

import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherAlgorithmMode;
import fancytext.encrypt.symmetric.CipherAlgorithmPadding;
import fancytext.encrypt.symmetric.CipherExceptionType;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherModeAE;
import kr.re.nsr.crypto.symm.LEA.CCM;
import kr.re.nsr.crypto.symm.LEA.GCM;

public class LEAAECipher extends AbstractCipher
{
	private final BlockCipherModeAE theCipher;
	private byte[] key;
	private byte[] nonce;
	private int aeadTagLength;

	public LEAAECipher(final CipherAlgorithm algorithm, final CipherAlgorithmMode mode, final CipherAlgorithmPadding padding) throws CipherException
	{
		super(algorithm, mode, padding);

		theCipher = getCipher();
	}

	private BlockCipherModeAE getCipher()
	{
		switch (mode)
		{
			case CCM:
				return new CCM();
			case GCM:
				return new GCM();
		}

		return null;
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
	}

	@Override
	public void init(final int opMode) throws CipherException
	{
		try
		{
			final Mode mode = opMode == Cipher.ENCRYPT_MODE ? Mode.ENCRYPT : Mode.DECRYPT;
			theCipher.init(mode, Objects.requireNonNull(key, "Key is not set!"), Objects.requireNonNull(nonce, "Nonce is not set!"), aeadTagLength);
		}
		catch (final IllegalArgumentException e)
		{
			throw new CipherException(CipherExceptionType.INVALID_KEY, e);
		}
	}

	@Override
	public byte[] doFinal(final byte[] bytes) throws CipherException
	{
		try
		{
			return theCipher.doFinal(bytes);
		}
		catch (final DataLengthException | BadPaddingException e)
		{
			throw new CipherException(CipherExceptionType.INVALID_CIPHERTEXT, e);
		}
	}

	@Override
	public int getBlockSize()
	{
		return 16;
	}
}
