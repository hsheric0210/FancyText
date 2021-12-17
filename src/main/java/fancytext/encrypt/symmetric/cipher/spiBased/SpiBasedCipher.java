package fancytext.encrypt.symmetric.cipher.spiBased;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jcajce.spec.AEADParameterSpec;

import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherAlgorithmMode;
import fancytext.encrypt.symmetric.CipherAlgorithmPadding;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.encrypt.symmetric.cipher.AbstractCipher;
import fancytext.encrypt.symmetric.cipher.CipherException;

public class SpiBasedCipher extends AbstractCipher
{
	private final String cipherSpi;
	private final Cipher theCipher;
	private final int unitBytes;
	private Key key;
	private AlgorithmParameterSpec parameter;

	public SpiBasedCipher(final CipherAlgorithm algorithm, final CipherAlgorithmMode mode, final CipherAlgorithmPadding padding, final int unitBytes) throws CipherException
	{
		super(algorithm, mode, padding);
		this.unitBytes = unitBytes;

		try
		{
			theCipher = Cipher.getInstance(cipherSpi = getCipherSpi(), algorithm.getProviderName());
		}
		catch (final NoSuchProviderException e)
		{
			throw new CipherException(CipherExceptionType.UNAVAILABLE_PROVIDER, e);
		}
		catch (final NoSuchAlgorithmException e)
		{
			throw new CipherException(CipherExceptionType.UNSUPPORTED_ALGORITHM, e);
		}
		catch (final NoSuchPaddingException e)
		{
			throw new CipherException(CipherExceptionType.UNSUPPORTED_PADDING, e);
		}
	}

	private String getCipherSpi()
	{
		final StringBuilder algorithmBuilder = new StringBuilder(getAlgorithmID()).append('/');

		/* <ID>/<Mode>/<Padding> transformation format */
		if (mode == CipherAlgorithmMode.CFB || mode == CipherAlgorithmMode.OFB)
			algorithmBuilder.append(mode).append(unitBytes).append('/').append(padding.getPaddingName()); // CFBn, OFBn
		else
			algorithmBuilder.append(mode).append('/').append(padding.getPaddingName());

		return algorithmBuilder.toString();
	}

	protected String getAlgorithmID()
	{
		return algorithm.getId();
	}

	@Override
	public void setKey(final byte[] key)
	{
		this.key = new SecretKeySpec(key, algorithm.getId());
	}

	@Override
	public void setIV(final byte[] iv, final int macSize)
	{
		parameter = mode.isAEADMode() ? new AEADParameterSpec(iv, macSize) : new IvParameterSpec(iv);
	}

	@Override
	public void init(final int opMode) throws CipherException
	{
		try
		{
			theCipher.init(opMode, Objects.requireNonNull(key, "Key is not set!"));
		}
		catch (final InvalidKeyException e)
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
		catch (final IllegalBlockSizeException e)
		{
			throw new CipherException(CipherExceptionType.ILLEGAL_BLOCK_SIZE, e);
		}
		catch (final BadPaddingException e)
		{
			throw new CipherException(CipherExceptionType.BAD_PADDING, e);
		}
	}

	@Override
	public int getBlockSize()
	{
		return theCipher.getBlockSize();
	}
}
