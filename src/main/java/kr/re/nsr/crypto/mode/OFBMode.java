package kr.re.nsr.crypto.mode;

import static kr.re.nsr.crypto.util.Ops.XOR;

import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherModeStream;

// DONE: block vs buffer
public class OFBMode extends BlockCipherModeStream
{

	private byte[] iv;
	private byte[] block;

	public OFBMode(final BlockCipher cipher)
	{
		super(cipher);
	}

	@Override
	public String getAlgorithmName()
	{
		return engine.getAlgorithmName() + "/OFB";
	}

	@Override
	public void init(final Mode mode, final byte[] mk, final byte[] iv)
	{
		this.mode = mode;
		engine.init(Mode.ENCRYPT, mk);

		this.iv = iv.clone();
		block = new byte[blockSize];
		reset();
	}

	@Override
	public void reset()
	{
		super.reset();
		System.arraycopy(iv, 0, block, 0, blockSize);
	}

	@Override
	protected int processBlock(final byte[] in, final int inOff, final byte[] out, final int outOff, final int outlen)
	{
		final int length = engine.processBlock(block, 0, block, 0);
		XOR(out, outOff, in, inOff, block, 0, outlen);

		return length;
	}
}
