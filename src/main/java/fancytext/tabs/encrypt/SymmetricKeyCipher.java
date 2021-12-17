package fancytext.tabs.encrypt;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.RijndaelEngine;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;

import fancytext.Main;
import fancytext.encrypt.symmetric.CipherAlgorithm;
import fancytext.encrypt.symmetric.CipherAlgorithmMode;
import fancytext.encrypt.symmetric.CipherAlgorithmPadding;
import fancytext.encrypt.symmetric.CipherExceptionType;
import fancytext.utils.CharsetWrapper;
import fancytext.utils.MultiThreading;
import fancytext.utils.PlainDocumentWithLimit;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherMode;
import kr.re.nsr.crypto.BlockCipherModeAE;
import kr.re.nsr.crypto.padding.PKCS5Padding;
import kr.re.nsr.crypto.symm.LEA.*;

public final class SymmetricKeyCipher extends JPanel
{
	private static final long serialVersionUID = -7257463203150608739L;
	private final JTextPane plainTextField;
	private final JTextPane encryptedTextField;
	private final JTextField keyTextField;
	private final JTextField ivTextField;
	private final JComboBox<CharsetWrapper> plainTextCharsetCB;
	private final JComboBox<Integer> keySizeCB;
	private final JComboBox<CipherAlgorithmMode> cipherAlgorithmModeCB;
	private final JComboBox<CipherAlgorithmPadding> cipherAlgorithmPaddingCB;
	private final JComboBox<CharsetWrapper> keyTextCharsetCB;
	private final JComboBox<CharsetWrapper> ivTextCharsetCB;
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
				0, 0
		};
		gbl_keyTextFieldPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_keyTextFieldPanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		keyTextFieldPanel.setLayout(gbl_keyTextFieldPanel);

		// Key-text field panel - Key-text field
		keyTextField = new JTextField();
		final GridBagConstraints gbc_keyTextField = new GridBagConstraints();
		gbc_keyTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_keyTextField.gridx = 0;
		gbc_keyTextField.gridy = 0;
		keyTextFieldPanel.add(keyTextField, gbc_keyTextField);

		// Key-text field panel - Charset panel
		final JPanel keyTextCharsetPanel = new JPanel();
		final GridBagConstraints gbc_keyTextCharsetPanel = new GridBagConstraints();
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
				0, 0
		};
		gbl_ivTextFieldPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_ivTextFieldPanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		ivTextFieldPanel.setLayout(gbl_ivTextFieldPanel);

		// IV-text/Counter-text field panel - IV-text/Counter-text field
		ivTextField = new JTextField();
		final GridBagConstraints gbc_ivTextField = new GridBagConstraints();
		gbc_ivTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_ivTextField.gridx = 0;
		gbc_ivTextField.gridy = 0;
		ivTextFieldPanel.add(ivTextField, gbc_ivTextField);

		// Plain-text/Decrypted-text field panel - Charset panel
		final JPanel ivTextCharsetPanel = new JPanel();
		final GridBagConstraints gbc_ivTextCharsetPanel = new GridBagConstraints();
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
		plainTextCharsetCB.setModel(new DefaultComboBoxModel<>(CharsetWrapper.values()));
		plainTextCharsetCB.setSelectedItem(CharsetWrapper.UTF_8); // UTF-8 is default charset

		// Key-text charset combo box model
		keyTextCharsetCB.setModel(new DefaultComboBoxModel<>(CharsetWrapper.values()));
		keyTextCharsetCB.setSelectedItem(CharsetWrapper.UTF_8); // UTF-8 is default charset

		// IV/Counter-text charset combo box model
		ivTextCharsetCB.setModel(new DefaultComboBoxModel<>(CharsetWrapper.values()));
		ivTextCharsetCB.setSelectedItem(CharsetWrapper.UTF_8); // UTF-8 is default charset

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

		cipherAlgorithmModeCB.setModel(new DefaultComboBoxModel<>(CipherAlgorithmMode.sunJCEDefaults()));
		cipherAlgorithmModeCB.setSelectedItem(CipherAlgorithmMode.CBC); // CBC mode is default mode

		cipherAlgorithmPaddingCB.setModel(new DefaultComboBoxModel<>(new CipherAlgorithmPadding[]
		{
				CipherAlgorithmPadding.NONE, CipherAlgorithmPadding.PKCS5, CipherAlgorithmPadding.ISO10126
		}));
		cipherAlgorithmPaddingCB.setSelectedItem(CipherAlgorithmPadding.PKCS5);

		final Vector<Integer> unitBytes = new Vector<>();
		final int blockSize = CipherAlgorithm.AES.getBlocksize();
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

			cipherAlgorithmModeCB.setSelectedItem(CipherAlgorithmMode.CBC);

			final CipherAlgorithmMode cipherMode = Optional.ofNullable((CipherAlgorithmMode) cipherAlgorithmModeCB.getSelectedItem()).orElse(CipherAlgorithmMode.ECB);

			// Check the cipher mode is using IV while encryption/decryption
			final boolean usingIV = cipherMode.isUsingIV() || newAlgorithm.isStreamCipher();
			ivTextFieldPanel.setEnabled(usingIV);
			ivTextField.setEnabled(usingIV);

			cipherAlgorithmPaddingCB.setModel(new DefaultComboBoxModel<>(newAlgorithm.getSupportedPaddings()));
			cipherAlgorithmPaddingCB.updateUI();

			cipherAlgorithmPaddingCB.setSelectedItem(CipherAlgorithmPadding.PKCS5);

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

			final boolean isCFBorOFB = cipherMode == CipherAlgorithmMode.CFB || cipherMode == CipherAlgorithmMode.OFB;
			cipherAlgorithmModeCFBOFBUnitBytesPanel.setEnabled(isCFBorOFB);
			cipherAlgorithmModeCFBOFBUnitBytesCB.setEnabled(isCFBorOFB);

			final boolean isAEAD = cipherMode.isAEADMode();
			cipherAlgorithmModeAEADTagLenPanel.setEnabled(isAEAD);
			cipherAlgorithmModeAEADTagLenCB.setEnabled(isAEAD);

			// If the new algorithm is supporting CFB or OFB mode
			if (Arrays.stream(newAlgorithm.getSupportedModes()).anyMatch(mode -> mode == CipherAlgorithmMode.CFB || mode == CipherAlgorithmMode.OFB))
			{
				final int lastSelectedUnitBytes = (int) Optional.ofNullable(cipherAlgorithmModeCFBOFBUnitBytesCB.getSelectedItem()).orElse(16);
				final int newBlockSize = newAlgorithm.getBlocksize();
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
			if (Arrays.stream(newAlgorithm.getSupportedModes()).anyMatch(CipherAlgorithmMode::isAEADMode))
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
				catch (final InterruptedException | ExecutionException | SymmetricKeyCryptionException ex)
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
				catch (final InterruptedException | ExecutionException | SymmetricKeyCryptionException ex)
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
			final CipherAlgorithmMode cipherMode = Optional.ofNullable((CipherAlgorithmMode) cipherAlgorithmModeCB.getSelectedItem()).orElse(CipherAlgorithmMode.ECB);

			// Check the cipher mode is using IV while encryption/decryption
			final boolean usingIV = cipherMode.isUsingIV() || cipherAlgorithm.isStreamCipher();
			ivTextFieldPanel.setEnabled(usingIV);
			ivTextField.setEnabled(usingIV);

			((TitledBorder) ivTextFieldPanel.getBorder()).setTitle(cipherMode.isUsingNonce() ? "Encryption/Decryption nonce" : "Encryption/Decryption IV(Initial Vector)");
			ivTextFieldPanel.updateUI();

			// Check the cipher mode 'CFB' or 'OFB' selected
			final boolean isCFBorOFB = cipherMode == CipherAlgorithmMode.CFB || cipherMode == CipherAlgorithmMode.OFB;

			cipherAlgorithmModeCFBOFBUnitBytesPanel.setEnabled(isCFBorOFB);
			cipherAlgorithmModeCFBOFBUnitBytesCB.setEnabled(isCFBorOFB);

			final boolean isAEAD = cipherMode.isAEADMode();
			cipherAlgorithmModeAEADTagLenPanel.setEnabled(isAEAD);
			cipherAlgorithmModeAEADTagLenCB.setEnabled(isAEAD);

			if (isAEAD)
				cipherAlgorithmPaddingCB.setModel(new DefaultComboBoxModel<>(new CipherAlgorithmPadding[]
				{
						CipherAlgorithmPadding.NONE
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

		final Charset charset = Optional.ofNullable((CharsetWrapper) plainTextCharsetCB.getSelectedItem()).orElse(CharsetWrapper.UTF_8).getCharset();
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
		final Charset charset = Optional.ofNullable((CharsetWrapper) plainTextCharsetCB.getSelectedItem()).orElse(CharsetWrapper.UTF_8).getCharset();
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

	private byte[] getKey(final CipherAlgorithm cipherAlgorithm, final char paddingChar)
	{
		final Charset charset = Optional.ofNullable((CharsetWrapper) keyTextCharsetCB.getSelectedItem()).orElse(CharsetWrapper.UTF_8).getCharset();

		final int defaultKeyLength = cipherAlgorithm.getAvailableKeySizes() != null ? (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(256) / 8 : -1;
		final int minKeySize = cipherAlgorithm.getMinKeySize();
		final int maxKeySize = cipherAlgorithm.getMaxKeySize();
		final int minKeyLength = minKeySize > 0 ? minKeySize : defaultKeyLength;
		final int maxKeyLength = maxKeySize > 0 ? maxKeySize : defaultKeyLength;

		final String keyString = keyTextField.getText();

		if (keyString == null || keyString.isEmpty())
			return null;

		final String paddedKeyString = clamp(charset, keyString, minKeyLength, maxKeyLength, paddingChar);
		final byte[] keyBytes = paddedKeyString.getBytes(charset);
		final int keyBytesSize = keyBytes.length;

		Main.LOGGER.info(String.format("key[%d]: \"%s\" -> paddedKey[%d]: \"%s\" - %d bytes (%d bits)", keyString.length(), keyString, paddedKeyString.length(), paddedKeyString, keyBytesSize, keyBytesSize << 3));

		return keyBytes;
	}

	private byte[] getInitialVector(final CipherAlgorithm cipherAlg, final CipherAlgorithmMode cipherMode, final char paddingChar, final int cipherBlockSize)
	{
		final Charset charset = Optional.ofNullable((CharsetWrapper) ivTextCharsetCB.getSelectedItem()).orElse(CharsetWrapper.UTF_8).getCharset();

		int ivSize = cipherBlockSize;
		if (cipherAlg == CipherAlgorithm.XSalsa20)
			ivSize = 24; // XSalsa20 requires exactly 24 bytes of IV
		else if (cipherMode == CipherAlgorithmMode.CCM)
			ivSize = 13; // nonce must have length from 7 to 13 octets
		else if (cipherAlg == CipherAlgorithm.LEA && cipherMode == CipherAlgorithmMode.GCM)
			ivSize = 12;

		if (ivSize == 0)
			ivSize = cipherAlg.getBlocksize() / 8;

		final String ivString = ivTextField.getText();

		final String paddedIV = clamp(charset, ivString, ivSize, ivSize, paddingChar);
		final byte[] ivBytes = paddedIV.getBytes(charset);
		final int ivBytesSize = ivBytes.length;

		Main.LOGGER.info(String.format("iv[%d]: \"%s\" -> paddedIV[%d]: \"%s\" - %d bytes (%d bits)", ivString.length(), ivString, paddedIV.length(), paddedIV, ivBytesSize, ivBytesSize << 3));

		return ivBytes;
	}

	byte[] doEncrypt(byte[] plainBytes) throws SymmetricKeyCryptionException
	{
		if (plainBytes == null || plainBytes.length == 0)
			return null;

		final Charset charset = Optional.ofNullable((CharsetWrapper) plainTextCharsetCB.getSelectedItem()).orElse(CharsetWrapper.UTF_8).getCharset();

		// <editor-fold desc="Cipher settings">
		final CipherAlgorithm cipherAlg = Optional.ofNullable((CipherAlgorithm) cipherAlgorithmCB.getSelectedItem()).orElse(CipherAlgorithm.AES);
		final CipherAlgorithmMode cipherMode = Optional.ofNullable((CipherAlgorithmMode) cipherAlgorithmModeCB.getSelectedItem()).orElse(CipherAlgorithmMode.ECB);
		final int cipherUnitBytes = (int) Optional.ofNullable(cipherAlgorithmModeCFBOFBUnitBytesCB.getSelectedItem()).orElse(16);
		final CipherAlgorithmPadding cipherPadding = Optional.ofNullable((CipherAlgorithmPadding) cipherAlgorithmPaddingCB.getSelectedItem()).orElse(CipherAlgorithmPadding.PKCS5);
		// </editor-fold>

		// <editor-fold desc="Key sizes">
		int keySizeBytes = -1;
		if (cipherAlg.getAvailableKeySizes() != null)
			keySizeBytes = (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(256) / 8;
		// </editor-fold>

		// Misc. options
		final boolean usingIV = cipherMode.isUsingIV() || cipherAlg.isStreamCipher();
		final boolean encodeEncrypted = base64EncryptedText.isSelected();
		final String paddingCharFieldText = paddingCharField.getText();

		// Check the resources
		if (paddingCharFieldText == null || paddingCharFieldText.isEmpty())
			return null;

		final char paddingChar = paddingCharField.getText().charAt(0);

		final String cipherAlgorithm = getAlgorithmString(cipherAlg, cipherMode, cipherPadding, keySizeBytes, cipherUnitBytes);

		final boolean isLEA = cipherAlg == CipherAlgorithm.LEA;

		final boolean isRijndael = cipherAlg == CipherAlgorithm.Rijndael;
		final int rijndaelBlockSize = (int) Optional.ofNullable(rijndaelBlockSizeCB.getSelectedItem()).orElse(256);

		try
		{

			// <editor-fold desc="Create the cipher">
			Cipher cipher = null;

			BufferedBlockCipher rijndaelCipher = null;
			AEADBlockCipher rijndaelAEADCipher = null;

			BlockCipherMode leaCipher = null;
			BlockCipherModeAE leaAEADCipher = null;

			final int cipherBlockSize;
			if (isLEA)
			{
				if (cipherMode.isAEADMode())
					leaAEADCipher = getLEAAEADCipher(cipherMode);
				else
					leaCipher = getLEACipher(cipherMode);
				cipherBlockSize = 16;
			}
			else if (isRijndael)
			{
				if (cipherMode.isAEADMode())
					rijndaelAEADCipher = createRijndaelAEADCipher(cipherMode, rijndaelBlockSize);
				else
					rijndaelCipher = createRijndaelCipher(cipherMode, cipherPadding, cipherUnitBytes, rijndaelBlockSize);
				cipherBlockSize = rijndaelBlockSize / 8;
			}
			else
			{
				cipher = Cipher.getInstance(cipherAlgorithm, cipherAlg.getProviderName());
				cipherBlockSize = cipher.getBlockSize();
			}
			// </editor-fold>

			dumpCipherAlgorithm(cipherAlg, cipherMode, cipherPadding);

			// <editor-fold desc="Create SecretKeySpec">
			final byte[] keyBytes = getKey(cipherAlg, paddingChar);
			final Key keySpec = new SecretKeySpec(keyBytes, cipherAlg.getId());
			// </editor-fold>

			// <editor-fold desc="Plain-text clamp">
			if (cipherPadding == CipherAlgorithmPadding.NONE && !cipherMode.isAEADMode() && cipherAlg.isPaddedInputRequired() && cipherBlockSize > 0 && plainBytes.length % cipherBlockSize != 0)
			{
				final String plainStr = new String(plainBytes, charset); // Convert the byte array to the string

				final int plainStringLength = plainStr.length();
				final int paddingCharSize = String.valueOf(paddingChar).getBytes(charset).length;
				final int paddingLength = cipherBlockSize * ((plainStringLength - plainStringLength % cipherBlockSize) / cipherBlockSize + 1) - plainStringLength;

				final String pad = IntStream.range(0, paddingLength).mapToObj(i -> String.valueOf(paddingChar)).collect(Collectors.joining("", plainStr, ""));

				plainBytes = new byte[plainBytes.length + paddingCharSize * paddingLength]; // Expand

				final byte[] paddedBytes = pad.getBytes(charset);
				System.arraycopy(paddedBytes, 0, plainBytes, 0, paddedBytes.length);

				// TODO: I know applying the clamp characters after converting the byte array to the string is the worst solution ever. Please someone fix it later. Or disable the auto-clamp for No_Padding option.
			}
			// </editor-fold>

			// <editor-fold desc="IV initialization">
			byte[] ivBytes = null;

			if (usingIV)
			{
				ivBytes = getInitialVector(cipherAlg, cipherMode, paddingChar, cipherBlockSize);
			}
			// </editor-fold>

			// <editor-fold desc="Cipher parameter spec initialization">
			AlgorithmParameterSpec cipherParameterSpec = null;
			final int aeadTagLength = (int) Optional.ofNullable(cipherAlgorithmModeAEADTagLenCB.getSelectedItem()).orElse(128);
			if (!isLEA && !isRijndael)
			{
				final String gost28147SBoxName = Optional.ofNullable((String) gost28147SBoxCB.getSelectedItem()).orElse("Default").toUpperCase(Locale.ENGLISH);
				final int rc5Rounds = (int) rc5RoundsSpinner.getValue();

				switch (cipherAlg)
				{
					case GOST28147:
						cipherParameterSpec = usingIV ? new GOST28147ParameterSpec(gost28147SBoxName, ivBytes) : new GOST28147ParameterSpec(gost28147SBoxName);
						break;
					case RC2:
						cipherParameterSpec = usingIV ? new RC2ParameterSpec(0, ivBytes) : new RC2ParameterSpec(0);
						break;
					case RC5_32:
						cipherParameterSpec = usingIV ? new RC5ParameterSpec(0, rc5Rounds, 32, ivBytes) : new RC5ParameterSpec(0, rc5Rounds, 32);
						break;
					case RC5_64:
						cipherParameterSpec = usingIV ? new RC5ParameterSpec(0, rc5Rounds, 64, ivBytes) : new RC5ParameterSpec(0, rc5Rounds, 64);
						break;
					default:
						if (usingIV)
							if (cipherAlg == CipherAlgorithm.AES && cipherMode == CipherAlgorithmMode.GCM) // AES-Dedicated parameter spec
								cipherParameterSpec = new GCMParameterSpec(aeadTagLength, ivBytes);
							else
								cipherParameterSpec = cipherMode.isAEADMode() ? new AEADParameterSpec(ivBytes, aeadTagLength, null) : new IvParameterSpec(ivBytes);
				}
			}
			// </editor-fold>

			// <editor-fold desc="Initialize the cipher and encrypt">
			final byte[] encryptedBytes;
			if (isLEA)
				if (leaAEADCipher == null)
				{
					// Non-AEAD
					if (cipherPadding == CipherAlgorithmPadding.PKCS5)
						leaCipher.setPadding(new PKCS5Padding(16));

					if (cipherMode == CipherAlgorithmMode.ECB)
						leaCipher.init(Mode.ENCRYPT, keyBytes);
					else
						leaCipher.init(Mode.ENCRYPT, keyBytes, ivBytes);

					encryptedBytes = leaCipher.doFinal(plainBytes);
				}
				else
				{
					// AEAD
					leaAEADCipher.init(Mode.ENCRYPT, keyBytes, ivBytes, aeadTagLength);
					encryptedBytes = leaAEADCipher.doFinal(plainBytes);
				}
			else if (isRijndael)
			{
				final CipherParameters params;
				if (usingIV)
					params = cipherMode.isAEADMode() ? new AEADParameters(new KeyParameter(keyBytes), aeadTagLength, ivBytes) : new ParametersWithIV(new KeyParameter(keyBytes), ivBytes);
				else
					params = new KeyParameter(keyBytes);

				/* https://stackoverflow.com/questions/50441959/how-can-i-do-for-rijndael-256-with-bouncycastle-api */

				if (rijndaelAEADCipher == null)
				{
					rijndaelCipher.init(true, params);

					final byte[] tmpEncryptedBytes = new byte[rijndaelCipher.getOutputSize(plainBytes.length)];

					int outOff = rijndaelCipher.processBytes(plainBytes, 0, plainBytes.length, tmpEncryptedBytes, 0);
					outOff += rijndaelCipher.doFinal(tmpEncryptedBytes, outOff); // FIXME: With CTS(Ciphertext Stealing) mode, org.bouncycastle.crypto.DataLengthException: need at least one block of input for CTS

					encryptedBytes = Arrays.copyOf(tmpEncryptedBytes, outOff);
				}
				else
				{
					rijndaelAEADCipher.init(true, params);

					final byte[] tmpEncryptedBytes = new byte[rijndaelAEADCipher.getOutputSize(plainBytes.length)];

					int outOff = rijndaelAEADCipher.processBytes(plainBytes, 0, plainBytes.length, tmpEncryptedBytes, 0);
					outOff += rijndaelAEADCipher.doFinal(tmpEncryptedBytes, outOff);

					encryptedBytes = Arrays.copyOf(tmpEncryptedBytes, outOff);
				}
			}
			else
			{
				if (cipherParameterSpec == null)
					cipher.init(Cipher.ENCRYPT_MODE, keySpec); // Without IV
				else
					cipher.init(Cipher.ENCRYPT_MODE, keySpec, cipherParameterSpec); // With IV

				encryptedBytes = cipher.doFinal(plainBytes);
			}
			// </editor-fold>

			// <editor-fold desc="Encode the encrypted-text with Base64 if the option is present">
			return encodeEncrypted ? Base64.getEncoder().encode(encryptedBytes) : encryptedBytes;
			// </editor-fold>
		}
		catch (final NoSuchProviderException e)
		{
			throw new SymmetricKeyCryptionException(CipherExceptionType.NO_SUCH_ALGORITHM_PROVIDER, cipherAlgorithm, e);
		}
		catch (final NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			throw new SymmetricKeyCryptionException(CipherExceptionType.UNSUPPORTED_CIPHER, cipherAlgorithm, e);
		}
		catch (final DataLengthException | AEADBadTagException e)
		{
			throw new SymmetricKeyCryptionException(CipherExceptionType.CORRUPTED_AEAD_TAG, e);
		}
		catch (final IllegalStateException | IllegalArgumentException | ArrayIndexOutOfBoundsException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | InvalidCipherTextException | NegativeArraySizeException e)
		{
			throw new SymmetricKeyCryptionException(CipherExceptionType.CORRUPTED_KEY_OR_INPUT, e);
		}
	}

	byte[] doDecrypt(byte[] encryptedBytes) throws SymmetricKeyCryptionException
	{
		// <editor-fold desc="Charsets setup">
		final Charset keyCharset = Optional.ofNullable((CharsetWrapper) keyTextCharsetCB.getSelectedItem()).orElse(CharsetWrapper.UTF_8).getCharset();
		final Charset ivCharset = Optional.ofNullable((CharsetWrapper) ivTextCharsetCB.getSelectedItem()).orElse(CharsetWrapper.UTF_8).getCharset();
		// </editor-fold>

		// <editor-fold desc="Cipher settings">
		final CipherAlgorithm cipherAlg = Optional.ofNullable((CipherAlgorithm) cipherAlgorithmCB.getSelectedItem()).orElse(CipherAlgorithm.AES);
		final CipherAlgorithmMode cipherMode = Optional.ofNullable((CipherAlgorithmMode) cipherAlgorithmModeCB.getSelectedItem()).orElse(CipherAlgorithmMode.ECB);
		final int cipherUnitBytes = (int) Optional.ofNullable(cipherAlgorithmModeCFBOFBUnitBytesCB.getSelectedItem()).orElse(16);
		final CipherAlgorithmPadding cipherPadding = Optional.ofNullable((CipherAlgorithmPadding) cipherAlgorithmPaddingCB.getSelectedItem()).orElse(CipherAlgorithmPadding.PKCS5);
		// </editor-fold>

		// <editor-fold desc="Key sizes">
		int keySizeBytes = -1;
		if (cipherAlg.getAvailableKeySizes() != null)
			keySizeBytes = (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(256) / 8; // 8 bit = 1 byte

		int minKeySizeBytes = keySizeBytes;
		int maxKeySizeBytes = keySizeBytes;

		// If cipher algorithm dependant key size is available, Use it instead.
		if (cipherAlg.getMinKeySize() != -1)
			minKeySizeBytes = cipherAlg.getMinKeySize() / 8; // 8 bit = 1 byte
		if (cipherAlg.getMaxKeySize() != -1)
			maxKeySizeBytes = cipherAlg.getMaxKeySize() / 8; // 8 bit = 1 byte
		// </editor-fold>

		// Misc. options
		final boolean usingIV = cipherMode.isUsingIV() || cipherAlg.isStreamCipher();
		final boolean decodeEncrypted = base64EncryptedText.isSelected();
		final String paddingCharFieldText = paddingCharField.getText();

		// <editor-fold desc="Key and IV">
		String key = keyTextField.getText();
		int keyLength = 0, keyBytesSize = 0;

		String iv = ivTextField.getText();
		int ivLength = 0, ivBytesSize = 0;
		// </editor-fold>

		// Check the resources
		if (encryptedBytes == null || key == null || encryptedBytes.length == 0 || key.isEmpty() || usingIV && (iv == null || iv.isEmpty()) || paddingCharFieldText == null || paddingCharFieldText.isEmpty())
			return null;

		final char paddingChar = paddingCharField.getText().charAt(0);

		// <editor-fold desc="Decode the encrypted-text with Base64 if the option is present">
		if (decodeEncrypted)
			try
			{
				encryptedBytes = Base64.getDecoder().decode(encryptedBytes);
			}
			catch (final IllegalArgumentException e)
			{
				throw new SymmetricKeyCryptionException(CipherExceptionType.BASE64_DECODE_EXCEPTION, null, "Base64-bytearray: " + new String(encryptedBytes, StandardCharsets.UTF_8), e);
			}
		// </editor-fold>

		// Key clamp
		key = clamp(keyCharset, key, minKeySizeBytes, maxKeySizeBytes, paddingChar);

		final String cipherAlgorithm = getAlgorithmString(cipherAlg, cipherMode, cipherPadding, keySizeBytes, cipherUnitBytes);
		final int stringByteArrayLength = encryptedBytes.length;

		final boolean isLEA = cipherAlg == CipherAlgorithm.LEA;
		final boolean isRijndael = cipherAlg == CipherAlgorithm.Rijndael;

		final int rijndaelBlockSize = (int) Optional.ofNullable(rijndaelBlockSizeCB.getSelectedItem()).orElse(256);

		try
		{
			// <editor-fold desc="Create the cipher">
			Cipher cipher = null;

			BufferedBlockCipher rijndaelCipher = null;
			AEADBlockCipher rijndaelAEADCipher = null;

			BlockCipherMode leaCipher = null;
			BlockCipherModeAE leaAEADCipher = null;

			final int cipherBlockSize;
			if (isLEA)
			{
				if (cipherMode.isAEADMode())
					leaAEADCipher = getLEAAEADCipher(cipherMode);
				else
					leaCipher = getLEACipher(cipherMode);
				cipherBlockSize = 16;
			}
			else if (isRijndael)
			{
				if (cipherMode.isAEADMode())
					rijndaelAEADCipher = createRijndaelAEADCipher(cipherMode, rijndaelBlockSize);
				else
					rijndaelCipher = createRijndaelCipher(cipherMode, cipherPadding, cipherUnitBytes, rijndaelBlockSize);
				cipherBlockSize = rijndaelBlockSize / 8;
			}
			else
			{
				cipher = Cipher.getInstance(cipherAlgorithm, cipherAlg.getProviderName());
				cipherBlockSize = cipher.getBlockSize();
			}
			// </editor-fold>

			dumpCipherAlgorithm(cipherAlg, cipherMode, cipherPadding);

			// <editor-fold desc="Create SecretKeySpec">
			final byte[] keyBytes = key.getBytes(keyCharset);
			keyLength = key.length();
			keyBytesSize = keyBytes.length;
			final Key keySpec = new SecretKeySpec(keyBytes, cipherAlg.getId());
			// </editor-fold>

			// <editor-fold desc="IV initialization">
			byte[] ivBytes = null;
			if (usingIV)
			{
				iv = getInitialVector(ivCharset, cipherAlg, cipherMode, iv, paddingChar, cipherBlockSize);

				ivBytes = iv.getBytes(ivCharset);
				ivLength = iv.length();
				ivBytesSize = ivBytes.length;
			}
			// </editor-fold>

			// <editor-fold desc="Cipher parameter spec initialization">
			AlgorithmParameterSpec cipherParameterSpec = null;
			final int aeadTagLength = (int) Optional.ofNullable(cipherAlgorithmModeAEADTagLenCB.getSelectedItem()).orElse(128);
			if (!isLEA && !isRijndael)
			{
				// Parameters setup
				final String gost28147SBoxName = Optional.ofNullable((String) gost28147SBoxCB.getSelectedItem()).orElse("Default").toUpperCase(Locale.ENGLISH);
				final int rc5Rounds = (int) rc5RoundsSpinner.getValue();

				switch (cipherAlg)
				{
					case GOST28147:
						cipherParameterSpec = usingIV ? new GOST28147ParameterSpec(gost28147SBoxName, ivBytes) : new GOST28147ParameterSpec(gost28147SBoxName);
						break;
					case RC2:
						cipherParameterSpec = usingIV ? new RC2ParameterSpec(0, ivBytes) : new RC2ParameterSpec(0);
						break;
					case RC5_32:
						cipherParameterSpec = usingIV ? new RC5ParameterSpec(0, rc5Rounds, 32, ivBytes) : new RC5ParameterSpec(0, rc5Rounds, 32);
						break;
					case RC5_64:
						cipherParameterSpec = usingIV ? new RC5ParameterSpec(0, rc5Rounds, 64, ivBytes) : new RC5ParameterSpec(0, rc5Rounds, 64);
						break;
					default:
						if (usingIV)
							if (cipherAlg == CipherAlgorithm.AES && cipherMode == CipherAlgorithmMode.GCM) // AES-Dedicated parameter spec
								cipherParameterSpec = new GCMParameterSpec(aeadTagLength, ivBytes);
							else
								cipherParameterSpec = cipherMode.isAEADMode() ? new AEADParameterSpec(ivBytes, aeadTagLength, null) : new IvParameterSpec(ivBytes);
				}
			}
			// </editor-fold>

			dumpKeyAndIV(key, keyLength, keyBytesSize, iv, ivLength, ivBytesSize);

			// <editor-fold desc="Initialize the cipher and decrypt">

			if (isLEA)
			{
				if (leaAEADCipher == null)
				{
					// Non-AEAD
					if (cipherPadding == CipherAlgorithmPadding.PKCS5)
						leaCipher.setPadding(new PKCS5Padding(16));

					if (cipherMode == CipherAlgorithmMode.ECB)
						leaCipher.init(Mode.DECRYPT, keyBytes);
					else
						leaCipher.init(Mode.DECRYPT, keyBytes, ivBytes);

					return leaCipher.doFinal(encryptedBytes);
				}
				// AEAD
				leaAEADCipher.init(Mode.DECRYPT, keyBytes, ivBytes, aeadTagLength);
				return leaAEADCipher.doFinal(encryptedBytes);
			}
			if (isRijndael)
			{
				final CipherParameters params;
				if (usingIV)
					params = cipherMode.isAEADMode() ? new AEADParameters(new KeyParameter(keyBytes), aeadTagLength, ivBytes) : new ParametersWithIV(new KeyParameter(keyBytes), ivBytes);
				else
					params = new KeyParameter(keyBytes);

				/* https://stackoverflow.com/questions/50441959/how-can-i-do-for-rijndael-256-with-bouncycastle-api */
				if (rijndaelAEADCipher == null)
				{
					rijndaelCipher.init(false, params);
					final byte[] tmpDecryptedBytes = new byte[rijndaelCipher.getOutputSize(encryptedBytes.length)];

					int outOff = rijndaelCipher.processBytes(encryptedBytes, 0, encryptedBytes.length, tmpDecryptedBytes, 0);
					outOff += rijndaelCipher.doFinal(tmpDecryptedBytes, outOff);

					return Arrays.copyOf(tmpDecryptedBytes, outOff);
				}
				rijndaelAEADCipher.init(false, params);

				final byte[] tmpDecryptedBytes = new byte[rijndaelAEADCipher.getOutputSize(encryptedBytes.length)];

				int outOff = rijndaelAEADCipher.processBytes(encryptedBytes, 0, encryptedBytes.length, tmpDecryptedBytes, 0);
				outOff += rijndaelAEADCipher.doFinal(tmpDecryptedBytes, outOff);

				return Arrays.copyOf(tmpDecryptedBytes, outOff);
			}
			if (cipherParameterSpec == null)
				cipher.init(Cipher.DECRYPT_MODE, keySpec); // Without IV
			else
				cipher.init(Cipher.DECRYPT_MODE, keySpec, cipherParameterSpec); // With IV

			return cipher.doFinal(encryptedBytes);
			// </editor-fold>
		}
		catch (final NoSuchProviderException e)
		{
			throw new SymmetricKeyCryptionException(CipherExceptionType.NO_SUCH_ALGORITHM_PROVIDER, cipherAlgorithm, e);
		}
		catch (final NoSuchAlgorithmException | NoSuchPaddingException e)
		{
			throw new SymmetricKeyCryptionException(CipherExceptionType.UNSUPPORTED_CIPHER, cipherAlgorithm, e);
		}
		catch (final DataLengthException | AEADBadTagException e)
		{
			throw new SymmetricKeyCryptionException(CipherExceptionType.CORRUPTED_AEAD_TAG, e);
		}
		catch (final IllegalStateException | IllegalArgumentException | ArrayIndexOutOfBoundsException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | InvalidCipherTextException | NegativeArraySizeException e)
		{
			throw new SymmetricKeyCryptionException(CipherExceptionType.CORRUPTED_KEY_OR_INPUT, e);
		}
	}

	private static void dumpCipherAlgorithm(final CipherAlgorithm alg, final CipherAlgorithmMode mode, final CipherAlgorithmPadding padding)
	{
		Main.LOGGER.info(String.format("algorithm: %s(%s) (provider: %s) / mode: %s / padding: %s(%s)", alg, alg.getId(), alg.getProviderName(), mode, padding, padding.getPaddingName()));
	}

	private static BlockCipherMode getLEACipher(final CipherAlgorithmMode mode)
	{

		switch (mode)
		{
			case ECB:
				return new ECB();
			case CBC:
			case PCBC:
				return new CBC();
			case CFB:
				return new CFB();
			case OFB:
				return new OFB();
			case CTR:
				return new CTR();
		}

		return null;
	}

	private static BlockCipherModeAE getLEAAEADCipher(final CipherAlgorithmMode mode)
	{

		switch (mode)
		{
			case CCM:
				return new CCM();
			case GCM:
				return new GCM();
		}

		return null;
	}

	/**
	 * @param  charset
	 *                     Charset used while padding
	 * @param  string
	 *                     String to apply truncation/pad
	 * @param  minLength
	 *                     Minimum string length; If the message length is shorter than this parameter, padding characters will be added to the tail of message to fill the space
	 * @param  maxLength
	 *                     Maximum string length; If the message length is loger than this parameter, the message will be truncated
	 * @param  paddingChar
	 *                     Padding character
	 * @return             The truncated/padded string
	 */
	private static String clamp(final Charset charset, final String string, final int minLength, final int maxLength, final char paddingChar)
	{
		final byte[] original = string.getBytes(charset);

		if (maxLength != -1 && original.length > maxLength)
		{
			// Truncation
			final byte[] truncated = new byte[maxLength];
			System.arraycopy(original, 0, truncated, 0, maxLength);
			return new String(truncated, charset);
		}
		if (original.length < minLength)
		{
			// Pad
			final byte[] padded = new byte[minLength];
			System.arraycopy(original, 0, padded, 0, original.length);
			final byte[] paddingCharBytes = String.valueOf(paddingChar).getBytes(charset);
			int index = original.length;
			final int indexLimit = padded.length;
			while (index < indexLimit)
			{
				System.arraycopy(paddingCharBytes, 0, padded, index, Math.min(indexLimit - index, paddingCharBytes.length));
				index += paddingCharBytes.length;
			}

			return new String(padded, charset);
		}

		return string;
	}

	private static String getAlgorithmString(final CipherAlgorithm algorithm, final CipherAlgorithmMode mode, final CipherAlgorithmPadding padding, final int keySizeBytes, final int unitBytes)
	{
		final StringBuilder algorithmBuilder = new StringBuilder(algorithm.getId());

		final int keySizeBits = keySizeBytes << 3;

		if (algorithm.getAvailableKeySizes() != null && algorithm == CipherAlgorithm.AES && padding == CipherAlgorithmPadding.NONE)
			algorithmBuilder.append("_").append(keySizeBits); // AES_128, AES_192, AES_256
		else if (algorithm == CipherAlgorithm.Threefish)
			algorithmBuilder.append("-").append(keySizeBits);

		/* <Algorithm>/<Mode>/<Padding> transformation format */
		if (mode == CipherAlgorithmMode.CFB || mode == CipherAlgorithmMode.OFB)
			algorithmBuilder.append("/").append(mode).append(unitBytes).append("/").append(padding.getPaddingName()); // CFBn, OFBn
		else
			algorithmBuilder.append("/").append(mode).append("/").append(padding.getPaddingName());

		// System.out.println(ret);
		return algorithmBuilder.toString();
	}

	private static BufferedBlockCipher createRijndaelCipher(final CipherAlgorithmMode cipherMode, final CipherAlgorithmPadding cipherPadding, final int unitBytes, final int blocksize)
	{

		BlockCipherPadding padding = null;
		switch (cipherPadding)
		{
			case ZERO_FILL:
				padding = new ZeroBytePadding();
				break;
			case PKCS5:
				padding = new PKCS7Padding();
				break;
			case ISO10126:
				padding = new ISO10126d2Padding();
				break;
			case X923:
				padding = new X923Padding();
				break;
			case ISO7816_4:
				padding = new ISO7816d4Padding();
				break;
			case TBC:
				padding = new TBCPadding();
		}

		BlockCipher cipher = null;

		BufferedBlockCipher bufferedCipher = null;
		switch (cipherMode)
		{
			case CBC:
			case PCBC:
				cipher = new CBCBlockCipher(new RijndaelEngine(blocksize));
				break;
			case CFB:
				cipher = new CFBBlockCipher(new RijndaelEngine(blocksize), unitBytes);
				break;
			case OFB:
				cipher = new OFBBlockCipher(new RijndaelEngine(blocksize), unitBytes);
				break;
			case CTR:
				cipher = new SICBlockCipher(new RijndaelEngine(blocksize));
				break;
			case CTS:
				bufferedCipher = new CTSBlockCipher(new CBCBlockCipher(new RijndaelEngine(blocksize)));
				break;
			default:
				cipher = new RijndaelEngine(blocksize);
		}

		if (bufferedCipher == null)
			bufferedCipher = padding == null ? new BufferedBlockCipher(cipher) : new PaddedBufferedBlockCipher(cipher, padding);

		return bufferedCipher;
	}

	private static AEADBlockCipher createRijndaelAEADCipher(final CipherAlgorithmMode cipherMode, final int blocksize)
	{
		final AEADBlockCipher cipher;

		switch (cipherMode)
		{
			case CCM:
				cipher = new CCMBlockCipher(new RijndaelEngine(blocksize));
				break;
			case GCM:
				cipher = new GCMBlockCipher(new RijndaelEngine(blocksize));
				break;
			default:
				cipher = null;
		}
		return cipher;
	}
}
