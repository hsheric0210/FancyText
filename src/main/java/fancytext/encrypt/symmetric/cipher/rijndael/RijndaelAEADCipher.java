package fancytext.encrypt.symmetric.cipher.rijndael;

import java.util.Arrays;
import java.util.StringJoiner;

import javax.crypto.Cipher;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import fancytext.Main;
import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.encrypt.symmetric.CipherMode;
import fancytext.encrypt.symmetric.CipherPadding;
import fancytext.encrypt.symmetric.cipher.AbstractCipher;
import fancytext.encrypt.symmetric.CipherException;

public class RijndaelAEADCipher extends AbstractCipher
{
	private final AEADBlockCipher theCipher;
	private final int blockSize;
	private KeyParameter keyParameter;
	private CipherParameters parameters;

	public RijndaelAEADCipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding, final int blockSize) throws CipherException
	{
		super(algorithm, mode, padding);
		this.blockSize = blockSize;

		theCipher = getCipher();
	}

	private AEADBlockCipher getCipher()
	{
		switch (mode)
		{
			case CCM:
				return new CCMBlockCipher(new RijndaelEngine(blockSize));
			case GCM:
				return new GCMBlockCipher(new RijndaelEngine(blockSize));
			default:
				return null;
		}
	}

	@Override
	protected void dumpAdditionalInformations(final StringBuilder builder)
	{
		builder.append("Cipher block size: ").append(blockSize).append(Main.lineSeparator);
	}

	@Override
	protected void serializeAdditionalInformations(final StringJoiner joiner)
	{
		joiner.add("BlockSize=" + blockSize);
	}

	@Override
	public void setKey(final byte[] key) throws CipherException
	{
		try
		{
			keyParameter = new KeyParameter(key);
		}
		catch (final Throwable e)
		{
			throw new CipherException(CipherExceptionType.INVALID_KEY, e);
		}
	}

	@Override
	public void setIV(final byte[] iv, final int macSize) throws CipherException
	{
		requirePresent(keyParameter, "Key");
		
		try
		{
			parameters = new AEADParameters(keyParameter, macSize, iv);
		}
		catch (final Throwable e)
		{
			throw new CipherException(CipherExceptionType.INVALID_IV, e);
		}
	}

	@Override
	public void init(final int opMode) throws CipherException
	{
		requirePresent(parameters, "AEAD parameter");

		try
		{
			theCipher.init(opMode == Cipher.ENCRYPT_MODE, parameters);
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
			final byte[] tmpBytes = new byte[theCipher.getOutputSize(bytes.length)];

			int outOff = theCipher.processBytes(bytes, 0, bytes.length, tmpBytes, 0);
			outOff += theCipher.doFinal(tmpBytes, outOff);

			return Arrays.copyOf(tmpBytes, outOff);
		}
		catch (final Throwable e)
		{
			throw new CipherException(CipherExceptionType.PROCESS_UNSUCCESSFUL, e);
		}
	}

	@Override
	public int getBlockSize()
	{
		return blockSize / 8;
	}
}
