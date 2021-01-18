package kr.re.nsr.crypto.mode;

import static kr.re.nsr.crypto.util.Ops.XOR;

import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherModeStream;

public class CTRMode extends BlockCipherModeStream
{

	private byte[] iv;
	private byte[] ctr;
	private byte[] block;

	public CTRMode(final BlockCipher cipher)
	{
		super(cipher);
	}

	@Override
	public String getAlgorithmName()
	{
		return engine.getAlgorithmName() + "/CTR";
	}

	@Override
	public void init(final Mode mode, final byte[] mk, final byte[] iv)
	{
		this.mode = mode;
		engine.init(Mode.ENCRYPT, mk);

		this.iv = iv.clone();
		ctr = new byte[blockSize];
		block = new byte[blockSize];
		reset();
	}

	@Override
	public void reset()
	{
		super.reset();
		System.arraycopy(iv, 0, ctr, 0, ctr.length);
	}

	@Override
	protected int processBlock(final byte[] in, final int inOff, final byte[] out, final int outOff, final int outlen)
	{
		final int length = engine.processBlock(ctr, 0, block, 0);
		addCounter();

		XOR(out, outOff, in, inOff, block, 0, outlen);

		return length;
	}

	private void addCounter()
	{
		for (int i = ctr.length - 1; i >= 0; --i)
			if (++ctr[i] != 0)
				break;
	}

}
