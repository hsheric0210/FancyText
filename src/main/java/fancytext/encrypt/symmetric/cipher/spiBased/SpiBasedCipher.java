package fancytext.encrypt.symmetric.cipher.spiBased;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.StringJoiner;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jcajce.spec.AEADParameterSpec;

import fancytext.Main;
import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.encrypt.symmetric.CipherMode;
import fancytext.encrypt.symmetric.CipherPadding;
import fancytext.encrypt.symmetric.cipher.AbstractCipher;
import fancytext.encrypt.symmetric.CipherException;

public class SpiBasedCipher extends AbstractCipher
{
	protected final String cipherSpi;
	protected final Cipher theCipher;
	protected final int unitBytes;
	protected Key key;
	protected AlgorithmParameterSpec parameter;

	public SpiBasedCipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding, final int unitBytes) throws CipherException
	{
		super(algorithm, mode, padding);
		this.unitBytes = unitBytes;

		try
		{
			theCipher = Cipher.getInstance(cipherSpi = getCipherSpi(), algorithm.getProviderName());
		}
		catch (final NoSuchProviderException e)
		{
			throw new CipherException(CipherExceptionType.PROVIDER_UNAVAILABLE, e);
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
		if (mode.isUsingUnitBytes())
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
	protected void dumpAdditionalInformations(final StringBuilder builder)
	{
		if (mode.isUsingUnitBytes())
			builder.append("Cipher unit-bytes: ").append(unitBytes).append(Main.lineSeparator);
	}

	@Override
	protected void serializeAdditionalInformations(final StringJoiner joiner)
	{
		if (mode.isUsingUnitBytes())
			joiner.add("UnitBytes=" + unitBytes);
	}

	@Override
	public void setKey(final byte[] key) throws CipherException
	{
		try
		{
			this.key = new SecretKeySpec(key, algorithm.getId());
		}
		catch (final Throwable e)
		{
			throw new CipherException(CipherExceptionType.INVALID_KEY, e);
		}
	}

	@Override
	public void setIV(final byte[] iv, final int macSize) throws CipherException
	{
		try
		{
			parameter = mode.isAEADMode() ? new AEADParameterSpec(iv, macSize) : new IvParameterSpec(iv);
		}
		catch (final Throwable e)
		{
			throw new CipherException(CipherExceptionType.INVALID_IV, e);
		}
	}

	@Override
	public void init(final int opMode) throws CipherException
	{
		requirePresent(key, "Key");

		try
		{
			if (parameter == null)
				theCipher.init(opMode, key);
			else
				theCipher.init(opMode, key, parameter);
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
		return theCipher.getBlockSize();
	}
}
