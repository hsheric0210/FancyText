package fancytext.tabs.languageconverter;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map.Entry;

import javax.management.MXBean;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import fancytext.Main;
import fancytext.utils.MultiThreading;

public final class LanguageConverter extends JPanel
{
	private static final long serialVersionUID = 56531445307439992L;
	private final JTextField keyField;
	private final JTextField valueField;
	@SuppressWarnings("WeakerAccess")
	List<Entry<String, List<String>>> conversionMap;
	@SuppressWarnings("WeakerAccess")
	JList<String> convertFrom;

	private JList<String> convertTo;
	private final JTextPane textInputField;
	private final JTextPane textOutputField;
	private final JCheckBox caseSensitiveCB;
	private final JSpinner convertRateSpinner;

	public LanguageConverter()
	{
		initDefaultConversionMap(ConversionTablePresets.LEET);

		// Main border setup
		setBorder(new TitledBorder(null, "Language Converter", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		// Main layout setup
		final GridBagLayout theLayout = new GridBagLayout();
		theLayout.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		theLayout.rowHeights = new int[]
		{
				0, 0, 0, 0, 0
		};
		theLayout.columnWeights = new double[]
		{
				1.0, 0.0, 1.0, Double.MIN_VALUE
		};
		theLayout.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE
		};
		setLayout(theLayout);

		// Text input field panel
		final JPanel textInputFieldPanel = new JPanel();
		textInputFieldPanel.setBorder(new TitledBorder(null, "Input-text", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_textInputFieldPanel = new GridBagConstraints();
		gbc_textInputFieldPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_textInputFieldPanel.ipady = 40;
		gbc_textInputFieldPanel.gridwidth = 3;
		gbc_textInputFieldPanel.insets = new Insets(0, 0, 5, 0);
		gbc_textInputFieldPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_textInputFieldPanel.gridx = 0;
		gbc_textInputFieldPanel.gridy = 0;
		add(textInputFieldPanel, gbc_textInputFieldPanel);
		final GridBagLayout gbl_textInputFieldPanel = new GridBagLayout();
		gbl_textInputFieldPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_textInputFieldPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_textInputFieldPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_textInputFieldPanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		textInputFieldPanel.setLayout(gbl_textInputFieldPanel);

		// Text input field
		textInputField = new JTextPane();
		final Font textInputFieldDefaultFont = textInputField.getFont();
		textInputField.setFont(new Font("Consolas", textInputFieldDefaultFont.getStyle(), 16));
		final GridBagConstraints gbc_textInputField = new GridBagConstraints();
		gbc_textInputField.fill = GridBagConstraints.BOTH;
		gbc_textInputField.gridx = 0;
		gbc_textInputField.gridy = 1;

		final JScrollPane textInputFieldScrollPane = new JScrollPane();
		final GridBagConstraints gbc_textInputFieldScrollPane = new GridBagConstraints();
		gbc_textInputFieldScrollPane.fill = GridBagConstraints.BOTH;
		gbc_textInputFieldScrollPane.gridx = 0;
		gbc_textInputFieldScrollPane.gridy = 0;
		textInputFieldScrollPane.setViewportView(textInputField);
		textInputFieldPanel.add(textInputFieldScrollPane, gbc_textInputFieldScrollPane);

		// Text output field panel
		final JPanel textOutputFieldPanel = new JPanel();
		textOutputFieldPanel.setBorder(new TitledBorder(null, "Output-text", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_textOutputFieldPanel = new GridBagConstraints();
		gbc_textOutputFieldPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_textOutputFieldPanel.ipady = 40;
		gbc_textOutputFieldPanel.gridwidth = 3;
		gbc_textOutputFieldPanel.insets = new Insets(0, 0, 5, 0);
		gbc_textOutputFieldPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_textOutputFieldPanel.gridx = 0;
		gbc_textOutputFieldPanel.gridy = 1;
		add(textOutputFieldPanel, gbc_textOutputFieldPanel);
		final GridBagLayout gbl_textOutputFieldPanel = new GridBagLayout();
		gbl_textOutputFieldPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_textOutputFieldPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_textOutputFieldPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_textOutputFieldPanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		textOutputFieldPanel.setLayout(gbl_textOutputFieldPanel);

		// Text output field
		textOutputField = new JTextPane();
		final Font textOutputFieldDefaultFont = textInputField.getFont();
		textOutputField.setFont(new Font("Consolas", textOutputFieldDefaultFont.getStyle(), 16));
		final GridBagConstraints gbc_textOutputField = new GridBagConstraints();
		gbc_textOutputField.fill = GridBagConstraints.BOTH;
		gbc_textOutputField.gridx = 0;
		gbc_textOutputField.gridy = 1;

		final JScrollPane textOutputFieldScrollPane = new JScrollPane();
		final GridBagConstraints gbc_textOutputFieldScrollPane = new GridBagConstraints();
		gbc_textOutputFieldScrollPane.fill = GridBagConstraints.BOTH;
		gbc_textOutputFieldScrollPane.gridx = 0;
		gbc_textOutputFieldScrollPane.gridy = 0;
		textOutputFieldScrollPane.setViewportView(textOutputField);
		textOutputFieldPanel.add(textOutputFieldScrollPane, gbc_textOutputFieldScrollPane);

		// Case sensitive checkbox
		caseSensitiveCB = new JCheckBox("Case-sensitive");
		final GridBagConstraints gbc_caseSensitiveCB = new GridBagConstraints();
		gbc_caseSensitiveCB.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_caseSensitiveCB.insets = new Insets(0, 0, 5, 5);
		gbc_caseSensitiveCB.gridx = 0;
		gbc_caseSensitiveCB.gridy = 2;
		add(caseSensitiveCB, gbc_caseSensitiveCB);

		// Convert button
		final JButton convertButton = new JButton("Convert");
		final GridBagConstraints gbc_convertButton = new GridBagConstraints();
		gbc_convertButton.ipadx = 60;
		gbc_convertButton.fill = GridBagConstraints.BOTH;
		gbc_convertButton.insets = new Insets(0, 0, 5, 5);
		gbc_convertButton.gridx = 1;
		gbc_convertButton.gridy = 2;
		add(convertButton, gbc_convertButton);

		// Convert Button
		convertButton.addActionListener(e ->
		{
			convertButton.setEnabled(false);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doConvert())
						Main.notificationMessageBox("Successfully converted!", "Successfully converted the message!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				finally
				{
					convertButton.setEnabled(true);
				}
			});
		});

		final JPanel convertRatePanel = new JPanel();
		convertRatePanel.setBorder(new CompoundBorder(new TitledBorder(null, "Convert rate (%)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(0, 0, 2, 2)));
		final GridBagConstraints gbc_convertRatePanel = new GridBagConstraints();
		gbc_convertRatePanel.anchor = GridBagConstraints.FIRST_LINE_END;
		gbc_convertRatePanel.ipadx = 80;
		gbc_convertRatePanel.insets = new Insets(0, 0, 5, 0);
		gbc_convertRatePanel.gridx = 2;
		gbc_convertRatePanel.gridy = 2;
		add(convertRatePanel, gbc_convertRatePanel);
		convertRatePanel.setLayout(new BorderLayout(0, 0));

		convertRateSpinner = new JSpinner();
		convertRateSpinner.setModel(new SpinnerNumberModel(100, 0, 100, 1));
		convertRatePanel.add(convertRateSpinner, BorderLayout.CENTER);

		// Conversion Table Panel
		final JPanel conversionTablePanel = new JPanel();
		conversionTablePanel.setBorder(new TitledBorder(null, "Conversion table", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_conversionTablePanel = new GridBagConstraints();
		gbc_conversionTablePanel.gridwidth = 3;
		gbc_conversionTablePanel.fill = GridBagConstraints.BOTH;
		gbc_conversionTablePanel.gridx = 0;
		gbc_conversionTablePanel.gridy = 3;
		add(conversionTablePanel, gbc_conversionTablePanel);
		final GridBagLayout gbl_conversionTablePanel = new GridBagLayout();
		gbl_conversionTablePanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_conversionTablePanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_conversionTablePanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_conversionTablePanel.rowWeights = new double[]
		{
				1.0, 1.0, Double.MIN_VALUE
		};
		conversionTablePanel.setLayout(gbl_conversionTablePanel);

		// Conversion Table - Convert from list
		convertFrom = new JList<>();
		convertFrom.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final GridBagConstraints gbc_convertFrom = new GridBagConstraints();
		gbc_convertFrom.ipady = 30;
		gbc_convertFrom.gridwidth = 3;
		gbc_convertFrom.insets = new Insets(0, 0, 5, 0);
		gbc_convertFrom.fill = GridBagConstraints.BOTH;
		gbc_convertFrom.gridx = 0;
		gbc_convertFrom.gridy = 1;

		// Conversion Table - Convert from panel
		final JPanel convertFromPanel = new JPanel();
		convertFromPanel.setBorder(new TitledBorder(null, "Convert from", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_convertFromPanel = new GridBagConstraints();
		gbc_convertFromPanel.insets = new Insets(0, 0, 5, 0);
		gbc_convertFromPanel.fill = GridBagConstraints.BOTH;
		gbc_convertFromPanel.gridx = 0;
		gbc_convertFromPanel.gridy = 0;
		final GridBagLayout gbl_convertFromPanel = new GridBagLayout();
		gbl_convertFromPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_convertFromPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_convertFromPanel.columnWeights = new double[]
		{
				1.0, 0.0, 0.0
		};
		gbl_convertFromPanel.rowWeights = new double[]
		{
				1.0, 0.0
		};
		convertFromPanel.setLayout(gbl_convertFromPanel);
		conversionTablePanel.add(convertFromPanel, gbc_convertFromPanel);

		// Conversion Table - Convert from panel - Convert from list scroll pane
		final JScrollPane convertFromScrollPane = new JScrollPane();
		final GridBagConstraints gbc_convertFromScrollPane = new GridBagConstraints();
		gbc_convertFromScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_convertFromScrollPane.ipady = 120;
		gbc_convertFromScrollPane.gridwidth = 3;
		gbc_convertFromScrollPane.fill = GridBagConstraints.BOTH;
		gbc_convertFromScrollPane.gridx = 0;
		gbc_convertFromScrollPane.gridy = 0;
		convertFromScrollPane.setViewportView(convertFrom);
		convertFromPanel.add(convertFromScrollPane, gbc_convertFromScrollPane);

		keyField = new JTextField();
		keyField.setColumns(10);
		final GridBagConstraints gbc_keyField = new GridBagConstraints();
		gbc_keyField.insets = new Insets(0, 0, 0, 5);
		gbc_keyField.fill = GridBagConstraints.HORIZONTAL;
		gbc_keyField.gridx = 0;
		gbc_keyField.gridy = 1;
		convertFromPanel.add(keyField, gbc_keyField);

		final JButton addConversionKeyButton = new JButton("Add");
		final GridBagConstraints gbc_addConversionKeyButton = new GridBagConstraints();
		gbc_addConversionKeyButton.insets = new Insets(0, 0, 0, 5);
		gbc_addConversionKeyButton.gridx = 1;
		gbc_addConversionKeyButton.gridy = 1;
		convertFromPanel.add(addConversionKeyButton, gbc_addConversionKeyButton);

		final JButton applyConversionKeyButton = new JButton("Apply change");
		final GridBagConstraints gbc_applyConversionKeyButton = new GridBagConstraints();
		gbc_applyConversionKeyButton.gridx = 2;
		gbc_applyConversionKeyButton.gridy = 1;
		convertFromPanel.add(applyConversionKeyButton, gbc_applyConversionKeyButton);

		// ConvertFrom list model
		convertFrom.setModel(new AbstractListModel<String>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public int getSize()
			{
				return conversionMap.size();
			}

			@Override
			public String getElementAt(final int index)
			{
				return String.valueOf(conversionMap.get(index).getKey());
			}
		});

		keyField.setText(convertFrom.getSelectedIndex() == -1 ? "" : String.valueOf(conversionMap.get(convertFrom.getSelectedIndex()).getKey()));

		convertFrom.addListSelectionListener(e ->
		{
			keyField.setText(convertFrom.getSelectedIndex() == -1 ? "" : String.valueOf(conversionMap.get(convertFrom.getSelectedIndex()).getKey()));

			keyField.updateUI();
			convertTo.updateUI();
		});

		addConversionKeyButton.addActionListener(e ->
		{
			if (!keyField.getText().isEmpty() && conversionMap.stream().noneMatch(conversion -> conversion.getKey().equals(keyField.getText())))
				conversionMap.add(createEntry(keyField.getText(), new ArrayList<>(0)));

			convertFrom.updateUI();
		});

		applyConversionKeyButton.addActionListener(e ->
		{
			if (keyField.getText().isEmpty())
				conversionMap.remove(convertFrom.getSelectedIndex());
			else
			{
				final List<String> backup = conversionMap.get(convertFrom.getSelectedIndex()).getValue();
				conversionMap.remove(convertFrom.getSelectedIndex()); // Backup and Remove
				conversionMap.add(createEntry(keyField.getText(), backup)); // Re-add
			}
			convertFrom.updateUI();
		});

		// Conversion Table - Convert to panel
		final JPanel convertToPanel = new JPanel();
		convertToPanel.setBorder(new TitledBorder(null, "Will be converted to", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_convertToPanel = new GridBagConstraints();
		gbc_convertToPanel.gridwidth = 1;
		gbc_convertToPanel.fill = GridBagConstraints.BOTH;
		gbc_convertToPanel.gridx = 0;
		gbc_convertToPanel.gridy = 1;
		conversionTablePanel.add(convertToPanel, gbc_convertToPanel);
		final GridBagLayout gbl_convertToPanel = new GridBagLayout();
		gbl_convertToPanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_convertToPanel.rowHeights = new int[]
		{
				0, 0, 0, 0
		};
		gbl_convertToPanel.columnWeights = new double[]
		{
				1.0, 0.0, 0.0, Double.MIN_VALUE
		};
		gbl_convertToPanel.rowWeights = new double[]
		{
				1.0, 0.0, 0.0, Double.MIN_VALUE
		};
		convertToPanel.setLayout(gbl_convertToPanel);

		// Conversion Table - Convert to panel - Convert to list scroll pane - Convert to list
		convertTo = new JList<>();
		final GridBagConstraints gbc_convertTo = new GridBagConstraints();
		gbc_convertTo.gridwidth = 3;
		gbc_convertTo.insets = new Insets(0, 0, 5, 0);
		gbc_convertTo.anchor = GridBagConstraints.PAGE_START;
		gbc_convertTo.fill = GridBagConstraints.HORIZONTAL;
		gbc_convertTo.gridx = 0;
		gbc_convertTo.gridy = 1;
		gbc_convertTo.ipady = 10;

		// Conversion Table - Convert to panel - Convert to list scroll pane
		final JScrollPane convertToScroll = new JScrollPane();
		final GridBagConstraints gbc_convertToScroll = new GridBagConstraints();
		gbc_convertToScroll.ipady = 120;
		gbc_convertToScroll.gridwidth = 3;
		gbc_convertToScroll.insets = new Insets(0, 0, 5, 0);
		gbc_convertToScroll.fill = GridBagConstraints.BOTH;
		gbc_convertToScroll.gridx = 0;
		gbc_convertToScroll.gridy = 0;
		convertToScroll.setViewportView(convertTo);
		convertToPanel.add(convertToScroll, gbc_convertToScroll);

		// Conversion Table - Convert to panel - Selected list component manipulation field
		valueField = new JTextField();
		final GridBagConstraints gbc_valueField = new GridBagConstraints();
		gbc_valueField.insets = new Insets(0, 0, 5, 5);
		gbc_valueField.fill = GridBagConstraints.HORIZONTAL;
		gbc_valueField.gridx = 0;
		gbc_valueField.gridy = 1;
		convertToPanel.add(valueField, gbc_valueField);
		valueField.setColumns(10);

		// Conversion Table - Convert to panel - Add button
		final JButton addConversionValueButton = new JButton("Add");
		final GridBagConstraints gbc_addConversionValueButton = new GridBagConstraints();
		gbc_addConversionValueButton.insets = new Insets(0, 0, 5, 5);
		gbc_addConversionValueButton.gridx = 1;
		gbc_addConversionValueButton.gridy = 1;
		convertToPanel.add(addConversionValueButton, gbc_addConversionValueButton);

		// Conversion Table - Convert to panel - Apply change button
		final JButton applyConversionValueButton = new JButton("Apply change");
		final GridBagConstraints gbc_applyConversionValueButton = new GridBagConstraints();
		gbc_applyConversionValueButton.insets = new Insets(0, 0, 5, 0);
		gbc_applyConversionValueButton.gridx = 2;
		gbc_applyConversionValueButton.gridy = 1;
		convertToPanel.add(applyConversionValueButton, gbc_applyConversionValueButton);

		// Conversion Table - Conversion table presets panel
		final JPanel conversionTablePresetsPanel = new JPanel();
		conversionTablePresetsPanel.setBorder(new TitledBorder(null, "Conversion table presets", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_conversionTablePresetsPanel = new GridBagConstraints();
		gbc_conversionTablePresetsPanel.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_conversionTablePresetsPanel.insets = new Insets(0, 0, 0, 5);
		gbc_conversionTablePresetsPanel.gridx = 0;
		gbc_conversionTablePresetsPanel.gridy = 2;
		convertToPanel.add(conversionTablePresetsPanel, gbc_conversionTablePresetsPanel);
		final GridBagLayout gbl_conversionTablePresetsPanel = new GridBagLayout();
		gbl_conversionTablePresetsPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_conversionTablePresetsPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_conversionTablePresetsPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_conversionTablePresetsPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		conversionTablePresetsPanel.setLayout(gbl_conversionTablePresetsPanel);

		// Conversion Table - Conversion table presets panel - Conversion table presets combo box
		final JComboBox<ConversionTablePresets> conversionTablePresets = new JComboBox<>();
		final GridBagConstraints gbc_conversionTablePresets = new GridBagConstraints();
		gbc_conversionTablePresets.fill = GridBagConstraints.HORIZONTAL;
		gbc_conversionTablePresets.gridx = 0;
		gbc_conversionTablePresets.gridy = 0;
		conversionTablePresetsPanel.add(conversionTablePresets, gbc_conversionTablePresets);

		// ConvertTo list model
		convertTo.setModel(new AbstractListModel<String>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public int getSize()
			{
				final int selectedIndex;
				return (selectedIndex = convertFrom.getSelectedIndex()) < 0 || selectedIndex > conversionMap.size() ? 0 : conversionMap.get(convertFrom.getSelectedIndex()).getValue().size();
			}

			@Override
			public String getElementAt(final int index)
			{
				return conversionMap.get(convertFrom.getSelectedIndex()).getValue().get(index);
			}
		});

		// Conversion table presets combo box model
		conversionTablePresets.setModel(new DefaultComboBoxModel<>(ConversionTablePresets.values()));

		valueField.setText(convertFrom.getSelectedIndex() != -1 && convertTo.getSelectedIndex() != -1 ? conversionMap.get(convertFrom.getSelectedIndex()).getValue().get(convertTo.getSelectedIndex()) : "");

		convertTo.addListSelectionListener(e ->
		{
			valueField.setText(convertFrom.getSelectedIndex() != -1 && convertTo.getSelectedIndex() != -1 ? conversionMap.get(convertFrom.getSelectedIndex()).getValue().get(convertTo.getSelectedIndex()) : "");

			valueField.updateUI();
		});

		addConversionValueButton.addActionListener(e ->
		{
			if (!valueField.getText().isEmpty() && !conversionMap.get(convertFrom.getSelectedIndex()).getValue().contains(valueField.getText()))
				conversionMap.get(convertFrom.getSelectedIndex()).getValue().add(valueField.getText());
			convertTo.updateUI();
		});

		applyConversionValueButton.addActionListener(e ->
		{
			if (valueField.getText().isEmpty())
				conversionMap.get(convertFrom.getSelectedIndex()).getValue().remove(convertTo.getSelectedIndex());
			else
				conversionMap.get(convertFrom.getSelectedIndex()).getValue().set(convertTo.getSelectedIndex(), valueField.getText());
			convertTo.updateUI();
		});

		conversionTablePresets.addActionListener(e ->
		{
			if (conversionTablePresets.getSelectedIndex() != -1)
			{
				initDefaultConversionMap((ConversionTablePresets) conversionTablePresets.getSelectedItem());
				convertFrom.updateUI();
				convertTo.updateUI();
			}
		});
	}

	private void initDefaultConversionMap(final ConversionTablePresets preset)
	{
		if (conversionMap == null)
			conversionMap = new Vector<>();
		else
			conversionMap.clear();

		preset.apply(conversionMap);
	}

	private static <K, V> Entry<K, V> createEntry(final K key, final V value)
	{
		return new SimpleImmutableEntry<>(key, value);
	}

	@SuppressWarnings("WeakerAccess")
	boolean doConvert()
	{
		final String input = textInputField.getText();
		final Random random = new Random();
		final boolean caseSensitive = caseSensitiveCB.isSelected();
		final double convertRate = (int) convertRateSpinner.getValue() / 100.0;

		if (input == null || input.isEmpty())
		{
			textInputField.grabFocus();
			textInputField.setCaretColor(Color.red);
			return false;
		}
		textInputField.setCaretColor(Color.black);

		// TODO: I know this is the worst solution for the problem. Fix it later when the better solution found.
		final List<Entry<String, List<String>>> fixedConversionMap = new ArrayList<>(conversionMap.size());

		conversionMap.forEach(entry ->
		{
			final Optional<Entry<String, List<String>>> duplicateEntry = fixedConversionMap.stream().filter(_entry -> caseSensitive ? _entry.getKey().equals(entry.getKey()) : _entry.getKey().equalsIgnoreCase(entry.getKey())).findFirst();
			// Merge the cases
			if (duplicateEntry.isPresent()) // If the key that have same name already exists, add all values onto it.
			{
				for (final String valueToAdd : entry.getValue())
					if (!duplicateEntry.get().getValue().contains(valueToAdd)) // Value duplication check
						duplicateEntry.get().getValue().add(valueToAdd);
			}
			else
				fixedConversionMap.add(new SimpleImmutableEntry<>(entry.getKey(), new ArrayList<>(entry.getValue()))); // Re-allocate is required because if we don't re-allocate it, it also TAMPER original conversion table.
		});

		// https://banana-media-lab.tistory.com/entry/JAVA-ArrayList-내-단어-길이-내림차순-정렬 [Banana Media Lab]
		fixedConversionMap.sort((entry1, entry2) -> Integer.compare(entry2.getKey().length(), entry1.getKey().length()));

		final char[] charArray = input.toCharArray();
		final StringBuilder outputBuilder = new StringBuilder(charArray.length);
		for (int i = 0, j = charArray.length; i < j; i++)
		{
			final char currentChar = charArray[i];
			boolean somethingChanged = false;
			if (random.nextFloat() <= convertRate)
				for (final Entry<String, List<String>> entry : fixedConversionMap)
				{
					final String replaceFrom = entry.getKey();
					if (replaceFrom.length() > 1) // TODO: Multi-character replacement algorithm is absolutely fucked-up. Someone pls fix it later.
					{
						if (input.length() - i >= replaceFrom.length()) // Input string length check
						{
							final String checkBuffer = input.substring(i, i + replaceFrom.length());
							final boolean similar = caseSensitive ? checkBuffer.equals(replaceFrom) : checkBuffer.equalsIgnoreCase(replaceFrom);

							if (similar)
							{
								final String replaceTo = entry.getValue().get(random.nextInt(entry.getValue().size()));
								outputBuilder.append(replaceTo);
								i += replaceFrom.length() - 1; // Skip already-read texts
								somethingChanged = true;
							}
						}
					}
					else if (!somethingChanged && (replaceFrom.charAt(0) == currentChar || !caseSensitive && (Character.toLowerCase(replaceFrom.charAt(0)) == Character.toLowerCase(currentChar) || Character.toUpperCase(replaceFrom.charAt(0)) == Character.toUpperCase(currentChar))))
					{
						outputBuilder.append(entry.getValue().get(random.nextInt(entry.getValue().size())));
						somethingChanged = true;
					}
				}

			if (!somethingChanged)
				outputBuilder.append(currentChar);
		}

		textOutputField.setText(outputBuilder.toString());
		textOutputField.updateUI();

		return true;
	}
}
