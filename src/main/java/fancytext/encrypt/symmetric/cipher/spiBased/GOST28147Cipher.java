package fancytext.encrypt.symmetric.cipher.spiBased;

import java.util.StringJoiner;

import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;

import fancytext.Main;
import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherMode;
import fancytext.encrypt.symmetric.CipherPadding;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.encrypt.symmetric.CipherException;

public class GOST28147Cipher extends SpiBasedCipher
{
	private final String sboxName;

	public GOST28147Cipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding, final int unitBytes, final String sboxName) throws CipherException
	{
		super(algorithm, mode, padding, unitBytes);
		if (algorithm != CipherAlgorithm.GOST28147)
			throw new CipherException(CipherExceptionType.UNSUPPORTED_ALGORITHM, "Only supports GOST28147");
		this.sboxName = sboxName;
		parameter = new GOST28147ParameterSpec(sboxName);
	}

	@Override
	protected void dumpAdditionalInformations(final StringBuilder builder)
	{
		super.dumpAdditionalInformations(builder);
		builder.append("Cipher S-Box name: ").append(sboxName).append(Main.lineSeparator);
	}

	@Override
	protected void serializeAdditionalInformations(final StringJoiner joiner)
	{
		super.serializeAdditionalInformations(joiner);
		joiner.add("SBox=" + sboxName);
	}

	@Override
	public void setIV(final byte[] iv, final int macSize) throws CipherException
	{
		try
		{
			parameter = new GOST28147ParameterSpec(sboxName, iv);
		}
		catch (final Throwable e)
		{
			throw new CipherException(CipherExceptionType.INVALID_IV, e);
		}
	}
}
