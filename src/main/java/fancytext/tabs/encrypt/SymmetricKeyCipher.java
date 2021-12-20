package fancytext.tabs.encrypt;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import fancytext.Main;
import fancytext.encrypt.symmetric.*;
import fancytext.encrypt.symmetric.cipher.AbstractCipher;
import fancytext.encrypt.symmetric.cipher.lea.LEAAECipher;
import fancytext.encrypt.symmetric.cipher.lea.LEACipher;
import fancytext.encrypt.symmetric.cipher.rijndael.RijndaelAEADCipher;
import fancytext.encrypt.symmetric.cipher.rijndael.RijndaelCipher;
import fancytext.encrypt.symmetric.cipher.spiBased.*;
import fancytext.utils.Encoding;
import fancytext.utils.MultiThreading;
import fancytext.utils.PlainDocumentWithLimit;

public final class SymmetricKeyCipher extends JPanel
{
	private static final long serialVersionUID = -7257463203150608739L;
	private final JTextPane plainTextField;
	private final JTextPane encryptedTextField;
	private final JTextField keyTextField;
	private final JLabel keyTextActualLabel;
	private final JTextField ivTextField;
	private final JLabel ivTextActualLabel;

	private final JComboBox<Encoding> plainTextCharsetCB;
	private final JComboBox<Integer> keySizeCB;
	private final JComboBox<CipherMode> cipherAlgorithmModeCB;
	private final JComboBox<CipherPadding> cipherAlgorithmPaddingCB;
	private final JComboBox<Encoding> keyTextCharsetCB;
	private final JComboBox<Encoding> ivTextCharsetCB;
	private final JCheckBox base64EncryptedText;
	private final JTextField paddingCharField;
	private final JComboBox<Integer> cipherAlgorithmModeCFBOFBUnitBytesCB;
	private final JTextField plainFileField;
	private final JTextField encryptedFileField;
	private final JRadioButton plainFromToTextButton;
	private final JRadioButton plainFromToFileButton;
	private final JRadioButton encryptedFromToFileButton;
	private final JComboBox<CipherAlgorithm> cipherAlgorithmCB;
	private final JPanel gost28147SBoxPanel;
	private final JComboBox<String> gost28147SBoxCB;
	private final JPanel rc5RoundsPanel;
	private final JSpinner rc5RoundsSpinner;
	private final JComboBox<Integer> rijndaelBlockSizeCB;
	private final JComboBox<Integer> cipherAlgorithmModeAEADTagLenCB;

