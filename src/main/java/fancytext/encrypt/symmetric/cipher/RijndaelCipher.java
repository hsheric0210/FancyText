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

public class RijndaelCipher extends AbstractCipher
{
	private final BufferedBlockCipher theCipher;
	private final int unitBytes;
	private final int blockSize;
	private KeyParameter keyParameter;
	private CipherParameters parameters;
	private final BlockCipherPadding _padding;

	public RijndaelCipher(final CipherAlgorithm algorithm, final CipherAlgorithmMode mode, final CipherAlgorithmPadding padding, final int unitBytes, final int blockSize) throws CipherException
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
	public void setKey(final byte[] key)
	{
		parameters = keyParameter = new KeyParameter(key);
	}

	@Override
	public void setIV(final byte[] iv, final int macSize)
	{
		parameters = new ParametersWithIV(Objects.requireNonNull(keyParameter, "Key is not set!"), iv);
	}

	@Override
	public void init(final int opMode) throws CipherException
	{
		try
		{
			theCipher.init(opMode == Cipher.ENCRYPT_MODE, Objects.requireNonNull(parameters, "Key is not set!"));
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
			outOff += theCipher.doFinal(tmpBytes, outOff); // FIXME: With CTS(Ciphertext Stealing) mode, org.bouncycastle.crypto.DataLengthException: need at least one block of input for CTS

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
