package fancytext.encrypt.symmetric.cipher.spiBased;

import java.util.StringJoiner;

import javax.crypto.spec.GCMParameterSpec;

import fancytext.Main;
import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.encrypt.symmetric.CipherMode;
import fancytext.encrypt.symmetric.CipherPadding;
import fancytext.encrypt.symmetric.CipherException;

public class AESCipher extends SpiBasedCipher
{
	private final int keyLengthBits;

	public AESCipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding, final int unitBytes, final int keyLengthBits) throws CipherException
	{
		super(algorithm, mode, padding, unitBytes);
		if (algorithm != CipherAlgorithm.AES)
			throw new CipherException(CipherExceptionType.UNSUPPORTED_ALGORITHM, "Only supports AES");
		this.keyLengthBits = keyLengthBits;
	}

	@Override
	protected String getAlgorithmID()
	{
		if (mode != CipherMode.PCBC && mode != CipherMode.CTR && mode != CipherMode.CTS && mode != CipherMode.CCM && padding == CipherPadding.NONE)
			return algorithm.getId() + "_" + keyLengthBits;
		return super.getAlgorithmID();
	}

	@Override
	protected void dumpAdditionalInformations(final StringBuilder builder)
	{
		super.dumpAdditionalInformations(builder);
		builder.append("Cipher key length: ").append(keyLengthBits).append(Main.lineSeparator);
	}

	@Override
	protected void serializeAdditionalInformations(final StringJoiner joiner)
	{
		super.serializeAdditionalInformations(joiner);
		joiner.add("KeyLength=" + keyLengthBits);
	}

	@Override
	public void setIV(final byte[] iv, final int aeadTagSize) throws CipherException
	{
		if (mode == CipherMode.GCM)
			try
			{
				parameter = new GCMParameterSpec(aeadTagSize, iv);
			}
			catch (final Throwable e)
			{
				throw new CipherException(CipherExceptionType.INVALID_IV, e);
			}
		else
			super.setIV(iv, aeadTagSize);
	}
}
