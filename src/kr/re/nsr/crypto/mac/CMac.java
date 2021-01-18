package kr.re.nsr.crypto.mac;

import static kr.re.nsr.crypto.util.Ops.XOR;
import static kr.re.nsr.crypto.util.Ops.shiftLeft;

import java.util.Arrays;

import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.Mac;

public class CMac extends Mac
{

	private static final byte[] R256 =
	{
			(byte) 0x04, (byte) 0x25
	};
	private static final byte[] R128 =
	{
			(byte) 0x87
	};
	private static final byte[] R64 =
	{
			(byte) 0x1b
	};

	private final BlockCipher engine;

	private int blockSize;
	private int blockIndex;
	private byte[] block;
	private byte[] mac;

	private byte[] RB;
	private byte[] k1;
	private byte[] k2;

	public CMac(final BlockCipher cipher)
	{
		engine = cipher;
	}

	@Override
	public void init(final byte[] key)
	{
		engine.init(Mode.ENCRYPT, key);

		blockIndex = 0;
		blockSize = engine.getBlockSize();
		block = new byte[blockSize];
		mac = new byte[blockSize];
		k1 = new byte[blockSize];
		k2 = new byte[blockSize];

		selectRB();

		final byte[] zero = new byte[blockSize];
		engine.processBlock(zero, 0, zero, 0);
		cmac_subkey(k1, zero);
		cmac_subkey(k2, k1);
	}

	@Override
	public void reset()
	{
		engine.reset();
		Arrays.fill(block, (byte) 0);
		Arrays.fill(mac, (byte) 0);
		blockIndex = 0;
	}

	@Override
	public void update(final byte[] msg)
	{
		if (msg == null || msg.length == 0)
			return;

		int len = msg.length;
		int msgOff = 0;
		final int gap = blockSize - blockIndex;

		if (len > gap)
		{
			System.arraycopy(msg, msgOff, block, blockIndex, gap);

			blockIndex = 0;
			len -= gap;
			msgOff += gap;

			while (len > blockSize)
			{
				XOR(block, mac);
				engine.processBlock(block, 0, mac, 0);
				System.arraycopy(msg, msgOff, block, 0, blockSize);

				len -= blockSize;
				msgOff += blockSize;
			}

			if (len > 0)
			{
				XOR(block, mac);
				engine.processBlock(block, 0, mac, 0);
			}

		}

		if (len > 0)
		{
			System.arraycopy(msg, msgOff, block, blockIndex, len);
			blockIndex += len;
		}
	}

	@Override
	public byte[] doFinal(final byte[] msg)
	{
		update(msg);
		return doFinal();
	}

	@Override
	public byte[] doFinal()
	{
		if (blockIndex < blockSize)
		{
			block[blockIndex] = (byte) 0x80;
			Arrays.fill(block, blockIndex + 1, blockSize, (byte) 0x00);
		}

		XOR(block, blockIndex == blockSize ? k1 : k2);
		XOR(block, mac);
		engine.processBlock(block, 0, mac, 0);

		return mac.clone();
	}

	private void selectRB()
	{
		switch (blockSize)
		{
			case 8:
				RB = R64;
				break;

			case 16:
				RB = R128;
				break;

			case 32:
				RB = R256;
				break;
		}
	}

	private void cmac_subkey(final byte[] newKey, final byte[] oldKey)
	{
		System.arraycopy(oldKey, 0, newKey, 0, blockSize);
		shiftLeft(newKey, 1);

		if ((oldKey[0] & 0x80) != 0)
			for (int i = 0, j = RB.length; i < j; ++i)
				newKey[blockSize - RB.length + i] ^= RB[i];
	}
}
