package fancytext.encrypt.symmetric.cipher.rijndael;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;

import javax.crypto.Cipher;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import fancytext.Main;
import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.encrypt.symmetric.CipherMode;
import fancytext.encrypt.symmetric.CipherPadding;
import fancytext.encrypt.symmetric.cipher.AbstractCipher;
import fancytext.encrypt.symmetric.CipherException;

public class RijndaelCipher extends AbstractCipher
{
	private final BufferedBlockCipher theCipher;
	private final int unitBytes;
	private final int blockSize;
	private KeyParameter keyParameter;
	private CipherParameters parameters;
	private final BlockCipherPadding _padding;

	public RijndaelCipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding, final int unitBytes, final int blockSize) throws CipherException
	{
		super(algorithm, mode, padding);
		this.unitBytes = unitBytes;
		this.blockSize = blockSize;

		_padding = getPadding();
		theCipher = getCipher();
	}

	private BlockCipherPadding getPadding()
	{
		switch (padding)
		{
			case ZERO_FILL:
				return new ZeroBytePadding();
			case PKCS5:
				return new PKCS7Padding();
			case ISO10126:
				return new ISO10126d2Padding();
			case X923:
				return new X923Padding();
			case ISO7816_4:
				return new ISO7816d4Padding();
			case TBC:
				return new TBCPadding();
			default:
				return null;
		}
	}

	private BufferedBlockCipher getCipher()
	{
		final BlockCipher cipher;
		switch (mode)
		{
			case CBC:
			case PCBC:
				cipher = new CBCBlockCipher(new RijndaelEngine(blockSize));
				break;
			case CFB:
				cipher = new CFBBlockCipher(new RijndaelEngine(blockSize), unitBytes);
				break;
			case OFB:
				cipher = new OFBBlockCipher(new RijndaelEngine(blockSize), unitBytes);
				break;
			case CTR:
				cipher = new SICBlockCipher(new RijndaelEngine(blockSize));
				break;
			case CTS:
				return new CTSBlockCipher(new CBCBlockCipher(new RijndaelEngine(blockSize)));
			default:
				cipher = new RijndaelEngine(blockSize);
		}

		return Optional.ofNullable(_padding).map(blockCipherPadding -> (BufferedBlockCipher) new PaddedBufferedBlockCipher(cipher, blockCipherPadding)).orElseGet(() -> new BufferedBlockCipher(cipher));
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
			parameters = keyParameter = new KeyParameter(key);
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
			parameters = new ParametersWithIV(keyParameter, iv);
		}
		catch (final Throwable e)
		{
			throw new CipherException(CipherExceptionType.INVALID_IV, e);
		}
	}

	@Override
	public void init(final int opMode) throws CipherException
	{
		requirePresent(parameters, "Key");

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
			outOff += theCipher.doFinal(tmpBytes, outOff); // FIXME: With CTS(Ciphertext Stealing) mode, org.bouncycastle.crypto.DataLengthException: need at least one block of input for CTS

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
