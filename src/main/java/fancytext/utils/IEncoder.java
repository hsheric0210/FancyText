package fancytext.utils;

public interface IEncoder
{
	String encode(final byte[] bytes);

	byte[] decode(final String string);
}
