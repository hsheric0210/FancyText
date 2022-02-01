package fancytext.gui.encrypt;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import fancytext.Main;
import fancytext.gui.Hasher;
import fancytext.hash.HashAlgorithm;
import fancytext.hash.digest.SpiBasedDigest;
import fancytext.utils.MultiThreading;
import fancytext.utils.PlainDocumentWithLimit;
import fancytext.utils.encoding.Encoding;

/**
 * <p>
 * TODO
 *
 * <ol>
 * <li>전체적으로 갈아엎기 (SymmetricKeyCipher처럼)</li>
 * <li>Asymmetric cipher는 알고리즘마다 요구하는 키나 매개변수 종류가 상이하게 다르니, 모든 알고리즘에서 공통적으로 사용되는 기본적인 정보 입력을 위한 패널들(Encrypt/Decrypt 버튼, Plain-text 패널, Cipher-text 패널, Cipher 알고리즘 선택 패널 등)만 유지하고,
 *
 * 나머지 키나 IV 등을 요구하는 패널들은 따로 개별 패널로 빼서 알고리즘에 따라 돌려쓸 수 있도록 만들기 (예시: RSACipherPanel, ElGamalCipherPanel 등)</li>
 * </ol>
 * </p>
 *
 */
public final class PublicKeyCipher extends JPanel
{
	private static final long serialVersionUID = -7257463203150608739L;
	private final JTextArea plainTextField;
	private final JTextArea encryptedTextField;
	private final JComboBox<Encoding> plainTextCharsetCB;
	private final JComboBox<Integer> keySizeCB;
	private final JCheckBox base64EncryptedText;
	private final JTextField publicKeyExponentField;
	private final JTextField privateKeyExponentField;
	private final JTextField publicKeyEncodedField;
	private final JTextField privateKeyEncodedField;
	private final JTextField keyModulusField;
	private final JToggleButton encryptWithPublicToggle;
	private final JComboBox<CipherAlgorithmPadding> cipherAlgorithmPaddingCB;
	private final JComboBox<HashAlgorithm> cipherAlgorithmPaddingOAEPHashAlgorithmCB;
	private final JTextField plainFileField;
	private final JTextField encryptedFileField;
	private final JRadioButton encryptedFromToFileButton;
	private final JRadioButton encryptedFromToTextButton;
	private final JRadioButton plainFromToTextButton;
	private final JRadioButton plainFromToFileButton;
	private final JComboBox<Algorithm> cipherAlgorithmCB;
	private final JComboBox<Integer> digestSizeCB;
	private final JComboBox<Integer> stateSizeCB;

	private enum Algorithm
	{
		RSA("RSA", "RSA"),
		ElGamal("ElGamal", "ElGamal"),
		DH_IES("IES", "IES(Diffie-Hellman)"),
		EC_IES("ECIES", "IES(Elliptic Curve)"),
		GM("SM2", "GM (SM2)");

		private final String id;
		private final String displayName;

		Algorithm(final String id, final String displayName)
		{
			this.id = id;
			this.displayName = displayName;
		}

