package fancytext.utils;

public interface IEncoder
{
	String encode(final byte[] bytes, final Object parameters, final int flags);

	byte[] decode(final String string);
}
