package fancytext.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import fancytext.Main;

public class EncodedIOPanel extends JPanel
{
	private final EncodingPanel encodingPanel;
	private final JRadioButton textButton;
	private final JTextArea textArea;
	private final JRadioButton fileButton;
	private final JTextField fileNameField;
	private final JButton fileFindButton;

	public EncodedIOPanel(final String title, final Encoding defaultEncoding)
	{
		setSize(1000, 1000); // DEBUG: ONLY FOR TEST, REMOVED LATER

		setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP));
		final GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[]
		{
				0, 0, 0
		};
		layout.rowHeights = new int[]
		{
				150, 0, 0
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

		final JPanel textPanel = new JPanel();
		textPanel.setBorder(BorderFactory.createTitledBorder(null, "From/To Text", TitledBorder.LEADING, TitledBorder.TOP));
		final GridBagConstraints textPanelConstraints = new GridBagConstraints();
		textPanelConstraints.insets = new Insets(0, 0, 5, 0);
		textPanelConstraints.fill = GridBagConstraints.BOTH;
		textPanelConstraints.gridx = 1;
		textPanelConstraints.gridy = 0;
		add(textPanel, textPanelConstraints);
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.LINE_AXIS));

		textButton = new JRadioButton("Text");
		textButton.setSelected(true);
		textPanel.add(textButton);

		final JScrollPane textScroll = new JScrollPane();
		textPanel.add(textScroll);

		textArea = new JTextArea();
		textArea.setDocument(new PlainDocumentWithLimit());
		textScroll.setViewportView(textArea);

		final JPanel filePanel = new JPanel();
		filePanel.setBorder(BorderFactory.createTitledBorder(null, "From/To File", TitledBorder.LEADING, TitledBorder.TOP));
		final GridBagConstraints filePanelConstraints = new GridBagConstraints();
		filePanelConstraints.fill = GridBagConstraints.BOTH;
		filePanelConstraints.gridx = 1;
		filePanelConstraints.gridy = 1;
		add(filePanel, filePanelConstraints);
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
