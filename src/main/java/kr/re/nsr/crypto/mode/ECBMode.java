package kr.re.nsr.crypto.mode;

import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherModeBlock;

public class ECBMode extends BlockCipherModeBlock
{
	public ECBMode(final BlockCipher cipher)
	{
		super(cipher);
	}

	@Override
	public String getAlgorithmName()
	{
		return engine.getAlgorithmName() + "/ECB";
	}

	@Override
	public void init(final Mode mode, final byte[] mk)
	{
		this.mode = mode;
		engine.init(mode, mk);
	}

	@Override
	protected int processBlock(final byte[] in, final int inOff, final byte[] out, final int outOff, final int outlen)
	{
		if (outlen != blockSize)
			throw new IllegalArgumentException("outlen should be " + blockSize + " in " + getAlgorithmName());

		if (inOff + blockSize > in.length)
			throw new IllegalArgumentException("input data too short");

		return engine.processBlock(in, inOff, out, outOff);
	}
}