	public SymmetricKeyCipher()
	{
		// <editor-fold desc="UI Setup">
		// Main border setup
		setBorder(new TitledBorder(null, "Symmetric-key algorithm cipher", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		setSize(2500, 1000); // TODO: For Debug. Remove it later

		// Main layout setup
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{
				0, 0, 0
		};
		gridBagLayout.rowHeights = new int[]
		{
				0, 0, 0, 0, 0, 0, 0
		};
		gridBagLayout.columnWeights = new double[]
		{
				0.0, 1.0, 0.0
		};
		gridBagLayout.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0
		};
		setLayout(gridBagLayout);

		// Encrypt button
		final JButton encryptButton = new JButton("Encrypt");

		final JPanel plainPanel = new JPanel();
		plainPanel.setBorder(new TitledBorder(null, "Plain(Decrypted) message", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_plainPanel = new GridBagConstraints();
		gbc_plainPanel.gridwidth = 3;
		gbc_plainPanel.insets = new Insets(0, 0, 5, 5);
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
				0.0, 1.0, Double.MIN_VALUE
		};
		plainPanel.setLayout(gbl_plainPanel);

		// Plain-text/Decrypted-text field panel
		final JPanel plainTextFieldPanel = new JPanel();
		final GridBagConstraints gbc_plainTextFieldPanel = new GridBagConstraints();
		gbc_plainTextFieldPanel.insets = new Insets(0, 0, 5, 0);
		gbc_plainTextFieldPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_plainTextFieldPanel.fill = GridBagConstraints.HORIZONTAL;
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
		plainTextField = new JTextPane();
		encryptPlainTextFieldScrollPane.setViewportView(plainTextField);

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
		gbc_cipherSettingsPanel.insets = new Insets(0, 0, 5, 5);
		gbc_cipherSettingsPanel.fill = GridBagConstraints.BOTH;
		gbc_cipherSettingsPanel.gridx = 0;
		gbc_cipherSettingsPanel.gridy = 1;
		add(cipherSettingsPanel, gbc_cipherSettingsPanel);
		final GridBagLayout gbl_cipherSettingsPanel = new GridBagLayout();
		gbl_cipherSettingsPanel.columnWidths = new int[]
		{
				0, 0, 0, 0, 0, 0, 0
		};
		gbl_cipherSettingsPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_cipherSettingsPanel.columnWeights = new double[]
		{
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE
		};
		gbl_cipherSettingsPanel.rowWeights = new double[]
		{
				0.0, 0.0, Double.MIN_VALUE
		};
		cipherSettingsPanel.setLayout(gbl_cipherSettingsPanel);

		final JPanel cipherAlgorithmPanel = new JPanel();
		cipherAlgorithmPanel.setBorder(new TitledBorder(null, "Cipher algorithm", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_cipherAlgorithmPanel = new GridBagConstraints();
		gbc_cipherAlgorithmPanel.gridwidth = 3;
		gbc_cipherAlgorithmPanel.ipadx = 20;
		gbc_cipherAlgorithmPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_cipherAlgorithmPanel.insets = new Insets(0, 0, 5, 5);
		gbc_cipherAlgorithmPanel.fill = GridBagConstraints.HORIZONTAL;
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
		gbc_keySizeCBPanel.ipadx = 20;
		gbc_keySizeCBPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_keySizeCBPanel.insets = new Insets(0, 0, 5, 5);
		gbc_keySizeCBPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_keySizeCBPanel.gridx = 3;
		gbc_keySizeCBPanel.gridy = 0;
		cipherSettingsPanel.add(keySizeCBPanel, gbc_keySizeCBPanel);
		final GridBagLayout gbl_keySizeCBPanel = new GridBagLayout();
		gbl_keySizeCBPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_keySizeCBPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_keySizeCBPanel.columnWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
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

		// Cipher settings panel - Key size panel - Key size label
		final JLabel keySizeLabel = new JLabel("bit");
		final GridBagConstraints gbc_keySizeLabel = new GridBagConstraints();
		gbc_keySizeLabel.gridx = 1;
		gbc_keySizeLabel.gridy = 0;
		keySizeCBPanel.add(keySizeLabel, gbc_keySizeLabel);

		// Cipher settings panel - Cipher algorithm mode panel
		final JPanel cipherAlgorithmModePanel = new JPanel();
		cipherAlgorithmModePanel.setBorder(new TitledBorder(null, "Cipher algorithm mode", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_cipherAlgorithmModePanel = new GridBagConstraints();
		gbc_cipherAlgorithmModePanel.anchor = GridBagConstraints.PAGE_START;
		gbc_cipherAlgorithmModePanel.insets = new Insets(0, 0, 5, 5);
		gbc_cipherAlgorithmModePanel.ipadx = 100;
		gbc_cipherAlgorithmModePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherAlgorithmModePanel.gridx = 4;
		gbc_cipherAlgorithmModePanel.gridy = 0;
		cipherSettingsPanel.add(cipherAlgorithmModePanel, gbc_cipherAlgorithmModePanel);
		final GridBagLayout gbl_cipherAlgorithmModePanel = new GridBagLayout();
		gbl_cipherAlgorithmModePanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_cipherAlgorithmModePanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_cipherAlgorithmModePanel.columnWeights = new double[]
		{
				1.0, 1.0, 1.0, Double.MIN_VALUE
		};
		gbl_cipherAlgorithmModePanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		cipherAlgorithmModePanel.setLayout(gbl_cipherAlgorithmModePanel);

		// Cipher settings panel - Cipher algorithm mode panel - Cipher algorithm mode combo box
		cipherAlgorithmModeCB = new JComboBox<>();
		final GridBagConstraints gbc_cipherOperationModeCB = new GridBagConstraints();
		gbc_cipherOperationModeCB.anchor = GridBagConstraints.PAGE_START;
		gbc_cipherOperationModeCB.insets = new Insets(0, 0, 0, 5);
		gbc_cipherOperationModeCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherOperationModeCB.gridx = 0;
		gbc_cipherOperationModeCB.gridy = 0;
		cipherAlgorithmModePanel.add(cipherAlgorithmModeCB, gbc_cipherOperationModeCB);
		final GridBagConstraints gbc_encryptButton = new GridBagConstraints();
		gbc_encryptButton.gridheight = 3;
		gbc_encryptButton.insets = new Insets(0, 0, 5, 5);
		gbc_encryptButton.gridx = 0;
		gbc_encryptButton.gridy = 2;
		add(encryptButton, gbc_encryptButton);

		// Key-text field panel
		final JPanel keyTextFieldPanel = new JPanel();
		keyTextFieldPanel.setBorder(new TitledBorder(null, "Encryption/Decryption key", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_keyTextFieldPanel = new GridBagConstraints();
		gbc_keyTextFieldPanel.insets = new Insets(10, 0, 5, 5);
		gbc_keyTextFieldPanel.fill = GridBagConstraints.BOTH;
		gbc_keyTextFieldPanel.gridx = 1;
		gbc_keyTextFieldPanel.gridy = 2;
		add(keyTextFieldPanel, gbc_keyTextFieldPanel);
		final GridBagLayout gbl_keyTextFieldPanel = new GridBagLayout();
		gbl_keyTextFieldPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_keyTextFieldPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_keyTextFieldPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_keyTextFieldPanel.rowWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		keyTextFieldPanel.setLayout(gbl_keyTextFieldPanel);

		// Key-text field panel - Key-text field
		keyTextField = new JTextField();
		final GridBagConstraints gbc_keyTextField = new GridBagConstraints();
		gbc_keyTextField.insets = new Insets(0, 0, 5, 5);
		gbc_keyTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_keyTextField.gridx = 0;
		gbc_keyTextField.gridy = 0;
		keyTextFieldPanel.add(keyTextField, gbc_keyTextField);

		// Key-text field panel - 'Actual key text' label
		keyTextActualLabel = new JLabel("");
		keyTextActualLabel.setLabelFor(keyTextField);
		keyTextActualLabel.setEnabled(false);
		final GridBagConstraints gbc_keyTextActualLabel = new GridBagConstraints();
		gbc_keyTextActualLabel.anchor = GridBagConstraints.LINE_START;
		gbc_keyTextActualLabel.insets = new Insets(0, 0, 0, 5);
		gbc_keyTextActualLabel.gridx = 0;
		gbc_keyTextActualLabel.gridy = 1;
		keyTextFieldPanel.add(keyTextActualLabel, gbc_keyTextActualLabel);

		// Key-text field panel - Charset panel
		final JPanel keyTextCharsetPanel = new JPanel();
		final GridBagConstraints gbc_keyTextCharsetPanel = new GridBagConstraints();
		gbc_keyTextCharsetPanel.insets = new Insets(0, 0, 5, 0);
		gbc_keyTextCharsetPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_keyTextCharsetPanel.gridx = 1;
		gbc_keyTextCharsetPanel.gridy = 0;
		keyTextFieldPanel.add(keyTextCharsetPanel, gbc_keyTextCharsetPanel);
		keyTextCharsetPanel.setBorder(new TitledBorder(null, "Charset", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_keyTextCharsetPanel = new GridBagLayout();
		gbl_keyTextCharsetPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_keyTextCharsetPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_keyTextCharsetPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_keyTextCharsetPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		keyTextCharsetPanel.setLayout(gbl_keyTextCharsetPanel);

		// Key-text field panel - Charset panel - Charset combo box
		keyTextCharsetCB = new JComboBox<>();
		final GridBagConstraints gbc_keyTextCharsetCB = new GridBagConstraints();
		gbc_keyTextCharsetCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_keyTextCharsetCB.gridx = 0;
		gbc_keyTextCharsetCB.gridy = 0;
		keyTextCharsetPanel.add(keyTextCharsetCB, gbc_keyTextCharsetCB);

		// Decrypt button
		final JButton decryptButton = new JButton("Decrypt");
		final GridBagConstraints gbc_decryptButton = new GridBagConstraints();
		gbc_decryptButton.gridheight = 3;
		gbc_decryptButton.insets = new Insets(0, 0, 5, 0);
		gbc_decryptButton.gridx = 2;
		gbc_decryptButton.gridy = 2;
		add(decryptButton, gbc_decryptButton);

		// IV-text/Counter-text field panel
		final JPanel ivTextFieldPanel = new JPanel();
		ivTextFieldPanel.setBorder(new TitledBorder(null, "Encryption/Decryption IV(Initial Vector)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_ivTextFieldPanel = new GridBagConstraints();
		gbc_ivTextFieldPanel.insets = new Insets(0, 0, 5, 5);
		gbc_ivTextFieldPanel.fill = GridBagConstraints.BOTH;
		gbc_ivTextFieldPanel.gridx = 1;
		gbc_ivTextFieldPanel.gridy = 3;
		add(ivTextFieldPanel, gbc_ivTextFieldPanel);
		final GridBagLayout gbl_ivTextFieldPanel = new GridBagLayout();
		gbl_ivTextFieldPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_ivTextFieldPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_ivTextFieldPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_ivTextFieldPanel.rowWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		ivTextFieldPanel.setLayout(gbl_ivTextFieldPanel);

		// IV-text/Counter-text field panel - IV-text/Counter-text field
		ivTextField = new JTextField();
		final GridBagConstraints gbc_ivTextField = new GridBagConstraints();
		gbc_ivTextField.insets = new Insets(0, 0, 5, 5);
		gbc_ivTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_ivTextField.gridx = 0;
		gbc_ivTextField.gridy = 0;
		ivTextFieldPanel.add(ivTextField, gbc_ivTextField);

		// IV-text/Counter-text field panel - 'Actual IV-text/Counter-text' field
		ivTextActualLabel = new JLabel("");
		ivTextActualLabel.setLabelFor(ivTextField);
		ivTextActualLabel.setEnabled(false);
		final GridBagConstraints gbc_ivTextActualLabel = new GridBagConstraints();
		gbc_ivTextActualLabel.anchor = GridBagConstraints.LINE_START;
		gbc_ivTextActualLabel.insets = new Insets(0, 0, 0, 5);
		gbc_ivTextActualLabel.gridx = 0;
		gbc_ivTextActualLabel.gridy = 1;
		ivTextFieldPanel.add(ivTextActualLabel, gbc_ivTextActualLabel);

		// Plain-text/Decrypted-text field panel - Charset panel
		final JPanel ivTextCharsetPanel = new JPanel();
		final GridBagConstraints gbc_ivTextCharsetPanel = new GridBagConstraints();
		gbc_ivTextCharsetPanel.insets = new Insets(0, 0, 5, 0);
		gbc_ivTextCharsetPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_ivTextCharsetPanel.gridx = 1;
		gbc_ivTextCharsetPanel.gridy = 0;
		ivTextFieldPanel.add(ivTextCharsetPanel, gbc_ivTextCharsetPanel);
		ivTextCharsetPanel.setBorder(new TitledBorder(null, "Charset", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_ivTextCharsetPanel = new GridBagLayout();
		gbl_ivTextCharsetPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_ivTextCharsetPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_ivTextCharsetPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_ivTextCharsetPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		ivTextCharsetPanel.setLayout(gbl_ivTextCharsetPanel);

		// Plain-text/Decrypted-text field panel - Charset panel - Charset combo box
		ivTextCharsetCB = new JComboBox<>();
		final GridBagConstraints gbc_ivTextCharsetCB = new GridBagConstraints();
		gbc_ivTextCharsetCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_ivTextCharsetCB.gridx = 0;
		gbc_ivTextCharsetCB.gridy = 0;
		ivTextCharsetPanel.add(ivTextCharsetCB, gbc_ivTextCharsetCB);

		// Padding character field panel
		final JPanel paddingCharFieldPanel = new JPanel();
		paddingCharFieldPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Key, IV (and Plain-text if cipher padding mode is None) Padding-character", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_paddingCharFieldPanel = new GridBagConstraints();
		gbc_paddingCharFieldPanel.insets = new Insets(0, 0, 10, 5);
		gbc_paddingCharFieldPanel.fill = GridBagConstraints.BOTH;
		gbc_paddingCharFieldPanel.gridx = 1;
		gbc_paddingCharFieldPanel.gridy = 4;
		add(paddingCharFieldPanel, gbc_paddingCharFieldPanel);
		final GridBagLayout gbl_paddingCharFieldPanel = new GridBagLayout();
		gbl_paddingCharFieldPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_paddingCharFieldPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_paddingCharFieldPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_paddingCharFieldPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		paddingCharFieldPanel.setLayout(gbl_paddingCharFieldPanel);

		// Padding character field panel - Padding character field
		paddingCharField = new JTextField();
		paddingCharField.setToolTipText("The padding character will be clamp insufficient characters in the secret key and the IV (If cipher algorithm padding is NONE, it will be clamp plain-text also.)");
		final GridBagConstraints gbc_paddingCharField = new GridBagConstraints();
		gbc_paddingCharField.ipadx = 20;
		gbc_paddingCharField.fill = GridBagConstraints.HORIZONTAL;
		gbc_paddingCharField.gridx = 0;
		gbc_paddingCharField.gridy = 0;
		paddingCharFieldPanel.add(paddingCharField, gbc_paddingCharField);
		paddingCharField.setColumns(10);

		final JPanel encryptedPanel = new JPanel();
		encryptedPanel.setBorder(new TitledBorder(null, "Encrypted message", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_encryptedPanel = new GridBagConstraints();
		gbc_encryptedPanel.gridwidth = 3;
		gbc_encryptedPanel.insets = new Insets(0, 0, 5, 5);
		gbc_encryptedPanel.fill = GridBagConstraints.BOTH;
		gbc_encryptedPanel.gridx = 0;
		gbc_encryptedPanel.gridy = 5;
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
				0.0, 0.0, 0.0, Double.MIN_VALUE
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
				0, 0, 0, 0
		};
		gbl_encryptedTextFieldPanel.columnWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		gbl_encryptedTextFieldPanel.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, Double.MIN_VALUE
		};
		encryptedTextFieldPanel.setLayout(gbl_encryptedTextFieldPanel);

		final JRadioButton encryptedFromToTextButton = new JRadioButton("Input/Output the encrypted-message from/to the text field");
		final GridBagConstraints gbc_encryptedFromToTextButton = new GridBagConstraints();
		gbc_encryptedFromToTextButton.gridheight = 3;
		gbc_encryptedFromToTextButton.insets = new Insets(0, 0, 5, 5);
		gbc_encryptedFromToTextButton.gridx = 0;
		gbc_encryptedFromToTextButton.gridy = 0;
		encryptedTextFieldPanel.add(encryptedFromToTextButton, gbc_encryptedFromToTextButton);

		// Encrypted-text field panel - Encrypted-text field scroll pane
		final JScrollPane encryptedTextFieldScrollPane = new JScrollPane();
		final GridBagConstraints gbc_encryptedTextFieldScrollPane = new GridBagConstraints();
		gbc_encryptedTextFieldScrollPane.gridheight = 2;
		gbc_encryptedTextFieldScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_encryptedTextFieldScrollPane.ipady = 40;
		gbc_encryptedTextFieldScrollPane.fill = GridBagConstraints.BOTH;
		gbc_encryptedTextFieldScrollPane.gridx = 1;
		gbc_encryptedTextFieldScrollPane.gridy = 0;
		encryptedTextFieldPanel.add(encryptedTextFieldScrollPane, gbc_encryptedTextFieldScrollPane);

		// Encrypted-text field panel - Encrypted-text field scroll pane - Encrypted-text field
		encryptedTextField = new JTextPane();
		encryptedTextFieldScrollPane.setViewportView(encryptedTextField);

		// Base64-encode encrypted-text - note
		final JLabel base64EncryptedTextNote = new JLabel("Note that the charset of RAW(Not base64-encoded) encrypted-text is always ISO-8859-1 (a.k.a. ISO-LATIN-1)");
		base64EncryptedTextNote.setEnabled(false);
		base64EncryptedTextNote.setVisible(false);
		base64EncryptedTextNote.setToolTipText("Because other charsets (UTF-8, UTF-16, etc.) are non-compatible with the RAW(Not base64-encoded) encrypted-text.");
		final GridBagConstraints gbc_base64EncryptedTextNote = new GridBagConstraints();
		gbc_base64EncryptedTextNote.anchor = GridBagConstraints.PAGE_START;
		gbc_base64EncryptedTextNote.gridx = 1;
		gbc_base64EncryptedTextNote.gridy = 2;
		encryptedTextFieldPanel.add(base64EncryptedTextNote, gbc_base64EncryptedTextNote);

		final JPanel encryptedFilePanel = new JPanel();
		encryptedFilePanel.setBorder(new TitledBorder(null, "Encrypted-file", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
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

		// Cipher settings panel - Cipher algorithm clamp panel
		final JPanel cipherAlgorithmPaddingPanel = new JPanel();
		cipherAlgorithmPaddingPanel.setBorder(new TitledBorder(null, "Cipher algorithm padding", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_cipherAlgorithmPaddingPanel = new GridBagConstraints();
		gbc_cipherAlgorithmPaddingPanel.insets = new Insets(0, 0, 5, 0);
		gbc_cipherAlgorithmPaddingPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_cipherAlgorithmPaddingPanel.ipadx = 60;
		gbc_cipherAlgorithmPaddingPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherAlgorithmPaddingPanel.gridx = 5;
		gbc_cipherAlgorithmPaddingPanel.gridy = 0;
		cipherSettingsPanel.add(cipherAlgorithmPaddingPanel, gbc_cipherAlgorithmPaddingPanel);
		final GridBagLayout gbl_cipherAlgorithmPaddingPanel = new GridBagLayout();
		gbl_cipherAlgorithmPaddingPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_cipherAlgorithmPaddingPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_cipherAlgorithmPaddingPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_cipherAlgorithmPaddingPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		cipherAlgorithmPaddingPanel.setLayout(gbl_cipherAlgorithmPaddingPanel);

		// Cipher settings panel - Cipher algorithm clamp panel - Cipher algorithm clamp check box
		cipherAlgorithmPaddingCB = new JComboBox<>();
		final GridBagConstraints gbc_cipherAlgorithmPaddingCB = new GridBagConstraints();
		gbc_cipherAlgorithmPaddingCB.anchor = GridBagConstraints.PAGE_START;
		gbc_cipherAlgorithmPaddingCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherAlgorithmPaddingCB.gridx = 0;
		gbc_cipherAlgorithmPaddingCB.gridy = 0;
		cipherAlgorithmPaddingPanel.add(cipherAlgorithmPaddingCB, gbc_cipherAlgorithmPaddingCB);

		final JPanel cipherAlgorithmModeCFBOFBUnitBytesPanel = new JPanel();
		cipherAlgorithmModeCFBOFBUnitBytesPanel.setBorder(new TitledBorder(null, "CFB/OFB mode unit-bytes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_cipherAlgorithmModeCFBOFBUnitBytesPanel = new GridBagConstraints();
		gbc_cipherAlgorithmModeCFBOFBUnitBytesPanel.insets = new Insets(0, 0, 0, 5);
		gbc_cipherAlgorithmModeCFBOFBUnitBytesPanel.ipadx = 80;
		gbc_cipherAlgorithmModeCFBOFBUnitBytesPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_cipherAlgorithmModeCFBOFBUnitBytesPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherAlgorithmModeCFBOFBUnitBytesPanel.gridx = 1;
		gbc_cipherAlgorithmModeCFBOFBUnitBytesPanel.gridy = 0;
		cipherAlgorithmModePanel.add(cipherAlgorithmModeCFBOFBUnitBytesPanel, gbc_cipherAlgorithmModeCFBOFBUnitBytesPanel);
		final GridBagLayout gbl_cipherAlgorithmModeCFBOFBUnitBytesPanel = new GridBagLayout();
		gbl_cipherAlgorithmModeCFBOFBUnitBytesPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_cipherAlgorithmModeCFBOFBUnitBytesPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_cipherAlgorithmModeCFBOFBUnitBytesPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_cipherAlgorithmModeCFBOFBUnitBytesPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		cipherAlgorithmModeCFBOFBUnitBytesPanel.setLayout(gbl_cipherAlgorithmModeCFBOFBUnitBytesPanel);

		cipherAlgorithmModeCFBOFBUnitBytesCB = new JComboBox<>();
		final GridBagConstraints gbc_cipherAlgorithmModeCFBOFBUnitBytesCB = new GridBagConstraints();
		gbc_cipherAlgorithmModeCFBOFBUnitBytesCB.anchor = GridBagConstraints.PAGE_START;
		gbc_cipherAlgorithmModeCFBOFBUnitBytesCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherAlgorithmModeCFBOFBUnitBytesCB.gridx = 0;
		gbc_cipherAlgorithmModeCFBOFBUnitBytesCB.gridy = 0;
		cipherAlgorithmModeCFBOFBUnitBytesPanel.add(cipherAlgorithmModeCFBOFBUnitBytesCB, gbc_cipherAlgorithmModeCFBOFBUnitBytesCB);

		base64EncryptedText.setSelected(true);
		plainFromToTextButton.setSelected(true);
		encryptedFromToTextButton.setSelected(true);

		gost28147SBoxPanel = new JPanel();
		gost28147SBoxPanel.setEnabled(false);
		gost28147SBoxPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "GOST28147 S-Box", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_gost28147SBoxPanel = new GridBagConstraints();
		gbc_gost28147SBoxPanel.ipadx = 80;
		gbc_gost28147SBoxPanel.insets = new Insets(0, 0, 0, 5);
		gbc_gost28147SBoxPanel.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_gost28147SBoxPanel.gridx = 0;
		gbc_gost28147SBoxPanel.gridy = 1;
		cipherSettingsPanel.add(gost28147SBoxPanel, gbc_gost28147SBoxPanel);
		final GridBagLayout gbl_gost28147SBoxPanel = new GridBagLayout();
		gbl_gost28147SBoxPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_gost28147SBoxPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_gost28147SBoxPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_gost28147SBoxPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		gost28147SBoxPanel.setLayout(gbl_gost28147SBoxPanel);

		gost28147SBoxCB = new JComboBox<>();
		gost28147SBoxCB.setEnabled(false);
		final GridBagConstraints gbc_gost28147SBoxCB = new GridBagConstraints();
		gbc_gost28147SBoxCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_gost28147SBoxCB.gridx = 0;
		gbc_gost28147SBoxCB.gridy = 0;
		gost28147SBoxPanel.add(gost28147SBoxCB, gbc_gost28147SBoxCB);

		rc5RoundsPanel = new JPanel();
		rc5RoundsPanel.setEnabled(false);
		rc5RoundsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "RC5 rounds", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_rc5RoundsPanel = new GridBagConstraints();
		gbc_rc5RoundsPanel.insets = new Insets(0, 0, 0, 5);
		gbc_rc5RoundsPanel.ipadx = 80;
		gbc_rc5RoundsPanel.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_rc5RoundsPanel.gridx = 1;
		gbc_rc5RoundsPanel.gridy = 1;
		cipherSettingsPanel.add(rc5RoundsPanel, gbc_rc5RoundsPanel);
		final GridBagLayout gbl_rc5RoundsPanel = new GridBagLayout();
		gbl_rc5RoundsPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_rc5RoundsPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_rc5RoundsPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_rc5RoundsPanel.rowWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		rc5RoundsPanel.setLayout(gbl_rc5RoundsPanel);

		rc5RoundsSpinner = new JSpinner();
		rc5RoundsSpinner.setEnabled(false);
		final GridBagConstraints gbc_rc5RoundsSpinner = new GridBagConstraints();
		gbc_rc5RoundsSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_rc5RoundsSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_rc5RoundsSpinner.gridx = 0;
		gbc_rc5RoundsSpinner.gridy = 0;
		rc5RoundsPanel.add(rc5RoundsSpinner, gbc_rc5RoundsSpinner);

		final JPanel rijndaelBlockSizePanel = new JPanel();
		rijndaelBlockSizePanel.setEnabled(false);
		rijndaelBlockSizePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Rijndael block size", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_rijndaelBlockSizePanel = new GridBagConstraints();
		gbc_rijndaelBlockSizePanel.insets = new Insets(0, 0, 0, 5);
		gbc_rijndaelBlockSizePanel.ipadx = 80;
		gbc_rijndaelBlockSizePanel.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_rijndaelBlockSizePanel.gridx = 2;
		gbc_rijndaelBlockSizePanel.gridy = 1;
		cipherSettingsPanel.add(rijndaelBlockSizePanel, gbc_rijndaelBlockSizePanel);
		final GridBagLayout gbl_rijndaelBlockSizePanel = new GridBagLayout();
		gbl_rijndaelBlockSizePanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_rijndaelBlockSizePanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_rijndaelBlockSizePanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_rijndaelBlockSizePanel.rowWeights = new double[]
		{
				0.0, 0.0, Double.MIN_VALUE
		};
		rijndaelBlockSizePanel.setLayout(gbl_rijndaelBlockSizePanel);

		rijndaelBlockSizeCB = new JComboBox<>();
		rijndaelBlockSizeCB.setEnabled(false);
		final GridBagConstraints gbc_rijndaelBlockSizeCB = new GridBagConstraints();
		gbc_rijndaelBlockSizeCB.insets = new Insets(0, 0, 5, 0);
		gbc_rijndaelBlockSizeCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_rijndaelBlockSizeCB.gridx = 0;
		gbc_rijndaelBlockSizeCB.gridy = 0;
		rijndaelBlockSizePanel.add(rijndaelBlockSizeCB, gbc_rijndaelBlockSizeCB);

		final JLabel rijndaelBlockSizeNote = new JLabel("Note: Rijndael-128 = AES");
		rijndaelBlockSizeNote.setEnabled(false);
		final GridBagConstraints gbc_rijndaelBlockSizeNote = new GridBagConstraints();
		gbc_rijndaelBlockSizeNote.gridx = 0;
		gbc_rijndaelBlockSizeNote.gridy = 1;
		rijndaelBlockSizePanel.add(rijndaelBlockSizeNote, gbc_rijndaelBlockSizeNote);

		final JPanel cipherAlgorithmModeAEADTagLenPanel = new JPanel();
		cipherAlgorithmModeAEADTagLenPanel.setEnabled(false);
		cipherAlgorithmModeAEADTagLenPanel.setBorder(new TitledBorder(null, "AEAD modes tag length", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_cipherAlgorithmModeAEADTagLenPanel = new GridBagConstraints();
		gbc_cipherAlgorithmModeAEADTagLenPanel.fill = GridBagConstraints.BOTH;
		gbc_cipherAlgorithmModeAEADTagLenPanel.gridx = 2;
		gbc_cipherAlgorithmModeAEADTagLenPanel.gridy = 0;
		cipherAlgorithmModePanel.add(cipherAlgorithmModeAEADTagLenPanel, gbc_cipherAlgorithmModeAEADTagLenPanel);
		final GridBagLayout gbl_cipherAlgorithmModeAEADTagLenPanel = new GridBagLayout();
		gbl_cipherAlgorithmModeAEADTagLenPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_cipherAlgorithmModeAEADTagLenPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_cipherAlgorithmModeAEADTagLenPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_cipherAlgorithmModeAEADTagLenPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		cipherAlgorithmModeAEADTagLenPanel.setLayout(gbl_cipherAlgorithmModeAEADTagLenPanel);

		cipherAlgorithmModeAEADTagLenCB = new JComboBox<>();
		cipherAlgorithmModeAEADTagLenCB.setEnabled(false);
		final GridBagConstraints gbc_cipherAlgorithmModeAEADTagLenCB = new GridBagConstraints();
		gbc_cipherAlgorithmModeAEADTagLenCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_cipherAlgorithmModeAEADTagLenCB.gridx = 0;
		gbc_cipherAlgorithmModeAEADTagLenCB.gridy = 0;
		cipherAlgorithmModeAEADTagLenPanel.add(cipherAlgorithmModeAEADTagLenCB, gbc_cipherAlgorithmModeAEADTagLenCB);
		// </editor-fold>

		// <editor-fold desc="List, ComboBox models">
		// Plain-text charset combo box model
		plainTextCharsetCB.setModel(new DefaultComboBoxModel<>(Encoding.values()));
		plainTextCharsetCB.setSelectedItem(Encoding.UTF_8); // UTF-8 is default charset

		// Key-text charset combo box model
		keyTextCharsetCB.setModel(new DefaultComboBoxModel<>(Encoding.values()));
		keyTextCharsetCB.setSelectedItem(Encoding.UTF_8); // UTF-8 is default charset

		// IV/Counter-text charset combo box model
		ivTextCharsetCB.setModel(new DefaultComboBoxModel<>(Encoding.values()));
		ivTextCharsetCB.setSelectedItem(Encoding.UTF_8); // UTF-8 is default charset

		keySizeCB.setModel(new DefaultComboBoxModel<>(new Integer[]
		{
				128, 192, 256
		}));
		keySizeCB.setSelectedIndex(0);

		keyTextField.setDocument(new PlainDocumentWithLimit());
		((PlainDocumentWithLimit) keyTextField.getDocument()).setLimit(16); // 128 bits = 16 bytes

		ivTextField.setDocument(new PlainDocumentWithLimit());
		((PlainDocumentWithLimit) ivTextField.getDocument()).setLimit(16); // 128 bits = 16 bytes

		paddingCharField.setDocument(new PlainDocumentWithLimit());
		((PlainDocumentWithLimit) paddingCharField.getDocument()).setLimit(1);
		paddingCharField.setText("+");

		cipherAlgorithmCB.setModel(new DefaultComboBoxModel<>(CipherAlgorithm.values()));
		cipherAlgorithmCB.setSelectedItem(CipherAlgorithm.AES);

		cipherAlgorithmModeCB.setModel(new DefaultComboBoxModel<>(CipherMode.SUNJCE_DEFAULT));
		cipherAlgorithmModeCB.setSelectedItem(CipherMode.CBC); // CBC mode is default mode

		cipherAlgorithmPaddingCB.setModel(new DefaultComboBoxModel<>(new CipherPadding[]
		{
				CipherPadding.NONE, CipherPadding.PKCS5, CipherPadding.ISO10126
		}));
		cipherAlgorithmPaddingCB.setSelectedItem(CipherPadding.PKCS5);

		final Vector<Integer> unitBytes = new Vector<>();
		final int blockSize = CipherAlgorithm.AES.getBlockSize();
		for (int i = 8; i <= blockSize; i += 8)
			unitBytes.add(i);
		cipherAlgorithmModeCFBOFBUnitBytesCB.setModel(new DefaultComboBoxModel<>(unitBytes));
		cipherAlgorithmModeCFBOFBUnitBytesCB.setSelectedItem(16);
		cipherAlgorithmModeCFBOFBUnitBytesPanel.setEnabled(false);
		cipherAlgorithmModeCFBOFBUnitBytesCB.setEnabled(false);

		gost28147SBoxCB.setModel(new DefaultComboBoxModel<>(new String[]
		{
				"Default", "E-TEST", "E-A", "E-B", "E-C", "E-D", "Param-Z", "D-TEST", "D-A"
		}));

		rc5RoundsSpinner.setModel(new SpinnerNumberModel(12, 1, 1073741822 /* Integer.MAX_VALUE / 2 - 1 */, 1));

		rijndaelBlockSizeCB.setModel(new DefaultComboBoxModel<>(new Integer[]
		{
				128, 160, 192, 224, 256
		}));

		cipherAlgorithmModeAEADTagLenCB.setModel(new DefaultComboBoxModel<>(new Integer[]
		{
				128, 120, 112, 104, 96
		}));
		// </editor-fold>

		// <editor-fold desc="ButtonGroups">
		final ButtonGroup plainModeButtonGroup = new ButtonGroup();
		plainModeButtonGroup.add(plainFromToTextButton);
		plainModeButtonGroup.add(plainFromToFileButton);

		final ButtonGroup encryptedModeButtonGroup = new ButtonGroup();
		encryptedModeButtonGroup.add(encryptedFromToTextButton);
		encryptedModeButtonGroup.add(encryptedFromToFileButton);
		// </editor-fold>

		// <editor-fold desc="Lambdas">
		base64EncryptedText.addActionListener(e -> base64EncryptedTextNote.setVisible(!base64EncryptedText.isSelected()));

		keySizeCB.addActionListener(e ->
		{
			final CipherAlgorithm algorithm = Optional.ofNullable((CipherAlgorithm) cipherAlgorithmCB.getSelectedItem()).orElse(CipherAlgorithm.AES);
			final String key = keyTextField.getText();

			int keySize = (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(256);
			if (algorithm.getMaxKeySize() != -1)
				keySize = algorithm.getMaxKeySize();

			((PlainDocumentWithLimit) keyTextField.getDocument()).setLimit(keySize / 8);
			keyTextField.updateUI();
			keyTextField.setText(key);
		});

		cipherAlgorithmCB.addActionListener(e ->
		{
			final CipherAlgorithm newAlgorithm = Optional.ofNullable((CipherAlgorithm) cipherAlgorithmCB.getSelectedItem()).orElse(CipherAlgorithm.AES);

			cipherAlgorithmModeCB.setModel(new DefaultComboBoxModel<>(newAlgorithm.getSupportedModes()));
			cipherAlgorithmModeCB.updateUI();

			cipherAlgorithmModeCB.setSelectedItem(CipherMode.CBC);

			final CipherMode cipherMode = Optional.ofNullable((CipherMode) cipherAlgorithmModeCB.getSelectedItem()).orElse(CipherMode.ECB);

			// Check the cipher mode is using IV while encryption/decryption
			final boolean usingIV = cipherMode.isUsingIV() || newAlgorithm.isStreamCipher();
			ivTextFieldPanel.setEnabled(usingIV);
			ivTextField.setEnabled(usingIV);

			cipherAlgorithmPaddingCB.setModel(new DefaultComboBoxModel<>(newAlgorithm.getSupportedPaddings()));
			cipherAlgorithmPaddingCB.updateUI();

			cipherAlgorithmPaddingCB.setSelectedItem(CipherPadding.PKCS5);

			final boolean keySizesAvailable = newAlgorithm.getAvailableKeySizes() != null;

			if (keySizesAvailable)
			{
				final int lastSelected = (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(256);
				keySizeCB.setModel(new DefaultComboBoxModel<>(newAlgorithm.getAvailableKeySizesBoxed()));
				keySizeCB.setSelectedItem(lastSelected);
			}
			keySizeCBPanel.setEnabled(keySizesAvailable);
			keySizeCB.setEnabled(keySizesAvailable);

			int keySizeBytes = -1;
			if (keySizesAvailable)
				keySizeBytes = (int) keySizeCB.getSelectedItem();
			if (newAlgorithm.getMaxKeySize() != -1)
				keySizeBytes = newAlgorithm.getMaxKeySize();
			final String key = keyTextField.getText();
//			if (keySizeBytes != -1)
			((PlainDocumentWithLimit) keyTextField.getDocument()).setLimit(keySizeBytes / 8);
			keyTextField.updateUI();
			keyTextField.setText(key);

			final boolean isCFBorOFB = cipherMode == CipherMode.CFB || cipherMode == CipherMode.OFB;
			cipherAlgorithmModeCFBOFBUnitBytesPanel.setEnabled(isCFBorOFB);
			cipherAlgorithmModeCFBOFBUnitBytesCB.setEnabled(isCFBorOFB);

			final boolean isAEAD = cipherMode.isAEADMode();
			cipherAlgorithmModeAEADTagLenPanel.setEnabled(isAEAD);
			cipherAlgorithmModeAEADTagLenCB.setEnabled(isAEAD);

			// If the new algorithm is supporting CFB or OFB mode
			if (Arrays.stream(newAlgorithm.getSupportedModes()).anyMatch(mode -> mode == CipherMode.CFB || mode == CipherMode.OFB))
			{
				final int lastSelectedUnitBytes = (int) Optional.ofNullable(cipherAlgorithmModeCFBOFBUnitBytesCB.getSelectedItem()).orElse(16);
				final int newBlockSize = newAlgorithm.getBlockSize();
				if (!newAlgorithm.isStreamCipher() && newBlockSize != -1)
				{
					final Vector<Integer> newUnitBytes = new Vector<>((newBlockSize - 8) / 8);
					for (int i = 8; i <= newBlockSize; i += 8)
						newUnitBytes.add(i);
					cipherAlgorithmModeCFBOFBUnitBytesCB.setModel(new DefaultComboBoxModel<>(newUnitBytes));
					cipherAlgorithmModeCFBOFBUnitBytesCB.updateUI();
					cipherAlgorithmModeCFBOFBUnitBytesCB.setSelectedItem(lastSelectedUnitBytes);
				}
			}

			// If the new algorithm is supporting AEAD modes
			if (Arrays.stream(newAlgorithm.getSupportedModes()).anyMatch(CipherMode::isAEADMode))
			{
				final int lastSelectedTagLength = (int) Optional.ofNullable(cipherAlgorithmModeAEADTagLenCB.getSelectedItem()).orElse(128);
				final int minTagLength = newAlgorithm.getMinAEADTagLength();
				final int maxTagLength = newAlgorithm.getMaxAEADTagLength();

				final Vector<Integer> newTagLengths = new Vector<>((maxTagLength - minTagLength) / 8);
				for (int i = maxTagLength; i >= minTagLength; i -= 8)
					newTagLengths.add(i);

				cipherAlgorithmModeAEADTagLenCB.setModel(new DefaultComboBoxModel<>(newTagLengths));
				cipherAlgorithmModeAEADTagLenCB.updateUI();
				cipherAlgorithmModeAEADTagLenCB.setSelectedItem(lastSelectedTagLength);
			}

			final boolean isGOST28147 = newAlgorithm == CipherAlgorithm.GOST28147;
			gost28147SBoxPanel.setEnabled(isGOST28147);
			gost28147SBoxCB.setEnabled(isGOST28147);

			final boolean isRC5 = newAlgorithm == CipherAlgorithm.RC5_32 || newAlgorithm == CipherAlgorithm.RC5_64;
			rc5RoundsPanel.setEnabled(isRC5);
			rc5RoundsSpinner.setEnabled(isRC5);

			final boolean isRijndael = newAlgorithm == CipherAlgorithm.Rijndael;
			rijndaelBlockSizePanel.setEnabled(isRijndael);
			rijndaelBlockSizeCB.setEnabled(isRijndael);
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
				catch (final Throwable ex)
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
				catch (final InterruptedException | ExecutionException | CipherException ex)
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

		cipherAlgorithmModeCB.addActionListener(e ->
		{
			final CipherAlgorithm cipherAlgorithm = Optional.ofNullable((CipherAlgorithm) cipherAlgorithmCB.getSelectedItem()).orElse(CipherAlgorithm.AES);
			final CipherMode cipherMode = Optional.ofNullable((CipherMode) cipherAlgorithmModeCB.getSelectedItem()).orElse(CipherMode.ECB);

			// Check the cipher mode is using IV while encryption/decryption
			final boolean usingIV = cipherMode.isUsingIV() || cipherAlgorithm.isStreamCipher();
			ivTextFieldPanel.setEnabled(usingIV);
			ivTextField.setEnabled(usingIV);

			((TitledBorder) ivTextFieldPanel.getBorder()).setTitle(cipherMode.isUsingNonce() ? "Encryption/Decryption nonce" : "Encryption/Decryption IV(Initial Vector)");
			ivTextFieldPanel.updateUI();

			// Check the cipher mode 'CFB' or 'OFB' selected
			final boolean isCFBorOFB = cipherMode == CipherMode.CFB || cipherMode == CipherMode.OFB;

			cipherAlgorithmModeCFBOFBUnitBytesPanel.setEnabled(isCFBorOFB);
			cipherAlgorithmModeCFBOFBUnitBytesCB.setEnabled(isCFBorOFB);

			final boolean isAEAD = cipherMode.isAEADMode();
			cipherAlgorithmModeAEADTagLenPanel.setEnabled(isAEAD);
			cipherAlgorithmModeAEADTagLenCB.setEnabled(isAEAD);

			if (isAEAD)
				cipherAlgorithmPaddingCB.setModel(new DefaultComboBoxModel<>(new CipherPadding[]
				{
						CipherPadding.NONE
				}));
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
		// </editor-fold>
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
			catch (@SuppressWarnings("unused") final IOException ignored)
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

	private byte[] getPlainBytes()
	{
		final Charset charset = StandardCharsets.UTF_8;
		final boolean fromFile = plainFromToFileButton.isSelected();

		if (fromFile)
		{
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

			try
			{
				return Files.readAllBytes(plainFile.toPath());
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

			return null;
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
			EventQueue.invokeLater(() ->
			{
				encryptedTextField.setText(new String(encryptedBytes, StandardCharsets.ISO_8859_1));
				encryptedTextField.updateUI();
			});

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

	private byte[] getKey(final CipherAlgorithm cipherAlgorithm, final byte paddingByte)
	{
		final Charset charset = StandardCharsets.UTF_8;

		final int defaultKeyLength = cipherAlgorithm.getAvailableKeySizes() != null ? (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(256) / 8 : -1;
		final int minKeySize = cipherAlgorithm.getMinKeySize();
		final int maxKeySize = cipherAlgorithm.getMaxKeySize();
		final int minKeyLength = minKeySize > 0 ? minKeySize : defaultKeyLength;
		final int maxKeyLength = maxKeySize > 0 ? maxKeySize : defaultKeyLength;

		final String keyString = keyTextField.getText();

		if (keyString == null || keyString.isEmpty())
			return null;

		final byte[] key = keyString.getBytes(charset);
		final byte[] paddedKey = CipherHelper.pad(key, minKeyLength, maxKeyLength, paddingByte);
		final int keyBytesSize = paddedKey.length;

		final String paddedKeyString = new String(paddedKey, charset);
		if (!Arrays.equals(key, paddedKey))
			EventQueue.invokeLater(() -> keyTextActualLabel.setText("Actual value is \"" + paddedKeyString + "\""));
		Main.LOGGER.info(String.format("key[%d]: \"%s\" -> paddedKey[%d]: \"%s\" - %d bytes (%d bits)", keyString.length(), keyString, paddedKeyString.length(), paddedKeyString, keyBytesSize, keyBytesSize << 3));

		return paddedKey;
	}

	private byte[] getInitialVector(final AbstractCipher cipher, final byte paddingByte)
	{
		final Charset charset = StandardCharsets.UTF_8;

		final String ivString = ivTextField.getText();

		if (ivString == null || ivString.isEmpty())
			return null;

		final int ivSize = cipher.getIVSize();
		final byte[] iv = ivString.getBytes(charset);
		final byte[] paddedIV = CipherHelper.pad(iv, ivSize, ivSize, paddingByte);
		final int paddedIVSize = paddedIV.length;

		final String paddedIVString = new String(paddedIV, charset);
		if (!Arrays.equals(iv, paddedIV))
			EventQueue.invokeLater(() -> ivTextActualLabel.setText("Actual value is \"" + paddedIVString + "\""));
		Main.LOGGER.info(String.format("iv[%d]: \"%s\" -> paddedIV[%d]: \"%s\" - %d bytes (%d bits)", ivString.length(), ivString, paddedIVString.length(), paddedIVString, paddedIVSize, paddedIVSize << 3));

		return paddedIV;
	}

	private AbstractCipher createCipher(final CipherAlgorithm algorithm, final CipherMode mode, final CipherPadding padding, final int unitBytes) throws CipherException
	{
		switch (algorithm)
		{
			case AES:
				return new AESCipher(algorithm, mode, padding, unitBytes, (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(256));
			case GOST28147:
				return new GOST28147Cipher(algorithm, mode, padding, unitBytes, Optional.ofNullable((String) gost28147SBoxCB.getSelectedItem()).orElse("Default").toUpperCase(Locale.ENGLISH));
			case RC2:
				return new RC2Cipher(algorithm, mode, padding, unitBytes);
			case RC5_32:
			case RC5_64:
				return new RC5Cipher(algorithm, mode, padding, unitBytes, (int) rc5RoundsSpinner.getValue());
			case Threefish:
				return new ThreefishCipher(algorithm, mode, padding, unitBytes, (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(256) / 8);
			case XSalsa20:
				return new XSalsa20Cipher(algorithm, mode, padding, unitBytes);
			case Rijndael:
			{
				final int blockSize = (int) Optional.ofNullable(rijndaelBlockSizeCB.getSelectedItem()).orElse(256);
				if (mode.isAEADMode())
					return new RijndaelAEADCipher(algorithm, mode, padding, blockSize);
				return new RijndaelCipher(algorithm, mode, padding, unitBytes, blockSize);
			}
			case LEA:
			{
				if (mode.isAEADMode())
					return new LEAAECipher(algorithm, mode, padding);
				return new LEACipher(algorithm, mode, padding);
			}
			default:
				return new SpiBasedCipher(algorithm, mode, padding, unitBytes);
		}
	}

	byte[] doEncrypt(byte[] bytes) throws CipherException
	{
		if (bytes == null || bytes.length == 0)
			throw new CipherException(CipherExceptionType.EMPTY_INPUT);

		final CipherAlgorithm cipherAlg = Optional.ofNullable((CipherAlgorithm) cipherAlgorithmCB.getSelectedItem()).orElse(CipherAlgorithm.AES);
		final CipherMode cipherMode = Optional.ofNullable((CipherMode) cipherAlgorithmModeCB.getSelectedItem()).orElse(CipherMode.ECB);

		// Padding
		final String paddingCharFieldText = paddingCharField.getText();
		if (paddingCharFieldText == null || paddingCharFieldText.isEmpty())
			throw new CipherException(CipherExceptionType.EMPTY_PADDING);

		final byte paddingByte = (byte) (paddingCharField.getText().charAt(0) & 0xFF);

		final AbstractCipher cipher = createCipher(cipherAlg, cipherMode, Optional.ofNullable((CipherPadding) cipherAlgorithmPaddingCB.getSelectedItem()).orElse(CipherPadding.PKCS5), (int) Optional.ofNullable(cipherAlgorithmModeCFBOFBUnitBytesCB.getSelectedItem()).orElse(16));

		Main.LOGGER.info(cipher.toString());

		final byte[] key = getKey(cipherAlg, paddingByte);
		if (key == null)
			throw new CipherException(CipherExceptionType.EMPTY_KEY);
		cipher.setKey(key);

		if (cipher.requireIV())
		{
			final byte[] iv = getInitialVector(cipher, paddingByte);
			if (iv == null)
				throw new CipherException(CipherExceptionType.EMPTY_IV);
			cipher.setIV(iv, (int) Optional.ofNullable(cipherAlgorithmModeAEADTagLenCB.getSelectedItem()).orElse(128));
		}

		cipher.init(Cipher.ENCRYPT_MODE);

		if (cipher.requirePaddedInput())
			bytes = CipherHelper.padMultipleOf(bytes, cipher.getBlockSize(), paddingByte);

		final byte[] result = cipher.doFinal(bytes);
		if (result == null)
			throw new CipherException(CipherExceptionType.EMPTY_RESPONSE);
		return base64EncryptedText.isSelected() ? Base64.getEncoder().encode(result) : result;
	}

	byte[] doDecrypt(byte[] bytes) throws CipherException
	{
		if (bytes == null || bytes.length == 0)
			return null;

		if (base64EncryptedText.isSelected())
			try
			{
				bytes = Base64.getDecoder().decode(bytes);
			}
			catch (final IllegalArgumentException e)
			{
				throw new CipherException(CipherExceptionType.BASE64_DECODE_EXCEPTION, new String(bytes, StandardCharsets.UTF_8));
			}

		final CipherAlgorithm cipherAlg = Optional.ofNullable((CipherAlgorithm) cipherAlgorithmCB.getSelectedItem()).orElse(CipherAlgorithm.AES);
		final CipherMode cipherMode = Optional.ofNullable((CipherMode) cipherAlgorithmModeCB.getSelectedItem()).orElse(CipherMode.ECB);

		// Padding
		final String paddingCharFieldText = paddingCharField.getText();
		if (paddingCharFieldText == null || paddingCharFieldText.isEmpty())
			return null;

		final byte paddingByte = (byte) (paddingCharField.getText().charAt(0) & 0xFF);

		final AbstractCipher cipher = createCipher(cipherAlg, cipherMode, Optional.ofNullable((CipherPadding) cipherAlgorithmPaddingCB.getSelectedItem()).orElse(CipherPadding.PKCS5), (int) Optional.ofNullable(cipherAlgorithmModeCFBOFBUnitBytesCB.getSelectedItem()).orElse(16));

		Main.LOGGER.info(cipher.toString());

		final byte[] key = getKey(cipherAlg, paddingByte);
		if (key == null)
			return null;
		cipher.setKey(key);

		if (cipher.requireIV())
		{
			final byte[] iv = getInitialVector(cipher, paddingByte);
			if (iv == null)
				return null;
			cipher.setIV(iv, (int) Optional.ofNullable(cipherAlgorithmModeAEADTagLenCB.getSelectedItem()).orElse(128));
		}

		cipher.init(Cipher.DECRYPT_MODE);

		if (cipher.requirePaddedInput())
			bytes = CipherHelper.padMultipleOf(bytes, cipher.getBlockSize(), paddingByte);

		return cipher.doFinal(bytes);
	}
}
