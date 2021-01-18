package kr.re.nsr.crypto.mode;

import static kr.re.nsr.crypto.util.Ops.XOR;

import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherModeBlock;

public class CBCMode extends BlockCipherModeBlock
{

	private byte[] iv;
	private byte[] feedback;

	public CBCMode(final BlockCipher cipher)
	{
		super(cipher);
	}

	@Override
	public String getAlgorithmName()
	{
		return engine.getAlgorithmName() + "/CBC";
	}

	@Override
	public void init(final Mode mode, final byte[] mk, final byte[] iv)
	{
		this.mode = mode;
		engine.init(mode, mk);
		this.iv = clone(iv);

		feedback = new byte[blockSize];
		reset();
	}

	@Override
	public void reset()
	{
		super.reset();
		System.arraycopy(iv, 0, feedback, 0, blockSize);
	}

	@Override
	protected int processBlock(final byte[] in, final int inOff, final byte[] out, final int outOff, final int outlen)
	{
		if (outlen != blockSize)
			throw new IllegalArgumentException("outlen should be " + blockSize + " in " + getAlgorithmName());

		return mode == Mode.ENCRYPT ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);

	}

	private int encryptBlock(final byte[] in, final int inOff, final byte[] out, final int outOff)
	{
		if (inOff + blockSize > in.length)
			throw new IllegalArgumentException("input data too short");

		XOR(feedback, 0, in, inOff, blockSize);

		engine.processBlock(feedback, 0, out, outOff);

		System.arraycopy(out, outOff, feedback, 0, blockSize);

		return blockSize;
	}

	private int decryptBlock(final byte[] in, final int inOff, final byte[] out, final int outOff)
	{
		if (inOff + blockSize > in.length)
			throw new IllegalArgumentException("input data too short");

		engine.processBlock(in, inOff, out, outOff);

		XOR(out, outOff, feedback, 0, blockSize);

		System.arraycopy(in, inOff, feedback, 0, blockSize);

		return blockSize;
	}

}
