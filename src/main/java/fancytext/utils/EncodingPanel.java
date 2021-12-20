package fancytext.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class EncodingPanel extends JPanel
{
	private final JComboBox<Encoding> encodingComboBox;
	private final Encoding defaultValue;
	private final JTextField hexTokenDelimiterField;
	private final JPanel hexTokenCasePanel;
	private final JRadioButton hexTokenUpperCaseButton;

	public EncodingPanel(final Encoding defaultValue)
	{
		this.defaultValue = defaultValue;

		final boolean hexadecimal = defaultValue == Encoding.HEXADECIMAL;

		setBorder(BorderFactory.createTitledBorder(null, "Encoding", TitledBorder.CENTER, TitledBorder.TOP));

		final GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[]
		{
				120, 0
		};
		layout.rowHeights = new int[]
		{
				20, 0, 0, 0
		};
		layout.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		layout.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, Double.MIN_VALUE
		};
		super.setLayout(layout);

		encodingComboBox = new JComboBox<>();
		encodingComboBox.setModel(new DefaultComboBoxModel<>(Encoding.values()));
		encodingComboBox.setSelectedItem(defaultValue);
		final GridBagConstraints encodingComboBoxConstraint = new GridBagConstraints();
		encodingComboBoxConstraint.insets = new Insets(0, 5, 5, 5);
		encodingComboBoxConstraint.fill = GridBagConstraints.HORIZONTAL;
		encodingComboBoxConstraint.anchor = GridBagConstraints.PAGE_START;
		encodingComboBoxConstraint.gridx = 0;
		encodingComboBoxConstraint.gridy = 0;
		add(encodingComboBox, encodingComboBoxConstraint);

		hexTokenCasePanel = new JPanel();
		hexTokenCasePanel.setBorder(BorderFactory.createTitledBorder(null, "Hexadecimal token case", TitledBorder.LEADING, TitledBorder.TOP));
		hexTokenCasePanel.setVisible(hexadecimal);
		final GridBagConstraints hexTokenCasePanelConstraints = new GridBagConstraints();
		hexTokenCasePanelConstraints.anchor = GridBagConstraints.PAGE_START;
		hexTokenCasePanelConstraints.insets = new Insets(0, 0, 5, 0);
		hexTokenCasePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		hexTokenCasePanelConstraints.gridx = 0;
		hexTokenCasePanelConstraints.gridy = 1;
		add(hexTokenCasePanel, hexTokenCasePanelConstraints);
		hexTokenCasePanel.setLayout(new BoxLayout(hexTokenCasePanel, BoxLayout.PAGE_AXIS));

		hexTokenUpperCaseButton = new JRadioButton("Upper-case");
		hexTokenUpperCaseButton.setSelected(true);
		hexTokenCasePanel.add(hexTokenUpperCaseButton);

		final JRadioButton hexTokenLowerCaseButton = new JRadioButton("Lower-case");
		hexTokenCasePanel.add(hexTokenLowerCaseButton);

		final JPanel hexTokenDelimiterPanel = new JPanel();
		hexTokenDelimiterPanel.setBorder(BorderFactory.createTitledBorder(null, "Hexadecimal token delimiter", TitledBorder.LEADING, TitledBorder.TOP));
		hexTokenDelimiterPanel.setVisible(hexadecimal);
		final GridBagConstraints hexTokenDelimiterPanelConstraints = new GridBagConstraints();
		hexTokenDelimiterPanelConstraints.anchor = GridBagConstraints.PAGE_START;
		hexTokenDelimiterPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		hexTokenDelimiterPanelConstraints.gridx = 0;
		hexTokenDelimiterPanelConstraints.gridy = 2;
		add(hexTokenDelimiterPanel, hexTokenDelimiterPanelConstraints);
		hexTokenDelimiterPanel.setLayout(new BorderLayout(5, 5));

		hexTokenDelimiterField = new JTextField();
		hexTokenDelimiterPanel.add(hexTokenDelimiterField, BorderLayout.CENTER);
		hexTokenDelimiterField.setColumns(10);

		final ButtonGroup caseButtonGroup = new ButtonGroup();
		caseButtonGroup.add(hexTokenUpperCaseButton);
		caseButtonGroup.add(hexTokenLowerCaseButton);

		encodingComboBox.addActionListener(e ->
		{
			final boolean isHex = getCurrentEncoding() == Encoding.HEXADECIMAL;
			hexTokenCasePanel.setVisible(isHex);
			hexTokenDelimiterPanel.setVisible(isHex);
		});
	}

	@Override
	public void setLayout(final LayoutManager mgr)
	{
	}

	private Encoding getCurrentEncoding()
	{
		return Optional.ofNullable((Encoding) encodingComboBox.getSelectedItem()).orElse(defaultValue);
	}

	public String encode(final byte[] bytes)
	{
		final Encoding encoding = getCurrentEncoding();
		final boolean isHex = encoding == Encoding.HEXADECIMAL;

		final boolean upper = hexTokenUpperCaseButton.isSelected();
		final String delimiter = hexTokenDelimiterField.getText();
		if (isHex && delimiter != null && !delimiter.isEmpty())
			return encodeHexTokenized(bytes, delimiter, upper);

		final String encode = encoding.encode(bytes);
		if (isHex && upper)
			return encode.toUpperCase(Locale.ROOT);

		return encode;
	}

	public byte[] decode(final String string)
	{
		return getCurrentEncoding().decode(string);
	}

	private static String encodeHexTokenized(final byte[] bytes, final CharSequence delimiter, final boolean upper)
	{
		return IntStream.range(0, bytes.length).mapToObj(i ->
		{
			String string = Integer.toUnsignedString(bytes[i], 16);
			final int convertedLength = string.length();

			if (convertedLength > 2)
				// Truncate start
				string = string.substring(convertedLength - 2);
			else if (convertedLength < 2)
			{
				// Pad start
				final char[] chars = new char[2];
				Arrays.fill(chars, '0');
				string.getChars(0, convertedLength, chars, 2 - convertedLength);
				string = new String(chars);
			}

			return upper ? string.toUpperCase(Locale.ROOT) : string;
		}).collect(Collectors.joining(delimiter));
	}

	public byte[] readEncoded(final File file) throws IOException
	{
		return getCurrentEncoding().readEncoded(file);
	}

	public void writeEncoded(final File file, final byte[] bytes, final OpenOption... options) throws IOException
	{
		getCurrentEncoding().writeEncoded(file, bytes, options);
	}
}
