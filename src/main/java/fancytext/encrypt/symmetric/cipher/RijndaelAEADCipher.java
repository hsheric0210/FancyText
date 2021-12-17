package fancytext.encrypt.symmetric.cipher;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import javax.crypto.Cipher;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherAlgorithmMode;
import fancytext.encrypt.symmetric.CipherAlgorithmPadding;
import fancytext.encrypt.symmetric.CipherExceptionType;

public class RijndaelAEADCipher extends AbstractCipher
{
	private final AEADBlockCipher theCipher;
	private final int blockSize;
	private KeyParameter keyParameter;
	private CipherParameters parameters;

	public RijndaelAEADCipher(final CipherAlgorithm algorithm, final CipherAlgorithmMode mode, final CipherAlgorithmPadding padding, final int blockSize) throws CipherException
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
	public void setKey(final byte[] key)
	{
		keyParameter = new KeyParameter(key);
	}

	@Override
	public void setIV(final byte[] iv, final int macSize)
	{
		parameters = new AEADParameters(Objects.requireNonNull(keyParameter, "Key is not set!"), macSize, iv);
	}

	@Override
	public void init(final int opMode) throws CipherException
	{
		try
		{
			theCipher.init(opMode == Cipher.ENCRYPT_MODE, Objects.requireNonNull(parameters, "AEAD parameter is not set!"));
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
			final byte[] tmpBytes = new byte[theCipher.getOutputSize(bytes.length)];

			int outOff = theCipher.processBytes(bytes, 0, bytes.length, tmpBytes, 0);
			outOff += theCipher.doFinal(tmpBytes, outOff);

			return Arrays.copyOf(tmpBytes, outOff);
		}
		catch (final InvalidCipherTextException | DataLengthException e)
		{
			throw new CipherException(CipherExceptionType.INVALID_CIPHERTEXT, e);
		}
	}

	@Override
	public int getBlockSize()
	{
		return blockSize / 8;
	}
}
