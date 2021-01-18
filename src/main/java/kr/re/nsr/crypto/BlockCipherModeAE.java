package kr.re.nsr.crypto;

import java.util.Optional;

import javax.crypto.AEADBadTagException;

import kr.re.nsr.crypto.BlockCipher.Mode;

public abstract class BlockCipherModeAE
{

	protected Mode mode;
	protected final BlockCipher engine;

	private final byte[] buffer;
	protected byte[] nonce;
	protected int bufOff;
	protected final int blocksize;

	protected int tagLength;

	public BlockCipherModeAE(final BlockCipher cipher)
	{
		engine = cipher;
		blocksize = engine.getBlockSize();
		buffer = new byte[blocksize];
	}

	public abstract void init(Mode mode, byte[] mk, byte[] nonce, int taglen);

	public abstract void updateAAD(byte[] aad);

	protected abstract byte[] update(byte[] msg);

	protected abstract byte[] doFinal() throws AEADBadTagException;

	public abstract int getOutputSize(int len);

	public final byte[] doFinal(final byte[] msg) throws AEADBadTagException
	{

		if (mode == Mode.ENCRYPT)
		{
			final byte[] part1 = update(msg);
			final byte[] part2 = doFinal();

			final int len1 = Optional.ofNullable(part1).map(part11 -> part11.length).orElse(0);
			final int len2 = Optional.ofNullable(part2).map(bytes -> bytes.length).orElse(0);

			final byte[] out = new byte[len1 + len2];
			if (part1 != null)
				System.arraycopy(part1, 0, out, 0, len1);

			if (part2 != null)
				System.arraycopy(part2, 0, out, len1, len2);

			return out;
		}

		update(msg);
		return doFinal();
	}
}
