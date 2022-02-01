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
import fancytext.encrypt.symmetric.*;
import fancytext.encrypt.symmetric.cipher.AbstractCipher;

public class RijndaelAEADCipher extends AbstractCipher
{
	private final int blockSize;

	private AEADBlockCipher theCipher;
	private KeyParameter keyParameter;
	private CipherParameters parameters;

	public RijndaelAEADCipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding, final int blockSize)
	{
		super(algorithm, mode, padding);
		this.blockSize = blockSize;
	}

	private AEADBlockCipher getCipher() throws CipherException
	{
		switch (mode)
		{
			case CCM:
				return new CCMBlockCipher(new RijndaelEngine(blockSize));
			case GCM:
				return new GCMBlockCipher(new RijndaelEngine(blockSize));
			default:
				throw new CipherException(CipherExceptionType.UNSUPPORTED_MODE, mode.name());
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
	public void constructCipher() throws CipherException
	{
		theCipher = getCipher();
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
	public void initCipher(final int opMode) throws CipherException
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
