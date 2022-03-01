package fancytext.gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import fancytext.Main;
import fancytext.utils.PlainDocumentWithLimit;
import fancytext.utils.encoding.Encoding;

public class EncodedIOPanel extends JPanel
{
	private final EncodingPanel encodingPanel;
	private final JPanel textPanel;
	public final JRadioButton textButton;
	public final JTextArea textArea;
	private final JPanel filePanel;
	private final JRadioButton fileButton;
	private final JTextField fileNameField;
	private final JButton fileFindButton;

	public EncodedIOPanel(final String title, final String prefix, final Encoding defaultEncoding)
	{
		setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP));
		final GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[]
		{
				0, 0, 0
		};
		layout.rowHeights = new int[]
		{
				125, 0, 0
		};
		layout.columnWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		layout.rowWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		setLayout(layout);

		encodingPanel = new EncodingPanel(defaultEncoding);
		final GridBagConstraints encodingPanelConstraints = new GridBagConstraints();
		encodingPanelConstraints.anchor = GridBagConstraints.PAGE_START;
		encodingPanelConstraints.gridheight = 2;
		encodingPanelConstraints.insets = new Insets(0, 0, 0, 5);
		encodingPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		encodingPanelConstraints.gridx = 0;
		encodingPanelConstraints.gridy = 0;
		add(encodingPanel, encodingPanelConstraints);

		textPanel = new JPanel();
		textPanel.setBorder(BorderFactory.createTitledBorder(null, prefix + "Text", TitledBorder.LEADING, TitledBorder.TOP));
		final GridBagConstraints gbc_textPanel = new GridBagConstraints();
		gbc_textPanel.insets = new Insets(0, 0, 5, 0);
		gbc_textPanel.fill = GridBagConstraints.BOTH;
		gbc_textPanel.gridx = 1;
		gbc_textPanel.gridy = 0;
		add(textPanel, gbc_textPanel);
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.LINE_AXIS));

		textButton = new JRadioButton("Text");
		textButton.setSelected(true);
		textPanel.add(textButton);

		final JScrollPane textScroll = new JScrollPane();
		textPanel.add(textScroll);

		textArea = new JTextArea();
		textArea.setDocument(new PlainDocumentWithLimit());
		textScroll.setViewportView(textArea);

		filePanel = new JPanel();
		filePanel.setBorder(BorderFactory.createTitledBorder(null, prefix + "File", TitledBorder.LEADING, TitledBorder.TOP));
		final GridBagConstraints gbc_filePanel = new GridBagConstraints();
		gbc_filePanel.fill = GridBagConstraints.BOTH;
		gbc_filePanel.gridx = 1;
		gbc_filePanel.gridy = 1;
		add(filePanel, gbc_filePanel);
		filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.LINE_AXIS));

		fileButton = new JRadioButton("File");
		filePanel.add(fileButton);

		fileNameField = new JTextField();
		fileNameField.setColumns(10);
		fileNameField.setEnabled(false);
		filePanel.add(fileNameField);

		final ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(textButton);
		buttonGroup.add(fileButton);

		fileFindButton = new JButton("Find");
		fileFindButton.setEnabled(false);
		filePanel.add(fileFindButton);

		textButton.addActionListener(e ->
		{
			if (textButton.isSelected())
			{
				textArea.setEnabled(true);

				fileNameField.setEnabled(false);
				fileFindButton.setEnabled(false);
			}
		});

		fileButton.addActionListener(e ->
		{
			if (fileButton.isSelected())
			{
				fileNameField.setEnabled(true);
				fileFindButton.setEnabled(true);

				textArea.setEnabled(false);
			}
		});

		fileFindButton.addActionListener(e ->
		{
			final String filePath = Main.generateFindFileGUI(fileNameField.getText());
			if (filePath != null)
				fileNameField.setText(filePath);
		});
	}

	@Override
	public void setEnabled(final boolean enabled)
	{
		encodingPanel.setEnabled(enabled);

		textPanel.setEnabled(enabled);
		textButton.setEnabled(enabled);
		textArea.setEnabled(enabled);

		filePanel.setEnabled(enabled);
		fileButton.setEnabled(enabled);
		fileNameField.setEnabled(enabled);
		fileFindButton.setEnabled(enabled);

		super.setEnabled(enabled);
	}

	public void setTextLimit(final int newLimit)
	{
		final String text = textArea.getText();
		((PlainDocumentWithLimit) textArea.getDocument()).setLimit(newLimit);
		textArea.updateUI();
		textArea.setText(text);
	}

	/**
	 * @throws IllegalArgumentException
	 *                                  Thrown when the decoding operation fails
	 */
	public byte[] read() throws IOException
	{
		if (textButton.isSelected())
			return encodingPanel.decode(textArea.getText());

		final File file = new File(fileNameField.getText());

		if (!file.exists())
		{
			EventQueue.invokeLater(() -> fileNameField.setBorder(BorderFactory.createLineBorder(Color.RED)));
			return null;
		}

		EventQueue.invokeLater(() -> fileNameField.setBorder(null));

		return encodingPanel.readEncoded(file);
	}

	public void write(final byte[] bytes) throws ExecutionException, InterruptedException, IOException
	{
		if (textButton.isSelected())
		{
			textArea.setText(encodingPanel.encode(bytes));
			return;
		}

		final File file = new File(fileNameField.getText());
		if (file.exists())
		{
			boolean isFileHasData;

			try
			{
				isFileHasData = Files.readAllBytes(file.toPath()).length > 0;
			}
			catch (final IOException ignored)
			{
				isFileHasData = true;
			}

			if (isFileHasData && Main.warningMessageBox("File already exists and not empty", "The encrypted-message output file is not empty." + Main.lineSeparator + "If you continue this action, IT WILL OVERWRITE THE EXISTING DATA!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, new String[]
			{
					"Overwrite", "Abort"
			}, "Abort").get() != 0)
			{
				EventQueue.invokeLater(() -> fileNameField.setBorder(BorderFactory.createLineBorder(Color.YELLOW)));
				return;
			}
		}

		EventQueue.invokeLater(() -> fileNameField.setBorder(null));

		encodingPanel.writeEncoded(file, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
