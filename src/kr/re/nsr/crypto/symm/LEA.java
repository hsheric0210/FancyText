package kr.re.nsr.crypto.symm;

import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.engine.LeaEngine;
import kr.re.nsr.crypto.mac.CMac;
import kr.re.nsr.crypto.mode.*;

public class LEA
{
	private LEA()
	{
		throw new AssertionError();
	}

	static BlockCipher getEngine()
	{
		return new LeaEngine();
	}

	public static final class ECB extends ECBMode
	{
		public ECB()
		{
			super(getEngine());
		}
	}

	public static final class CBC extends CBCMode
	{
		public CBC()
		{
			super(getEngine());
		}
	}

	public static final class CTR extends CTRMode
	{
		public CTR()
		{
			super(getEngine());
		}
	}

	public static final class CFB extends CFBMode
	{
		public CFB()
		{
			super(getEngine());
		}
	}

	public static final class OFB extends OFBMode
	{
		public OFB()
		{
			super(getEngine());
		}
	}

	public static final class CCM extends CCMMode
	{
		public CCM()
		{
			super(getEngine());
		}
	}

	public static final class GCM extends GCMMode
	{
		public GCM()
		{
			super(getEngine());
		}
	}

	private static final class CMAC extends CMac
	{
		public CMAC()
		{
			super(getEngine());
		}
	}
}
