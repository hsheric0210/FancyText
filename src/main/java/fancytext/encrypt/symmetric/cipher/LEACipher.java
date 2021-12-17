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
import kr.re.nsr.crypto.BlockCipherMode;
import kr.re.nsr.crypto.padding.PKCS5Padding;
import kr.re.nsr.crypto.symm.LEA.*;

public class LEACipher extends AbstractCipher
{
	private final BlockCipherMode theCipher;
	private byte[] key;
	private byte[] iv;

	public LEACipher(final CipherAlgorithm algorithm, final CipherAlgorithmMode mode, final CipherAlgorithmPadding padding) throws CipherException
	{
		super(algorithm, mode, padding);

		theCipher = getCipher();

		if (padding == CipherAlgorithmPadding.PKCS5)
			theCipher.setPadding(new PKCS5Padding(getBlockSize()));
	}

	private BlockCipherMode getCipher()
	{
		switch (mode)
		{
			case ECB:
				return new ECB();
			case CBC:
			case PCBC:
				return new CBC();
			case CFB:
				return new CFB();
			case OFB:
				return new OFB();
			case CTR:
				return new CTR();
		}

		return null;
	}

	@Override
	public void setKey(final byte[] key)
	{
		this.key = key;
	}

	@Override
	public void setIV(final byte[] iv, final int macSize)
	{
		if (mode != CipherAlgorithmMode.ECB)
			this.iv = iv;
	}

	@Override
	public void init(final int opMode) throws CipherException
	{
		Objects.requireNonNull(key, "Key is not set!");

		try
		{
			final Mode mode = opMode == Cipher.ENCRYPT_MODE ? Mode.ENCRYPT : Mode.DECRYPT;
			if (this.mode == CipherAlgorithmMode.ECB)
				theCipher.init(mode, key);
			else
				theCipher.init(mode, key, Objects.requireNonNull(iv, "IV is not set!"));
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
