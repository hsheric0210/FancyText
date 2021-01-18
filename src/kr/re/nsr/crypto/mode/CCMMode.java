package kr.re.nsr.crypto.mode;

import static kr.re.nsr.crypto.util.Hex.toBytes;
import static kr.re.nsr.crypto.util.Ops.XOR;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherModeAE;

public class CCMMode extends BlockCipherModeAE
{

	private final byte[] ctr;
	private final byte[] mac;
	private byte[] tag;
	private final byte[] block;

	private ByteArrayOutputStream aadBytes;
	private ByteArrayOutputStream inputBytes;

	private int msglen;
	private int taglen;
	private int noncelen;

	public CCMMode(final BlockCipher cipher)
	{
		super(cipher);

		ctr = new byte[blocksize];
		mac = new byte[blocksize];
		block = new byte[blocksize];
	}

	@Override
	public void init(final Mode mode, final byte[] mk, final byte[] nonce, final int taglen)
	{
		this.mode = mode;
		engine.init(Mode.ENCRYPT, mk);

		aadBytes = new ByteArrayOutputStream();
		inputBytes = new ByteArrayOutputStream();

		setTaglen(taglen);
		setNonce(nonce);
	}

	@Override
	public void updateAAD(final byte[] aad)
	{
		if (aad == null || aad.length == 0)
			return;

		aadBytes.write(aad, 0, aad.length);
	}

	@Override
	public byte[] update(final byte[] msg)
	{
		inputBytes.write(msg, 0, msg.length);
		return null;
	}

	@Override
	public byte[] doFinal()
	{
		close(aadBytes);
		close(inputBytes);

		if (aadBytes.size() > 0)
			block[0] |= (byte) 0x40;

		msglen = inputBytes.toByteArray().length;
		if (mode == Mode.DECRYPT)
			msglen -= taglen;

		toBytes(msglen, block, noncelen + 1, 15 - noncelen);
		engine.processBlock(block, 0, mac, 0);

		final byte[] out;

		processAAD();
		if (mode == Mode.ENCRYPT)
		{
			out = new byte[msglen + taglen];
			encryptData(out, 0);

		}
		else
		{
			out = new byte[msglen];
			decryptData(out, 0);
		}

		resetCounter();
		engine.processBlock(ctr, 0, block, 0);

		if (mode == Mode.ENCRYPT)
		{
			XOR(mac, block);
			System.arraycopy(mac, 0, tag, 0, taglen);
			System.arraycopy(mac, 0, out, out.length - taglen, taglen);

		}
		else if (!Arrays.equals(tag, mac))
			Arrays.fill(out, (byte) 0);

		return out;
	}

	@Override
	public int getOutputSize(final int len)
	{
		final int outSize = len + bufOff;
		if (mode == Mode.ENCRYPT)
			return outSize + taglen;

		return outSize < taglen ? 0 : outSize - taglen;
	}

	private void setNonce(final byte[] nonce)
	{
		Objects.requireNonNull(nonce, "nonce");

		noncelen = nonce.length;
		if (noncelen < 7 || noncelen > 13)
			throw new IllegalArgumentException("length of nonce should be 7 ~ 13 bytes");

		// init counter
		ctr[0] = (byte) (14 - noncelen);
		System.arraycopy(nonce, 0, ctr, 1, noncelen);

		// init b0
		final int tagfield = (taglen - 2) / 2;
		block[0] = (byte) (tagfield << 3 & 0xff);
		block[0] |= (byte) (14 - noncelen & 0xff);
		System.arraycopy(nonce, 0, block, 1, noncelen);
	}

	private void setTaglen(final int taglen)
	{
		if (taglen < 4 || taglen > 16 || (taglen & 0x01) != 0)
			throw new IllegalArgumentException("length of tag should be 4, 6, 8, 10, 12, 14, 16 bytes");

		this.taglen = taglen;
		tag = new byte[taglen];
	}

	private void resetCounter()
	{
		Arrays.fill(ctr, noncelen + 1, ctr.length, (byte) 0);
	}

	private void increaseCounter()
	{
		int i = ctr.length - 1;
		while (++ctr[i] == 0)
		{
			--i;
			if (i < noncelen + 1)
				throw new IllegalStateException("exceed maximum counter");
		}
	}

	private void processAAD()
	{
		final byte[] aad = aadBytes.toByteArray();

		Arrays.fill(block, (byte) 0);

		final int alen;
		if (aad.length < 0xff00)
		{
			alen = 2;
			toBytes(aad.length, block, 0, 2);

		}
		else
		{
			alen = 6;
			block[0] = (byte) 0xff;
			block[1] = (byte) 0xfe;
			toBytes(aad.length, block, 2, 4);
		}

		if (aad.length == 0)
			return;

		int i = 0;
		int remained = aad.length;
		int processed = remained > blocksize - alen ? blocksize - alen : aad.length;
		i += processed;
		remained -= processed;
		System.arraycopy(aad, 0, block, alen, processed);

		XOR(mac, block);
		engine.processBlock(mac, 0, mac, 0);

		while (remained > 0)
		{
			processed = Math.min(remained, blocksize);
			XOR(mac, 0, mac, 0, aad, i, processed);
			engine.processBlock(mac, 0, mac, 0);

			i += processed;
			remained -= processed;
		}
	}

	private void encryptData(final byte[] out, final int offset)
	{
		int inIdx = 0;
		int remained;
		int processed;
		int outIdx = offset;

		final byte[] in = inputBytes.toByteArray();

		remained = msglen;
		while (remained > 0)
		{
			processed = Math.min(remained, blocksize);

			XOR(mac, 0, mac, 0, in, inIdx, processed);
			engine.processBlock(mac, 0, mac, 0);

			increaseCounter();
			engine.processBlock(ctr, 0, block, 0);
			XOR(out, outIdx, block, 0, in, inIdx, processed);

			inIdx += processed;
			outIdx += processed;
			remained -= processed;
		}
	}

	private void decryptData(final byte[] out, final int offset)
	{
		int i = 0;
		int remained;
		int processed;
		int outIdx = offset;

		final byte[] in = inputBytes.toByteArray();

		System.arraycopy(in, msglen, tag, 0, taglen);
		engine.processBlock(ctr, 0, block, 0);
		XOR(tag, 0, block, 0, taglen);

		remained = msglen;
		while (remained > 0)
		{
			processed = Math.min(remained, blocksize);

			increaseCounter();
			engine.processBlock(ctr, 0, block, 0);
			XOR(out, outIdx, block, 0, in, i, processed);

			XOR(mac, 0, mac, 0, out, outIdx, processed);
			engine.processBlock(mac, 0, mac, 0);

			i += processed;
			outIdx += processed;
			remained -= processed;
		}
	}

	private static void close(final Closeable obj)
	{
		if (obj == null)
			return;

		try
		{
			obj.close();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}
}
