package fancytext.encrypt.symmetric.cipher;

import java.util.StringJoiner;

import fancytext.Main;
import fancytext.encrypt.symmetric.*;

public abstract class AbstractCipher
{
	protected final CipherAlgorithm algorithm;
	protected final CipherMode mode;
	protected final CipherPadding padding;

	/**
	 * @param algorithm
	 *                  Cipher algorithm (example: AES)
	 * @param mode
	 *                  Cipher mode (example: ECB)
	 * @param padding
	 *                  Cipher padding (example: PKCS5Padding)
	 */
	public AbstractCipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding) throws CipherException
	{
		this.algorithm = algorithm;
		this.mode = mode;
		this.padding = padding;
	}

	public boolean requireIV()
	{
		return algorithm.isStreamCipher() || mode.isUsingIV() || mode.isUsingNonce();
	}

	public boolean requirePaddedInput()
	{
		return algorithm.isPaddedInputRequired() && !mode.isAEADMode() && padding == CipherPadding.NONE && getBlockSize() > 0;
	}

	protected void dumpAdditionalInformations(final StringBuilder builder)
	{
	}

	protected void serializeAdditionalInformations(final StringJoiner builder)
	{
	}

	public final String dumpInformations()
	{
		final StringBuilder builder = new StringBuilder();

		builder.append("Cipher algorithm: ").append(algorithm.getId()).append("(").append(algorithm).append(")").append(Main.lineSeparator);
		builder.append("Cipher mode: ").append(mode.name()).append(Main.lineSeparator);
		builder.append("Cipher algorithm: ").append(padding.getPaddingName()).append("(").append(algorithm).append(")").append(Main.lineSeparator);

		dumpAdditionalInformations(builder);

		return builder.toString();
	}

	public int getIVSize()
	{
		if (mode == CipherMode.CCM)
			return 13; // nonce must have length from 7 to 13 octets

		final int cipherBlockSize = getBlockSize();
		final int algorithmBlockSize = algorithm.getBlockSize();
		return cipherBlockSize == 0 && algorithmBlockSize > 0 ? algorithmBlockSize / 8 : cipherBlockSize;
	}

	@Override
	public final String toString()
	{
		final StringJoiner joiner = new StringJoiner(" / ");
		joiner.add("Algorithm=" + algorithm.getId() + "(" + algorithm + ") - (Provider=" + algorithm.getProviderName() + ")");
		joiner.add("Mode=" + mode);
		joiner.add("Padding=" + padding.getPaddingName() + "(" + padding + ")");
		serializeAdditionalInformations(joiner);
		return joiner.toString();
	}

	/**
	 * Set the cipher key
	 * 
	 * @param key
	 *            The key
	 */
	public abstract void setKey(final byte[] key) throws CipherException;

	/**
	 * Set the cipher initial vector (or nonce)
	 * 
	 * <p>
	 * <strong>Note: This method must be call after {@code setKey()} is called.</strong>
	 * </p>
	 * 
	 * @param iv
	 *                IV (or nonce)
	 * @param macSize
	 *                length of the nonce, if present
	 */
	public abstract void setIV(final byte[] iv, final int macSize) throws CipherException;

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

	/**
	 * Checks that the specified object reference is not {@code null} and
	 * throws a customized {@link CipherException} if it is. This method
	 * is designed primarily for doing parameter validation in methods and
	 * constructors with multiple parameters, as demonstrated below:
	 * <blockquote>
	 * 
	 * <pre>
	 * public Foo(Bar bar, Baz baz) throws CipherException
	 * {
	 * 	this.bar = requirePresent(bar, "bar");
	 * 	this.baz = requirePresent(baz, "baz");
	 * }
	 * </pre>
	 * 
	 * </blockquote>
	 *
	 * @param  nullableRef
	 *                         the object reference to check for nullity
	 * @param  objectName
	 *                         detail message to be used in the event that a {@code
	 *                NullPointerException} is thrown
	 * @param  <T>
	 *                         the type of the reference
	 * @return                 {@code obj} if not {@code null}
	 * @throws CipherException
	 *                         with CipherExceptionType.ARGUMENT_ABSENT if {@code obj} is {@code null}
	 */
	@SuppressWarnings("IfCanBeAssertion")
	protected static <T> T requirePresent(final T nullableRef, final String objectName) throws CipherException
	{
		if (nullableRef == null)
			throw new CipherException(CipherExceptionType.ABSENT_ARGUMENT, objectName);
		return nullableRef;
	}
}
