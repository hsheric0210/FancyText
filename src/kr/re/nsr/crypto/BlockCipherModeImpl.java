package kr.re.nsr.crypto;

import javax.crypto.BadPaddingException;

import kr.re.nsr.crypto.BlockCipher.Mode;

import java.util.Optional;

public abstract class BlockCipherModeImpl extends BlockCipherMode
{

	protected Mode mode;
	protected final BlockCipher engine;

	final byte[] buffer;
	int bufferOffset;
	protected final int blockSize;
	final int blockMask;

	BlockCipherModeImpl(final BlockCipher cipher)
	{
		engine = cipher;
		blockSize = engine.getBlockSize();
		blockMask = getBlockmask(blockSize);
		buffer = new byte[blockSize];
	}

	@Override
	public byte[] doFinal(final byte[] msg) throws BadPaddingException
	{
		final byte[] part1 = update(msg);
		final byte[] part2 = doFinal();

		final int len1 = Optional.ofNullable(part1).map(part11 -> part11.length).orElse(0);
		final int len2 = Optional.ofNullable(part2).map(bytes -> bytes.length).orElse(0);

		final byte[] out = new byte[len1 + len2];

		if (len1 > 0)
			System.arraycopy(part1, 0, out, 0, len1);

		if (len2 > 0)
			System.arraycopy(part2, 0, out, len1, len2);

		return out;
	}

	protected abstract int processBlock(byte[] in, int inOff, byte[] out, int outOff, int length);

	final int processBlock(final byte[] in, final int inOff, final byte[] out, final int outOff)
	{
		return processBlock(in, inOff, out, outOff, blockSize);
	}

	private static int getBlockmask(final int blocksize)
	{

		switch (blocksize)
		{
			case 8: // 64-bit
				return 0xfffffff7;

			case 16: // 128-bit
				return 0xfffffff0;

			case 32: // 256-bit
				return 0xffffffe0;
		}

		return 0;
	}

	protected static byte[] clone(final byte[] array)
	{
		if (array == null)
			return null;

		final byte[] clone = new byte[array.length];
		System.arraycopy(array, 0, clone, 0, clone.length);

		return clone;
	}
}
