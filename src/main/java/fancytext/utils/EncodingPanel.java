package fancytext.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.OpenOption;
import java.util.Optional;

import javax.swing.*;
import javax.swing.border.TitledBorder;

// TODO: Configurable Base64 (as like Base16)
public class EncodingPanel extends JPanel
{
	private final JComboBox<Encoding> encodingComboBox;
	private final Encoding defaultValue;
	private final JTextField hexTokenDelimiterField;
	private final JPanel hexTokenCasePanel;
	private final JRadioButton hexTokenUpperCaseButton;
	private final JPanel base64OptionsPanel;
	private final JRadioButton urlsafeBase64Button;
	private final JRadioButton mimeBase64Button;
	private final JCheckBox doPaddingBase64Button;

	public EncodingPanel(final Encoding defaultValue)
	{
		this.defaultValue = defaultValue;

		final boolean hexadecimal = defaultValue == Encoding.HEXADECIMAL;
		final boolean base64 = defaultValue == Encoding.BASE64;

		setBorder(BorderFactory.createTitledBorder(null, "Encoding", TitledBorder.CENTER, TitledBorder.TOP));

		final GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[]
		{
				120, 0
		};
		layout.rowHeights = new int[]
		{
				20, 0, 0, 0, 0
		};
		layout.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		layout.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE
		};
		setLayout(layout);

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
		hexTokenDelimiterPanelConstraints.insets = new Insets(0, 0, 5, 0);
		hexTokenDelimiterPanelConstraints.anchor = GridBagConstraints.PAGE_START;
		hexTokenDelimiterPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		hexTokenDelimiterPanelConstraints.gridx = 0;
		hexTokenDelimiterPanelConstraints.gridy = 2;
		add(hexTokenDelimiterPanel, hexTokenDelimiterPanelConstraints);
		hexTokenDelimiterPanel.setLayout(new BorderLayout(5, 5));

		hexTokenDelimiterField = new JTextField();
		hexTokenDelimiterPanel.add(hexTokenDelimiterField, BorderLayout.CENTER);
		hexTokenDelimiterField.setColumns(10);

		base64OptionsPanel = new JPanel();
		base64OptionsPanel.setBorder(BorderFactory.createTitledBorder(null, "Base64 options", TitledBorder.LEADING, TitledBorder.TOP));
		base64OptionsPanel.setVisible(base64);
		final GridBagConstraints gbc_base64OptionsPanel = new GridBagConstraints();
		gbc_base64OptionsPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_base64OptionsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_base64OptionsPanel.gridx = 0;
		gbc_base64OptionsPanel.gridy = 3;
		add(base64OptionsPanel, gbc_base64OptionsPanel);
		base64OptionsPanel.setLayout(new BoxLayout(base64OptionsPanel, BoxLayout.PAGE_AXIS));

		final JRadioButton basicBase64Button = new JRadioButton("Basic");
		basicBase64Button.setSelected(true);
		base64OptionsPanel.add(basicBase64Button);

		urlsafeBase64Button = new JRadioButton("URL and Filename safe");
		base64OptionsPanel.add(urlsafeBase64Button);

		mimeBase64Button = new JRadioButton("MIME");
		base64OptionsPanel.add(mimeBase64Button);

		doPaddingBase64Button = new JCheckBox("Do Padding");
		doPaddingBase64Button.setSelected(true);
		base64OptionsPanel.add(doPaddingBase64Button);

		final ButtonGroup hexCaseButtonGroup = new ButtonGroup();
		hexCaseButtonGroup.add(hexTokenUpperCaseButton);
		hexCaseButtonGroup.add(hexTokenLowerCaseButton);

		final ButtonGroup base64ButtonGroup = new ButtonGroup();
		base64ButtonGroup.add(basicBase64Button);
		base64ButtonGroup.add(urlsafeBase64Button);
		base64ButtonGroup.add(mimeBase64Button);

		encodingComboBox.addActionListener(e ->
		{
			final boolean isHex = getCurrentEncoding() == Encoding.HEXADECIMAL;
			hexTokenCasePanel.setVisible(isHex);
			hexTokenDelimiterPanel.setVisible(isHex);

			base64OptionsPanel.setVisible(getCurrentEncoding() == Encoding.BASE64);
		});
	}

	private Encoding getCurrentEncoding()
	{
		return Optional.ofNullable((Encoding) encodingComboBox.getSelectedItem()).orElse(defaultValue);
	}

	public String encode(final byte[] bytes)
	{
		return getCurrentEncoding().encode(bytes, getEncoderParameters(), getEncoderFlags());
	}

	public byte[] decode(final String string)
	{
		return getCurrentEncoding().decode(string);
	}

	public byte[] readEncoded(final File file) throws IOException
	{
		return getCurrentEncoding().readEncoded(file);
	}

	public void writeEncoded(final File file, final byte[] bytes, final OpenOption... options) throws IOException
	{
		getCurrentEncoding().writeEncoded(file, bytes, getEncoderParameters(), getEncoderFlags(), options);
	}

	private Object getEncoderParameters()
	{
		if (getCurrentEncoding() == Encoding.HEXADECIMAL)
			return hexTokenDelimiterField.getText();

		return null;
	}

	private int getEncoderFlags()
	{
		switch (getCurrentEncoding())
		{
			case HEXADECIMAL:
				if (hexTokenUpperCaseButton.isSelected())
					return HexEncoder.HEX_UPPERCASE;
				break;

			case BASE64:
				int flags = 0;

				if (urlsafeBase64Button.isSelected())
					flags |= Base64Encoder.BASE64_URLSAFE;
				if (mimeBase64Button.isSelected())
					flags |= Base64Encoder.BASE64_MIME;
				if (doPaddingBase64Button.isSelected())
					flags |= Base64Encoder.BASE64_DO_PADDING;

				return flags;

			default:
		}

		return 0;
	}
}
