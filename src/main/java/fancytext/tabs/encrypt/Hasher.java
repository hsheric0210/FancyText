package fancytext.tabs.encrypt;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.security.*;
import java.util.Locale;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.zip.Adler32;
import java.util.zip.CRC32;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import fancytext.Main;
import fancytext.utils.CRC16;
import fancytext.utils.CharsetWrapper;
import fancytext.utils.MultiThreading;

public final class Hasher extends JPanel
{
	private static final long serialVersionUID = 8738449172274570395L;
	private final JTextPane inputTextField;
	private final JComboBox<HashAlgorithm> hashAlgorithmCB;
	private final JTextPane hashTextField;
	private final JComboBox<CharsetWrapper> inputTextCharsetCB;
	private final JTextField inputFileField;
	private final JRadioButton inputFromTextButton;
	private final JRadioButton inputFromFileButton;
	private final JComboBox<Integer> hashDigestSizeBitsCB;
	private final JPanel hashStateSizeBitsPanel;
	private final JComboBox<Integer> hashStateSizeBitsCB;
	private final JComboBox<Integer> hashStringRadixCB;
	private final JRadioButton hashStringUppercaseCB;
	private final JRadioButton hashStringLowercaseCB;
	private final JCheckBox hashStringTokenizeCB;
	private final JTextField hashStringTokenizeDelimiterField;
	private final JCheckBox hashStringHexPrefixCB;

	public enum HashAlgorithm
	{
		// <editor-fold desc="CRC-16">
		CRC_16("", "CRC-16", null, null),
		// </editor-fold>

		// <editor-fold desc="CRC-32">
		CRC_32("", "CRC-32", null, null),
		// </editor-fold>

		// <editor-fold desc="Adler-32">
		ADLER_32("", "Adler-32", null, null),
		// </editor-fold>

		// <editor-fold desc="Message digest algorithm">
		MD2("MD2", "MD-2", "SUN", null),

		MD4("MD4", "MD-4", "BC", null),

		MD5("MD5", "MD-5", "SUN", null),
		// </editor-fold>

		// <editor-fold desc="SHA 1, 2">
		SHA1("SHA-1", "SHA-1", "SUN", null),

		SHA2("SHA", "SHA-2", "SUN", new int[]
		{
				224, 256, 384, 512
		}),
		// </editor-fold>

		// <editor-fold desc="SHA-3 family">
		SHA3("SHA3", "SHA-3", "BC", new int[]
		{
				224, 256, 384, 512
		}),

		Keccak("KECCAK", "Keccak", "BC", new int[]
		{
				224, 256, 288, 384, 512
		}),
		SHAKE("SHAKE", "SHAKE", "BC", new int[]
		{
				256, 512
		}),
		// </editor-fold>

		// <editor-fold desc="BLAKE family">
		BLAKE2s("BLAKE2S", "BLAKE2s (a.k.a. Successor of BLAKE-256)", "BC", new int[]
		{
				128, 160, 224, 256
		}),

		BLAKE2b("BLAKE2B", "BLAKE2b (a.k.a. Successor of BLAKE-512)", "BC", new int[]
		{
				160, 256, 384, 512
		}),
		// </editor-fold>

		// <editor-fold desc="DSTU7564 a.k.a. Kupyna">
		DSTU7564("DSTU7564", "DSTU7564 (a.k.a. Kupyna)", "BC", new int[]
		{
				256, 384, 512
		}),
		// </editor-fold>

		// <editor-fold desc="GOST3411 family">
		GOST3411("GOST3411", "GOST3411", "BC", null),

		GOST3411_2012("GOST3411-2012", "GOST 3411-2012 (a.k.a. Streebog)", "BC", new int[]
		{
				256, 512
		}),
		// </editor-fold>

		// <editor-fold desc="Haraka">
		Haraka("HARAKA", "Haraka", "BC", new int[]
		{
				256, 512
		}),
		// </editor-fold>

		// <editor-fold desc="RIPE message digest">
		RIPEMD("RIPEMD", "RIPE Message Digest", "BC", new int[]
		{
				128, 160, 256, 320
		}),
		// </editor-fold>

		// <editor-fold desc="Skein">
		Skein("SKEIN", "Skein", "BC", new int[]
		{
				256, 512, 1024
		}),
		// </editor-fold>

		// <editor-fold desc="SM3">
		SM3("SM3", "SM3", "BC", null),
		// </editor-fold>

		// <editor-fold desc="Tiger">
		Tiger("TIGER", "Tiger", "BC", null),
		// </editor-fold>

		// <editor-fold desc="Whirlpool">
		Whirlpool("WHIRLPOOL", "Whirlpool", "BC", null);
		// </editor-fold>

		private final String id;
		private final String displayName;
		private final String providerName;
		private final int[] availableDigestSizes;

		HashAlgorithm(final String id, final String displayName, final String providerName, final int[] availableDigestSizes)
		{
			this.id = id;
			this.displayName = displayName;
			this.providerName = providerName;
			this.availableDigestSizes = availableDigestSizes;
		}

		String getId()
		{
			return id;
		}

		public String getProviderName()
		{
			return providerName;
		}

		public int[] getAvailableDigestSizes()
		{
			return availableDigestSizes;
		}

