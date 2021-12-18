package fancytext.encrypt.symmetric.cipher.spiBased;

import java.util.StringJoiner;

import javax.crypto.spec.RC5ParameterSpec;

import fancytext.Main;
import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherMode;
import fancytext.encrypt.symmetric.CipherPadding;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.encrypt.symmetric.CipherException;

public class RC5Cipher extends SpiBasedCipher
{
	private final int rounds;

	public RC5Cipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding, final int unitBytes, final int rounds) throws CipherException
	{
		super(algorithm, mode, padding, unitBytes);
		if (algorithm != CipherAlgorithm.RC5_32 && algorithm != CipherAlgorithm.RC5_64)
			throw new CipherException(CipherExceptionType.UNSUPPORTED_ALGORITHM, "Only supports RC5_32 and RC5_64");
		this.rounds = rounds;
		parameter = new RC5ParameterSpec(0, rounds, algorithm == CipherAlgorithm.RC5_64 ? 64 : 32);
	}

	@Override
	protected void dumpAdditionalInformations(final StringBuilder builder)
	{
		super.dumpAdditionalInformations(builder);
		builder.append("Cipher rounds: ").append(rounds).append(Main.lineSeparator);
	}

	@Override
	protected void serializeAdditionalInformations(final StringJoiner joiner)
	{
		super.serializeAdditionalInformations(joiner);
		joiner.add("Rounds=" + rounds);
	}

	@Override
	public void setIV(final byte[] iv, final int macSize) throws CipherException
	{
		try
		{
			parameter = new RC5ParameterSpec(0, rounds, algorithm == CipherAlgorithm.RC5_64 ? 64 : 32, iv);
		}
		catch (final Throwable e)
		{
			throw new CipherException(CipherExceptionType.INVALID_IV, e);
		}
	}
}
