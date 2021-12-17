package fancytext.encrypt.symmetric.cipher;

import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherAlgorithmMode;
import fancytext.encrypt.symmetric.CipherAlgorithmPadding;

public abstract class AbstractCipher
{
	protected final CipherAlgorithm algorithm;
	protected final CipherAlgorithmMode mode;
	protected final CipherAlgorithmPadding padding;

	/**
	 * @param algorithm
	 *                  Cipher algorithm (example: AES)
	 * @param mode
	 *                  Cipher mode (example: ECB)
	 * @param padding
	 *                  Cipher padding (example: PKCS5Padding)
	 */
	public AbstractCipher(final CipherAlgorithm algorithm, final CipherAlgorithmMode mode, final CipherAlgorithmPadding padding) throws CipherException
	{
		this.algorithm = algorithm;
		this.mode = mode;
		this.padding = padding;
	}

	/**
	 * Set the cipher key
	 * 
	 * @param key
	 *            The key
	 */
	public abstract void setKey(final byte[] key);

	/**
	 * Set the cipher initial vector (or nonce)
	 * 
	 * <p>
	 * <strong>Note: This method must be call after {@code setKey()} is called.</strong>
	 * </p>
	 * 
	 * @param iv
	 *                    IV (or nonce)
	 * @param macSize
	 *                    length of the nonce, if present
	 */
	public abstract void setIV(final byte[] iv, final int macSize);

	/**
	 * Initialize the cipher with operation mode
	 *
	 * <p>
	 * <strong>Note: This method must be call after {@code setKey()} and {@code setIV()} is called.</strong>
	 * </p>
	 * 
	 * @param opMode
	 *               Cipher operation mode
	 */
	public abstract void init(final int opMode) throws CipherException;

	/**
	 * Process the data
	 * 
	 * @param  bytes
	 *               Data to proceed
	 * @return       Processed data
	 */
	public abstract byte[] doFinal(final byte[] bytes) throws CipherException;

	public abstract int getBlockSize();
}
