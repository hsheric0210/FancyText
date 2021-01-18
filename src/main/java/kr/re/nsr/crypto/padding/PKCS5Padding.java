package kr.re.nsr.crypto.padding;

import java.util.Arrays;
import java.util.Objects;

import javax.crypto.BadPaddingException;

import kr.re.nsr.crypto.Padding;

public class PKCS5Padding extends Padding
{

	public PKCS5Padding(final int blocksize)
	{
		super(blocksize);
	}

	@Override
	public byte[] pad(final byte[] in) throws BadPaddingException
	{
		Objects.requireNonNull(in, "in");

		if (in.length > blocksize)
			throw new BadPaddingException("input should be shorter than blocksize");

		final byte[] out = new byte[blocksize];
		System.arraycopy(in, 0, out, 0, in.length);
		pad(out, in.length);
		return out;
	}

	@Override
	public void pad(final byte[] in, final int inOff) throws BadPaddingException
	{
		Objects.requireNonNull(in, "in");

		if (in.length < inOff)
			throw new BadPaddingException("in.length < inOff");

		final byte code = (byte) (in.length - inOff);
		Arrays.fill(in, inOff, in.length, code);
	}

	@Override
	public byte[] unpad(final byte[] in) throws BadPaddingException
	{
		Objects.requireNonNull(in, "in");

		if (in.length < 1)
			throw new BadPaddingException("in.length < 1");

		if (in.length % blocksize != 0)
			throw new BadPaddingException("in.length % blocksize != 0");

		final int cnt = in.length - getPadCount(in);
		if (cnt == 0)
			return null;

		final byte[] out = new byte[cnt];
		System.arraycopy(in, 0, out, 0, out.length);

		return out;
	}

	@Override
	public int getPadCount(final byte[] in) throws BadPaddingException
	{
		Objects.requireNonNull(in, "in");

		if (in.length < 1)
			throw new BadPaddingException("in.length < 1");

		if (in.length % blocksize != 0)
			throw new BadPaddingException("in.length % blocksize != 0");

		final int count = in[in.length - 1] & 0xff;

		boolean isBadPadding = false;
		final int lower_bound = in.length - count;
		for (int i = in.length - 1; i > lower_bound; --i)
			if (in[i] != count)
			{
				isBadPadding = true;
				break;
			}

		if (isBadPadding)
			throw new BadPaddingException("Padding has an invalid character(s).");

		return count;
	}

}
