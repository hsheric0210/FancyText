package fancytext.encrypt.symmetric;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.Whitebox;

public class CipherHelperTest
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testPadInternal() throws Exception
	{
		assertThat("Pad", Whitebox.invokeMethod(CipherHelper.class, "pad", new byte[]
		{
				1, 2, 3, 4, 5
		}, 10, (byte) 0), is(new byte[]
		{
				1, 2, 3, 4, 5, 0, 0, 0, 0, 0
		}));

		assertThat("Same", Whitebox.invokeMethod(CipherHelper.class, "pad", new byte[]
		{
				1, 2, 3, 4, 5
		}, 5, (byte) 0), is(new byte[]
		{
				1, 2, 3, 4, 5
		}));

		exception.expect(ArrayIndexOutOfBoundsException.class);
		assertThat("Exception", Whitebox.invokeMethod(CipherHelper.class, "pad", new byte[]
		{
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10
		}, 5, (byte) 0), is(new byte[]
		{
				1, 2, 3, 4, 5
		}));
	}

	@Test
	public void testPad()
	{
		assertThat("Pad - Zerobyte", CipherHelper.pad(new byte[]
		{
				1, 2, 3, 4, 5
		}, 10, 10, (byte) 0), is(new byte[]
		{
				1, 2, 3, 4, 5, 0, 0, 0, 0, 0
		}));

		assertThat("Pad - Space-char", CipherHelper.pad(new byte[]
		{
				1, 2, 3, 4, 5
		}, 10, 10, (byte) ' '), is(new byte[]
		{
				1, 2, 3, 4, 5, ' ', ' ', ' ', ' ', ' '
		}));

		assertThat("Same", CipherHelper.pad(new byte[]
		{
				1, 2, 3, 4, 5
		}, 5, 5, (byte) 0), is(new byte[]
		{
				1, 2, 3, 4, 5
		}));

		assertThat("Truncate", CipherHelper.pad(new byte[]
		{
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10
		}, 5, 5, (byte) 0), is(new byte[]
		{
				1, 2, 3, 4, 5
		}));

		assertThat("Shorter than minimum - pad", CipherHelper.pad(new byte[]
		{
				1, 2, 3, 4, 5
		}, 10, 20, (byte) 0), is(new byte[]
		{
				1, 2, 3, 4, 5, 0, 0, 0, 0, 0
		}));

		assertThat("Shorter than maximum, Loger than mimimum - same", CipherHelper.pad(new byte[]
		{
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
		}, 10, 20, (byte) 0), is(new byte[]
		{
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
		}));

		assertThat("Longer than maximum - truncate", CipherHelper.pad(new byte[]
		{
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25
		}, 10, 20, (byte) 0), is(new byte[]
		{
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
		}));
	}

	@Test
	public void testPadOfMultiple()
	{
		assertThat("Zerobyte", CipherHelper.padMultipleOf(new byte[]
		{
				1, 2, 3, 4, 5
		}, 16, (byte) 0), is(new byte[]
		{
				1, 2, 3, 4, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		}));

		assertThat("Space-char", CipherHelper.padMultipleOf(new byte[]
		{
				1, 2, 3, 4, 5
		}, 16, (byte) ' '), is(new byte[]
		{
				1, 2, 3, 4, 5, ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '
		}));
	}
}
