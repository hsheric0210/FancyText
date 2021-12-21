package fancytext.gui.encode;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import fancytext.Main;
import fancytext.utils.encoding.Encoding;
import fancytext.utils.MultiThreading;

public final class Base64Coder extends JPanel
{
	private static final long serialVersionUID = -5074560595722321177L;

	private final JComboBox<Encoding> plainTextCharsetCB;
	private final JTextPane plainTextField;
	private final JComboBox<Base64Mode> base64ModeCB;
	private final JTextPane encodedTextField;
	private final JCheckBox base64EncodeWithoutPadding;
	private final JTextField plainFileField;
	private final JTextField encodedFileField;
	private final JRadioButton plainFromToTextButton;
	private final JRadioButton plainFromToFileButton;
	private final JRadioButton encodedFromToTextButton;
	private final JRadioButton encodedFromToFileButton;

	private enum Base64Mode
	{
		DEFAULT("Normal encoder (RFC4648)"),
		MIME("MIME encoder (RFC2045)"),
		URL_SAFE("URL-safe encoder (RFC4648_URLSAFE)");

		private final String presetName;

		Base64Mode(final String name)
		{
			presetName = name;
		}

		@Override
		public String toString()
		{
			return presetName;
		}
	}

	public Base64Coder()
	{
		// Main border setup
		setBorder(new TitledBorder(null, "Base64 Converter", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		// Main layout setup
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{
				0, 0, 0
		};
		gridBagLayout.rowHeights = new int[]
		{
				0, 0, 0, 0, 0, 0
		};
		gridBagLayout.columnWeights = new double[]
		{
				1.0, 1.0, Double.MIN_VALUE
		};
		gridBagLayout.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE
		};
		setLayout(gridBagLayout);

		// Plain-text input/output field panel
		final JPanel plainPanel = new JPanel();
		plainPanel.setBorder(new TitledBorder(null, "Plain(Decoded) message", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_plainPanel = new GridBagConstraints();
		gbc_plainPanel.gridwidth = 2;
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

		final JPanel plainTextPanel = new JPanel();
		plainTextPanel.setBorder(new TitledBorder(null, "Plain-text (Decoded-text)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_plainTextPanel = new GridBagConstraints();
		gbc_plainTextPanel.ipady = 40;
		gbc_plainTextPanel.insets = new Insets(0, 0, 5, 0);
		gbc_plainTextPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_plainTextPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_plainTextPanel.gridx = 0;
		gbc_plainTextPanel.gridy = 0;
		plainPanel.add(plainTextPanel, gbc_plainTextPanel);
		final GridBagLayout gbl_plainTextPanel = new GridBagLayout();
		gbl_plainTextPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_plainTextPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_plainTextPanel.columnWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		gbl_plainTextPanel.rowWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		plainTextPanel.setLayout(gbl_plainTextPanel);

		plainFromToTextButton = new JRadioButton("Input the message from the text field / Output the decoded-message to the text field");
		final GridBagConstraints gbc_plainFromToTextButton = new GridBagConstraints();
		gbc_plainFromToTextButton.anchor = GridBagConstraints.LINE_START;
		gbc_plainFromToTextButton.gridheight = 2;
		gbc_plainFromToTextButton.insets = new Insets(0, 0, 5, 5);
		gbc_plainFromToTextButton.gridx = 0;
		gbc_plainFromToTextButton.gridy = 0;
		plainTextPanel.add(plainFromToTextButton, gbc_plainFromToTextButton);

		// Charset panel
		final JPanel plainTextCharsetPanel = new JPanel();
		final GridBagConstraints gbc_plainTextCharsetPanel = new GridBagConstraints();
		gbc_plainTextCharsetPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_plainTextCharsetPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_plainTextCharsetPanel.insets = new Insets(0, 0, 5, 0);
		gbc_plainTextCharsetPanel.gridx = 1;
		gbc_plainTextCharsetPanel.gridy = 0;
		plainTextPanel.add(plainTextCharsetPanel, gbc_plainTextCharsetPanel);
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

		// Charset combo box
		plainTextCharsetCB = new JComboBox<>();
		final GridBagConstraints gbc_plainTextCharsetCB = new GridBagConstraints();
		gbc_plainTextCharsetCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_plainTextCharsetCB.gridx = 0;
		gbc_plainTextCharsetCB.gridy = 0;
		plainTextCharsetPanel.add(plainTextCharsetCB, gbc_plainTextCharsetCB);

		// Plain-text input/output field
		plainTextField = new JTextPane();

		// Plain-text input/output field scroll pane
		final JScrollPane plainTextScrollPane = new JScrollPane();
		final GridBagConstraints gbc_plainTextScrollPane = new GridBagConstraints();
		gbc_plainTextScrollPane.fill = GridBagConstraints.BOTH;
		gbc_plainTextScrollPane.gridx = 1;
		gbc_plainTextScrollPane.gridy = 1;
		plainTextPanel.add(plainTextScrollPane, gbc_plainTextScrollPane);
		plainTextScrollPane.setViewportView(plainTextField);

		final JPanel plainFilePanel = new JPanel();
		plainFilePanel.setBorder(new TitledBorder(null, "Plain-file (Decoded-file)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
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

		plainFromToFileButton = new JRadioButton("Input the message from the file / Output the decoded-message to the file");
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

		// Encode button
		final JButton encodeButton = new JButton("Encode");
		final GridBagConstraints gbc_encodeButton = new GridBagConstraints();
		gbc_encodeButton.insets = new Insets(0, 0, 5, 5);
		gbc_encodeButton.gridx = 0;
		gbc_encodeButton.gridy = 1;
		add(encodeButton, gbc_encodeButton);

		// Decode button
		final JButton decodeButton = new JButton("Decode");
		final GridBagConstraints gbc_decodeButton = new GridBagConstraints();
		gbc_decodeButton.insets = new Insets(0, 0, 5, 0);
		gbc_decodeButton.gridx = 1;
		gbc_decodeButton.gridy = 1;
		add(decodeButton, gbc_decodeButton);

		// Encode button lambda
		encodeButton.addActionListener(e ->
		{
			encodeButton.setEnabled(false);
			decodeButton.setEnabled(false);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doSaveEncodedBytes(doEncode(getPlainBytes())))
						Main.notificationMessageBox("Successfully encoded!", "Successfully encoded the plain message!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				catch (final InterruptedException | ExecutionException ex)
				{
					Main.exceptionMessageBox("Exception while encoding", "An exception occurred while encoding.", ex);
				}
				finally
				{
					// Reset buttons
					EventQueue.invokeLater(() ->
					{
						encodeButton.setEnabled(true);
						decodeButton.setEnabled(true);
					});
				}
			});
		});

		// Decode button lambda
		decodeButton.addActionListener(e ->
		{
			encodeButton.setEnabled(false);
			decodeButton.setEnabled(false);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doSavePlainBytes(doDecode(getEncodedBytes())))
						Main.notificationMessageBox("Successfully decoded!", "Successfully decoded the Base64-encoded message!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				catch (final InterruptedException | ExecutionException ex)
				{
					Main.exceptionMessageBox("Exception while decoding", "An exception occurred while decoding.", ex);
				}
				finally
				{
					// Reset buttons
					EventQueue.invokeLater(() ->
					{
						encodeButton.setEnabled(true);
						decodeButton.setEnabled(true);
					});
				}
			});
		});

		final JPanel base64ModePanel = new JPanel();
		base64ModePanel.setBorder(new TitledBorder(null, "Base64 encoder/decoder mode", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_base64ModePanel = new GridBagConstraints();
		gbc_base64ModePanel.anchor = GridBagConstraints.LINE_START;
		gbc_base64ModePanel.insets = new Insets(0, 0, 5, 5);
		gbc_base64ModePanel.gridx = 0;
		gbc_base64ModePanel.gridy = 2;
		add(base64ModePanel, gbc_base64ModePanel);
		final GridBagLayout gbl_base64ModePanel = new GridBagLayout();
		gbl_base64ModePanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_base64ModePanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_base64ModePanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_base64ModePanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		base64ModePanel.setLayout(gbl_base64ModePanel);

		// Base64 mode combo box
		base64ModeCB = new JComboBox<>();
		final GridBagConstraints gbc_base64ModeCB = new GridBagConstraints();
		gbc_base64ModeCB.anchor = GridBagConstraints.LINE_START;
		gbc_base64ModeCB.gridx = 0;
		gbc_base64ModeCB.gridy = 0;
		base64ModePanel.add(base64ModeCB, gbc_base64ModeCB);

		// Base64 mode combo box model
		base64ModeCB.setModel(new DefaultComboBoxModel<>(Base64Mode.values()));

		// Base64 encode-without-padding check box
		base64EncodeWithoutPadding = new JCheckBox("Encode without padding");
		final GridBagConstraints gbc_base64EncodeWithoutPadding = new GridBagConstraints();
		gbc_base64EncodeWithoutPadding.anchor = GridBagConstraints.LINE_END;
		gbc_base64EncodeWithoutPadding.insets = new Insets(0, 0, 5, 0);
		gbc_base64EncodeWithoutPadding.gridx = 1;
		gbc_base64EncodeWithoutPadding.gridy = 2;
		add(base64EncodeWithoutPadding, gbc_base64EncodeWithoutPadding);

		final JPanel encodedPanel = new JPanel();
		encodedPanel.setBorder(new TitledBorder(null, "Encoded message", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_encodedPanel = new GridBagConstraints();
		gbc_encodedPanel.gridwidth = 2;
		gbc_encodedPanel.insets = new Insets(0, 0, 0, 5);
		gbc_encodedPanel.fill = GridBagConstraints.BOTH;
		gbc_encodedPanel.gridx = 0;
		gbc_encodedPanel.gridy = 4;
		add(encodedPanel, gbc_encodedPanel);
		final GridBagLayout gbl_encodedPanel = new GridBagLayout();
		gbl_encodedPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_encodedPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_encodedPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_encodedPanel.rowWeights = new double[]
		{
				0.0, 0.0, Double.MIN_VALUE
		};
		encodedPanel.setLayout(gbl_encodedPanel);

		final JPanel encodedTextPanel = new JPanel();
		encodedTextPanel.setBorder(new TitledBorder(null, "Encoded-text", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_encodedTextPanel = new GridBagConstraints();
		gbc_encodedTextPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_encodedTextPanel.insets = new Insets(0, 0, 5, 0);
		gbc_encodedTextPanel.ipady = 40;
		gbc_encodedTextPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_encodedTextPanel.gridx = 0;
		gbc_encodedTextPanel.gridy = 0;
		encodedPanel.add(encodedTextPanel, gbc_encodedTextPanel);
		final GridBagLayout gbl_encodedTextPanel = new GridBagLayout();
		gbl_encodedTextPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_encodedTextPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_encodedTextPanel.columnWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		gbl_encodedTextPanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		encodedTextPanel.setLayout(gbl_encodedTextPanel);

		encodedFromToTextButton = new JRadioButton("Input/Output the encoded-message from/to the text field");
		final GridBagConstraints gbc_encodedFromToTextButton = new GridBagConstraints();
		gbc_encodedFromToTextButton.insets = new Insets(0, 0, 0, 5);
		gbc_encodedFromToTextButton.gridx = 0;
		gbc_encodedFromToTextButton.gridy = 0;
		encodedTextPanel.add(encodedFromToTextButton, gbc_encodedFromToTextButton);

		// Encoded-text input/output field panel
		final JPanel encodedTextFieldPanel = new JPanel();
		final GridBagConstraints gbc_encodedTextFieldPanel = new GridBagConstraints();
		gbc_encodedTextFieldPanel.fill = GridBagConstraints.BOTH;
		gbc_encodedTextFieldPanel.gridx = 1;
		gbc_encodedTextFieldPanel.gridy = 0;
		encodedTextPanel.add(encodedTextFieldPanel, gbc_encodedTextFieldPanel);
		encodedTextFieldPanel.setBorder(new TitledBorder(null, "Encoded-text", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_encodedTextFieldPanel = new GridBagLayout();
		gbl_encodedTextFieldPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_encodedTextFieldPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_encodedTextFieldPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_encodedTextFieldPanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		encodedTextFieldPanel.setLayout(gbl_encodedTextFieldPanel);

		// Encoded-text input/output field
		encodedTextField = new JTextPane();

		// Encoded-text input/output field scroll pane
		final JScrollPane encodedTextScrollPane = new JScrollPane();
		final GridBagConstraints gbc_encodedTextScrollPane = new GridBagConstraints();
		gbc_encodedTextScrollPane.fill = GridBagConstraints.BOTH;
		gbc_encodedTextScrollPane.gridx = 0;
		gbc_encodedTextScrollPane.gridy = 0;
		encodedTextScrollPane.setViewportView(encodedTextField);
		encodedTextFieldPanel.add(encodedTextScrollPane, gbc_encodedTextScrollPane);

		final JPanel encodedFilePanel = new JPanel();
		encodedFilePanel.setBorder(new TitledBorder(null, "Encoded-file", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_encodedFilePanel = new GridBagConstraints();
		gbc_encodedFilePanel.anchor = GridBagConstraints.PAGE_START;
		gbc_encodedFilePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_encodedFilePanel.gridx = 0;
		gbc_encodedFilePanel.gridy = 1;
		encodedPanel.add(encodedFilePanel, gbc_encodedFilePanel);
		final GridBagLayout gbl_encodedFilePanel = new GridBagLayout();
		gbl_encodedFilePanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_encodedFilePanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_encodedFilePanel.columnWeights = new double[]
		{
				0.0, 1.0, 0.0, Double.MIN_VALUE
		};
		gbl_encodedFilePanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		encodedFilePanel.setLayout(gbl_encodedFilePanel);

		encodedFromToFileButton = new JRadioButton("Input/Output the encoded-message from/to the file");
		final GridBagConstraints gbc_encodedFromToFileButton = new GridBagConstraints();
		gbc_encodedFromToFileButton.insets = new Insets(0, 0, 0, 5);
		gbc_encodedFromToFileButton.gridx = 0;
		gbc_encodedFromToFileButton.gridy = 0;
		encodedFilePanel.add(encodedFromToFileButton, gbc_encodedFromToFileButton);

		encodedFileField = new JTextField();
		encodedFileField.setEnabled(false);
		encodedFileField.setColumns(10);
		final GridBagConstraints gbc_encodedFileField = new GridBagConstraints();
		gbc_encodedFileField.insets = new Insets(0, 0, 0, 5);
		gbc_encodedFileField.fill = GridBagConstraints.HORIZONTAL;
		gbc_encodedFileField.gridx = 1;
		gbc_encodedFileField.gridy = 0;
		encodedFilePanel.add(encodedFileField, gbc_encodedFileField);

		final JButton encodedFileFindButton = new JButton("Find");
		encodedFileFindButton.setEnabled(false);
		final GridBagConstraints gbc_encodedFileFindButton = new GridBagConstraints();
		gbc_encodedFileFindButton.gridx = 2;
		gbc_encodedFileFindButton.gridy = 0;
		encodedFilePanel.add(encodedFileFindButton, gbc_encodedFileFindButton);

		/* List, ComboBox models */

		// Charset combo box model
		plainTextCharsetCB.setModel(new DefaultComboBoxModel<>(Encoding.values()));
		plainTextCharsetCB.setSelectedItem(Encoding.UTF_8); // UTF-8 is default charset

		plainFromToTextButton.setSelected(true);
		encodedFromToTextButton.setSelected(true);

		/* ButtonGroups */

		final ButtonGroup plainModeButtonGroup = new ButtonGroup();
		plainModeButtonGroup.add(plainFromToTextButton);
		plainModeButtonGroup.add(plainFromToFileButton);

		final ButtonGroup encodedModeButtonGroup = new ButtonGroup();
		encodedModeButtonGroup.add(encodedFromToTextButton);
		encodedModeButtonGroup.add(encodedFromToFileButton);

		/* Lambdas */

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

		encodedFromToTextButton.addActionListener(e ->
		{
			if (encodedFromToTextButton.isSelected())
			{
				encodedTextField.setEnabled(true);

				encodedFileField.setEnabled(false);
				encodedFileFindButton.setEnabled(false);
			}
		});
		encodedFromToFileButton.addActionListener(e ->
		{
			if (encodedFromToFileButton.isSelected())
			{
				encodedFileField.setEnabled(true);
				encodedFileFindButton.setEnabled(true);

				encodedTextField.setEnabled(false);
			}
		});

		plainFileFindButton.addActionListener(e ->
		{
			final String filePath = Main.generateFindFileGUI(plainFileField.getText());
			if (filePath != null)
				plainFileField.setText(filePath);
		});

		encodedFileFindButton.addActionListener(e ->
		{
			final String filePath = Main.generateFindFileGUI(encodedFileField.getText());
			if (filePath != null)
				encodedFileField.setText(filePath);
		});
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
			catch (final IOException ioe)
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
				if (!plainFile.exists() && !plainFile.createNewFile())
				{
					Main.notificationMessageBox("Can't create the file", "Can't create the new file named '" + plainFilePath + "'", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null);
					return false;
				}

				Files.write(plainFile.toPath(), plainBytes, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			}
			catch (final IOException e)
			{
				// If IOException occurs while writing all bytes to the output file

				final StringBuilder messageBuilder = new StringBuilder("Failed to save decoded string to the file.").append(Main.lineSeparator).append(Main.lineSeparator);

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

			final File plainFile = new File(plainFilePath);
			if (!plainFile.exists())
			{
				// If input file doesn't exists

				final StringBuilder messageBuilder = new StringBuilder("Failed to encode the given string.").append(Main.lineSeparator).append(Main.lineSeparator);

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

				final StringBuilder messageBuilder = new StringBuilder("Failed to encode the given string.").append(Main.lineSeparator).append(Main.lineSeparator);

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

	private boolean doSaveEncodedBytes(final byte[] encodedBytes) throws ExecutionException, InterruptedException
	{
		if (encodedBytes == null || encodedBytes.length == 0)
			return false;

		final boolean toFile = encodedFromToFileButton.isSelected();
		if (toFile)
		{
			final String encodedFilePath = encodedFileField.getText();

			final File encodedFile = new File(encodedFilePath);

			boolean isFileHasData;

			try
			{
				isFileHasData = Files.readAllBytes(encodedFile.toPath()).length > 0;
			}
			catch (final IOException ignored)
			{
				isFileHasData = true;
			}

			if (encodedFile.exists() && isFileHasData)
				if (Main.warningMessageBox("File already exists and not empty", "The encoded-message output file is not empty." + Main.lineSeparator + "If you continue this action, IT WILL OVERWRITE THE EXISTING DATA!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, new String[]
				{
						"Continue", "Cancel"
				}, "Cancel").get() != 0)
					return false;

			try
			{
				if (!encodedFile.exists() && !encodedFile.createNewFile())
				{
					Main.notificationMessageBox("Can't create the file", "Can't create the new file named '" + encodedFilePath + "'", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, null);
					return false;
				}

				Files.write(encodedFile.toPath(), encodedBytes, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			}
			catch (final IOException e)
			{
				// If IOException occurs while writing all bytes to the output file

				final StringBuilder messageBuilder = new StringBuilder("Failed to save encoded string to the file.").append(Main.lineSeparator).append(Main.lineSeparator);

				// Print the cause of the problem
				messageBuilder.append("IOException occurred while create the output file or writing all bytes to the output file").append(Main.lineSeparator).append(Main.lineSeparator);

				// Encoded file path
				messageBuilder.append("Encoded file path: ").append(encodedFilePath).append(Main.lineSeparator);

				Main.exceptionMessageBox(e.getClass().getCanonicalName(), messageBuilder.toString(), e);
				return false;
			}
		}
		else
		{
			encodedTextField.setText(new String(encodedBytes, StandardCharsets.ISO_8859_1));
			encodedTextField.updateUI();
		}

		return true;
	}

	private byte[] getEncodedBytes()
	{
		final boolean toFile = encodedFromToFileButton.isSelected();

		if (toFile)
		{
			final String encodedFilePath = encodedFileField.getText();

			final File encodedFile = new File(encodedFilePath);
			if (!encodedFile.exists())
			{
				// If input file doesn't exists

				final StringBuilder messageBuilder = new StringBuilder("Failed to decode the given Base64-encoded string.").append(Main.lineSeparator).append(Main.lineSeparator);

				// Print the cause of the problem
				messageBuilder.append("Encoded input file doesn't exists.").append(Main.lineSeparator).append(Main.lineSeparator);

				// Encoded file path
				messageBuilder.append("Encoded file path: ").append(encodedFilePath).append(Main.lineSeparator);

				Main.exceptionMessageBox("Encoded file doesn't exists", messageBuilder.toString(), new NoSuchFileException(encodedFilePath));

				return null;
			}

			try
			{
				return Files.readAllBytes(encodedFile.toPath());
			}
			catch (final IOException e)
			{
				// If IOException occurs while reading all bytes from the input file

				final StringBuilder messageBuilder = new StringBuilder("Failed to decode the given Base64-encoded string").append(Main.lineSeparator).append(Main.lineSeparator);

				// Print the cause of the problem
				messageBuilder.append("IOException occurred while reading all bytes from the encoded file").append(Main.lineSeparator).append(Main.lineSeparator);

				// Encoded file path
				messageBuilder.append("Encoded file path: ").append(encodedFilePath).append(Main.lineSeparator);

				Main.exceptionMessageBox(e.getClass().getCanonicalName(), messageBuilder.toString(), e);
			}

			return null;
		}
		return encodedTextField.getText().getBytes(StandardCharsets.ISO_8859_1);
	}

	@SuppressWarnings("WeakerAccess")
	byte[] doEncode(final byte[] plainBytes)
	{
		if (plainBytes == null || plainBytes.length == 0)
			return null;

		Encoder encoder;
		switch (Optional.ofNullable((Base64Mode) base64ModeCB.getSelectedItem()).orElse(Base64Mode.DEFAULT))
		{
			case MIME:
				encoder = Base64.getMimeEncoder();
				break;
			case URL_SAFE:
				encoder = Base64.getUrlEncoder();
				break;
			default:
				encoder = Base64.getEncoder();
		}

		if (base64EncodeWithoutPadding.isSelected())
			encoder = encoder.withoutPadding();

		return encoder.encode(plainBytes);
	}

	@SuppressWarnings("WeakerAccess")
	byte[] doDecode(final byte[] encodedBytes)
	{
		if (encodedBytes == null || encodedBytes.length == 0)
			return null;

		final Decoder decoder;
		switch (Optional.ofNullable((Base64Mode) base64ModeCB.getSelectedItem()).orElse(Base64Mode.DEFAULT))
		{
			case MIME:
				decoder = Base64.getMimeDecoder();
				break;
			case URL_SAFE:
				decoder = Base64.getUrlDecoder();
				break;
			default:
				decoder = Base64.getDecoder();
		}

		try
		{
			return decoder.decode(encodedBytes);
		}
		catch (final IllegalArgumentException e)
		{
			Main.exceptionMessageBox(e.getClass().getCanonicalName(), "Failed to decode given Base64-encoded string" + Main.lineSeparator + Main.lineSeparator + "Base64 byte array of the input string is corrupted." + Main.lineSeparator, e);
			return null;
		}
	}
}
