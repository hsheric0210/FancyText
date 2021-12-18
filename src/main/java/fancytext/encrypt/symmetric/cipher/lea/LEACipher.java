package fancytext.encrypt.symmetric.cipher.lea;

import javax.crypto.Cipher;

import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherMode;
import fancytext.encrypt.symmetric.CipherPadding;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.encrypt.symmetric.cipher.AbstractCipher;
import fancytext.encrypt.symmetric.CipherException;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherMode;
import kr.re.nsr.crypto.padding.PKCS5Padding;
import kr.re.nsr.crypto.symm.LEA.*;

public class LEACipher extends AbstractCipher
{
	private final BlockCipherMode theCipher;
	private byte[] key;
	private byte[] iv;

	public LEACipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding) throws CipherException
	{
		super(algorithm, mode, padding);

		theCipher = getCipher();

		if (padding == CipherPadding.PKCS5)
			theCipher.setPadding(new PKCS5Padding(getBlockSize()));
	}

	private BlockCipherMode getCipher() throws CipherException
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
			default:
				throw new CipherException(CipherExceptionType.UNSUPPORTED_MODE, mode.name());
		}
	}

	@Override
	public void setKey(final byte[] key)
	{
		this.key = key;
	}

	@Override
	public void setIV(final byte[] iv, final int macSize)
	{
		this.iv = iv;
	}

	@Override
	public void init(final int opMode) throws CipherException
	{
		requirePresent(key, "Key");

		try
		{
			final Mode mode = opMode == Cipher.ENCRYPT_MODE ? Mode.ENCRYPT : Mode.DECRYPT;
			if (iv != null)
				theCipher.init(mode, key, iv);
			else
				theCipher.init(mode, key);
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
