package fancytext.gui.encode;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import fancytext.Main;
import fancytext.exceptions.InputReadException;
import fancytext.exceptions.OutputWriteException;
import fancytext.gui.EncodedIOPanel;
import fancytext.utils.SimpleDocumentListener;
import fancytext.utils.StackTraceToString;
import fancytext.utils.encoding.Encoding;
import fancytext.utils.MultiThreading;
import fancytext.utils.encoding.HexEncoder;

public final class Encoder extends JPanel
{
	private static final long serialVersionUID = -5074560595722321177L;
	final EncodedIOPanel plainPanel;
	final JTextArea intermediateText;
	final JButton encodeButton;
	final JCheckBox realtimeEncodeCB;
	final EncodedIOPanel encodedPanel;

	public Encoder()
	{
		// Main border setup
		setBorder(new TitledBorder(null, "Encoder", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		setSize(1000, 1000);
		// Main layout setup
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{
				0, 0, 0
		};
		gridBagLayout.rowHeights = new int[]
		{
				0, 0, 0, 0, 0
		};
		gridBagLayout.columnWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		gridBagLayout.rowWeights = new double[]
		{
				1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE
		};
		setLayout(gridBagLayout);

		plainPanel = new EncodedIOPanel("Plain text", "From ", Encoding.UTF_8);
		final GridBagConstraints gbc_plainPanel = new GridBagConstraints();
		gbc_plainPanel.gridwidth = 2;
		gbc_plainPanel.insets = new Insets(0, 0, 5, 0);
		gbc_plainPanel.fill = GridBagConstraints.BOTH;
		gbc_plainPanel.gridx = 0;
		gbc_plainPanel.gridy = 0;
		add(plainPanel, gbc_plainPanel);

		final JPanel intermediatePanel = new JPanel();
		intermediatePanel.setBorder(new TitledBorder(null, "Intermediate text", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_intermediatePanel = new GridBagConstraints();
		gbc_intermediatePanel.gridheight = 2;
		gbc_intermediatePanel.insets = new Insets(0, 0, 5, 5);
		gbc_intermediatePanel.fill = GridBagConstraints.BOTH;
		gbc_intermediatePanel.gridx = 0;
		gbc_intermediatePanel.gridy = 1;
		add(intermediatePanel, gbc_intermediatePanel);
		intermediatePanel.setLayout(new BorderLayout(0, 0));

		intermediateText = new JTextArea();
		intermediateText.setEditable(false);
		intermediatePanel.add(new JScrollPane(intermediateText), BorderLayout.CENTER);

		encodeButton = new JButton("Encode");
		final GridBagConstraints gbc_encodeButton = new GridBagConstraints();
		gbc_encodeButton.insets = new Insets(0, 0, 5, 0);
		gbc_encodeButton.gridx = 1;
		gbc_encodeButton.gridy = 1;
		add(encodeButton, gbc_encodeButton);

		final JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_optionsPanel = new GridBagConstraints();
		gbc_optionsPanel.fill = GridBagConstraints.VERTICAL;
		gbc_optionsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_optionsPanel.gridx = 1;
		gbc_optionsPanel.gridy = 2;
		add(optionsPanel, gbc_optionsPanel);
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));

		realtimeEncodeCB = new JCheckBox("Real-time Encode");
		optionsPanel.add(realtimeEncodeCB);

		encodedPanel = new EncodedIOPanel("Encoded text", "To ", Encoding.BASE64);
		encodedPanel.textArea.setEditable(false);
		final GridBagConstraints gbc_encodedPanel = new GridBagConstraints();
		gbc_encodedPanel.gridwidth = 2;
		gbc_encodedPanel.fill = GridBagConstraints.BOTH;
		gbc_encodedPanel.gridx = 0;
		gbc_encodedPanel.gridy = 3;
		add(encodedPanel, gbc_encodedPanel);

		plainPanel.textArea.getDocument().addDocumentListener(new SimpleDocumentListener(() ->
		{
			if (isRealtimeEncodeEnabled())
				doEncode();
		}));

		encodeButton.addActionListener(a -> doEncode());

		realtimeEncodeCB.addActionListener(a ->
		{
			if (realtimeEncodeCB.isSelected())
				doEncode();
		});

		plainPanel.textButton.addActionListener(a ->
		{
			realtimeEncodeCB.setEnabled(isRealtimeEncodeAvailable());
			if (isRealtimeEncodeEnabled())
				doEncode();
		});

		plainPanel.fileButton.addActionListener(a -> realtimeEncodeCB.setEnabled(isRealtimeEncodeAvailable()));

		encodedPanel.textButton.addActionListener(a ->
		{
			realtimeEncodeCB.setEnabled(isRealtimeEncodeAvailable());
			if (isRealtimeEncodeEnabled())
				doEncode();
		});

		encodedPanel.fileButton.addActionListener(a -> realtimeEncodeCB.setEnabled(isRealtimeEncodeAvailable()));
	}

	boolean isRealtimeEncodeAvailable()
	{
		return plainPanel.textButton.isSelected() && encodedPanel.textButton.isSelected();
	}

	boolean isRealtimeEncodeEnabled()
	{
		return isRealtimeEncodeAvailable() && realtimeEncodeCB.isSelected();
	}

	private void printError(final String message, final Throwable t)
	{
		if (isRealtimeEncodeEnabled())
			encodedPanel.textArea.setText(message + ": " + StackTraceToString.convert(t));
		else
			Main.exceptionMessageBox(t.getClass().getCanonicalName(), message, t);
	}

	void doEncode()
	{
		encodeButton.setEnabled(false);
		Main.setBusyCursor(this, true);

		MultiThreading.getDefaultWorkers().submit(() ->
		{
			try
			{
				printOutput(updateIntermediate(readInput()));
			}
			catch (final Throwable t)
			{
				printError("Exception while progress", t);
			}
			finally
			{
				EventQueue.invokeLater(() ->
				{
					encodeButton.setEnabled(true);
					Main.setBusyCursor(this, false);
				});
			}
		});
	}

	private byte[] readInput() throws InputReadException
	{
		try
		{
			return plainPanel.read();
		}
		catch (final Throwable t)
		{
			throw new InputReadException(t);
		}
	}

	public byte[] updateIntermediate(final byte[] input)
	{
		if (input != null)
			if (input.length > 256)
				intermediateText.setText("Intermediate byte-array is too large; not displayed");
			else
				try
				{
					intermediateText.setText(Encoding.HEXADECIMAL.encode(input, " ", HexEncoder.HEX_UPPERCASE));
				}
				catch (final Throwable e)
				{
					intermediateText.setText("Failed to encode intermediate text: " + StackTraceToString.convert(e));
				}
		return input;
	}

	private void printOutput(final byte[] bytes) throws OutputWriteException
	{
		if (bytes == null)
			return;

		try
		{
			encodedPanel.write(bytes);
		}
		catch (final Throwable t)
		{
			throw new OutputWriteException(t);
		}
	}
}