		String getId()
		{
			return id;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	private enum ExceptionType
	{
		MALFORMED_MODULUS("Malformed modulus number"),
		MALFORMED_PUBLIC_EXPONENT("Malformed public exponent number"),
		MALFORMED_PRIVATE_EXPONENT("Malformed private exponent number"),
		INVALID_PUBLIC("Unable to generate public key from the modulus and the public exponent"),
		INVALID_PRIVATE("Unable to generate private key from the modulus and the private exponent"),
		UNSUPPORTED_CIPHER_ALGORITHM("Unsupported cipher algorithm"),
		UNSUPPORTED_KEYFACTORY_ALGORITHM("Unsupported cipher algorithm"),
		CORRUPTED_PUBLIC_KEY("Corrupted or unsupported public key"),
		CORRUPTED_PRIVATE_KEY("Corrupted or unsupported private key"),
		BASE64_DECODE_EXCEPTION("Corrupted Base64 byte array"),
		INVALID_PUBLIC_KEYSPEC("Invalid or corrupted X.509 encoded keyspec"),
		INVALID_PRIVATE_KEYSPEC("Invalid or corrupted PKCS #8 encoded keyspec");

		final String description;

		ExceptionType(final String description)
		{
			this.description = description;
		}
	}

	@SuppressWarnings("WeakerAccess")
	private static class PublicKeyCryptionException extends GeneralSecurityException
	{
		private final ExceptionType type;
		private final String algorithm;
		private final String theProblem;
		private final Throwable cause;

		PublicKeyCryptionException(final ExceptionType type, final Throwable cause)
		{
			this.type = type;
			this.cause = cause;
			algorithm = null;
			theProblem = null;

		}

		PublicKeyCryptionException(final ExceptionType type, final String algorithm, final Throwable cause)
		{
			this.type = type;
			this.algorithm = algorithm;
			this.cause = cause;
			theProblem = null;
		}

		PublicKeyCryptionException(final ExceptionType type, final String algorithm, final String problem, final Throwable cause)
		{
			this.type = type;
			this.algorithm = algorithm;
			theProblem = problem;
			this.cause = cause;
		}

		@Override
		public final String toString()
		{
			final StringBuilder builder = new StringBuilder(64);
			builder.append(getClass().getName()).append(":").append(Main.lineSeparator);

			builder.append("* ERROR CODE ").append(type.ordinal()).append("(").append(type.name()).append(") - ").append(type.description).append(Main.lineSeparator);
			builder.append("* Caused by ").append(cause).append(Main.lineSeparator);

			Optional.ofNullable(algorithm).ifPresent(str -> builder.append(str).append(Main.lineSeparator));

			Optional.ofNullable(theProblem).ifPresent(str -> builder.append(str).append(Main.lineSeparator));

			builder.append(Main.lineSeparator);

			return builder.toString();
		}
	}

	private enum CipherAlgorithmPadding
	{
		NONE("NoPadding", "None"),
		PKCS1("PKCS1Padding", "PKCS #1 padding"),
		OAEP("OAEPPadding", "Optimal-asymmetric-encryption(OAEP) padding");

		private final String paddingName;
		private final String displayName;

		CipherAlgorithmPadding(final String paddingName, final String displayName)
		{
			this.paddingName = paddingName;
			this.displayName = displayName;
		}

		String getPaddingName()
		{
			return paddingName;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	public PublicKeyCipher()
	{
		// Main border setup
		setBorder(new TitledBorder(null, "Public-key(Asymmetric-key) algorithm cipher", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		setSize(1000, 1000); // TODO: For Debug. Remove it later

		// Main layout setup
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{
				0, 0
		};
		gridBagLayout.rowHeights = new int[]
		{
				0, 0, 0, 0
		};
		gridBagLayout.columnWeights = new double[]
		{
				0.0, 1.0
		};
		gridBagLayout.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, 0.0
		};
		setLayout(gridBagLayout);

		final JPanel plainPanel = new JPanel();
		plainPanel.setBorder(new TitledBorder(null, "Plain(Decrypted) message", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_plainPanel = new GridBagConstraints();
		gbc_plainPanel.gridwidth = 3;
		gbc_plainPanel.insets = new Insets(0, 0, 5, 0);
		gbc_plainPanel.fill = GridBagConstraints.BOTH;
		gbc_plainPanel.gridx = 0;
		gbc_plainPanel.gridy = 0;
		add(plainPanel, gbc_plainPanel);
		final GridBagLayout gbl_plainPanel = new GridBagLayout();
		gbl_plainPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_plainPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_plainPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_plainPanel.rowWeights = new double[]
		{
				0.0, 0.0, Double.MIN_VALUE
		};
		plainPanel.setLayout(gbl_plainPanel);

		// Plain-text/Decrypted-text field panel
		final JPanel plainTextFieldPanel = new JPanel();
		final GridBagConstraints gbc_plainTextFieldPanel = new GridBagConstraints();
		gbc_plainTextFieldPanel.insets = new Insets(0, 0, 5, 0);
		gbc_plainTextFieldPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_plainTextFieldPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_plainTextFieldPanel.gridx = 0;
		gbc_plainTextFieldPanel.gridy = 0;
		plainPanel.add(plainTextFieldPanel, gbc_plainTextFieldPanel);
		plainTextFieldPanel.setBorder(new TitledBorder(null, "Plain-text (Decrypted-text)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_plainTextFieldPanel = new GridBagLayout();
		gbl_plainTextFieldPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_plainTextFieldPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_plainTextFieldPanel.columnWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		gbl_plainTextFieldPanel.rowWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		plainTextFieldPanel.setLayout(gbl_plainTextFieldPanel);

		// Plain-text/Decrypted-text field panel - Charset panel
		final JPanel plainTextCharsetPanel = new JPanel();
		final GridBagConstraints gbc_plainTextCharsetPanel = new GridBagConstraints();
		gbc_plainTextCharsetPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_plainTextCharsetPanel.insets = new Insets(0, 0, 5, 0);
		gbc_plainTextCharsetPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_plainTextCharsetPanel.gridx = 1;
		gbc_plainTextCharsetPanel.gridy = 0;
		plainTextFieldPanel.add(plainTextCharsetPanel, gbc_plainTextCharsetPanel);
		plainTextCharsetPanel.setBorder(new TitledBorder(null, "Charset", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_plainTextCharsetPanel = new GridBagLayout();
		gbl_plainTextCharsetPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_plainTextCharsetPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_plainTextCharsetPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_plainTextCharsetPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		plainTextCharsetPanel.setLayout(gbl_plainTextCharsetPanel);

		// Plain-text/Decrypted-text field panel - Charset panel - Charset combo box
		plainTextCharsetCB = new JComboBox<>();
		final GridBagConstraints gbc_plainTextCharsetCB = new GridBagConstraints();
		gbc_plainTextCharsetCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_plainTextCharsetCB.gridx = 0;
		gbc_plainTextCharsetCB.gridy = 0;
		plainTextCharsetPanel.add(plainTextCharsetCB, gbc_plainTextCharsetCB);

		plainFromToTextButton = new JRadioButton("Input the message from the text field / Output the decrypted-message to the text field");
		final GridBagConstraints gbc_plainFromToTextButton = new GridBagConstraints();
		gbc_plainFromToTextButton.gridheight = 2;
		gbc_plainFromToTextButton.insets = new Insets(0, 0, 0, 5);
		gbc_plainFromToTextButton.gridx = 0;
		gbc_plainFromToTextButton.gridy = 0;
		plainTextFieldPanel.add(plainFromToTextButton, gbc_plainFromToTextButton);

		// Plain-text/Decrypted-text field panel - Plain-text/Decrypted-text field scroll pane
		final JScrollPane encryptPlainTextFieldScrollPane = new JScrollPane();
		final GridBagConstraints gbc_plainTextFieldScrollPane = new GridBagConstraints();
		gbc_plainTextFieldScrollPane.ipady = 40;
		gbc_plainTextFieldScrollPane.fill = GridBagConstraints.BOTH;
		gbc_plainTextFieldScrollPane.gridx = 1;
		gbc_plainTextFieldScrollPane.gridy = 1;
		plainTextFieldPanel.add(encryptPlainTextFieldScrollPane, gbc_plainTextFieldScrollPane);

		// Plain-text/Decrypted-text field panel - Plain-text/Decrypted-text field scroll pane - Plain-text/Decrypted-text field
		plainTextField = new JTextArea();
		encryptPlainTextFieldScrollPane.setViewportView(plainTextField);
		plainTextField.setDocument(new PlainDocumentWithLimit());

		final JPanel plainFilePanel = new JPanel();
		plainFilePanel.setBorder(new TitledBorder(null, "Plain-file (Decrypted-file)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_plainFilePanel = new GridBagConstraints();
		gbc_plainFilePanel.fill = GridBagConstraints.BOTH;
		gbc_plainFilePanel.gridx = 0;
		gbc_plainFilePanel.gridy = 1;
		plainPanel.add(plainFilePanel, gbc_plainFilePanel);
		final GridBagLayout gbl_plainFilePanel = new GridBagLayout();
		gbl_plainFilePanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_plainFilePanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_plainFilePanel.columnWeights = new double[]
		{
				0.0, 1.0, 0.0, Double.MIN_VALUE
		};
		gbl_plainFilePanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		plainFilePanel.setLayout(gbl_plainFilePanel);

		plainFromToFileButton = new JRadioButton("Input the message from the file / Output the decrypted-message to the file");
		final GridBagConstraints gbc_plainFromToFileButton = new GridBagConstraints();
		gbc_plainFromToFileButton.insets = new Insets(0, 0, 0, 5);
		gbc_plainFromToFileButton.gridx = 0;
		gbc_plainFromToFileButton.gridy = 0;
		plainFilePanel.add(plainFromToFileButton, gbc_plainFromToFileButton);

		plainFileField = new JTextField();
		plainFileField.setEnabled(false);
		plainFileField.setColumns(10);
		final GridBagConstraints gbc_plainFileField = new GridBagConstraints();
		gbc_plainFileField.fill = GridBagConstraints.HORIZONTAL;
		gbc_plainFileField.insets = new Insets(0, 0, 0, 5);
		gbc_plainFileField.gridx = 1;
		gbc_plainFileField.gridy = 0;
		plainFilePanel.add(plainFileField, gbc_plainFileField);

		final JButton plainFileFindButton = new JButton("Find");
		plainFileFindButton.setEnabled(false);
		final GridBagConstraints gbc_plainFileFindButton = new GridBagConstraints();
		gbc_plainFileFindButton.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_plainFileFindButton.gridx = 2;
		gbc_plainFileFindButton.gridy = 0;
		plainFilePanel.add(plainFileFindButton, gbc_plainFileFindButton);

		// Cipher settings panel
		final JPanel cipherSettingsPanel = new JPanel();
		cipherSettingsPanel.setBorder(new TitledBorder(null, "Cipher settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_cipherSettingsPanel = new GridBagConstraints();
		gbc_cipherSettingsPanel.gridwidth = 3;
		gbc_cipherSettingsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_cipherSettingsPanel.fill = GridBagConstraints.BOTH;
		gbc_cipherSettingsPanel.gridx = 0;
		gbc_cipherSettingsPanel.gridy = 1;
		add(cipherSettingsPanel, gbc_cipherSettingsPanel);
		final GridBagLayout gbl_cipherSettingsPanel = new GridBagLayout();
		gbl_cipherSettingsPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_cipherSettingsPanel.rowHeights = new int[]
		{
				0, 0, 0, 0, 0
		};
		gbl_cipherSettingsPanel.columnWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		gbl_cipherSettingsPanel.rowWeights = new double[]
		{
				1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE
		};
		cipherSettingsPanel.setLayout(gbl_cipherSettingsPanel);

		// Key panel
		final JPanel keyPanel = new JPanel();
		keyPanel.setBorder(new TitledBorder(null, "Key", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_keyPanel = new GridBagConstraints();
		gbc_keyPanel.insets = new Insets(0, 0, 5, 5);
		gbc_keyPanel.fill = GridBagConstraints.BOTH;
		gbc_keyPanel.gridx = 1;
		gbc_keyPanel.gridy = 2;
		add(keyPanel, gbc_keyPanel);
		final GridBagLayout gbl_keyPanel = new GridBagLayout();
		gbl_keyPanel.columnWidths = new int[]
		{
				0
		};
		gbl_keyPanel.rowHeights = new int[]
		{
				0, 0, 0, 0
		};
		gbl_keyPanel.columnWeights = new double[]
		{
				1.0
		};
		gbl_keyPanel.rowWeights = new double[]
		{
				1.0, 0.0, 0.0, 0.0
		};
		keyPanel.setLayout(gbl_keyPanel);

		// Encrypt button
		final JButton encryptButton = new JButton("Encrypt");
		final GridBagConstraints gbc_encryptButton = new GridBagConstraints();
		gbc_encryptButton.insets = new Insets(0, 0, 5, 5);
		gbc_encryptButton.gridx = 0;
		gbc_encryptButton.gridy = 2;
		add(encryptButton, gbc_encryptButton);

		// Decrypt button
		final JButton decryptButton = new JButton("Decrypt");
		final GridBagConstraints gbc_decryptButton = new GridBagConstraints();
		gbc_decryptButton.insets = new Insets(0, 0, 5, 0);
		gbc_decryptButton.gridx = 2;
		gbc_decryptButton.gridy = 2;
		add(decryptButton, gbc_decryptButton);

		// Key panel - modulus panel
		final JPanel keyModulusPanel = new JPanel();
		keyModulusPanel.setBorder(new TitledBorder(null, "Modulus (n)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_keyModulusPanel = new GridBagConstraints();
		gbc_keyModulusPanel.insets = new Insets(0, 0, 5, 0);
		gbc_keyModulusPanel.fill = GridBagConstraints.BOTH;
		gbc_keyModulusPanel.gridx = 0;
		gbc_keyModulusPanel.gridy = 0;
		keyPanel.add(keyModulusPanel, gbc_keyModulusPanel);
		final GridBagLayout gbl_keyModulusPanel = new GridBagLayout();
		gbl_keyModulusPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_keyModulusPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_keyModulusPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_keyModulusPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		keyModulusPanel.setLayout(gbl_keyModulusPanel);

		// Key panel - Modulus panel - Modulus field
		keyModulusField = new JTextField();
		final GridBagConstraints gbc_keyModulusField = new GridBagConstraints();
		gbc_keyModulusField.fill = GridBagConstraints.HORIZONTAL;
		gbc_keyModulusField.gridx = 0;
		gbc_keyModulusField.gridy = 0;
		keyModulusPanel.add(keyModulusField, gbc_keyModulusField);
		keyModulusField.setColumns(10);

		// Public key panel
		final JPanel publicKeyPanel = new JPanel();
		publicKeyPanel.setBorder(new TitledBorder(null, "Public key (e)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_publicKeyPanel = new GridBagConstraints();
		gbc_publicKeyPanel.insets = new Insets(0, 0, 5, 0);
		gbc_publicKeyPanel.fill = GridBagConstraints.BOTH;
		gbc_publicKeyPanel.gridx = 0;
		gbc_publicKeyPanel.gridy = 1;
		keyPanel.add(publicKeyPanel, gbc_publicKeyPanel);
		final GridBagLayout gbl_publicKeyPanel = new GridBagLayout();
		gbl_publicKeyPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_publicKeyPanel.rowHeights = new int[]
		{
				0, 0, 0, 0
		};
		gbl_publicKeyPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_publicKeyPanel.rowWeights = new double[]
		{
				0.0, 1.0, 1.0, Double.MIN_VALUE
		};
		publicKeyPanel.setLayout(gbl_publicKeyPanel);

		// Public key panel - Public key exponent panel
		final JPanel publicKeyExponentPanel = new JPanel();
		publicKeyExponentPanel.setBorder(new TitledBorder(null, "Exponent", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_publicKeyExponentPanel = new GridBagConstraints();
		gbc_publicKeyExponentPanel.insets = new Insets(0, 0, 15, 0);
		gbc_publicKeyExponentPanel.fill = GridBagConstraints.BOTH;
		gbc_publicKeyExponentPanel.gridx = 0;
		gbc_publicKeyExponentPanel.gridy = 0;
		publicKeyPanel.add(publicKeyExponentPanel, gbc_publicKeyExponentPanel);
		final GridBagLayout gbl_publicKeyExponentPanel = new GridBagLayout();
		gbl_publicKeyExponentPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_publicKeyExponentPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_publicKeyExponentPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_publicKeyExponentPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		publicKeyExponentPanel.setLayout(gbl_publicKeyExponentPanel);

		// Public key panel - Public key exponent panel - Public key exponent field
		publicKeyExponentField = new JTextField();
		final GridBagConstraints gbc_publicKeyExponentField = new GridBagConstraints();
		gbc_publicKeyExponentField.fill = GridBagConstraints.HORIZONTAL;
		gbc_publicKeyExponentField.gridx = 0;
		gbc_publicKeyExponentField.gridy = 0;
		publicKeyExponentPanel.add(publicKeyExponentField, gbc_publicKeyExponentField);
		publicKeyExponentField.setColumns(10);

		// Public key panel - Public key import panel
		final JPanel publicKeyImportExportPanel = new JPanel();
		publicKeyImportExportPanel.setBorder(new TitledBorder(null, "Import/Export public key from X.509 encoded keyspec", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_publicKeyImportExportPanel = new GridBagConstraints();
		gbc_publicKeyImportExportPanel.insets = new Insets(0, 0, 5, 0);
		gbc_publicKeyImportExportPanel.fill = GridBagConstraints.BOTH;
		gbc_publicKeyImportExportPanel.gridx = 0;
		gbc_publicKeyImportExportPanel.gridy = 1;
		publicKeyPanel.add(publicKeyImportExportPanel, gbc_publicKeyImportExportPanel);
		final GridBagLayout gbl_publicKeyImportExportPanel = new GridBagLayout();
		gbl_publicKeyImportExportPanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_publicKeyImportExportPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_publicKeyImportExportPanel.columnWeights = new double[]
		{
				0.0, 1.0, 0.0, Double.MIN_VALUE
		};
		gbl_publicKeyImportExportPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		publicKeyImportExportPanel.setLayout(gbl_publicKeyImportExportPanel);

		// Public key panel - Public key export panel - X.509 encoded keyspec public key export button
		final JButton publicKeyExportButton = new JButton("Export");
		final GridBagConstraints gbc_publicKeyExportButton = new GridBagConstraints();
		gbc_publicKeyExportButton.insets = new Insets(0, 0, 0, 5);
		gbc_publicKeyExportButton.gridx = 0;
		gbc_publicKeyExportButton.gridy = 0;
		publicKeyImportExportPanel.add(publicKeyExportButton, gbc_publicKeyExportButton);

		// Public key panel - Public key import panel - X.509 encoded keyspec public key import field
		publicKeyEncodedField = new JTextField();
		final GridBagConstraints gbc_publicKeyEncodedField = new GridBagConstraints();
		gbc_publicKeyEncodedField.insets = new Insets(0, 0, 0, 5);
		gbc_publicKeyEncodedField.fill = GridBagConstraints.HORIZONTAL;
		gbc_publicKeyEncodedField.gridx = 1;
		gbc_publicKeyEncodedField.gridy = 0;
		publicKeyImportExportPanel.add(publicKeyEncodedField, gbc_publicKeyEncodedField);
		publicKeyEncodedField.setColumns(10);

		// Public key panel - Public key import panel - X.509 encoded keyspec public key import button
		final JButton publicKeyImportButton = new JButton("Import");
		final GridBagConstraints gbc_publicKeyImportButton = new GridBagConstraints();
		gbc_publicKeyImportButton.gridx = 2;
		gbc_publicKeyImportButton.gridy = 0;
		publicKeyImportExportPanel.add(publicKeyImportButton, gbc_publicKeyImportButton);

		// Private key panel
		final JPanel privateKeyPanel = new JPanel();
		privateKeyPanel.setBorder(new TitledBorder(null, "Private key (d)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_privateKeyPanel = new GridBagConstraints();
		gbc_privateKeyPanel.insets = new Insets(0, 0, 5, 0);
		gbc_privateKeyPanel.fill = GridBagConstraints.BOTH;
		gbc_privateKeyPanel.gridx = 0;
		gbc_privateKeyPanel.gridy = 2;
		keyPanel.add(privateKeyPanel, gbc_privateKeyPanel);
		final GridBagLayout gbl_privateKeyPanel = new GridBagLayout();
		gbl_privateKeyPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_privateKeyPanel.rowHeights = new int[]
		{
				0, 0, 0, 0
		};
		gbl_privateKeyPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_privateKeyPanel.rowWeights = new double[]
		{
				0.0, 1.0, 1.0, Double.MIN_VALUE
		};
		privateKeyPanel.setLayout(gbl_privateKeyPanel);

		// Private key panel - Private key exponent panel
		final JPanel privateKeyExponentPanel = new JPanel();
		privateKeyExponentPanel.setBorder(new TitledBorder(null, "Exponent", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_privateKeyExponentPanel = new GridBagConstraints();
		gbc_privateKeyExponentPanel.insets = new Insets(0, 0, 15, 0);
		gbc_privateKeyExponentPanel.fill = GridBagConstraints.BOTH;
		gbc_privateKeyExponentPanel.gridx = 0;
		gbc_privateKeyExponentPanel.gridy = 0;
		privateKeyPanel.add(privateKeyExponentPanel, gbc_privateKeyExponentPanel);
		final GridBagLayout gbl_privateKeyExponentPanel = new GridBagLayout();
		gbl_privateKeyExponentPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_privateKeyExponentPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_privateKeyExponentPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_privateKeyExponentPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		privateKeyExponentPanel.setLayout(gbl_privateKeyExponentPanel);

		// Private key panel - Private key exponent panel - Private key exponent field
		privateKeyExponentField = new JTextField();
		privateKeyExponentField.setColumns(10);
		final GridBagConstraints gbc_privateKeyExponentField = new GridBagConstraints();
		gbc_privateKeyExponentField.fill = GridBagConstraints.HORIZONTAL;
		gbc_privateKeyExponentField.gridx = 0;
		gbc_privateKeyExponentField.gridy = 0;
		privateKeyExponentPanel.add(privateKeyExponentField, gbc_privateKeyExponentField);

		// Private key panel - Private key import panel
		final JPanel privateKeyImportExportPanel = new JPanel();
		privateKeyImportExportPanel.setBorder(new TitledBorder(null, "Import/Export private key from PKCS #8 encoded keyspec", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_privateKeyImportExportPanel = new GridBagConstraints();
		gbc_privateKeyImportExportPanel.insets = new Insets(0, 0, 5, 0);
		gbc_privateKeyImportExportPanel.fill = GridBagConstraints.BOTH;
		gbc_privateKeyImportExportPanel.gridx = 0;
		gbc_privateKeyImportExportPanel.gridy = 1;
		privateKeyPanel.add(privateKeyImportExportPanel, gbc_privateKeyImportExportPanel);
		final GridBagLayout gbl_privateKeyImportExportPanel = new GridBagLayout();
		gbl_privateKeyImportExportPanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_privateKeyImportExportPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_privateKeyImportExportPanel.columnWeights = new double[]
		{
				0.0, 1.0, 0.0, Double.MIN_VALUE
		};
		gbl_privateKeyImportExportPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		privateKeyImportExportPanel.setLayout(gbl_privateKeyImportExportPanel);

		// Private key panel - Private key export panel - PKCS #8 encoded keyspec private key export button
		final JButton privateKeyExportButton = new JButton("Export");
		final GridBagConstraints gbc_privateKeyExportButton = new GridBagConstraints();
		gbc_privateKeyExportButton.insets = new Insets(0, 0, 0, 5);
		gbc_privateKeyExportButton.gridx = 0;
		gbc_privateKeyExportButton.gridy = 0;
		privateKeyImportExportPanel.add(privateKeyExportButton, gbc_privateKeyExportButton);

		// Private key panel - Private key import panel - PKCS #8 encoded keyspec private key import field
		privateKeyEncodedField = new JTextField();
		final GridBagConstraints gbc_privateKeyEncodedField = new GridBagConstraints();
		gbc_privateKeyEncodedField.insets = new Insets(0, 0, 0, 5);
		gbc_privateKeyEncodedField.fill = GridBagConstraints.HORIZONTAL;
		gbc_privateKeyEncodedField.gridx = 1;
		gbc_privateKeyEncodedField.gridy = 0;
		privateKeyImportExportPanel.add(privateKeyEncodedField, gbc_privateKeyEncodedField);
		privateKeyEncodedField.setColumns(10);

		// Private key panel - Private key import panel - PKCS #8 encoded keyspec private key import button
		final JButton privateKeyImportButton = new JButton("Import");
		final GridBagConstraints gbc_privateKeyImportButton = new GridBagConstraints();
		gbc_privateKeyImportButton.gridx = 2;
		gbc_privateKeyImportButton.gridy = 0;
		privateKeyImportExportPanel.add(privateKeyImportButton, gbc_privateKeyImportButton);

		// Generate key button
		final JButton generateKeyButton = new JButton("Generate the key");
		final GridBagConstraints gbc_generateKeyButton = new GridBagConstraints();
		gbc_generateKeyButton.gridx = 0;
		gbc_generateKeyButton.gridy = 3;
		keyPanel.add(generateKeyButton, gbc_generateKeyButton);

		final JPanel encryptedPanel = new JPanel();
		encryptedPanel.setBorder(new TitledBorder(null, "Encrypted message", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_encryptedPanel = new GridBagConstraints();
		gbc_encryptedPanel.gridwidth = 3;
		gbc_encryptedPanel.fill = GridBagConstraints.BOTH;
		gbc_encryptedPanel.gridx = 0;
		gbc_encryptedPanel.gridy = 3;
		add(encryptedPanel, gbc_encryptedPanel);
		final GridBagLayout gbl_encryptedPanel = new GridBagLayout();
		gbl_encryptedPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_encryptedPanel.rowHeights = new int[]
		{
				0, 0, 0, 0
		};
		gbl_encryptedPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_encryptedPanel.rowWeights = new double[]
		{
				0.0, 0.0, 1.0, Double.MIN_VALUE
		};
		encryptedPanel.setLayout(gbl_encryptedPanel);

		// Base64-encode encrypted-text
		base64EncryptedText = new JCheckBox("Base64 encode/decode Encrypted-text");
		final GridBagConstraints gbc_base64EncryptedText = new GridBagConstraints();
		gbc_base64EncryptedText.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_base64EncryptedText.insets = new Insets(0, 0, 5, 0);
		gbc_base64EncryptedText.gridx = 0;
		gbc_base64EncryptedText.gridy = 0;
		encryptedPanel.add(base64EncryptedText, gbc_base64EncryptedText);

		// Encrypted-text field panel
		final JPanel encryptedTextFieldPanel = new JPanel();
		final GridBagConstraints gbc_encryptedTextFieldPanel = new GridBagConstraints();
		gbc_encryptedTextFieldPanel.insets = new Insets(0, 0, 5, 0);
		gbc_encryptedTextFieldPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_encryptedTextFieldPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_encryptedTextFieldPanel.gridx = 0;
		gbc_encryptedTextFieldPanel.gridy = 1;
		encryptedPanel.add(encryptedTextFieldPanel, gbc_encryptedTextFieldPanel);
		encryptedTextFieldPanel.setBorder(new TitledBorder(null, "Encrypted-text", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_encryptedTextFieldPanel = new GridBagLayout();
		gbl_encryptedTextFieldPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_encryptedTextFieldPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_encryptedTextFieldPanel.columnWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		gbl_encryptedTextFieldPanel.rowWeights = new double[]
		{
				0.0, 0.0, Double.MIN_VALUE
		};
		encryptedTextFieldPanel.setLayout(gbl_encryptedTextFieldPanel);

		encryptedFromToTextButton = new JRadioButton("Input/Output the encrypted-message from/to the text field");
		final GridBagConstraints gbc_encryptedFromToTextButton = new GridBagConstraints();
		gbc_encryptedFromToTextButton.gridheight = 2;
		gbc_encryptedFromToTextButton.insets = new Insets(0, 0, 5, 5);
		gbc_encryptedFromToTextButton.gridx = 0;
		gbc_encryptedFromToTextButton.gridy = 0;
		encryptedTextFieldPanel.add(encryptedFromToTextButton, gbc_encryptedFromToTextButton);

		// Encrypted-text field panel - Encrypted-text field scroll pane
		final JScrollPane encryptedTextFieldScrollPane = new JScrollPane();
		final GridBagConstraints gbc_encryptedTextFieldScrollPane = new GridBagConstraints();
		gbc_encryptedTextFieldScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_encryptedTextFieldScrollPane.ipady = 40;
		gbc_encryptedTextFieldScrollPane.fill = GridBagConstraints.BOTH;
		gbc_encryptedTextFieldScrollPane.gridx = 1;
		gbc_encryptedTextFieldScrollPane.gridy = 0;
		encryptedTextFieldPanel.add(encryptedTextFieldScrollPane, gbc_encryptedTextFieldScrollPane);

		// Encrypted-text field panel - Encrypted-text field scroll pane - Encrypted-text field
		encryptedTextField = new JTextArea();
		encryptedTextFieldScrollPane.setViewportView(encryptedTextField);

		// Base64-encode encrypted-text - note
		final JLabel base64EncryptedTextNote = new JLabel("Note that the charset of RAW encrypted-text is always ISO-8859-1 (a.k.a. ISO-LATIN-1)");
		base64EncryptedTextNote.setEnabled(false);
		base64EncryptedTextNote.setVisible(false);
		base64EncryptedTextNote.setToolTipText("Because other charsets (UTF-8, UTF-16, etc.) are non-compatible with the RAW encrypted-text.");
		final GridBagConstraints gbc_base64EncryptedTextNote = new GridBagConstraints();
		gbc_base64EncryptedTextNote.anchor = GridBagConstraints.PAGE_START;
		gbc_base64EncryptedTextNote.gridx = 1;
		gbc_base64EncryptedTextNote.gridy = 1;
		encryptedTextFieldPanel.add(base64EncryptedTextNote, gbc_base64EncryptedTextNote);

		final JPanel cipherAlgorithmPanel = new JPanel();
		cipherAlgorithmPanel.setBorder(new TitledBorder(null, "Cipher algorithm", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_cipherAlgorithmPanel = new GridBagConstraints();
		gbc_cipherAlgorithmPanel.gridwidth = 2;
		gbc_cipherAlgorithmPanel.insets = new Insets(0, 0, 5, 5);
		gbc_cipherAlgorithmPanel.fill = GridBagConstraints.BOTH;
		gbc_cipherAlgorithmPanel.gridx = 0;
		gbc_cipherAlgorithmPanel.gridy = 0;
		cipherSettingsPanel.add(cipherAlgorithmPanel, gbc_cipherAlgorithmPanel);
		final GridBagLayout gbl_cipherAlgorithmPanel = new GridBagLayout();
		gbl_cipherAlgorithmPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_cipherAlgorithmPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_cipherAlgorithmPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_cipherAlgorithmPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		cipherAlgorithmPanel.setLayout(gbl_cipherAlgorithmPanel);

		cipherAlgorithmCB = new JComboBox<>();
		final GridBagConstraints gbc_cipherAlgorithmCB = new GridBagConstraints();
		gbc_cipherAlgorithmCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherAlgorithmCB.gridx = 0;
		gbc_cipherAlgorithmCB.gridy = 0;
		cipherAlgorithmPanel.add(cipherAlgorithmCB, gbc_cipherAlgorithmCB);

		// Cipher settings panel - Key size panel
		final JPanel keySizeCBPanel = new JPanel();
		keySizeCBPanel.setBorder(new TitledBorder(null, "Key size", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_keySizeCBPanel = new GridBagConstraints();
		gbc_keySizeCBPanel.gridwidth = 2;
		gbc_keySizeCBPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_keySizeCBPanel.insets = new Insets(0, 0, 5, 0);
		gbc_keySizeCBPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_keySizeCBPanel.gridx = 0;
		gbc_keySizeCBPanel.gridy = 1;
		cipherSettingsPanel.add(keySizeCBPanel, gbc_keySizeCBPanel);
		final GridBagLayout gbl_keySizeCBPanel = new GridBagLayout();
		gbl_keySizeCBPanel.columnWidths = new int[]
		{
				0, 0, 0, 0, 0
		};
		gbl_keySizeCBPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_keySizeCBPanel.columnWeights = new double[]
		{
				0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE
		};
		gbl_keySizeCBPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		keySizeCBPanel.setLayout(gbl_keySizeCBPanel);

		// Cipher settings panel - Key size panel - Key size combo box
		keySizeCB = new JComboBox<>();
		final GridBagConstraints gbc_keySizeCB = new GridBagConstraints();
		gbc_keySizeCB.insets = new Insets(0, 0, 0, 5);
		gbc_keySizeCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_keySizeCB.gridx = 0;
		gbc_keySizeCB.gridy = 0;
		keySizeCBPanel.add(keySizeCB, gbc_keySizeCB);

		final JSlider keySizeSlider = new JSlider();
		keySizeSlider.setMinorTickSpacing(128);
		keySizeSlider.setSnapToTicks(true);
		keySizeSlider.setPaintLabels(true);
		keySizeSlider.setMajorTickSpacing(512);
		keySizeSlider.setPaintTicks(true);
		keySizeSlider.setMinimum(512);
		keySizeSlider.setMaximum(8192);
		final GridBagConstraints gbc_keySizeSlider = new GridBagConstraints();
		gbc_keySizeSlider.fill = GridBagConstraints.HORIZONTAL;
		gbc_keySizeSlider.insets = new Insets(0, 0, 0, 5);
		gbc_keySizeSlider.gridx = 1;
		gbc_keySizeSlider.gridy = 0;
		keySizeCBPanel.add(keySizeSlider, gbc_keySizeSlider);

		final JPanel cipherAlgorithmPaddingPanel = new JPanel();
		cipherAlgorithmPaddingPanel.setBorder(new TitledBorder(null, "Cipher algorithm padding", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_cipherAlgorithmPaddingPanel = new GridBagConstraints();
		gbc_cipherAlgorithmPaddingPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_cipherAlgorithmPaddingPanel.insets = new Insets(0, 0, 5, 5);
		gbc_cipherAlgorithmPaddingPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherAlgorithmPaddingPanel.gridx = 0;
		gbc_cipherAlgorithmPaddingPanel.gridy = 2;
		cipherSettingsPanel.add(cipherAlgorithmPaddingPanel, gbc_cipherAlgorithmPaddingPanel);
		final GridBagLayout gbl_cipherAlgorithmPaddingPanel = new GridBagLayout();
		gbl_cipherAlgorithmPaddingPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_cipherAlgorithmPaddingPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_cipherAlgorithmPaddingPanel.columnWeights = new double[]
		{
				1.0, 1.0, Double.MIN_VALUE
		};
		gbl_cipherAlgorithmPaddingPanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		cipherAlgorithmPaddingPanel.setLayout(gbl_cipherAlgorithmPaddingPanel);

		cipherAlgorithmPaddingCB = new JComboBox<>();
		final GridBagConstraints gbc_cipherAlgorithmPaddingCB = new GridBagConstraints();
		gbc_cipherAlgorithmPaddingCB.anchor = GridBagConstraints.PAGE_START;
		gbc_cipherAlgorithmPaddingCB.insets = new Insets(0, 0, 0, 5);
		gbc_cipherAlgorithmPaddingCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherAlgorithmPaddingCB.gridx = 0;
		gbc_cipherAlgorithmPaddingCB.gridy = 0;
		cipherAlgorithmPaddingPanel.add(cipherAlgorithmPaddingCB, gbc_cipherAlgorithmPaddingCB);

		encryptWithPublicToggle = new JToggleButton("Encrypt with public key, Decrypt with private key");
		final GridBagConstraints gbc_encryptWithPublicToggle = new GridBagConstraints();
		gbc_encryptWithPublicToggle.gridwidth = 2;
		gbc_encryptWithPublicToggle.gridx = 0;
		gbc_encryptWithPublicToggle.gridy = 3;
		cipherSettingsPanel.add(encryptWithPublicToggle, gbc_encryptWithPublicToggle);

		final JPanel cipherAlgorithmPaddingOAEPHashAlgorithmPanel = new JPanel();
		cipherAlgorithmPaddingOAEPHashAlgorithmPanel.setBorder(new TitledBorder(null, "OAEP mode message digest hash algorithm", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_cipherAlgorithmPaddingOAEPHashAlgorithmPanel = new GridBagConstraints();
		gbc_cipherAlgorithmPaddingOAEPHashAlgorithmPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_cipherAlgorithmPaddingOAEPHashAlgorithmPanel.ipadx = 260;
		gbc_cipherAlgorithmPaddingOAEPHashAlgorithmPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherAlgorithmPaddingOAEPHashAlgorithmPanel.gridx = 1;
		gbc_cipherAlgorithmPaddingOAEPHashAlgorithmPanel.gridy = 0;
		cipherAlgorithmPaddingPanel.add(cipherAlgorithmPaddingOAEPHashAlgorithmPanel, gbc_cipherAlgorithmPaddingOAEPHashAlgorithmPanel);
		final GridBagLayout gbl_cipherAlgorithmPaddingOAEPHashAlgorithmPanel = new GridBagLayout();
		gbl_cipherAlgorithmPaddingOAEPHashAlgorithmPanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_cipherAlgorithmPaddingOAEPHashAlgorithmPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_cipherAlgorithmPaddingOAEPHashAlgorithmPanel.columnWeights = new double[]
		{
				1.0, 0.0, 0.0, Double.MIN_VALUE
		};
		gbl_cipherAlgorithmPaddingOAEPHashAlgorithmPanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		cipherAlgorithmPaddingOAEPHashAlgorithmPanel.setLayout(gbl_cipherAlgorithmPaddingOAEPHashAlgorithmPanel);

		cipherAlgorithmPaddingOAEPHashAlgorithmCB = new JComboBox<>();
		final GridBagConstraints gbc_cipherAlgorithmPaddingOAEPHashAlgorithmCB = new GridBagConstraints();
		gbc_cipherAlgorithmPaddingOAEPHashAlgorithmCB.insets = new Insets(0, 0, 0, 5);
		gbc_cipherAlgorithmPaddingOAEPHashAlgorithmCB.anchor = GridBagConstraints.PAGE_START;
		gbc_cipherAlgorithmPaddingOAEPHashAlgorithmCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherAlgorithmPaddingOAEPHashAlgorithmCB.gridx = 0;
		gbc_cipherAlgorithmPaddingOAEPHashAlgorithmCB.gridy = 0;
		cipherAlgorithmPaddingOAEPHashAlgorithmPanel.add(cipherAlgorithmPaddingOAEPHashAlgorithmCB, gbc_cipherAlgorithmPaddingOAEPHashAlgorithmCB);

		final JPanel encryptedFilePanel = new JPanel();
		encryptedFilePanel.setBorder(new TitledBorder(null, "Encrypted-file", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_encryptedFilePanel = new GridBagConstraints();
		gbc_encryptedFilePanel.anchor = GridBagConstraints.PAGE_START;
		gbc_encryptedFilePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_encryptedFilePanel.gridx = 0;
		gbc_encryptedFilePanel.gridy = 2;
		encryptedPanel.add(encryptedFilePanel, gbc_encryptedFilePanel);
		final GridBagLayout gbl_encryptedFilePanel = new GridBagLayout();
		gbl_encryptedFilePanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_encryptedFilePanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_encryptedFilePanel.columnWeights = new double[]
		{
				0.0, 1.0, 0.0, Double.MIN_VALUE
		};
		gbl_encryptedFilePanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		encryptedFilePanel.setLayout(gbl_encryptedFilePanel);

		encryptedFromToFileButton = new JRadioButton("Input/Output the encrypted-message from/to the file");
		final GridBagConstraints gbc_encryptedFromToFileButton = new GridBagConstraints();
		gbc_encryptedFromToFileButton.insets = new Insets(0, 0, 0, 5);
		gbc_encryptedFromToFileButton.gridx = 0;
		gbc_encryptedFromToFileButton.gridy = 0;
		encryptedFilePanel.add(encryptedFromToFileButton, gbc_encryptedFromToFileButton);

		encryptedFileField = new JTextField();
		encryptedFileField.setEnabled(false);
		encryptedFileField.setColumns(10);
		final GridBagConstraints gbc_encryptedFileField = new GridBagConstraints();
		gbc_encryptedFileField.fill = GridBagConstraints.HORIZONTAL;
		gbc_encryptedFileField.insets = new Insets(0, 0, 0, 5);
		gbc_encryptedFileField.gridx = 1;
		gbc_encryptedFileField.gridy = 0;
		encryptedFilePanel.add(encryptedFileField, gbc_encryptedFileField);

		final JButton encryptedFileFindButton = new JButton("Find");
		encryptedFileFindButton.setEnabled(false);
		final GridBagConstraints gbc_encryptedFileFindButton = new GridBagConstraints();
		gbc_encryptedFileFindButton.gridx = 2;
		gbc_encryptedFileFindButton.gridy = 0;
		encryptedFilePanel.add(encryptedFileFindButton, gbc_encryptedFileFindButton);

		final JSpinner keySizeSpinner = new JSpinner();
		final GridBagConstraints gbc_keySizeSpinner = new GridBagConstraints();
		gbc_keySizeSpinner.ipadx = 20;
		gbc_keySizeSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_keySizeSpinner.insets = new Insets(0, 0, 0, 5);
		gbc_keySizeSpinner.gridx = 2;
		gbc_keySizeSpinner.gridy = 0;
		keySizeCBPanel.add(keySizeSpinner, gbc_keySizeSpinner);

		// Cipher settings panel - Key size panel - Key size label
		final JLabel keySizeLabel = new JLabel("bit");
		final GridBagConstraints gbc_keySizeLabel = new GridBagConstraints();
		gbc_keySizeLabel.gridx = 3;
		gbc_keySizeLabel.gridy = 0;
		keySizeCBPanel.add(keySizeLabel, gbc_keySizeLabel);

		final JPanel stateSizePanel = new JPanel();
		stateSizePanel.setVisible(false);
		stateSizePanel.setBorder(new TitledBorder(null, "Hash state size bits", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_stateSizePanel = new GridBagConstraints();
		gbc_stateSizePanel.ipadx = 100;
		gbc_stateSizePanel.anchor = GridBagConstraints.PAGE_START;
		gbc_stateSizePanel.insets = new Insets(0, 0, 0, 5);
		gbc_stateSizePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_stateSizePanel.gridx = 1;
		gbc_stateSizePanel.gridy = 0;
		cipherAlgorithmPaddingOAEPHashAlgorithmPanel.add(stateSizePanel, gbc_stateSizePanel);
		final GridBagLayout gbl_stateSizePanel = new GridBagLayout();
		gbl_stateSizePanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_stateSizePanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_stateSizePanel.columnWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		gbl_stateSizePanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		stateSizePanel.setLayout(gbl_stateSizePanel);

		stateSizeCB = new JComboBox<>();
		final GridBagConstraints gbc_stateSizeCB = new GridBagConstraints();
		gbc_stateSizeCB.insets = new Insets(0, 0, 0, 5);
		gbc_stateSizeCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_stateSizeCB.gridx = 0;
		gbc_stateSizeCB.gridy = 0;
		stateSizePanel.add(stateSizeCB, gbc_stateSizeCB);

		final JLabel stateSizeLabel = new JLabel("bit");
		final GridBagConstraints gbc_stateSizeLabel = new GridBagConstraints();
		gbc_stateSizeLabel.gridx = 1;
		gbc_stateSizeLabel.gridy = 0;
		stateSizePanel.add(stateSizeLabel, gbc_stateSizeLabel);

		final JPanel digestSizePanel = new JPanel();
		digestSizePanel.setEnabled(false);
		digestSizePanel.setBorder(new TitledBorder(null, "Hash digest size bits", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_digestSizePanel = new GridBagConstraints();
		gbc_digestSizePanel.anchor = GridBagConstraints.PAGE_START;
		gbc_digestSizePanel.ipadx = 100;
		gbc_digestSizePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_digestSizePanel.gridx = 2;
		gbc_digestSizePanel.gridy = 0;
		cipherAlgorithmPaddingOAEPHashAlgorithmPanel.add(digestSizePanel, gbc_digestSizePanel);
		final GridBagLayout gbl_digestSizePanel = new GridBagLayout();
		gbl_digestSizePanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_digestSizePanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_digestSizePanel.columnWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		gbl_digestSizePanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		digestSizePanel.setLayout(gbl_digestSizePanel);

		digestSizeCB = new JComboBox<>();
		digestSizeCB.setEnabled(false);
		final GridBagConstraints gbc_digestSizeCB = new GridBagConstraints();
		gbc_digestSizeCB.insets = new Insets(0, 0, 0, 5);
		gbc_digestSizeCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_digestSizeCB.gridx = 0;
		gbc_digestSizeCB.gridy = 0;
		digestSizePanel.add(digestSizeCB, gbc_digestSizeCB);

		final JLabel digestSizeLabel = new JLabel("bit");
		digestSizeLabel.setEnabled(false);
		final GridBagConstraints gbc_digestSizeLabel = new GridBagConstraints();
		gbc_digestSizeLabel.gridx = 1;
		gbc_digestSizeLabel.gridy = 0;
		digestSizePanel.add(digestSizeLabel, gbc_digestSizeLabel);

		/* List, ComboBox models */

		// Plain-text charset combo box model
		plainTextCharsetCB.setModel(new DefaultComboBoxModel<>(Encoding.values()));
		plainTextCharsetCB.setSelectedItem(Encoding.UTF_8);

		base64EncryptedText.setSelected(true);

		cipherAlgorithmCB.setModel(new DefaultComboBoxModel<>(Algorithm.values()));
		cipherAlgorithmCB.setSelectedItem(Algorithm.RSA);

		// Cipher algorithm padding combo box model
		cipherAlgorithmPaddingCB.setModel(new DefaultComboBoxModel<>(CipherAlgorithmPadding.values()));
		cipherAlgorithmPaddingCB.setSelectedItem(CipherAlgorithmPadding.PKCS1);

		// Key-size combo box initialization
		final Integer[] keyLengths = new Integer[5];
		for (int keySize = 512, keyIndex = 0; keySize <= 8192; keySize <<= 1)
			keyLengths[keyIndex++] = keySize;
		keySizeCB.setModel(new DefaultComboBoxModel<>(keyLengths));
		keySizeCB.setSelectedItem(2048);
		keySizeCB.addActionListener(e ->
		{
			((PlainDocumentWithLimit) plainTextField.getDocument()).setLimit((int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(2048) / 8 - 11); // limit = n/8 bytes - 11(padding bytes)
			plainTextField.updateUI();
			keySizeSpinner.setValue(keySizeCB.getSelectedItem());
			keySizeSlider.setValue((int) keySizeCB.getSelectedItem());
		});

		keySizeSlider.setValue(2048);
		keySizeSlider.addChangeListener(e ->
		{
			keySizeCB.setSelectedItem(keySizeSlider.getValue());
			keySizeSpinner.setValue(keySizeSlider.getValue());
		});

		keySizeSpinner.setModel(new SpinnerNumberModel(2048, 128, 8192, 128));
		keySizeSpinner.addChangeListener(e ->
		{
			keySizeCB.setSelectedItem(keySizeSpinner.getValue());
			keySizeSlider.setValue((int) keySizeSpinner.getValue());
		});

		((PlainDocumentWithLimit) plainTextField.getDocument()).setLimit(117); // (1024 bits => 128 bytes) - 11(padding bytes) = 117 bytes

		// OAEP hash algorithm combo box initialization
		cipherAlgorithmPaddingOAEPHashAlgorithmCB.setModel(new DefaultComboBoxModel<>(new Vector<>(Arrays.stream(HashAlgorithm.values()).filter(a -> a.getProviderName() != null).collect(Collectors.toList()))));
		cipherAlgorithmPaddingOAEPHashAlgorithmCB.setSelectedItem(HashAlgorithm.SHA2);
		cipherAlgorithmPaddingOAEPHashAlgorithmPanel.setEnabled(false);
		cipherAlgorithmPaddingOAEPHashAlgorithmCB.setEnabled(false);

		stateSizeCB.setModel(new DefaultComboBoxModel<>(HashAlgorithm.Skein.getAvailableDigestSizesBoxed())); // This combo box is exist only for Skein mode

		plainFromToTextButton.setSelected(true);
		encryptedFromToTextButton.setSelected(true);

		/* ButtonGroups */

		final ButtonGroup plainModeButtonGroup = new ButtonGroup();
		plainModeButtonGroup.add(plainFromToTextButton);
		plainModeButtonGroup.add(plainFromToFileButton);

		final ButtonGroup encryptedModeButtonGroup = new ButtonGroup();
		encryptedModeButtonGroup.add(encryptedFromToTextButton);
		encryptedModeButtonGroup.add(encryptedFromToFileButton);

		/* Lambdas */

		base64EncryptedText.addActionListener(e -> base64EncryptedTextNote.setVisible(!base64EncryptedText.isSelected()));

		encryptWithPublicToggle.addActionListener(e -> encryptWithPublicToggle.setText(encryptWithPublicToggle.isSelected() ? "Encrypt with private key, Decrypt with public key" : "Encrypt with public key, Decrypt with private key"));

		cipherAlgorithmPaddingCB.addActionListener(e ->
		{
			final boolean oaep = cipherAlgorithmPaddingCB.getSelectedItem() == CipherAlgorithmPadding.OAEP;
			cipherAlgorithmPaddingOAEPHashAlgorithmPanel.setEnabled(oaep);
			cipherAlgorithmPaddingOAEPHashAlgorithmCB.setEnabled(oaep);

			final HashAlgorithm oaepAlg = (HashAlgorithm) cipherAlgorithmPaddingOAEPHashAlgorithmCB.getSelectedItem();
			final boolean digestSizesAvailable = oaepAlg != null && oaepAlg.getAvailableDigestSizes() != null && oaep;
			if (digestSizesAvailable)
				digestSizeCB.setModel(new DefaultComboBoxModel<>(oaepAlg.getAvailableDigestSizesBoxed()));
			digestSizePanel.setEnabled(digestSizesAvailable);
			digestSizeCB.setEnabled(digestSizesAvailable);
			digestSizeLabel.setEnabled(digestSizesAvailable);

			final boolean isSkein = oaepAlg == HashAlgorithm.Skein;
			final int stateSizeBits = (int) Optional.ofNullable(stateSizeCB.getSelectedItem()).orElse(256);

			if (isSkein)
			{
				final Integer[] digestSizeBits;
				switch (stateSizeBits)
				{
					case 512:
						digestSizeBits = new Integer[]
						{
								128, 160, 224, 256, 384, 512
						};
						break;
					case 1024:
						digestSizeBits = new Integer[]
						{
								384, 512, 1024
						};
						break;
					default: // 256
						digestSizeBits = new Integer[]
						{
								128, 160, 224, 256
						};
						break;
				}

				final int lastSelected = (int) Optional.ofNullable(digestSizeCB.getSelectedItem()).orElse(256);
				digestSizeCB.setModel(new DefaultComboBoxModel<>(digestSizeBits));
				digestSizeCB.updateUI();

				digestSizeCB.setSelectedItem(lastSelected);
			}

			stateSizePanel.setVisible(isSkein);
		});

		cipherAlgorithmPaddingOAEPHashAlgorithmCB.addActionListener(e ->
		{
			final HashAlgorithm newOAEPAlg = (HashAlgorithm) cipherAlgorithmPaddingOAEPHashAlgorithmCB.getSelectedItem();
			final boolean digestSizesAvailable = newOAEPAlg != null && newOAEPAlg.getAvailableDigestSizes() != null;
			if (digestSizesAvailable)
				digestSizeCB.setModel(new DefaultComboBoxModel<>(newOAEPAlg.getAvailableDigestSizesBoxed()));
			digestSizePanel.setEnabled(digestSizesAvailable);
			digestSizeCB.setEnabled(digestSizesAvailable);
			digestSizeLabel.setEnabled(digestSizesAvailable);

			final boolean isSkein = newOAEPAlg == HashAlgorithm.Skein;
			final int stateSizeBits = (int) Optional.ofNullable(stateSizeCB.getSelectedItem()).orElse(256);

			if (isSkein)
			{
				final Integer[] digestSizeBits;
				switch (stateSizeBits)
				{
					case 512:
						digestSizeBits = new Integer[]
						{
								128, 160, 224, 256, 384, 512
						};
						break;
					case 1024:
						digestSizeBits = new Integer[]
						{
								384, 512, 1024
						};
						break;
					default: // 256
						digestSizeBits = new Integer[]
						{
								128, 160, 224, 256
						};
						break;
				}

				final int lastSelected = (int) Optional.ofNullable(digestSizeCB.getSelectedItem()).orElse(256);
				digestSizeCB.setModel(new DefaultComboBoxModel<>(digestSizeBits));
				digestSizeCB.updateUI();

				digestSizeCB.setSelectedItem(lastSelected);
			}
			stateSizePanel.setVisible(isSkein);
		});

		// Encrypt button lambda
		encryptButton.addActionListener(e ->
		{
			encryptButton.setEnabled(false);
			decryptButton.setEnabled(false);
			Main.setBusyCursor(this, true);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doSaveEncryptedBytes(doEncrypt(getPlainBytes())))
						Main.notificationMessageBox("Successfully encrypted!", "Successfully encrypted the plain message!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				catch (final InterruptedException | ExecutionException | PublicKeyCryptionException ex)
				{
					Main.exceptionMessageBox("Exception while encryption", "An exception occurred while encryption.", ex);
				}
				finally
				{
					// Reset buttons
					EventQueue.invokeLater(() ->
					{
						encryptButton.setEnabled(true);
						decryptButton.setEnabled(true);
						Main.setBusyCursor(this, false);
					});
				}
			});
		});

		// Decrypt button lambda
		decryptButton.addActionListener(e ->
		{
			encryptButton.setEnabled(false);
			decryptButton.setEnabled(false);
			Main.setBusyCursor(this, true);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doSavePlainBytes(doDecrypt(getEncryptedBytes())))
						Main.notificationMessageBox("Successfully decrypted!", "Successfully decrypted the plain message!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				catch (final InterruptedException | ExecutionException | PublicKeyCryptionException ex)
				{
					Main.exceptionMessageBox("Exception while decryption", "An exception occurred while decryption.", ex);
				}
				finally
				{
					// Reset buttons
					EventQueue.invokeLater(() ->
					{
						encryptButton.setEnabled(true);
						decryptButton.setEnabled(true);
						Main.setBusyCursor(this, false);
					});
				}
			});
		});

		// Generate key button lambda
		generateKeyButton.addActionListener(e ->
		{
			generateKeyButton.setEnabled(false);
			Main.setBusyCursor(this, true);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doGenerateKey())
						Main.notificationMessageBox("Successfully generated!", "Successfully generated the key pair!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				finally
				{
					// Reset buttons
					EventQueue.invokeLater(() ->
					{
						generateKeyButton.setEnabled(true);
						Main.setBusyCursor(this, false);
					});
				}
			});
		});

		// Public key import/export button lambdas
		publicKeyImportButton.addActionListener(e ->
		{
			publicKeyImportButton.setEnabled(false);
			publicKeyExportButton.setEnabled(false);
			Main.setBusyCursor(this, true);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doImportPublicKey())
						Main.notificationMessageBox("Successfully imported!", "Successfully imported the public key from the X.509 encoded keyspec!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				catch (final PublicKeyCryptionException ex)
				{
					Main.exceptionMessageBox("Exception while importing the public key", "An exception occurred while importing the public key.", ex);
				}
				finally
				{
					// Reset buttons
					EventQueue.invokeLater(() ->
					{
						publicKeyImportButton.setEnabled(true);
						publicKeyExportButton.setEnabled(true);
						Main.setBusyCursor(this, false);
					});
				}
			});
		});

		publicKeyExportButton.addActionListener(e ->
		{
			publicKeyImportButton.setEnabled(false);
			publicKeyExportButton.setEnabled(false);
			Main.setBusyCursor(this, true);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doExportPublicKey())
						Main.notificationMessageBox("Successfully exported!", "Successfully exported the public key to the X.509 encoded keyspec!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				catch (final PublicKeyCryptionException ex)
				{
					Main.exceptionMessageBox("Exception while exporting the public key", "An exception occurred while exporting the public key.", ex);
				}
				finally
				{
					// Reset buttons
					EventQueue.invokeLater(() ->
					{
						publicKeyImportButton.setEnabled(true);
						publicKeyExportButton.setEnabled(true);
						Main.setBusyCursor(this, false);
					});
				}
			});
		});

		// Private key import/export button lambdas
		privateKeyImportButton.addActionListener(e ->
		{
			privateKeyImportButton.setEnabled(false);
			privateKeyExportButton.setEnabled(false);
			Main.setBusyCursor(this, true);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doImportPrivateKey())
						Main.notificationMessageBox("Successfully imported!", "Successfully imported the private key from the PKCS #8 encoded keyspec!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				catch (final PublicKeyCryptionException ex)
				{
					Main.exceptionMessageBox("Exception while importing the private key", "An exception occurred while importing the private key.", ex);
				}
				finally
				{
					// Reset buttons
					EventQueue.invokeLater(() ->
					{
						privateKeyImportButton.setEnabled(true);
						privateKeyExportButton.setEnabled(true);
						Main.setBusyCursor(this, false);
					});
				}
			});
		});
		privateKeyExportButton.addActionListener(e ->
		{
			privateKeyImportButton.setEnabled(false);
			privateKeyExportButton.setEnabled(false);
			Main.setBusyCursor(this, true);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doExportPrivateKey())
						Main.notificationMessageBox("Successfully exported!", "Successfully exported the private key to the PKCS #8 encoded keyspec!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				catch (final PublicKeyCryptionException ex)
				{
					Main.exceptionMessageBox("Exception while exporting the private key", "An exception occurred while exporting the private key.", ex);
				}
				finally
				{
					// Reset buttons
					EventQueue.invokeLater(() ->
					{
						privateKeyImportButton.setEnabled(true);
						privateKeyExportButton.setEnabled(true);
						Main.setBusyCursor(this, false);
					});
				}
			});
		});

		plainFromToTextButton.addActionListener(e ->
		{
			if (plainFromToTextButton.isSelected())
			{
				plainTextField.setEnabled(true);

				plainFileField.setEnabled(false);
				plainFileFindButton.setEnabled(false);
			}
		});
		plainFromToFileButton.addActionListener(e ->
		{
			if (plainFromToFileButton.isSelected())
			{
				plainFileField.setEnabled(true);
				plainFileFindButton.setEnabled(true);

				plainTextField.setEnabled(false);
			}
		});

		encryptedFromToTextButton.addActionListener(e ->
		{
			if (encryptedFromToTextButton.isSelected())
			{
				encryptedTextField.setEnabled(true);

				encryptedFileField.setEnabled(false);
				encryptedFileFindButton.setEnabled(false);
			}
		});
		encryptedFromToFileButton.addActionListener(e ->
		{
			if (encryptedFromToFileButton.isSelected())
			{
				encryptedFileField.setEnabled(true);
				encryptedFileFindButton.setEnabled(true);

				encryptedTextField.setEnabled(false);
			}
		});

		plainFileFindButton.addActionListener(e ->
		{
			final String filePath = Main.generateFindFileGUI(plainFileField.getText());
			if (filePath != null)
				plainFileField.setText(filePath);
		});

		encryptedFileFindButton.addActionListener(e ->
		{
			final String filePath = Main.generateFindFileGUI(encryptedFileField.getText());
			if (filePath != null)
				encryptedFileField.setText(filePath);
		});
	}

	@SuppressWarnings("WeakerAccess")
	boolean doGenerateKey()
	{
		final Algorithm alg = (Algorithm) Optional.ofNullable(cipherAlgorithmCB.getSelectedItem()).orElse(Algorithm.RSA);
		final int keySize = (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(2048);

		try
		{
			final KeyPairGenerator kpGen = KeyPairGenerator.getInstance(alg.getId());

			final KeyFactory keyFactory = KeyFactory.getInstance(alg.getId());

			if (alg == Algorithm.RSA)
			{
				kpGen.initialize(keySize);
				final KeyPair keyPair = kpGen.generateKeyPair();
				final PublicKey publicKey = keyPair.getPublic();
				final PrivateKey privateKey = keyPair.getPrivate();

				final RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
				final RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

				final String keyMod = publicKeySpec.getModulus().toString(16).toUpperCase(Locale.ENGLISH);

				final String publicKeyExp = publicKeySpec.getPublicExponent().toString(16).toUpperCase(Locale.ENGLISH);

				final String privateKeyExp = privateKeySpec.getPrivateExponent().toString(16).toUpperCase(Locale.ENGLISH);

				keyModulusField.setText(keyMod);
				publicKeyExponentField.setText(publicKeyExp);
				privateKeyExponentField.setText(privateKeyExp);
			}
		}
		catch (final NoSuchAlgorithmException | InvalidKeySpecException e)
		{
			Main.exceptionMessageBox("Failed to generate keypair", "Key Generator Failed To Generate KeyPair." + Main.lineSeparator + Main.lineSeparator + "Key size: " + keySize + " bits", e);
			return false;
		}

		return true;
	}

	private boolean doSavePlainBytes(final byte[] plainBytes) throws ExecutionException, InterruptedException
	{
		if (plainBytes == null || plainBytes.length == 0)
			return false;

		final Charset charset = StandardCharsets.UTF_8;
		final boolean toFile = plainFromToFileButton.isSelected();

		if (toFile)
		{
			final String plainFilePath = plainFileField.getText();

			final File plainFile = new File(plainFilePath);

			boolean isFileHasData;
			try
			{
				isFileHasData = Files.readAllBytes(plainFile.toPath()).length > 0;
			}
			catch (final IOException ignored)
			{
				isFileHasData = true;
			}

			if (plainFile.exists() && isFileHasData)
				if (Main.warningMessageBox("File already exists and not empty", "The plain-message output file is not empty." + Main.lineSeparator + "If you continue this action, IT WILL OVERWRITE THE EXISTING DATA!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, new String[]
				{
						"Continue", "Cancel"
				}, "Cancel").get() != 0)
					return false;

			try
			{
				if (!plainFile.exists())
					plainFile.createNewFile();

				Files.write(plainFile.toPath(), plainBytes, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			}
			catch (final IOException e)
			{
				// If IOException occurs while writing all bytes to the output file

				final StringBuilder messageBuilder = new StringBuilder("Failed to save decrypted message to the file.").append(Main.lineSeparator).append(Main.lineSeparator);

				// Print the cause of the problem
				messageBuilder.append("IOException occurred while create the output file or writing all bytes to the output file").append(Main.lineSeparator).append(Main.lineSeparator);

				// Plain file path
				messageBuilder.append("Plain file path: ").append(plainFilePath).append(Main.lineSeparator);

				Main.exceptionMessageBox(e.getClass().getCanonicalName(), messageBuilder.toString(), e);
				return false;
			}
		}
		else
		{
			plainTextField.setText(new String(plainBytes, charset));
			plainTextField.updateUI();
		}

		return true;
	}

	private byte[] getPlainBytes() throws ExecutionException, InterruptedException
	{
		final Charset charset = StandardCharsets.UTF_8;
		final Algorithm alg = (Algorithm) cipherAlgorithmCB.getSelectedItem();
		final boolean fromFile = plainFromToFileButton.isSelected();

		if (fromFile)
		{
			int fileBytesLimit = (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(2048) / 8 - 11;

			if (alg != Algorithm.RSA)
				fileBytesLimit = 0; // TODO: FIX IT LATER

			final String plainFilePath = plainFileField.getText();

			if (plainFilePath == null || plainFilePath.isEmpty())
				return null;

			final File plainFile = new File(plainFilePath);
			if (!plainFile.exists())
			{
				// If input file doesn't exists

				final StringBuilder messageBuilder = new StringBuilder("Failed to encrypt the given string.").append(Main.lineSeparator).append(Main.lineSeparator);

				// Print the cause of the problem
				messageBuilder.append("Plain input file doesn't exists.").append(Main.lineSeparator).append(Main.lineSeparator);

				// Plain file path
				messageBuilder.append("Plain file path: ").append(plainFilePath).append(Main.lineSeparator);

				Main.exceptionMessageBox("Plain input file doesn't exists", messageBuilder.toString(), new NoSuchFileException(plainFilePath));

				return null;
			}

			byte[] fixedBytes = null;
			try
			{
				final byte[] messageBytes = Files.readAllBytes(plainFile.toPath());

				if (plainFile.exists() && messageBytes.length > fileBytesLimit)
					if (Main.warningMessageBox("File is too big for encryption", "File is too big (" + messageBytes.length + " bytes) for encryption (" + fileBytesLimit + " bytes is limit)" + Main.lineSeparator + "If you continue this action, THE DATA THAT OVERED THE SIZE LIMIT WILL BE TRUNCATED(LOST) WHILE ENCRYPTION.", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, new String[]
					{
							"Continue", "Cancel"
					}, "Cancel").get() != 0)
						return null;

				fixedBytes = new byte[Math.min(messageBytes.length, fileBytesLimit)];

				System.arraycopy(messageBytes, 0, fixedBytes, 0, Math.min(messageBytes.length, fileBytesLimit));
			}
			catch (final IOException e)
			{
				// If IOException occurs while reading all bytes from the input file

				final StringBuilder messageBuilder = new StringBuilder("Failed to encrypt the given string.").append(Main.lineSeparator).append(Main.lineSeparator);

				// Print the cause of the problem
				messageBuilder.append("IOException occurred while reading all bytes from the input file").append(Main.lineSeparator).append(Main.lineSeparator);

				// Plain file path
				messageBuilder.append("Plain file path: ").append(plainFilePath).append(Main.lineSeparator);

				Main.exceptionMessageBox(e.getClass().getCanonicalName(), messageBuilder.toString(), e);
			}

			return fixedBytes;
		}
		return plainTextField.getText().getBytes(charset);
	}

	private boolean doSaveEncryptedBytes(final byte[] encryptedBytes) throws ExecutionException, InterruptedException
	{
		if (encryptedBytes == null || encryptedBytes.length == 0)
			return false;

		final boolean toFile = encryptedFromToFileButton.isSelected();
		if (toFile)
		{
			final String encryptedFilePath = encryptedFileField.getText();

			final File encryptedFile = new File(encryptedFilePath);

			boolean isFileHasData;

			try
			{
				isFileHasData = Files.readAllBytes(encryptedFile.toPath()).length > 0;
			}
			catch (final IOException ignored)
			{
				isFileHasData = true;
			}

			if (encryptedFile.exists() && isFileHasData)
				if (Main.warningMessageBox("File already exists and not empty", "The encrypted-message output file is not empty." + Main.lineSeparator + "If you continue this action, IT WILL OVERWRITE THE EXISTING DATA!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, new String[]
				{
						"Continue", "Cancel"
				}, "Cancel").get() != 0)
					return false;

			try
			{
				if (!encryptedFile.exists())
					encryptedFile.createNewFile();

				Files.write(encryptedFile.toPath(), encryptedBytes, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			}
			catch (final IOException e)
			{
				// If IOException occurs while writing all bytes to the output file

				final StringBuilder messageBuilder = new StringBuilder("Failed to save encrypted message to the file.").append(Main.lineSeparator).append(Main.lineSeparator);

				// Print the cause of the problem
				messageBuilder.append("IOException occurred while create the output file or writing all bytes to the output file").append(Main.lineSeparator).append(Main.lineSeparator);

				// Encrypted file path
				messageBuilder.append("Encrypted file path: ").append(encryptedFilePath).append(Main.lineSeparator);

				Main.exceptionMessageBox(e.getClass().getCanonicalName(), messageBuilder.toString(), e);
				return false;
			}
		}
		else
		{
			encryptedTextField.setText(new String(encryptedBytes, StandardCharsets.ISO_8859_1));
			encryptedTextField.updateUI();
		}

		return true;
	}

	private byte[] getEncryptedBytes()
	{
		final boolean toFile = encryptedFromToFileButton.isSelected();

		if (toFile)
		{
			final String encryptedFilePath = encryptedFileField.getText();

			if (encryptedFilePath == null || encryptedFilePath.isEmpty())
				return null;

			final File encryptedFile = new File(encryptedFilePath);
			if (!encryptedFile.exists())
			{
				// If input file doesn't exists

				final StringBuilder messageBuilder = new StringBuilder("Failed to decrypt the given encrypted message.").append(Main.lineSeparator).append(Main.lineSeparator);

				// Print the cause of the problem
				messageBuilder.append("Encrypted input file doesn't exists.").append(Main.lineSeparator).append(Main.lineSeparator);

				// Encrypted file path
				messageBuilder.append("Encrypted file path: ").append(encryptedFilePath).append(Main.lineSeparator);

				Main.exceptionMessageBox("Encrypted file doesn't exists", messageBuilder.toString(), new NoSuchFileException(encryptedFilePath));

				return null;
			}

			try
			{
				return Files.readAllBytes(encryptedFile.toPath());
			}
			catch (final IOException e)
			{
				// If IOException occurs while reading all bytes from the input file

				final StringBuilder messageBuilder = new StringBuilder("Failed to decrypt the given encrypted message").append(Main.lineSeparator).append(Main.lineSeparator);

				// Print the cause of the problem
				messageBuilder.append("IOException occurred while reading all bytes from the encrypted file").append(Main.lineSeparator).append(Main.lineSeparator);

				// Encrypted file path
				messageBuilder.append("Encrypted file path: ").append(encryptedFilePath).append(Main.lineSeparator);

				Main.exceptionMessageBox(e.getClass().getCanonicalName(), messageBuilder.toString(), e);
			}

			return null;
		}
		return encryptedTextField.getText().getBytes(StandardCharsets.ISO_8859_1);
	}

	@SuppressWarnings("WeakerAccess")
	boolean doImportPublicKey() throws PublicKeyCryptionException
	{
		final Algorithm alg = Optional.ofNullable((Algorithm) cipherAlgorithmCB.getSelectedItem()).orElse(Algorithm.RSA);

		final String encodedPublicString = publicKeyEncodedField.getText();

		if (encodedPublicString == null || encodedPublicString.isEmpty())
			return false;

		final byte[] encodedPublicBytes;
		try
		{
			encodedPublicBytes = Base64.getDecoder().decode(encodedPublicString);
		}
		catch (final IllegalArgumentException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.BASE64_DECODE_EXCEPTION, null, "Base64-bytearray: " + encodedPublicString, e);
		}

		try
		{
			final KeyFactory keyFactory = KeyFactory.getInstance(alg.getId());

			/* X509EncodedKeySpec -keyFactory.generatePublic-> PublicKey -keyFactory.getKeySpec-> PublicKeySpec */

			// Generate X509EncodedKeySpec with input bytes
			final KeySpec keySpec = new X509EncodedKeySpec(encodedPublicBytes);

			// Generate PublicKey from generated X509EncodedKeySpec
			final PublicKey publicKey = keyFactory.generatePublic(keySpec);

			String modulus = null;
			String publicExponent = null;

			// Generate PublicKeySpec from generated PublicKey
			if (alg == Algorithm.RSA)
			{
				final RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);

				modulus = publicKeySpec.getModulus().toString(16).toUpperCase(Locale.ENGLISH);
				publicExponent = publicKeySpec.getPublicExponent().toString(16).toUpperCase(Locale.ENGLISH);
			}

			boolean changed = false;
			if (!keyModulusField.getText().equals(modulus))
			{
				keyModulusField.setText(modulus);
				changed = true;
			}

			if (!publicKeyExponentField.getText().equals(publicExponent))
			{
				publicKeyExponentField.setText(publicExponent);
				return true;
			}

			return changed;
		}
		catch (final NoSuchAlgorithmException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.UNSUPPORTED_KEYFACTORY_ALGORITHM, alg.getId(), e);
		}
		catch (final RuntimeException | InvalidKeySpecException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.INVALID_PUBLIC_KEYSPEC, e);
		}
	}

	@SuppressWarnings("WeakerAccess")
	boolean doExportPublicKey() throws PublicKeyCryptionException
	{
		final Algorithm alg = Optional.ofNullable((Algorithm) cipherAlgorithmCB.getSelectedItem()).orElse(Algorithm.RSA);

		final String modulusString = keyModulusField.getText();
		final String publicExponentString = publicKeyExponentField.getText();

		if (modulusString == null || publicExponentString == null || modulusString.isEmpty() || publicExponentString.isEmpty())
			return false;

		final BigInteger modulus;
		try
		{
			modulus = new BigInteger(modulusString, 16);
		}
		catch (final NumberFormatException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.MALFORMED_MODULUS, e);
		}

		final BigInteger publicExponent;
		try
		{
			publicExponent = new BigInteger(publicExponentString, 16);
		}
		catch (final NumberFormatException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.MALFORMED_PUBLIC_EXPONENT, e);
		}

		try
		{
			final KeyFactory keyFactory = KeyFactory.getInstance(alg.getId());

			/* RSAPublicKeySpec -keyFactory.generatePublic-> PublicKey -keyFactory.getKeySpec-> X509EncodedKeySpec */

			// Generate PublicKeySpec with modulus, publicExponent
			KeySpec publicKeySpec = null;

			if (alg == Algorithm.RSA)
				publicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);

			// Generate PublicKey from generated RSAPublicKeySpec
			final PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			// Generate X509EncodedKeySpec from generated PublicKey
			final X509EncodedKeySpec encodedKeySpec = keyFactory.getKeySpec(publicKey, X509EncodedKeySpec.class);

			final byte[] encodedBytes = encodedKeySpec.getEncoded();

			final String encoded = new String(Base64.getEncoder().encode(encodedBytes), StandardCharsets.UTF_8);

			if (!publicKeyEncodedField.getText().equals(encoded))
				publicKeyEncodedField.setText(encoded);

			return true;
		}
		catch (final NoSuchAlgorithmException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.UNSUPPORTED_KEYFACTORY_ALGORITHM, alg.getId(), e);
		}
		catch (final RuntimeException | InvalidKeySpecException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.INVALID_PUBLIC_KEYSPEC, e);
		}
	}

	@SuppressWarnings("WeakerAccess")
	boolean doImportPrivateKey() throws PublicKeyCryptionException
	{
		final Algorithm alg = (Algorithm) cipherAlgorithmCB.getSelectedItem();

		if (alg == null)
			return false;

		final String encodedPrivateString = privateKeyEncodedField.getText();

		if (encodedPrivateString == null || encodedPrivateString.isEmpty())
			return false;

		// <editor-fold desc="Base64 decode the encoded private key spec">
		final byte[] privateBytes;
		try
		{
			privateBytes = Base64.getDecoder().decode(encodedPrivateString);
		}
		catch (final IllegalArgumentException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.BASE64_DECODE_EXCEPTION, null, "Base64-bytearray: " + encodedPrivateString, e);
		}
		// </editor-fold>

		try
		{
			final KeyFactory keyFactory = KeyFactory.getInstance(alg.getId());

			// <editor-fold desc="[PKCS8EncodedKeySpec] --(keyFactory.generatePrivate())--> [PrivateKey] --(keyFactory.getKeySpec())--> [RSAPrivateKeySpec]">
			// Generate X509EncodedKeySpec with input bytes
			final KeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);

			// Generate PrivateKey from generated PKCS8EncodedKeySpec
			final PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

			String modulus = null;
			String privateExponent = null;

			// Generate PublicKeySpec from generated PublicKey
			if (alg == Algorithm.RSA)
			{
				final RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(privateKey, RSAPublicKeySpec.class);

				modulus = publicKeySpec.getModulus().toString(16).toUpperCase(Locale.ENGLISH);
				privateExponent = publicKeySpec.getPublicExponent().toString(16).toUpperCase(Locale.ENGLISH);
			}
			// </editor-fold>

			// <editor-fold desc="Apply the loaded key">
			boolean changed = false;
			if (!keyModulusField.getText().equals(modulus))
			{
				keyModulusField.setText(modulus);
				changed = true;
			}

			if (!privateKeyExponentField.getText().equals(privateExponent))
			{
				privateKeyExponentField.setText(privateExponent);
				return true;
			}
			// </editor-fold>

			return changed;
		}
		catch (final NoSuchAlgorithmException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.UNSUPPORTED_KEYFACTORY_ALGORITHM, alg.getId(), e);
		}
		catch (final RuntimeException | InvalidKeySpecException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.INVALID_PRIVATE_KEYSPEC, e);
		}
	}

	@SuppressWarnings("WeakerAccess")
	boolean doExportPrivateKey() throws PublicKeyCryptionException
	{
		final Algorithm alg = Optional.ofNullable((Algorithm) cipherAlgorithmCB.getSelectedItem()).orElse(Algorithm.RSA);

		final String modulusString = keyModulusField.getText();
		final String privateExponentString = privateKeyExponentField.getText();

		if (modulusString == null || privateExponentString == null || modulusString.isEmpty() || privateExponentString.isEmpty())
			return false;

		// <editor-fold desc="Parse the modulus and private exponent from text fields">
		final BigInteger modulus;
		try
		{
			modulus = new BigInteger(modulusString, 16);
		}
		catch (final NumberFormatException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.MALFORMED_MODULUS, e);
		}

		final BigInteger privateExponent;
		try
		{
			privateExponent = new BigInteger(privateExponentString, 16);
		}
		catch (final NumberFormatException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.MALFORMED_PRIVATE_EXPONENT, e);
		}
		// </editor-fold>

		try
		{
			final KeyFactory keyFactory = KeyFactory.getInstance(alg.getId());

			// <editor-fold desc="[RSAPrivateKeySpec] --(keyFactory.generatePrivate())--> [PrivateKey] --(keyFactory.getKeySpec())--> [PKCS8EncodedKeySpec]">
			// Generate RSAPrivateKeySpec with modulus, privateExponent
			KeySpec privateKeySpec = null;

			if (alg == Algorithm.RSA)
				privateKeySpec = new RSAPrivateKeySpec(modulus, privateExponent);

			// Generate PrivateKey from generated RSAPrivateKeySpec
			final PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

			// Generate PKCS8EncodedKeySpec from generated PrivateKey
			final PKCS8EncodedKeySpec encodedKeySpec = keyFactory.getKeySpec(privateKey, PKCS8EncodedKeySpec.class);

			final byte[] encodedBytes = encodedKeySpec.getEncoded();

			final String encoded = new String(Base64.getEncoder().encode(encodedBytes), StandardCharsets.UTF_8);
			// </editor-fold>

			// <editor-fold desc="Print the encoded key">
			if (!privateKeyEncodedField.getText().equals(encoded))
				privateKeyEncodedField.setText(encoded);

			return true;
			// </editor-fold>
		}
		catch (final NoSuchAlgorithmException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.UNSUPPORTED_KEYFACTORY_ALGORITHM, alg.getId(), e);
		}
		catch (final RuntimeException | InvalidKeySpecException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.INVALID_PUBLIC_KEYSPEC, e);
		}

	}

	@SuppressWarnings("WeakerAccess")
	byte[] doEncrypt(final byte[] plainBytes) throws PublicKeyCryptionException
	{
		final Charset charset = StandardCharsets.UTF_8;

		final String modulusString = keyModulusField.getText();
		final String publicExponentString = publicKeyExponentField.getText();
		final String privateExponentString = privateKeyExponentField.getText();

		final Algorithm alg = Optional.ofNullable((Algorithm) cipherAlgorithmCB.getSelectedItem()).orElse(Algorithm.RSA);
		final CipherAlgorithmPadding cipherPadding = Optional.ofNullable((CipherAlgorithmPadding) cipherAlgorithmPaddingCB.getSelectedItem()).orElse(CipherAlgorithmPadding.PKCS1);
		final HashAlgorithm oaepPaddingDigest = Optional.ofNullable((HashAlgorithm) cipherAlgorithmPaddingOAEPHashAlgorithmCB.getSelectedItem()).orElse(HashAlgorithm.SHA2);

		final boolean usePrivateToEncrypt = encryptWithPublicToggle.isSelected();
		final boolean encodeEncrypted = base64EncryptedText.isSelected();

		// Check the required resources
		if (plainBytes == null || modulusString == null || plainBytes.length == 0 || modulusString.isEmpty())
			return null;

		if (usePrivateToEncrypt)
		{
			if (privateExponentString == null || privateExponentString.isEmpty())
				return null;
		}
		else if (publicExponentString == null || publicExponentString.isEmpty())
			return null;

		BigInteger modulus = null;
		BigInteger exponent = null;

		if (alg == Algorithm.RSA)
		{
			try
			{
				modulus = new BigInteger(modulusString, 16);
			}
			catch (final NumberFormatException e)
			{
				throw new PublicKeyCryptionException(ExceptionType.MALFORMED_MODULUS, e);
			}

			try
			{
				exponent = new BigInteger(usePrivateToEncrypt ? privateExponentString : publicExponentString, 16);
			}
			catch (final NumberFormatException e)
			{
				if (usePrivateToEncrypt)
					throw new PublicKeyCryptionException(ExceptionType.MALFORMED_PRIVATE_EXPONENT, e);
				throw new PublicKeyCryptionException(ExceptionType.MALFORMED_PUBLIC_EXPONENT, e);
			}
		}

		final Key key;
		try
		{
			final KeyFactory keyFactory = KeyFactory.getInstance(alg.getId());

			if (usePrivateToEncrypt)
			{
				KeySpec privateKeySpec = null;
				if (alg == Algorithm.RSA)
					privateKeySpec = new RSAPrivateKeySpec(modulus, exponent);

				key = keyFactory.generatePrivate(privateKeySpec);
			}
			else
			{
				KeySpec publicKeySpec = null;
				if (alg == Algorithm.RSA)
					publicKeySpec = new RSAPublicKeySpec(modulus, exponent);

				key = keyFactory.generatePublic(publicKeySpec);
			}
		}
		catch (final RuntimeException | NoSuchAlgorithmException | InvalidKeySpecException e)
		{
			if (usePrivateToEncrypt)
				throw new PublicKeyCryptionException(ExceptionType.INVALID_PRIVATE, e);
			throw new PublicKeyCryptionException(ExceptionType.INVALID_PUBLIC, e);
		}

		String cipherAlgorithm = null;
		try
		{
			byte[] encryptedBytes = null;
			if (alg == Algorithm.RSA)
			{
				final String padding = cipherPadding.getPaddingName();

				OAEPParameterSpec oaepSpec = null;
				// OAEP padding with custom MessageDigest algorithm support
				if (cipherPadding == CipherAlgorithmPadding.OAEP)
				{
					final int stateSize = (int) Optional.ofNullable(stateSizeCB.getSelectedItem()).orElse(256);
					final int digestSize = (int) Optional.ofNullable(digestSizeCB.getSelectedItem()).orElse(256);
					SpiBasedDigest hash = null;
					try
					{
						hash = (SpiBasedDigest) Hasher.createHash(oaepPaddingDigest, stateSize, digestSize);
					}
					catch (final Throwable ignored)
					{

					}
					final String algorithmString = hash.getMessageDigestSpi(); // TODO: create stateSizeBits Combo box for SKEIN
					oaepSpec = new OAEPParameterSpec(algorithmString, "MGF1", new MGF1ParameterSpec(algorithmString), PSpecified.DEFAULT);
				}

				// Build the cipher algorithm string
				cipherAlgorithm = Algorithm.RSA.getId() + "/ECB/" + padding;

				// Create the cipher
				final Cipher cipher = Cipher.getInstance(cipherAlgorithm);

				// Initialize the cipher
				if (oaepSpec == null)
					cipher.init(Cipher.ENCRYPT_MODE, key);
				else
					cipher.init(Cipher.ENCRYPT_MODE, key, oaepSpec);

				// Encrypt the plain-text
				encryptedBytes = cipher.doFinal(plainBytes);
			}

			// Encode the encrypted-text with Base64 if the option is present.
			return encodeEncrypted ? Base64.getEncoder().encode(encryptedBytes) : encryptedBytes;

		}
		catch (final NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.UNSUPPORTED_CIPHER_ALGORITHM, cipherAlgorithm, e);
		}
		catch (final IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
		{
			throw new PublicKeyCryptionException(usePrivateToEncrypt ? ExceptionType.CORRUPTED_PRIVATE_KEY : ExceptionType.CORRUPTED_PUBLIC_KEY, e);
		}
	}

	@SuppressWarnings("WeakerAccess")
	byte[] doDecrypt(byte[] encryptedBytes) throws PublicKeyCryptionException
	{
		final String modulusString = keyModulusField.getText();
		final String publicExponentString = publicKeyExponentField.getText();
		final String privateExponentString = privateKeyExponentField.getText();

		final Algorithm alg = Optional.ofNullable((Algorithm) cipherAlgorithmCB.getSelectedItem()).orElse(Algorithm.RSA);
		final CipherAlgorithmPadding cipherPadding = Optional.ofNullable((CipherAlgorithmPadding) cipherAlgorithmPaddingCB.getSelectedItem()).orElse(CipherAlgorithmPadding.PKCS1);
		final HashAlgorithm oaepPaddingDigest = Optional.ofNullable((HashAlgorithm) cipherAlgorithmPaddingOAEPHashAlgorithmCB.getSelectedItem()).orElse(HashAlgorithm.SHA2);

		final boolean usePublicToDecrypt = encryptWithPublicToggle.isSelected();
		final boolean decodeEncrypted = base64EncryptedText.isSelected();

		// Check the required resources
		if (encryptedBytes == null || modulusString == null || encryptedBytes.length == 0 || modulusString.isEmpty())
			return null;

		// Check the exponent string
		if (usePublicToDecrypt)
		{
			if (publicExponentString == null || publicExponentString.isEmpty())
				return null;
		}
		else if (privateExponentString == null || privateExponentString.isEmpty())
			return null;

		// (Public) Modulus
		final BigInteger modulus;
		try
		{
			modulus = new BigInteger(modulusString, 16);
		}
		catch (final NumberFormatException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.MALFORMED_MODULUS, e);
		}

		// Exponent that used on decryption
		final BigInteger exponent;
		try
		{
			exponent = new BigInteger(usePublicToDecrypt ? publicExponentString : privateExponentString, 16);
		}
		catch (final NumberFormatException e)
		{
			if (usePublicToDecrypt)
				throw new PublicKeyCryptionException(ExceptionType.MALFORMED_PUBLIC_EXPONENT, e);
			throw new PublicKeyCryptionException(ExceptionType.MALFORMED_PRIVATE_EXPONENT, e);
		}

		final Key key;
		try
		{
			final KeyFactory keyFactory = KeyFactory.getInstance(alg.getId());

			if (usePublicToDecrypt)
			{
				final KeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);

				key = keyFactory.generatePublic(publicKeySpec);
			}
			else
			{
				final KeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, exponent);

				key = keyFactory.generatePrivate(privateKeySpec);
			}
		}
		catch (final RuntimeException | NoSuchAlgorithmException | InvalidKeySpecException e)
		{
			if (usePublicToDecrypt)
				throw new PublicKeyCryptionException(ExceptionType.INVALID_PUBLIC, e);
			throw new PublicKeyCryptionException(ExceptionType.INVALID_PRIVATE, e);
		}

		// Decode the encrypted-text with Base64 if the option is present.
		if (decodeEncrypted)
			try
			{
				encryptedBytes = Base64.getDecoder().decode(encryptedBytes);
			}
			catch (final IllegalArgumentException e)
			{
				throw new PublicKeyCryptionException(ExceptionType.BASE64_DECODE_EXCEPTION, null, "Base64-bytearray: " + new String(encryptedBytes, StandardCharsets.UTF_8), e);
			}

		final int stringByteArrayLength = encryptedBytes.length;

		String cipherAlgorithm = null;
		try
		{
			final String padding = cipherPadding.getPaddingName();

			OAEPParameterSpec oaepSpec = null;
			// OAEP padding with custom MessageDigest algorithm support
			if (cipherPadding == CipherAlgorithmPadding.OAEP)
			{
				final int stateSize = (int) Optional.ofNullable(stateSizeCB.getSelectedItem()).orElse(256);
				final int digestSize = (int) Optional.ofNullable(digestSizeCB.getSelectedItem()).orElse(256);
				SpiBasedDigest hash = null;
				try
				{
					hash = (SpiBasedDigest) Hasher.createHash(oaepPaddingDigest, stateSize, digestSize);
				}
				catch (final Throwable ignored)
				{

				}
				final String algorithmString = hash.getMessageDigestSpi();
				oaepSpec = new OAEPParameterSpec(algorithmString, "MGF1", new MGF1ParameterSpec(algorithmString), PSpecified.DEFAULT);
			}

			// Build the cipher algorithm string
			cipherAlgorithm = alg.getId() + "/ECB/" + padding;

			// Create the cipher
			final Cipher cipher = Cipher.getInstance(cipherAlgorithm);

			if (oaepSpec == null)
				cipher.init(Cipher.DECRYPT_MODE, key);
			else
				cipher.init(Cipher.DECRYPT_MODE, key, oaepSpec);

			return cipher.doFinal(encryptedBytes);
		}
		catch (final NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			throw new PublicKeyCryptionException(ExceptionType.UNSUPPORTED_CIPHER_ALGORITHM, cipherAlgorithm, e);
		}
		catch (final IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e)
		{
			throw new PublicKeyCryptionException(usePublicToDecrypt ? ExceptionType.CORRUPTED_PUBLIC_KEY : ExceptionType.CORRUPTED_PRIVATE_KEY, e);
		}
	}
}
