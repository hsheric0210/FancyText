package kr.re.nsr.crypto;

import java.util.Arrays;

import javax.crypto.BadPaddingException;

import kr.re.nsr.crypto.BlockCipher.Mode;

public abstract class BlockCipherModeBlock extends BlockCipherModeImpl
{
	private Padding padding;

	public BlockCipherModeBlock(final BlockCipher cipher)
	{
		super(cipher);
	}

	@Override
	public final int getOutputSize(final int len)
	{
		// TODO

		final int size = (len + bufferOffset & blockMask) + blockSize;
		if (mode == Mode.ENCRYPT)
			return padding != null ? size : len;

		return len;
	}

	@Override
	public final int getUpdateOutputSize(final int len)
	{
		return (mode == Mode.DECRYPT && padding != null ? len + bufferOffset - blockSize : len + bufferOffset) & blockMask;
	}

	@Override
	public void init(final Mode mode, final byte[] mk)
	{
		throw new UnsupportedOperationException("This initCipher method is not applicable to " + getAlgorithmName());
	}

	@Override
	public void init(final Mode mode, final byte[] mk, final byte[] iv)
	{
		throw new UnsupportedOperationException("This initCipher method is not applicable to " + getAlgorithmName());
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
		this.padding = padding;
	}

	@Override
	public byte[] update(final byte[] msg)
	{
		if (padding != null && mode == Mode.DECRYPT)
			return decryptWithPadding(msg);

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
	public final byte[] doFinal() throws BadPaddingException
	{
		if (padding != null)
			return doFinalWithPadding();

		if (bufferOffset == 0)
			return null;

		if (bufferOffset != blockSize)
			throw new BadPaddingException("bufferOffset != blocksize");

		final byte[] out = new byte[blockSize];
		processBlock(buffer, 0, out, 0, blockSize);

		return out;
	}

	/**
	 * 패딩 사용시 복호화 처리, 마지막 블록을 위해 데이터를 남겨둠
	 * 
	 * @param  msg
	 * @return
	 */
	private byte[] decryptWithPadding(final byte[] msg)
	{
		if (msg == null)
			return null;

		int length = msg.length;
		final int bufferLength = buffer.length;
		final int gap = buffer.length - bufferOffset;
		int inOff = 0;
		final byte[] out = new byte[getUpdateOutputSize(length)];

		if (length > gap)
		{
			System.arraycopy(msg, inOff, buffer, bufferOffset, gap);
			int outOff = processBlock(buffer, 0, out, 0);

			bufferOffset = 0;
			length -= gap;
			inOff += gap;

			while (length > bufferLength)
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

	/**
	 * 패딩 사용시 마지막 블록 처리
	 * 
	 * @return
	 */
	private byte[] doFinalWithPadding() throws BadPaddingException
	{
		if (mode == Mode.ENCRYPT)
		{
			padding.pad(buffer, bufferOffset);
			final byte[] out = new byte[getOutputSize(0)];
			processBlock(buffer, 0, out, 0);
			return out;
		}

		final byte[] block = new byte[blockSize];
		processBlock(buffer, 0, block, 0);
		return padding.unpad(block);
	}
}
