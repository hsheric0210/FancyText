package fancytext.gui.encrypt.symmetric;

import java.awt.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Vector;

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
import fancytext.gui.EncodedIOPanel;
import fancytext.utils.encoding.Encoding;
import fancytext.utils.MultiThreading;
import fancytext.utils.PlainDocumentWithLimit;

public final class SymmetricKeyCipher extends JPanel
{
	private static final long serialVersionUID = -7257463203150608739L;
	private final JComboBox<Integer> keySizeCB;
	private final JComboBox<CipherMode> cipherAlgorithmModeCB;
	private final JComboBox<CipherPadding> cipherAlgorithmPaddingCB;
	private final JTextField paddingCharField;
	private final JComboBox<Integer> cipherAlgorithmModeCFBOFBUnitBytesCB;
	private final JComboBox<CipherAlgorithm> cipherAlgorithmCB;
	private final JPanel gost28147SBoxPanel;
	private final JComboBox<String> gost28147SBoxCB;
	private final JPanel rc5RoundsPanel;
	private final JSpinner rc5RoundsSpinner;
	private final JComboBox<Integer> rijndaelBlockSizeCB;
	private final JComboBox<Integer> cipherAlgorithmModeAEADTagLenCB;
	private final EncodedIOPanel plainTextPanel;
	private final EncodedIOPanel keyPanel;
	private final EncodedIOPanel ivPanel;
	private final EncodedIOPanel cipherTextPanel;

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
				0, 0, 0, 0
		};
		gridBagLayout.rowHeights = new int[]
		{
				0, 0, 0, 0, 0, 0
		};
		gridBagLayout.columnWeights = new double[]
		{
				0.0, 1.0, 1.0, 0.0
		};
		gridBagLayout.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, 0.0, 0.0, 1.0
		};
		setLayout(gridBagLayout);

		// Encrypt button
		final JButton encryptButton = new JButton("Encrypt");

		plainTextPanel = new EncodedIOPanel("Plain-text", Encoding.UTF_8);
		final GridBagConstraints gbc_plainPanel = new GridBagConstraints();
		gbc_plainPanel.gridwidth = 4;
		gbc_plainPanel.insets = new Insets(0, 0, 5, 0);
		gbc_plainPanel.fill = GridBagConstraints.BOTH;
		gbc_plainPanel.gridx = 0;
		gbc_plainPanel.gridy = 0;
		add(plainTextPanel, gbc_plainPanel);

		// Cipher settings panel
		final JPanel cipherSettingsPanel = new JPanel();
		cipherSettingsPanel.setBorder(new TitledBorder(null, "Cipher settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_cipherSettingsPanel = new GridBagConstraints();
		gbc_cipherSettingsPanel.gridwidth = 4;
		gbc_cipherSettingsPanel.insets = new Insets(0, 0, 5, 0);
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
		gbc_encryptButton.insets = new Insets(0, 0, 5, 5);
		gbc_encryptButton.gridx = 0;
		gbc_encryptButton.gridy = 2;
		add(encryptButton, gbc_encryptButton);

		// Key-text field panel
		keyPanel = new EncodedIOPanel("Cipher Key", Encoding.UTF_8);
		final GridBagConstraints gbc_keyTextFieldPanel = new GridBagConstraints();
		gbc_keyTextFieldPanel.insets = new Insets(10, 0, 5, 5);
		gbc_keyTextFieldPanel.fill = GridBagConstraints.BOTH;
		gbc_keyTextFieldPanel.gridx = 1;
		gbc_keyTextFieldPanel.gridy = 2;
		add(keyPanel, gbc_keyTextFieldPanel);

		// Decrypt button
		final JButton decryptButton = new JButton("Decrypt");
		final GridBagConstraints gbc_decryptButton = new GridBagConstraints();
		gbc_decryptButton.gridheight = 2;
		gbc_decryptButton.insets = new Insets(0, 0, 5, 0);
		gbc_decryptButton.gridx = 3;
		gbc_decryptButton.gridy = 2;
		add(decryptButton, gbc_decryptButton);

		// IV-text/Counter-text field panel
		ivPanel = new EncodedIOPanel("Cipher Initial Vector", Encoding.UTF_8);
		final GridBagConstraints gbc_ivTextFieldPanel = new GridBagConstraints();
		gbc_ivTextFieldPanel.insets = new Insets(0, 0, 5, 5);
		gbc_ivTextFieldPanel.fill = GridBagConstraints.BOTH;
		gbc_ivTextFieldPanel.gridx = 2;
		gbc_ivTextFieldPanel.gridy = 2;
		add(ivPanel, gbc_ivTextFieldPanel);

		// Padding character field panel
		final JPanel paddingCharFieldPanel = new JPanel();
		paddingCharFieldPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Key, IV (and Plain-text if cipher padding mode is None) Padding-character", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_paddingCharFieldPanel = new GridBagConstraints();
		gbc_paddingCharFieldPanel.gridwidth = 2;
		gbc_paddingCharFieldPanel.insets = new Insets(0, 0, 10, 5);
		gbc_paddingCharFieldPanel.fill = GridBagConstraints.BOTH;
		gbc_paddingCharFieldPanel.gridx = 1;
		gbc_paddingCharFieldPanel.gridy = 3;
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

		paddingCharField.setDocument(new PlainDocumentWithLimit());
		((PlainDocumentWithLimit) paddingCharField.getDocument()).setLimit(1);
		paddingCharField.setText("+");

		cipherTextPanel = new EncodedIOPanel("Cipher-text", Encoding.BASE64);
		cipherTextPanel.setBorder(new TitledBorder(null, "Encrypted message", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_encryptedPanel = new GridBagConstraints();
		gbc_encryptedPanel.gridwidth = 4;
		gbc_encryptedPanel.insets = new Insets(0, 0, 5, 0);
		gbc_encryptedPanel.fill = GridBagConstraints.BOTH;
		gbc_encryptedPanel.gridx = 0;
		gbc_encryptedPanel.gridy = 4;
		add(cipherTextPanel, gbc_encryptedPanel);

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

		keySizeCB.setModel(new DefaultComboBoxModel<>(new Integer[]
		{
				128, 192, 256
		}));
		keySizeCB.setSelectedIndex(0);

		cipherAlgorithmCB.setModel(new DefaultComboBoxModel<>(CipherAlgorithm.values()));
		cipherAlgorithmCB.setSelectedItem(CipherAlgorithm.AES);

		cipherAlgorithmModeCB.setModel(new DefaultComboBoxModel<>(CipherMode.SUNJCE_DEFAULT));
		cipherAlgorithmModeCB.setSelectedItem(CipherMode.CBC); // CBC mode is default mode

		cipherAlgorithmPaddingCB.setModel(new DefaultComboBoxModel<>(new CipherPadding[]
		{
				CipherPadding.NONE, CipherPadding.PKCS7, CipherPadding.ISO10126
		}));
		cipherAlgorithmPaddingCB.setSelectedItem(CipherPadding.PKCS7);

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

		keySizeCB.addActionListener(e ->
		{
			final CipherAlgorithm algorithm = Optional.ofNullable((CipherAlgorithm) cipherAlgorithmCB.getSelectedItem()).orElse(CipherAlgorithm.AES);

			int keySize = (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(256);
			if (algorithm.getMaxKeySize() != -1)
				keySize = algorithm.getMaxKeySize();

			keyPanel.setTextLimit(keySize / 8);
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
			ivPanel.setEnabled(usingIV);

			cipherAlgorithmPaddingCB.setModel(new DefaultComboBoxModel<>(newAlgorithm.getSupportedPaddings()));
			cipherAlgorithmPaddingCB.updateUI();

			cipherAlgorithmPaddingCB.setSelectedItem(CipherPadding.PKCS7);

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
			keyPanel.setTextLimit(keySizeBytes / 8);

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
				catch (final Throwable ex)
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
			ivPanel.setEnabled(usingIV);

			((TitledBorder) ivPanel.getBorder()).setTitle(cipherMode.isUsingNonce() ? "Encryption/Decryption nonce" : "Encryption/Decryption IV(Initial Vector)");
			ivPanel.updateUI();

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
		// </editor-fold>
	}

	private boolean doSavePlainBytes(final byte[] bytes)
	{
		if (bytes == null || bytes.length == 0)
			return false;

		try
		{
			plainTextPanel.write(bytes);
			return true;
		}
		catch (final Throwable e)
		{
			Main.exceptionMessageBox(e.getClass().getCanonicalName(), "Exception occurred while encoding and writing plain-text", e);
			return false;
		}
	}

	private byte[] getPlainBytes()
	{
		try
		{
			return plainTextPanel.read();
		}
		catch (final Throwable e)
		{
			Main.exceptionMessageBox(e.getClass().getCanonicalName(), "Exception occurred while reading, parsing and decoding plain-text", e);
			return null;
		}
	}

	private boolean doSaveEncryptedBytes(final byte[] bytes)
	{
		if (bytes == null || bytes.length == 0)
			return false;

		try
		{
			cipherTextPanel.write(bytes);
			return true;
		}
		catch (final Throwable e)
		{
			Main.exceptionMessageBox(e.getClass().getCanonicalName(), "Exception occurred while encoding and writing cipher-text", e);
			return false;
		}
	}

	private byte[] getEncryptedBytes()
	{
		try
		{
			return cipherTextPanel.read();
		}
		catch (final Throwable e)
		{
			Main.exceptionMessageBox(e.getClass().getCanonicalName(), "Exception occurred while reading, parsing and decoding cipher-text", e);
			return null;
		}
	}

	private byte[] getKey(final CipherAlgorithm cipherAlgorithm, final byte paddingByte)
	{
		final int defaultKeyLength = cipherAlgorithm.getAvailableKeySizes() != null ? (int) Optional.ofNullable(keySizeCB.getSelectedItem()).orElse(256) / 8 : -1;
		final int minKeySize = cipherAlgorithm.getMinKeySize();
		final int maxKeySize = cipherAlgorithm.getMaxKeySize();
		final int minKeyLength = minKeySize > 0 ? minKeySize : defaultKeyLength;
		final int maxKeyLength = maxKeySize > 0 ? maxKeySize : defaultKeyLength;

		final byte[] key;

		try
		{
			key = keyPanel.read();
		}
		catch (final Throwable t)
		{
			Main.exceptionMessageBox(t.getClass().getCanonicalName(), "Exception occurred while reading, parsing and decoding key", t);
			return null;
		}

		if (key == null || key.length <= 0)
			return null;

		// TODO
		// if (!Arrays.equals(key, paddedKey))
		// EventQueue.invokeLater(() -> keyTextActualLabel.setText("Actual value is \"" + paddedKeyString + "\""));

		return CipherHelper.pad(key, minKeyLength, maxKeyLength, paddingByte);
	}

	private byte[] getInitialVector(final AbstractCipher cipher, final byte paddingByte)
	{
		final byte[] iv;

		try
		{
			iv = ivPanel.read();
		}
		catch (final Throwable t)
		{
			Main.exceptionMessageBox(t.getClass().getCanonicalName(), "Exception occurred while reading, parsing and decoding initial vector", t);
			return null;
		}

		if (iv == null || iv.length <= 0)
			return null;

		final int ivSize = cipher.getIVSize();
		return CipherHelper.pad(iv, ivSize, ivSize, paddingByte);
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
			return null;

		final CipherAlgorithm cipherAlg = Optional.ofNullable((CipherAlgorithm) cipherAlgorithmCB.getSelectedItem()).orElse(CipherAlgorithm.AES);
		final CipherMode cipherMode = Optional.ofNullable((CipherMode) cipherAlgorithmModeCB.getSelectedItem()).orElse(CipherMode.ECB);

		final byte paddingByte = (byte) (requireNonBlank(paddingCharField.getText(), CipherExceptionType.EMPTY_PADDING).charAt(0) & 0xFF);

		final AbstractCipher cipher = createCipher(cipherAlg, cipherMode, Optional.ofNullable((CipherPadding) cipherAlgorithmPaddingCB.getSelectedItem()).orElse(CipherPadding.PKCS7), (int) Optional.ofNullable(cipherAlgorithmModeCFBOFBUnitBytesCB.getSelectedItem()).orElse(16));

		cipher.setKey(requireNonNull(getKey(cipherAlg, paddingByte), CipherExceptionType.EMPTY_KEY));

		if (cipher.requireIV())
			cipher.setIV(requireNonNull(getInitialVector(cipher, paddingByte), CipherExceptionType.EMPTY_IV), (int) Optional.ofNullable(cipherAlgorithmModeAEADTagLenCB.getSelectedItem()).orElse(128));

		cipher.init(Cipher.ENCRYPT_MODE);

		if (cipher.requirePaddedInput())
			bytes = CipherHelper.padMultipleOf(bytes, cipher.getBlockSize(), paddingByte);

		return cipher.doFinal(bytes);
	}

	byte[] doDecrypt(byte[] bytes) throws CipherException
	{
		if (bytes == null || bytes.length == 0)
			return null;

		final CipherAlgorithm cipherAlg = Optional.ofNullable((CipherAlgorithm) cipherAlgorithmCB.getSelectedItem()).orElse(CipherAlgorithm.AES);
		final CipherMode cipherMode = Optional.ofNullable((CipherMode) cipherAlgorithmModeCB.getSelectedItem()).orElse(CipherMode.ECB);

		final byte paddingByte = (byte) (requireNonBlank(paddingCharField.getText(), CipherExceptionType.EMPTY_PADDING).charAt(0) & 0xFF);

		final AbstractCipher cipher = createCipher(cipherAlg, cipherMode, Optional.ofNullable((CipherPadding) cipherAlgorithmPaddingCB.getSelectedItem()).orElse(CipherPadding.PKCS7), (int) Optional.ofNullable(cipherAlgorithmModeCFBOFBUnitBytesCB.getSelectedItem()).orElse(16));

		cipher.setKey(requireNonNull(getKey(cipherAlg, paddingByte), CipherExceptionType.EMPTY_KEY));

		if (cipher.requireIV())
			cipher.setIV(requireNonNull(getInitialVector(cipher, paddingByte), CipherExceptionType.EMPTY_IV), (int) Optional.ofNullable(cipherAlgorithmModeAEADTagLenCB.getSelectedItem()).orElse(128));

		cipher.init(Cipher.DECRYPT_MODE);

		if (cipher.requirePaddedInput())
			bytes = CipherHelper.padMultipleOf(bytes, cipher.getBlockSize(), paddingByte);

		return cipher.doFinal(bytes);
	}

	private static <T> T requireNonNull(final T ref, final CipherExceptionType ifNull) throws CipherException
	{
		if (ref == null)
			throw new CipherException(ifNull);
		return ref;
	}

	private static String requireNonBlank(final String stringRef, final CipherExceptionType ifNull) throws CipherException
	{
		if (stringRef == null || stringRef.isEmpty())
			throw new CipherException(ifNull);
		return stringRef;
	}
}
