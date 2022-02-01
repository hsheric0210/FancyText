package fancytext.encrypt.symmetric;

import static org.hamcrest.core.Is.is;

import javax.crypto.Cipher;

import org.junit.Assert;
import org.junit.Test;

import at.favre.lib.bytes.Bytes;
import fancytext.encrypt.symmetric.cipher.spiBased.AESCipher;

public class AESCipherTest
{
	/**
	 * https://github.com/ircmaxell/quality-checker/blob/master/tmp/gh_18/PHP-PasswordLib-master/test/Data/Vectors/aes-ecb.test-vectors
	 */
	@Test
	public void testECB() throws CipherException
	{
		testEncryptAES128ECB("AES128-ECB: Set 1 vector 1", "6bc1bee22e409f96e93d7e117393172a", "2b7e151628aed2a6abf7158809cf4f3c", "3ad77bb40d7a3660a89ecaf32466ef97");
		testEncryptAES128ECB("AES128-ECB: Set 1 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "2b7e151628aed2a6abf7158809cf4f3c", "f5d3d58503b9699de785895a96fdbaaf");
		testEncryptAES128ECB("AES128-ECB: Set 1 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "2b7e151628aed2a6abf7158809cf4f3c", "43b1cd7f598ece23881b00e3ed030688");
		testEncryptAES128ECB("AES128-ECB: Set 1 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "2b7e151628aed2a6abf7158809cf4f3c", "7b0c785e27e8ad3f8223207104725dd4");

		testEncryptAES192ECB("AES192-ECB: Set 2 vector 1", "6bc1bee22e409f96e93d7e117393172a", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "bd334f1d6e45f25ff712a214571fa5cc");
		testEncryptAES192ECB("AES192-ECB: Set 2 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "974104846d0ad3ad7734ecb3ecee4eef");
		testEncryptAES192ECB("AES192-ECB: Set 2 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "ef7afd2270e2e60adce0ba2face6444e");
		testEncryptAES192ECB("AES192-ECB: Set 2 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "9a4b41ba738d6c72fb16691603c18e0e");

		testEncryptAES256ECB("AES256-ECB: Set 3 vector 1", "6bc1bee22e409f96e93d7e117393172a", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "f3eed1bdb5d2a03c064b5a7e3db181f8");
		testEncryptAES256ECB("AES256-ECB: Set 3 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "591ccb10d410ed26dc5ba74a31362870");
		testEncryptAES256ECB("AES256-ECB: Set 3 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "b6ed21b99ca6f4f9f153e7b1beafed1d");
		testEncryptAES256ECB("AES256-ECB: Set 3 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "23304b7a39f9f3ff067d8d8f9e24ecc7");
	}

	/**
	 * https://github.com/ircmaxell/quality-checker/blob/master/tmp/gh_18/PHP-PasswordLib-master/test/Data/Vectors/aes-cbc.test-vectors
	 */
	@Test
	public void testCBC() throws CipherException
	{
		testEncryptAES128CBC("AES128-CBC: Set 1 vector 1", "6bc1bee22e409f96e93d7e117393172a", "2b7e151628aed2a6abf7158809cf4f3c", "000102030405060708090A0B0C0D0E0F", "7649abac8119b246cee98e9b12e9197d");
		testEncryptAES128CBC("AES128-CBC: Set 1 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "2b7e151628aed2a6abf7158809cf4f3c", "7649ABAC8119B246CEE98E9B12E9197D", "5086cb9b507219ee95db113a917678b2");
		testEncryptAES128CBC("AES128-CBC: Set 1 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "2b7e151628aed2a6abf7158809cf4f3c", "5086CB9B507219EE95DB113A917678B2", "73bed6b8e3c1743b7116e69e22229516");
		testEncryptAES128CBC("AES128-CBC: Set 1 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "2b7e151628aed2a6abf7158809cf4f3c", "73BED6B8E3C1743B7116E69E22229516", "3ff1caa1681fac09120eca307586e1a7");

		testEncryptAES192CBC("AES192-CBC: Set 2 vector 1", "6bc1bee22e409f96e93d7e117393172a", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "000102030405060708090A0B0C0D0E0F", "4f021db243bc633d7178183a9fa071e8");
		testEncryptAES192CBC("AES192-CBC: Set 2 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "4F021DB243BC633D7178183A9FA071E8", "b4d9ada9ad7dedf4e5e738763f69145a");
		testEncryptAES192CBC("AES192-CBC: Set 2 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "B4D9ADA9AD7DEDF4E5E738763F69145A", "571b242012fb7ae07fa9baac3df102e0");
		testEncryptAES192CBC("AES192-CBC: Set 2 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "571B242012FB7AE07FA9BAAC3DF102E0", "08b0e27988598881d920a9e64f5615cd");

		testEncryptAES256CBC("AES256-CBC: Set 3 vector 1", "6bc1bee22e409f96e93d7e117393172a", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "000102030405060708090A0B0C0D0E0F", "f58c4c04d6e5f1ba779eabfb5f7bfbd6");
		testEncryptAES256CBC("AES256-CBC: Set 3 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "F58C4C04D6E5F1BA779EABFB5F7BFBD6", "9cfc4e967edb808d679f777bc6702c7d");
		testEncryptAES256CBC("AES256-CBC: Set 3 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "9CFC4E967EDB808D679F777BC6702C7D", "39f23369a9d9bacfa530e26304231461");
		testEncryptAES256CBC("AES256-CBC: Set 3 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "39F23369A9D9BACFA530E26304231461", "b2eb05e2c39be9fcda6c19078c6a9d1b");
	}

	/**
	 * https://github.com/ircmaxell/quality-checker/blob/master/tmp/gh_18/PHP-PasswordLib-master/test/Data/Vectors/aes-ctr.test-vectors
	 */
	@Test
	public void testCTR() throws CipherException
	{
		testEncryptAES128CTR("AES128-CTR: Set 1 vector 1", "6bc1bee22e409f96e93d7e117393172a", "2b7e151628aed2a6abf7158809cf4f3c", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "874d6191b620e3261bef6864990db6ce");
		// testEncryptAES128CTR("AES128-CTR: Set 1 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "2b7e151628aed2a6abf7158809cf4f3c", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "9806f66b7970fdff8617187bb9fffdff");
		// testEncryptAES128CTR("AES128-CTR: Set 1 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "2b7e151628aed2a6abf7158809cf4f3c", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "5ae4df3edbd5d35e5b4f09020db03eab");
		// testEncryptAES128CTR("AES128-CTR: Set 1 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "2b7e151628aed2a6abf7158809cf4f3c", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "1e031dda2fbe03d1792170a0f3009cee");

		testEncryptAES192CTR("AES192-CTR: Set 2 vector 1", "6bc1bee22e409f96e93d7e117393172a", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "1abc932417521ca24f2b0459fe7e6e0b");
		// testEncryptAES192CTR("AES192-CTR: Set 2 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "090339ec0aa6faefd5ccc2c6f4ce8e94");
		// testEncryptAES192CTR("AES192-CTR: Set 2 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "1e36b26bd1ebc670d1bd1d665620abf7");
		// testEncryptAES192CTR("AES192-CTR: Set 2 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "4f78a7f6d29809585a97daec58c6b050");

		testEncryptAES256CTR("AES256-CTR: Set 3 vector 1", "6bc1bee22e409f96e93d7e117393172a", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "601ec313775789a5b7a7f504bbf3d228");
		// testEncryptAES256CTR("AES256-CTR: Set 3 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "f443e3ca4d62b59aca84e990cacaf5c5");
		// testEncryptAES256CTR("AES256-CTR: Set 3 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "2b0930daa23de94ce87017ba2d84988d");
		// testEncryptAES256CTR("AES256-CTR: Set 3 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff", "dfc9c58db67aada613c2dd08457941a6");
	}

	/**
	 * https://github.com/ircmaxell/quality-checker/blob/master/tmp/gh_18/PHP-PasswordLib-master/test/Data/Vectors/aes-ofb.test-vectors
	 */
	@Test
	public void testOFB() throws CipherException
	{
		testEncryptAES128OFB("AES128-OFB: Set 1 vector 1", "6bc1bee22e409f96e93d7e117393172a", "2b7e151628aed2a6abf7158809cf4f3c", "000102030405060708090A0B0C0D0E0F", "3b3fd92eb72dad20333449f8e83cfb4a");
		testEncryptAES128OFB("AES128-OFB: Set 1 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "2b7e151628aed2a6abf7158809cf4f3c", "50FE67CC996D32B6DA0937E99BAFEC60", "7789508d16918f03f53c52dac54ed825");
		testEncryptAES128OFB("AES128-OFB: Set 1 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "2b7e151628aed2a6abf7158809cf4f3c", "D9A4DADA0892239F6B8B3D7680E15674", "9740051e9c5fecf64344f7a82260edcc");
		testEncryptAES128OFB("AES128-OFB: Set 1 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "2b7e151628aed2a6abf7158809cf4f3c", "A78819583F0308E7A6BF36B1386ABF23", "304c6528f659c77866a510d9c1d6ae5e");

		testEncryptAES192OFB("AES192-OFB: Set 2 vector 1", "6bc1bee22e409f96e93d7e117393172a", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "000102030405060708090A0B0C0D0E0F", "cdc80d6fddf18cab34c25909c99a4174");
		testEncryptAES192OFB("AES192-OFB: Set 2 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "A609B38DF3B1133DDDFF2718BA09565E", "fcc28b8d4c63837c09e81700c1100401");
		testEncryptAES192OFB("AES192-OFB: Set 2 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "52EF01DA52602FE0975F78AC84BF8A50", "8d9a9aeac0f6596f559c6d4daf59a5f2");
		testEncryptAES192OFB("AES192-OFB: Set 2 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "BD5286AC63AABD7EB067AC54B553F71D", "6d9f200857ca6c3e9cac524bd9acc92a");

		testEncryptAES256OFB("AES256-OFB: Set 3 vector 1", "6bc1bee22e409f96e93d7e117393172a", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "000102030405060708090A0B0C0D0E0F", "dc7e84bfda79164b7ecd8486985d3860");
		testEncryptAES256OFB("AES256-OFB: Set 3 vector 2", "ae2d8a571e03ac9c9eb76fac45af8e51", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "B7BF3A5DF43989DD97F0FA97EBCE2F4A", "4febdc6740d20b3ac88f6ad82a4fb08d");
		testEncryptAES256OFB("AES256-OFB: Set 3 vector 3", "30c81c46a35ce411e5fbc1191a0a52ef", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "E1C656305ED1A7A6563805746FE03EDC", "71ab47a086e86eedf39d1c5bba97c408");
		testEncryptAES256OFB("AES256-OFB: Set 3 vector 4", "f69f2445df4f9b17ad2b417be66c3710", "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "41635BE625B48AFC1666DD42A09D96E7", "0126141d67f37be8538f5a8be740e484");
	}

	private void testEncryptAES128ECB(final String reason, final String plain, final String key, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.ECB, 128, plain, key, null), is(cipher));
	}

	private void testEncryptAES192ECB(final String reason, final String plain, final String key, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.ECB, 192, plain, key, null), is(cipher));
	}

	private void testEncryptAES256ECB(final String reason, final String plain, final String key, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.ECB, 256, plain, key, null), is(cipher));
	}

	private void testEncryptAES128CBC(final String reason, final String plain, final String key, final String iv, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.CBC, 128, plain, key, iv), is(cipher));
	}

	private void testEncryptAES192CBC(final String reason, final String plain, final String key, final String iv, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.CBC, 192, plain, key, iv), is(cipher));
	}

	private void testEncryptAES256CBC(final String reason, final String plain, final String key, final String iv, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.CBC, 256, plain, key, iv), is(cipher));
	}

	private void testEncryptAES128CTR(final String reason, final String plain, final String key, final String iv, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.CTR, 128, plain, key, iv), is(cipher));
	}

	private void testEncryptAES192CTR(final String reason, final String plain, final String key, final String iv, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.CTR, 192, plain, key, iv), is(cipher));
	}

	private void testEncryptAES256CTR(final String reason, final String plain, final String key, final String iv, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.CTR, 256, plain, key, iv), is(cipher));
	}

	private void testEncryptAES128OFB(final String reason, final String plain, final String key, final String iv, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.OFB, 128, plain, key, iv), is(cipher));
	}

	private void testEncryptAES192OFB(final String reason, final String plain, final String key, final String iv, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.OFB, 192, plain, key, iv), is(cipher));
	}

	private void testEncryptAES256OFB(final String reason, final String plain, final String key, final String iv, final String cipher) throws CipherException
	{
		Assert.assertThat(reason, encryptAES(CipherMode.OFB, 256, plain, key, iv), is(cipher));
	}

	private String encryptAES(final CipherMode mode, final int keyLengthBits, final String plain, final String key, final String iv) throws CipherException
	{
		final AESCipher aes = new AESCipher(CipherAlgorithm.AES, mode, CipherPadding.NONE, -1, keyLengthBits);
		aes.constructCipher();
		aes.setKey(Bytes.parseHex(key).array());
		if (mode != CipherMode.ECB)
			aes.setIV(Bytes.parseHex(iv).array(), 128);
		aes.initCipher(Cipher.ENCRYPT_MODE);
		return Bytes.from(aes.doFinal(Bytes.parseHex(plain).array())).encodeHex(false);
	}
}