		public Integer[] getAvailableDigestSizesBoxed()
		{
			final Integer[] digestSizes = new Integer[availableDigestSizes.length];
			int j = 0;
			for (final int i : availableDigestSizes)
				digestSizes[j++] = i;

			return digestSizes;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	public Hasher()
	{
		// <editor-fold desc="UI Setup">
		// Main border setup
		setBorder(new TitledBorder(null, "Hasher", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		setSize(1000, 1000);

		// Main layout setup
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{
				0, 0
		};
		gridBagLayout.rowHeights = new int[]
		{
				0, 0, 0, 0, 0
		};
		gridBagLayout.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gridBagLayout.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE
		};
		setLayout(gridBagLayout);

		// Input panel
		final JPanel inputPanel = new JPanel();
		inputPanel.setBorder(new TitledBorder(null, "Input", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_inputPanel = new GridBagConstraints();
		gbc_inputPanel.insets = new Insets(0, 0, 5, 0);
		gbc_inputPanel.fill = GridBagConstraints.BOTH;
		gbc_inputPanel.gridx = 0;
		gbc_inputPanel.gridy = 0;
		add(inputPanel, gbc_inputPanel);
		final GridBagLayout gbl_inputPanel = new GridBagLayout();
		gbl_inputPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_inputPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_inputPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_inputPanel.rowWeights = new double[]
		{
				0.0, 0.0, Double.MIN_VALUE
		};
		inputPanel.setLayout(gbl_inputPanel);

		// Input panel - Input-text field panel
		final JPanel inputTextPanel = new JPanel();
		final GridBagConstraints gbc_inputTextPanel = new GridBagConstraints();
		gbc_inputTextPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_inputTextPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_inputTextPanel.insets = new Insets(0, 0, 5, 0);
		gbc_inputTextPanel.ipady = 40;
		gbc_inputTextPanel.gridx = 0;
		gbc_inputTextPanel.gridy = 0;
		inputPanel.add(inputTextPanel, gbc_inputTextPanel);
		inputTextPanel.setBorder(new TitledBorder(null, "Input-text", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_inputTextPanel = new GridBagLayout();
		gbl_inputTextPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_inputTextPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_inputTextPanel.columnWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		gbl_inputTextPanel.rowWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		inputTextPanel.setLayout(gbl_inputTextPanel);

		// Input panel - Input-text field panel - Input charset panel
		final JPanel inputTextCharsetPanel = new JPanel();
		final GridBagConstraints gbc_inputTextCharsetPanel = new GridBagConstraints();
		gbc_inputTextCharsetPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_inputTextCharsetPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_inputTextCharsetPanel.insets = new Insets(0, 0, 5, 0);
		gbc_inputTextCharsetPanel.gridx = 1;
		gbc_inputTextCharsetPanel.gridy = 0;
		inputTextPanel.add(inputTextCharsetPanel, gbc_inputTextCharsetPanel);
		inputTextCharsetPanel.setBorder(new TitledBorder(null, "Input-text charset", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_inputTextCharsetPanel = new GridBagLayout();
		gbl_inputTextCharsetPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_inputTextCharsetPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_inputTextCharsetPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_inputTextCharsetPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		inputTextCharsetPanel.setLayout(gbl_inputTextCharsetPanel);

		// Input panel - Input-text field panel - Input charset panel - Input charset combo box
		inputTextCharsetCB = new JComboBox<>();
		final GridBagConstraints gbc_inputTextCharsetCB = new GridBagConstraints();
		gbc_inputTextCharsetCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_inputTextCharsetCB.gridx = 0;
		gbc_inputTextCharsetCB.gridy = 0;
		inputTextCharsetPanel.add(inputTextCharsetCB, gbc_inputTextCharsetCB);

		// Input panel - Input from text radio button
		inputFromTextButton = new JRadioButton("Input from the text field");
		final GridBagConstraints gbc_inputFromTextButton = new GridBagConstraints();
		gbc_inputFromTextButton.gridheight = 2;
		gbc_inputFromTextButton.insets = new Insets(0, 0, 0, 5);
		gbc_inputFromTextButton.gridx = 0;
		gbc_inputFromTextButton.gridy = 0;
		inputTextPanel.add(inputFromTextButton, gbc_inputFromTextButton);

		// Input panel - Input-text field panel - Input-text field scroll panel
		final JScrollPane inputTextFieldScrollPane = new JScrollPane();
		final GridBagConstraints gbc_inputTextFieldScrollPane = new GridBagConstraints();
		gbc_inputTextFieldScrollPane.fill = GridBagConstraints.BOTH;
		gbc_inputTextFieldScrollPane.gridx = 1;
		gbc_inputTextFieldScrollPane.gridy = 1;
		inputTextPanel.add(inputTextFieldScrollPane, gbc_inputTextFieldScrollPane);

		// Input panel - Input-text field panel - Input-text field scroll panel - Input-text field
		inputTextField = new JTextPane();
		inputTextFieldScrollPane.setViewportView(inputTextField);

		// Input panel - Input file panel
		final JPanel inputFilePanel = new JPanel();
		inputFilePanel.setBorder(new TitledBorder(null, "Input-file", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_inputFilePanel = new GridBagConstraints();
		gbc_inputFilePanel.anchor = GridBagConstraints.PAGE_START;
		gbc_inputFilePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_inputFilePanel.gridx = 0;
		gbc_inputFilePanel.gridy = 1;
		inputPanel.add(inputFilePanel, gbc_inputFilePanel);
		final GridBagLayout gbl_inputFilePanel = new GridBagLayout();
		gbl_inputFilePanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_inputFilePanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_inputFilePanel.columnWeights = new double[]
		{
				0.0, 1.0, 0.0, Double.MIN_VALUE
		};
		gbl_inputFilePanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		inputFilePanel.setLayout(gbl_inputFilePanel);

		// Input panel - input file panel - Input from file radio button
		inputFromFileButton = new JRadioButton("Input from the file");
		final GridBagConstraints gbc_inputFromFileButton = new GridBagConstraints();
		gbc_inputFromFileButton.insets = new Insets(0, 0, 0, 5);
		gbc_inputFromFileButton.gridx = 0;
		gbc_inputFromFileButton.gridy = 0;
		inputFilePanel.add(inputFromFileButton, gbc_inputFromFileButton);

		// Input panel - input file panel - Input file field
		inputFileField = new JTextField();
		final GridBagConstraints gbc_inputFileField = new GridBagConstraints();
		gbc_inputFileField.anchor = GridBagConstraints.PAGE_START;
		gbc_inputFileField.insets = new Insets(0, 0, 0, 5);
		gbc_inputFileField.fill = GridBagConstraints.HORIZONTAL;
		gbc_inputFileField.gridx = 1;
		gbc_inputFileField.gridy = 0;
		inputFilePanel.add(inputFileField, gbc_inputFileField);
		inputFileField.setColumns(10);

		// Input panel - Input file panel - Input file find button
		final JButton inputFileFindButton = new JButton("Find");
		final GridBagConstraints gbc_inputFileFindButton = new GridBagConstraints();
		gbc_inputFileFindButton.fill = GridBagConstraints.VERTICAL;
		gbc_inputFileFindButton.anchor = GridBagConstraints.LINE_START;
		gbc_inputFileFindButton.gridx = 2;
		gbc_inputFileFindButton.gridy = 0;
		inputFilePanel.add(inputFileFindButton, gbc_inputFileFindButton);

		// Hash settings panel
		final JPanel hashSettingsPanel = new JPanel();
		hashSettingsPanel.setBorder(new TitledBorder(null, "Hash settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_hashSettingsPanel = new GridBagConstraints();
		gbc_hashSettingsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_hashSettingsPanel.fill = GridBagConstraints.BOTH;
		gbc_hashSettingsPanel.gridx = 0;
		gbc_hashSettingsPanel.gridy = 1;
		add(hashSettingsPanel, gbc_hashSettingsPanel);
		final GridBagLayout gbl_hashSettingsPanel = new GridBagLayout();
		gbl_hashSettingsPanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_hashSettingsPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_hashSettingsPanel.columnWeights = new double[]
		{
				0.0, 0.0, 0.0, Double.MIN_VALUE
		};
		gbl_hashSettingsPanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		hashSettingsPanel.setLayout(gbl_hashSettingsPanel);

		// Hash settings panel - Hash algorithm panel
		final JPanel hashAlgorithmPanel = new JPanel();
		hashAlgorithmPanel.setBorder(new TitledBorder(null, "Hash algorithm", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_hashAlgorithmPanel = new GridBagConstraints();
		gbc_hashAlgorithmPanel.ipadx = 60;
		gbc_hashAlgorithmPanel.insets = new Insets(0, 0, 0, 5);
		gbc_hashAlgorithmPanel.fill = GridBagConstraints.BOTH;
		gbc_hashAlgorithmPanel.gridx = 0;
		gbc_hashAlgorithmPanel.gridy = 0;
		hashSettingsPanel.add(hashAlgorithmPanel, gbc_hashAlgorithmPanel);
		final GridBagLayout gbl_hashAlgorithmPanel = new GridBagLayout();
		gbl_hashAlgorithmPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_hashAlgorithmPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_hashAlgorithmPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_hashAlgorithmPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		hashAlgorithmPanel.setLayout(gbl_hashAlgorithmPanel);

		// Hash settings panel - Hash algorithm panel - Hash algorithm combo box
		hashAlgorithmCB = new JComboBox<>();
		final GridBagConstraints gbc_hashAlgorithmCB = new GridBagConstraints();
		gbc_hashAlgorithmCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_hashAlgorithmCB.gridx = 0;
		gbc_hashAlgorithmCB.gridy = 0;
		hashAlgorithmPanel.add(hashAlgorithmCB, gbc_hashAlgorithmCB);

		// Hash button
		final JButton hashButton = new JButton("Hash");
		final GridBagConstraints gbc_hashButton = new GridBagConstraints();
		gbc_hashButton.ipadx = 40;
		gbc_hashButton.insets = new Insets(0, 0, 5, 0);
		gbc_hashButton.gridx = 0;
		gbc_hashButton.gridy = 2;
		add(hashButton, gbc_hashButton);

		// Hash field panel
		final JPanel hashTextFieldPanel = new JPanel();
		hashTextFieldPanel.setBorder(new TitledBorder(null, "Hash string", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_hashTextFieldPanel = new GridBagConstraints();
		gbc_hashTextFieldPanel.fill = GridBagConstraints.BOTH;
		gbc_hashTextFieldPanel.gridx = 0;
		gbc_hashTextFieldPanel.gridy = 3;
		add(hashTextFieldPanel, gbc_hashTextFieldPanel);
		final GridBagLayout gbl_hashTextFieldPanel = new GridBagLayout();
		gbl_hashTextFieldPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_hashTextFieldPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_hashTextFieldPanel.columnWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		gbl_hashTextFieldPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		hashTextFieldPanel.setLayout(gbl_hashTextFieldPanel);

		final JPanel hashStringOptionsPanel = new JPanel();
		hashStringOptionsPanel.setBorder(new TitledBorder(null, "Hash string options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_hashStringOptionsPanel = new GridBagConstraints();
		gbc_hashStringOptionsPanel.anchor = GridBagConstraints.LINE_START;
		gbc_hashStringOptionsPanel.insets = new Insets(0, 0, 0, 5);
		gbc_hashStringOptionsPanel.fill = GridBagConstraints.VERTICAL;
		gbc_hashStringOptionsPanel.gridx = 0;
		gbc_hashStringOptionsPanel.gridy = 0;
		hashTextFieldPanel.add(hashStringOptionsPanel, gbc_hashStringOptionsPanel);
		final GridBagLayout gbl_hashStringOptionsPanel = new GridBagLayout();
		gbl_hashStringOptionsPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_hashStringOptionsPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_hashStringOptionsPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_hashStringOptionsPanel.rowWeights = new double[]
		{
				1.0, 1.0, Double.MIN_VALUE
		};
		hashStringOptionsPanel.setLayout(gbl_hashStringOptionsPanel);

		final JPanel hashStringRadix = new JPanel();
		hashStringRadix.setBorder(new TitledBorder(null, "Radix", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_hashStringRadix = new GridBagConstraints();
		gbc_hashStringRadix.insets = new Insets(0, 0, 5, 0);
		gbc_hashStringRadix.fill = GridBagConstraints.BOTH;
		gbc_hashStringRadix.gridx = 0;
		gbc_hashStringRadix.gridy = 0;
		hashStringOptionsPanel.add(hashStringRadix, gbc_hashStringRadix);
		final GridBagLayout gbl_hashStringRadix = new GridBagLayout();
		gbl_hashStringRadix.columnWidths = new int[]
		{
				0, 0
		};
		gbl_hashStringRadix.rowHeights = new int[]
		{
				0, 0
		};
		gbl_hashStringRadix.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_hashStringRadix.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		hashStringRadix.setLayout(gbl_hashStringRadix);

		hashStringRadixCB = new JComboBox<>();
		final GridBagConstraints gbc_hashStringRadixCB = new GridBagConstraints();
		gbc_hashStringRadixCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_hashStringRadixCB.gridx = 0;
		gbc_hashStringRadixCB.gridy = 0;
		hashStringRadix.add(hashStringRadixCB, gbc_hashStringRadixCB);

		final JPanel hashStringHexOutputOptionsPanel = new JPanel();
		hashStringHexOutputOptionsPanel.setBorder(new TitledBorder(null, "Hex string options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_hashStringHexOutputOptionsPanel = new GridBagConstraints();
		gbc_hashStringHexOutputOptionsPanel.fill = GridBagConstraints.BOTH;
		gbc_hashStringHexOutputOptionsPanel.gridx = 0;
		gbc_hashStringHexOutputOptionsPanel.gridy = 1;
		hashStringOptionsPanel.add(hashStringHexOutputOptionsPanel, gbc_hashStringHexOutputOptionsPanel);
		final GridBagLayout gbl_hashStringHexOutputOptionsPanel = new GridBagLayout();
		gbl_hashStringHexOutputOptionsPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_hashStringHexOutputOptionsPanel.rowHeights = new int[]
		{
				0, 0, 0, 0
		};
		gbl_hashStringHexOutputOptionsPanel.columnWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		gbl_hashStringHexOutputOptionsPanel.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, Double.MIN_VALUE
		};
		hashStringHexOutputOptionsPanel.setLayout(gbl_hashStringHexOutputOptionsPanel);

		final JPanel hashStringCaseOptionPanel = new JPanel();
		final GridBagConstraints gbc_hashStringCaseOptionPanel = new GridBagConstraints();
		gbc_hashStringCaseOptionPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_hashStringCaseOptionPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_hashStringCaseOptionPanel.insets = new Insets(0, 0, 5, 0);
		gbc_hashStringCaseOptionPanel.gridx = 0;
		gbc_hashStringCaseOptionPanel.gridy = 0;
		hashStringHexOutputOptionsPanel.add(hashStringCaseOptionPanel, gbc_hashStringCaseOptionPanel);
		hashStringCaseOptionPanel.setBorder(new TitledBorder(null, "Character-case", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_hashStringCaseOptionPanel = new GridBagLayout();
		gbl_hashStringCaseOptionPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_hashStringCaseOptionPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_hashStringCaseOptionPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_hashStringCaseOptionPanel.rowWeights = new double[]
		{
				0.0, 0.0, Double.MIN_VALUE
		};
		hashStringCaseOptionPanel.setLayout(gbl_hashStringCaseOptionPanel);

		hashStringUppercaseCB = new JRadioButton("Upper case");
		hashStringUppercaseCB.setSelected(true);
		final GridBagConstraints gbc_hashStringUppercaseCB = new GridBagConstraints();
		gbc_hashStringUppercaseCB.anchor = GridBagConstraints.LINE_START;
		gbc_hashStringUppercaseCB.insets = new Insets(0, 0, 5, 0);
		gbc_hashStringUppercaseCB.gridx = 0;
		gbc_hashStringUppercaseCB.gridy = 0;
		hashStringCaseOptionPanel.add(hashStringUppercaseCB, gbc_hashStringUppercaseCB);

		hashStringLowercaseCB = new JRadioButton("Lower case");
		final GridBagConstraints gbc_hashStringLowercaseCB = new GridBagConstraints();
		gbc_hashStringLowercaseCB.anchor = GridBagConstraints.LINE_START;
		gbc_hashStringLowercaseCB.gridx = 0;
		gbc_hashStringLowercaseCB.gridy = 1;
		hashStringCaseOptionPanel.add(hashStringLowercaseCB, gbc_hashStringLowercaseCB);

		final JPanel hashStringTokenizePanel = new JPanel();
		final GridBagConstraints gbc_hashStringTokenizePanel = new GridBagConstraints();
		gbc_hashStringTokenizePanel.anchor = GridBagConstraints.PAGE_START;
		gbc_hashStringTokenizePanel.insets = new Insets(0, 0, 5, 0);
		gbc_hashStringTokenizePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_hashStringTokenizePanel.gridx = 0;
		gbc_hashStringTokenizePanel.gridy = 1;
		hashStringHexOutputOptionsPanel.add(hashStringTokenizePanel, gbc_hashStringTokenizePanel);
		hashStringTokenizePanel.setBorder(new TitledBorder(null, "Hash string tokenize", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagLayout gbl_hashStringTokenizePanel = new GridBagLayout();
		gbl_hashStringTokenizePanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_hashStringTokenizePanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_hashStringTokenizePanel.columnWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		gbl_hashStringTokenizePanel.rowWeights = new double[]
		{
				0.0, 0.0, Double.MIN_VALUE
		};
		hashStringTokenizePanel.setLayout(gbl_hashStringTokenizePanel);

		hashStringTokenizeCB = new JCheckBox("Tokenize the hash string");
		final GridBagConstraints gbc_hashStringTokenizeCB = new GridBagConstraints();
		gbc_hashStringTokenizeCB.anchor = GridBagConstraints.LINE_START;
		gbc_hashStringTokenizeCB.insets = new Insets(0, 0, 5, 0);
		gbc_hashStringTokenizeCB.gridx = 0;
		gbc_hashStringTokenizeCB.gridy = 0;
		hashStringTokenizePanel.add(hashStringTokenizeCB, gbc_hashStringTokenizeCB);

		hashStringTokenizeDelimiterField = new JTextField();
		hashStringTokenizeDelimiterField.setText("-");
		hashStringTokenizeDelimiterField.setEnabled(false);
		final GridBagConstraints gbc_hashStringTokenizeDelimiterField = new GridBagConstraints();
		gbc_hashStringTokenizeDelimiterField.fill = GridBagConstraints.HORIZONTAL;
		gbc_hashStringTokenizeDelimiterField.gridx = 0;
		gbc_hashStringTokenizeDelimiterField.gridy = 1;
		hashStringTokenizePanel.add(hashStringTokenizeDelimiterField, gbc_hashStringTokenizeDelimiterField);
		hashStringTokenizeDelimiterField.setColumns(10);

		hashStringHexPrefixCB = new JCheckBox("'0x' prefix");
		final GridBagConstraints gbc_hashStringHexPrefixCB = new GridBagConstraints();
		gbc_hashStringHexPrefixCB.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_hashStringHexPrefixCB.gridx = 0;
		gbc_hashStringHexPrefixCB.gridy = 2;
		hashStringHexOutputOptionsPanel.add(hashStringHexPrefixCB, gbc_hashStringHexPrefixCB);

		// Hash field panel - Hash field scroll pane
		final JScrollPane hashTextFieldScrollPane = new JScrollPane();
		final GridBagConstraints gbc_hashTextFieldScrollPane = new GridBagConstraints();
		gbc_hashTextFieldScrollPane.fill = GridBagConstraints.BOTH;
		gbc_hashTextFieldScrollPane.gridx = 1;
		gbc_hashTextFieldScrollPane.gridy = 0;
		hashTextFieldPanel.add(hashTextFieldScrollPane, gbc_hashTextFieldScrollPane);

		// Hash field panel - Hash field scroll pane - Hash field
		hashTextField = new JTextPane();
		hashTextField.setEditable(false);
		hashTextFieldScrollPane.setViewportView(hashTextField);

		hashStateSizeBitsPanel = new JPanel();
		hashStateSizeBitsPanel.setVisible(false);
		hashStateSizeBitsPanel.setBorder(new TitledBorder(null, "Hash state size bits", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_hashStateSizeBitsPanel = new GridBagConstraints();
		gbc_hashStateSizeBitsPanel.ipadx = 100;
		gbc_hashStateSizeBitsPanel.insets = new Insets(0, 0, 0, 5);
		gbc_hashStateSizeBitsPanel.fill = GridBagConstraints.BOTH;
		gbc_hashStateSizeBitsPanel.gridx = 1;
		gbc_hashStateSizeBitsPanel.gridy = 0;
		hashSettingsPanel.add(hashStateSizeBitsPanel, gbc_hashStateSizeBitsPanel);
		final GridBagLayout gbl_hashStateSizeBitsPanel = new GridBagLayout();
		gbl_hashStateSizeBitsPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_hashStateSizeBitsPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_hashStateSizeBitsPanel.columnWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		gbl_hashStateSizeBitsPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		hashStateSizeBitsPanel.setLayout(gbl_hashStateSizeBitsPanel);

		hashStateSizeBitsCB = new JComboBox<>();
		final GridBagConstraints gbc_hashStateSizeBitsCB = new GridBagConstraints();
		gbc_hashStateSizeBitsCB.insets = new Insets(0, 0, 0, 5);
		gbc_hashStateSizeBitsCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_hashStateSizeBitsCB.gridx = 0;
		gbc_hashStateSizeBitsCB.gridy = 0;
		hashStateSizeBitsPanel.add(hashStateSizeBitsCB, gbc_hashStateSizeBitsCB);

		final JPanel hashDigestSizeBitsPanel = new JPanel();
		hashDigestSizeBitsPanel.setBorder(new TitledBorder(null, "Hash digest size bits", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_hashSizeBitsPanel = new GridBagConstraints();
		gbc_hashSizeBitsPanel.ipadx = 100;
		gbc_hashSizeBitsPanel.fill = GridBagConstraints.BOTH;
		gbc_hashSizeBitsPanel.gridx = 2;
		gbc_hashSizeBitsPanel.gridy = 0;
		hashSettingsPanel.add(hashDigestSizeBitsPanel, gbc_hashSizeBitsPanel);
		final GridBagLayout gbl_hashSizeBitsPanel = new GridBagLayout();
		gbl_hashSizeBitsPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_hashSizeBitsPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_hashSizeBitsPanel.columnWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		gbl_hashSizeBitsPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		hashDigestSizeBitsPanel.setLayout(gbl_hashSizeBitsPanel);

		hashDigestSizeBitsCB = new JComboBox<>();
		final GridBagConstraints gbc_hashSizeBitsCB = new GridBagConstraints();
		gbc_hashSizeBitsCB.insets = new Insets(0, 0, 0, 5);
		gbc_hashSizeBitsCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_hashSizeBitsCB.gridx = 0;
		gbc_hashSizeBitsCB.gridy = 0;
		hashDigestSizeBitsPanel.add(hashDigestSizeBitsCB, gbc_hashSizeBitsCB);
		// </editor-fold>

		// <editor-fold desc="ButtonGroups">
		final ButtonGroup inputModeButtonGroup = new ButtonGroup();
		inputModeButtonGroup.add(inputFromTextButton);
		inputModeButtonGroup.add(inputFromFileButton);

		final ButtonGroup caseButtonGroup = new ButtonGroup();
		caseButtonGroup.add(hashStringUppercaseCB);
		caseButtonGroup.add(hashStringLowercaseCB);
		// </editor-fold>

		// <editor-fold desc="List, ComboBox models">
		inputFromTextButton.setSelected(true);

		inputTextCharsetCB.setModel(new DefaultComboBoxModel<>(CharsetWrapper.values()));
		inputTextCharsetCB.setSelectedItem(CharsetWrapper.UTF_8); // UTF-8 is default charset

		hashAlgorithmCB.setModel(new DefaultComboBoxModel<>(HashAlgorithm.values()));
		hashAlgorithmCB.setSelectedItem(HashAlgorithm.SHA2);

		hashDigestSizeBitsCB.setModel(new DefaultComboBoxModel<>(HashAlgorithm.SHA2.getAvailableDigestSizesBoxed()));
		hashDigestSizeBitsCB.setSelectedItem(256);

		hashStateSizeBitsCB.setModel(new DefaultComboBoxModel<>(HashAlgorithm.Skein.getAvailableDigestSizesBoxed())); // This combo box is exist only for Skein mode

		hashStringRadixCB.setModel(new DefaultComboBoxModel<>(new Integer[]
		{
				2, 8, 10, 16
		}));
		hashStringRadixCB.setSelectedItem(16);

		final JLabel hashStateSizeBitsLabel = new JLabel("bit");
		final GridBagConstraints gbc_hashStateSizeBitsLabel = new GridBagConstraints();
		gbc_hashStateSizeBitsLabel.gridx = 1;
		gbc_hashStateSizeBitsLabel.gridy = 0;
		hashStateSizeBitsPanel.add(hashStateSizeBitsLabel, gbc_hashStateSizeBitsLabel);
		hashDigestSizeBitsCB.setSelectedItem(256);

		final JLabel hashDigestSizeBitsLabel = new JLabel("bit");
		final GridBagConstraints gbc_hashDigestSizeBitsLabel = new GridBagConstraints();
		gbc_hashDigestSizeBitsLabel.gridx = 1;
		gbc_hashDigestSizeBitsLabel.gridy = 0;
		hashDigestSizeBitsPanel.add(hashDigestSizeBitsLabel, gbc_hashDigestSizeBitsLabel);

		inputFileField.setEnabled(false);
		inputFileFindButton.setEnabled(false);
		// </editor-fold>

		// <editor-fold desc="Lambdas">
		inputFromTextButton.addActionListener(e ->
		{
			if (inputFromTextButton.isSelected())
			{
				inputTextField.setEnabled(true);

				inputFileField.setEnabled(false);
				inputFileFindButton.setEnabled(false);
			}
		});
		inputFromFileButton.addActionListener(e ->
		{
			if (inputFromFileButton.isSelected())
			{
				inputFileField.setEnabled(true);
				inputFileFindButton.setEnabled(true);

				inputTextField.setEnabled(false);
			}
		});

		hashAlgorithmCB.addActionListener(e ->
		{
			final HashAlgorithm selected = Optional.ofNullable((HashAlgorithm) hashAlgorithmCB.getSelectedItem()).orElse(HashAlgorithm.SHA2);

			final boolean isDigestSizesAvailable = selected.getAvailableDigestSizes() != null;
			final boolean isSkein = selected == HashAlgorithm.Skein;

			final int stateSizeBits = (int) Optional.ofNullable(hashStateSizeBitsCB.getSelectedItem()).orElse(256);

			if (isSkein)
			{
				final Integer[] sizeBits;
				switch (stateSizeBits)
				{
					case 512:
						sizeBits = new Integer[]
						{
								128, 160, 224, 256, 384, 512
						};
						break;
					case 1024:
						sizeBits = new Integer[]
						{
								384, 512, 1024
						};
						break;
					default: // 256
						sizeBits = new Integer[]
						{
								128, 160, 224, 256
						};
						break;
				}

				final int lastSelected = (int) Optional.ofNullable(hashDigestSizeBitsCB.getSelectedItem()).orElse(256);
				hashDigestSizeBitsCB.setModel(new DefaultComboBoxModel<>(sizeBits));
				hashDigestSizeBitsCB.updateUI();

				hashDigestSizeBitsCB.setSelectedItem(lastSelected);
			}
			else if (isDigestSizesAvailable)
			{
				final int lastSelected = (int) Optional.ofNullable(hashDigestSizeBitsCB.getSelectedItem()).orElse(256);
				hashDigestSizeBitsCB.setModel(new DefaultComboBoxModel<>(selected.getAvailableDigestSizesBoxed()));
				hashDigestSizeBitsCB.updateUI();

				hashDigestSizeBitsCB.setSelectedItem(lastSelected);
			}
			hashDigestSizeBitsPanel.setEnabled(isDigestSizesAvailable);
			hashDigestSizeBitsCB.setEnabled(isDigestSizesAvailable);

			hashStateSizeBitsPanel.setVisible(isSkein);
		});

		hashStateSizeBitsCB.addActionListener(e ->
		{
			final int stateSizeBits = (int) Optional.ofNullable(hashStateSizeBitsCB.getSelectedItem()).orElse(256);

			final Integer[] sizeBits;
			switch (stateSizeBits)
			{
				case 512:
					sizeBits = new Integer[]
					{
							128, 160, 224, 256, 384, 512
					};
					break;
				case 1024:
					sizeBits = new Integer[]
					{
							384, 512, 1024
					};
					break;
				default: // 256
					sizeBits = new Integer[]
					{
							128, 160, 224, 256
					};
					break;
			}

			final int lastSelected = (int) Optional.ofNullable(hashDigestSizeBitsCB.getSelectedItem()).orElse(256);
			hashDigestSizeBitsCB.setModel(new DefaultComboBoxModel<>(sizeBits));
			hashDigestSizeBitsCB.updateUI();

			hashDigestSizeBitsCB.setSelectedItem(lastSelected);
		});

		hashButton.addActionListener(e ->
		{
			hashButton.setEnabled(false);
			Main.setBusyCursor(this, true);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doHash(getInputBytes()))
						Main.notificationMessageBox("Successfully hashed!", "Successfully hashed the message!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				finally
				{
					// Reset buttons
					EventQueue.invokeLater(() ->
					{
						hashButton.setEnabled(true);
						Main.setBusyCursor(this, false);
					});
				}
			});
		});

		inputFileFindButton.addActionListener(e ->
		{
			final String filePath = Main.generateFindFileGUI(inputFileField.getText());
			if (filePath != null)
				inputFileField.setText(filePath);
		});

		hashStringRadixCB.addActionListener(e ->
		{
			final boolean isHex = (int) Optional.ofNullable(hashStringRadixCB.getSelectedItem()).orElse(16) == 16;

			hashStringCaseOptionPanel.setEnabled(isHex);
			hashStringUppercaseCB.setEnabled(isHex);
			hashStringLowercaseCB.setEnabled(isHex);

			hashStringTokenizePanel.setEnabled(isHex);
			hashStringTokenizeCB.setEnabled(isHex);
			if (hashStringTokenizeCB.isSelected())
				hashStringTokenizeDelimiterField.setEnabled(isHex);
			hashStringHexPrefixCB.setEnabled(isHex);
		});

		hashStringTokenizeCB.addActionListener(e -> hashStringTokenizeDelimiterField.setEnabled(hashStringTokenizeCB.isSelected()));
		// </editor-fold>
	}

	private byte[] getInputBytes()
	{
		final Charset charset = Optional.ofNullable((CharsetWrapper) inputTextCharsetCB.getSelectedItem()).orElse(CharsetWrapper.UTF_8).getCharset();
		final boolean fromFile = inputFromFileButton.isSelected();

		if (fromFile)
		{
			final String inputFilePath = inputFileField.getText();

			final File inputFile = new File(inputFilePath);
			if (!inputFile.exists())
			{
				// If input file doesn't exists

				final String messageBuilder = "Failed to hash the given message." + Main.lineSeparator + Main.lineSeparator +

						"Input file doesn't exists." + Main.lineSeparator + Main.lineSeparator + // Print the cause of the problem

						"Input file path: " + inputFilePath + Main.lineSeparator; // Message to be hashed

				Main.exceptionMessageBox("Input file doesn't exists", messageBuilder, new NoSuchFileException(inputFilePath));

				return null;
			}

			try
			{
				return Files.readAllBytes(inputFile.toPath());
			}
			catch (final IOException e)
			{
				// If IOException occurs while reading all bytes from the input file

				final String messageBuilder = "Failed to hash the given message." + Main.lineSeparator + Main.lineSeparator +

						"IOException occurred while reading all bytes from the input file" + Main.lineSeparator + Main.lineSeparator + // Print the cause of the problem

						"Input file path: " + inputFilePath + Main.lineSeparator; // Message to be hashed

				Main.exceptionMessageBox(e.getClass().getCanonicalName(), messageBuilder, e);
			}

			return null;
		}
		return inputTextField.getText().getBytes(charset);
	}

	private boolean doHash(final byte[] messageBytes)
	{
		if (messageBytes == null)
			return false;

		String hashString;
		final HashAlgorithm hashAlgorithm = Optional.ofNullable((HashAlgorithm) hashAlgorithmCB.getSelectedItem()).orElse(HashAlgorithm.SHA2);
		final int messageBytesSize = messageBytes.length;

		final int stateSizeBits = (int) Optional.ofNullable(hashStateSizeBitsCB.getSelectedItem()).orElse(256);
		final int digestSizeBits = (int) Optional.ofNullable(hashDigestSizeBitsCB.getSelectedItem()).orElse(256);
		final int hashStringRadix = (int) Optional.ofNullable(hashStringRadixCB.getSelectedItem()).orElse(16);

		final String algorithmString = getAlgorithmString(hashAlgorithm, stateSizeBits, digestSizeBits);

		try
		{
			final boolean isHex = hashStringRadix == 16;

			if (hashAlgorithm.getProviderName() != null)
			{
				// Message digest algorithms supported by security provider
				final MessageDigest md = MessageDigest.getInstance(algorithmString, hashAlgorithm.getProviderName());
				md.update(messageBytes);
				final byte[] hash = md.digest();

				if (isHex && hashStringTokenizeCB.isSelected())
				{
					final StringJoiner tokenizer = new StringJoiner(hashStringTokenizeDelimiterField.getText());

					for (final byte b : hash)
						tokenizer.add(String.format("%02x", b));

					hashString = tokenizer.toString();
				}
				else
					hashString = new BigInteger(1, hash).toString(hashStringRadix);
			}
			else
			{
				// Check-sum algorithms supported by natively
				final long checksum;
				switch (hashAlgorithm)
				{
					case CRC_16:
					{
						final CRC16 crc16 = new CRC16();
						for (final byte b : messageBytes)
							crc16.update(b);
						checksum = crc16.getValue();
						break;
					}
					case CRC_32:
					{
						final CRC32 crc32 = new CRC32();
						crc32.update(messageBytes);
						checksum = crc32.getValue();
						break;
					}
					case ADLER_32:
					{
						final Adler32 adler32 = new Adler32();
						adler32.update(messageBytes);
						checksum = adler32.getValue();
						break;
					}
					default:
						throw new NoSuchAlgorithmException("Algorithm is not implemented");
				}

				hashString = new BigInteger(String.valueOf(checksum)).toString(hashStringRadix);
			}

			if (isHex)
			{
				if (hashStringLowercaseCB.isSelected())
					hashString = hashString.toLowerCase(Locale.ENGLISH);
				else
					hashString = hashString.toUpperCase(Locale.ENGLISH);

				if (hashStringHexPrefixCB.isSelected())
					hashString = "0x" + hashString;
			}
		}
		catch (final NoSuchAlgorithmException | RuntimeException | NoSuchProviderException e)
		{
			// If the specified hash algorithm is not supported
			hashString = String.format("Error: %s", e);

			final StringBuilder messageBuilder = new StringBuilder("Failed to hash the given message.").append(Main.lineSeparator).append(Main.lineSeparator);

			// Print the cause of the problem
			if (e instanceof RuntimeException)
				messageBuilder.append("It seems your input is not supported by the specified hash algorithm.").append(Main.lineSeparator);
			else if (e instanceof NoSuchProviderException)
				messageBuilder.append("It seems your Java version is not supporting the specified security provider.").append(Main.lineSeparator);
			else
				messageBuilder.append("It seems your Java version is not supporting the specified hash algorithm.").append(Main.lineSeparator);

			// Hash algorithm provider
			final Provider provider = Security.getProvider(hashAlgorithm.getProviderName());
			final String providerInfo = provider != null ? provider.getInfo() : "null";
			messageBuilder.append("Hash algorithm provider: ").append(hashAlgorithm.getProviderName()).append("(").append(providerInfo).append(")").append(Main.lineSeparator);

			// Hash algorithm
			messageBuilder.append("Hash algorithm: ").append(algorithmString).append("(").append(hashAlgorithm).append(")").append(Main.lineSeparator);

			// Message to be hashed
			messageBuilder.append("Message to be hashed: ").append(Main.filterStringForPopup(new String(messageBytes, StandardCharsets.UTF_8))).append(" (byteArrayLength: ").append(messageBytesSize).append(")").append(Main.lineSeparator);

			Main.exceptionMessageBox(e.getClass().getCanonicalName(), messageBuilder.toString(), e);
		}

		final String finalHashString = hashString;
		EventQueue.invokeLater(() -> hashTextField.setText(finalHashString));
		return true;
	}

	static String getAlgorithmString(final HashAlgorithm algorithm, final int stateSizeBits, final int digestSizeBits)
	{
		final StringBuilder algorithmBuilder = new StringBuilder(algorithm.getId());

		if (algorithm.getAvailableDigestSizes() != null)
			switch (algorithm)
			{
				case Skein:
					algorithmBuilder.append("-").append(stateSizeBits).append("-").append(digestSizeBits); /* example: SKEIN-256-256 */
					break;
				case RIPEMD:
				case SHAKE:
					algorithmBuilder.append(digestSizeBits); /* example: SHAKE256 */
					break;
				default:
					algorithmBuilder.append("-").append(digestSizeBits); /* example: SHA-256 */
			}

		return algorithmBuilder.toString();
	}
}
