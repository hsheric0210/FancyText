package kr.re.nsr.crypto;

import java.util.Arrays;

import kr.re.nsr.crypto.BlockCipher.Mode;

public abstract class BlockCipherModeStream extends BlockCipherModeImpl
{

	public BlockCipherModeStream(final BlockCipher cipher)
	{
		super(cipher);
	}

	@Override
	public final int getOutputSize(final int len)
	{
		return len + bufferOffset;
	}

	@Override
	public final int getUpdateOutputSize(final int len)
	{
		return len + bufferOffset & blockMask;
	}

	@Override
	public final void init(final Mode mode, final byte[] mk)
	{
		throw new UnsupportedOperationException("This init method is not applicable to " + getAlgorithmName());
	}

	@Override
	public void init(final Mode mode, final byte[] mk, final byte[] iv)
	{
		throw new UnsupportedOperationException("This init method is not applicable to " + getAlgorithmName());
	}

	@Override
	public void reset()
	{
		bufferOffset = 0;
		Arrays.fill(buffer, (byte) 0);
	}

	@Override
	public void setPadding(final Padding padding)
	{
		// Do nothing for this modes
	}

	@Override
	public final byte[] update(final byte[] msg)
	{
		if (msg == null)
			return null;

		int length = msg.length;
		final int bufferLength = buffer.length;
		final int gap = buffer.length - bufferOffset;
		int inOff = 0;
		final byte[] out = new byte[getUpdateOutputSize(length)];

		if (length >= gap)
		{
			System.arraycopy(msg, inOff, buffer, bufferOffset, gap);
			int outOff = processBlock(buffer, 0, out, 0);

			bufferOffset = 0;
			length -= gap;
			inOff += gap;

			while (length >= bufferLength)
			{
				outOff += processBlock(msg, inOff, out, outOff);
				length -= blockSize;
				inOff += blockSize;
			}
		}

		if (length > 0)
		{
			System.arraycopy(msg, inOff, buffer, bufferOffset, length);
			bufferOffset += length;
		}

		return out;
	}

	@Override
	public final byte[] doFinal()
	{
		if (bufferOffset == 0)
			return null;

		final byte[] out = new byte[bufferOffset];
		processBlock(buffer, 0, out, 0, bufferOffset);

		return out;
	}

}
