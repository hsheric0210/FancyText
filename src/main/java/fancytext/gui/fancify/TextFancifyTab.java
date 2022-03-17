package fancytext.gui.fancify;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import fancytext.Main;
import fancytext.utils.MultiThreading;

public final class TextFancifyTab extends JPanel
{
	private static final long serialVersionUID = 56531445307439992L;
	private final JTextField keyField;
	private final JTextField valueField;
	private final JCheckBox conversionEnabledCB;
	@SuppressWarnings("WeakerAccess")
	List<Entry<String, List<String>>> conversionMap;
	List<String> insertionList;
	@SuppressWarnings("WeakerAccess")
	JList<String> convertFrom;

	private final JList<String> convertTo;
	private final JTextPane textInputField;
	private final JTextPane textOutputField;
	private final JCheckBox conversionCaseSensitiveCB;
	private final JSpinner conversionRateSpinner;
	private final JCheckBox insertionEnabledCB;
	private final JList<String> insertionTable;
	private final JSpinner insertionMinSpinner;
	private final JSpinner insertionMaxSpinner;

	public TextFancifyTab()
	{
		initDefaultConversionMap(ConversionTablePresets.LEET);
		initDefaultInsertionMap(InsertionTablePresets.COMBINING_DIACRITICAL_MARKS);

		setSize(1000, 1000); // TODO: remove it

		// Main border setup
		setBorder(new TitledBorder(null, "Fancify Text", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		// Main layout setup
		final GridBagLayout theLayout = new GridBagLayout();
		theLayout.columnWidths = new int[]
		{
				0, 0, 0
		};
		theLayout.rowHeights = new int[]
		{
				0, 0, 0, 0, 0, 0
		};
		theLayout.columnWeights = new double[]
		{
				1.0, 1.0, Double.MIN_VALUE
		};
		theLayout.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE
		};
		setLayout(theLayout);

		// Text input field panel
		final JPanel textInputFieldPanel = new JPanel();
		textInputFieldPanel.setBorder(new TitledBorder(null, "Input-text", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_textInputFieldPanel = new GridBagConstraints();
		gbc_textInputFieldPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_textInputFieldPanel.ipady = 40;
		gbc_textInputFieldPanel.gridwidth = 2;
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
		gbc_textOutputFieldPanel.gridwidth = 2;
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

		final JScrollPane textOutputFieldScrollPane = new JScrollPane();
		final GridBagConstraints gbc_textOutputFieldScrollPane = new GridBagConstraints();
		gbc_textOutputFieldScrollPane.fill = GridBagConstraints.BOTH;
		gbc_textOutputFieldScrollPane.gridx = 0;
		gbc_textOutputFieldScrollPane.gridy = 0;
		textOutputFieldScrollPane.setViewportView(textOutputField);
		textOutputFieldPanel.add(textOutputFieldScrollPane, gbc_textOutputFieldScrollPane);

		// Convert button
		final JButton convertButton = new JButton("Convert");
		final GridBagConstraints gbc_convertButton = new GridBagConstraints();
		gbc_convertButton.gridwidth = 2;
		gbc_convertButton.ipadx = 60;
		gbc_convertButton.insets = new Insets(0, 0, 5, 0);
		gbc_convertButton.gridx = 0;
		gbc_convertButton.gridy = 2;
		add(convertButton, gbc_convertButton);

		final JCheckBox realtimeUpdateCB = new JCheckBox("Real-time update");
		final GridBagConstraints gbc_realtimeUpdateCB = new GridBagConstraints();
		gbc_realtimeUpdateCB.gridwidth = 2;
		gbc_realtimeUpdateCB.insets = new Insets(0, 0, 5, 0);
		gbc_realtimeUpdateCB.gridx = 0;
		gbc_realtimeUpdateCB.gridy = 3;
		add(realtimeUpdateCB, gbc_realtimeUpdateCB);

		// Conversion Table Panel
		final JPanel conversionPanel = new JPanel();
		conversionPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Conversion", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_conversionPanel = new GridBagConstraints();
		gbc_conversionPanel.insets = new Insets(0, 0, 0, 5);
		gbc_conversionPanel.fill = GridBagConstraints.BOTH;
		gbc_conversionPanel.gridx = 0;
		gbc_conversionPanel.gridy = 4;
		add(conversionPanel, gbc_conversionPanel);
		final GridBagLayout gbl_conversionPanel = new GridBagLayout();
		gbl_conversionPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_conversionPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_conversionPanel.columnWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		gbl_conversionPanel.rowWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		conversionPanel.setLayout(gbl_conversionPanel);

		final JPanel conversionTabelPanel = new JPanel();
		conversionTabelPanel.setBorder(new TitledBorder(null, "Conversion Table", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_conversionTabelPanel = new GridBagConstraints();
		gbc_conversionTabelPanel.gridheight = 2;
		gbc_conversionTabelPanel.fill = GridBagConstraints.BOTH;
		gbc_conversionTabelPanel.gridx = 1;
		gbc_conversionTabelPanel.gridy = 0;
		conversionPanel.add(conversionTabelPanel, gbc_conversionTabelPanel);
		final GridBagLayout gbl_conversionTabelPanel = new GridBagLayout();
		gbl_conversionTabelPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_conversionTabelPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_conversionTabelPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_conversionTabelPanel.rowWeights = new double[]
		{
				1.0, 1.0, Double.MIN_VALUE
		};
		conversionTabelPanel.setLayout(gbl_conversionTabelPanel);

		// Conversion Table - Convert from list
		convertFrom = new JList<>();
		convertFrom.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Conversion Table - Convert from panel
		final JPanel convertFromPanel = new JPanel();
		final GridBagConstraints gbc_convertFromPanel = new GridBagConstraints();
		gbc_convertFromPanel.fill = GridBagConstraints.BOTH;
		gbc_convertFromPanel.insets = new Insets(0, 0, 5, 0);
		gbc_convertFromPanel.gridx = 0;
		gbc_convertFromPanel.gridy = 0;
		conversionTabelPanel.add(convertFromPanel, gbc_convertFromPanel);
		convertFromPanel.setBorder(new TitledBorder(null, "Convert from", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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

		// Conversion Table - Convert to panel
		final JPanel convertToPanel = new JPanel();
		convertToPanel.setBorder(new TitledBorder(null, "Will be converted to", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_convertToPanel = new GridBagConstraints();
		gbc_convertToPanel.fill = GridBagConstraints.BOTH;
		gbc_convertToPanel.gridx = 0;
		gbc_convertToPanel.gridy = 1;
		conversionTabelPanel.add(convertToPanel, gbc_convertToPanel);
		final GridBagLayout gbl_convertToPanel = new GridBagLayout();
		gbl_convertToPanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_convertToPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_convertToPanel.columnWeights = new double[]
		{
				1.0, 0.0, 0.0, Double.MIN_VALUE
		};
		gbl_convertToPanel.rowWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		convertToPanel.setLayout(gbl_convertToPanel);

		// Conversion Table - Convert to panel - Convert to list scroll pane - Convert to list
		convertTo = new JList<>();

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
		gbc_valueField.insets = new Insets(0, 0, 0, 5);
		gbc_valueField.fill = GridBagConstraints.HORIZONTAL;
		gbc_valueField.gridx = 0;
		gbc_valueField.gridy = 1;
		convertToPanel.add(valueField, gbc_valueField);
		valueField.setColumns(10);

		// Conversion Table - Convert to panel - Add button
		final JButton addConversionValueButton = new JButton("Add");
		final GridBagConstraints gbc_addConversionValueButton = new GridBagConstraints();
		gbc_addConversionValueButton.insets = new Insets(0, 0, 0, 5);
		gbc_addConversionValueButton.gridx = 1;
		gbc_addConversionValueButton.gridy = 1;
		convertToPanel.add(addConversionValueButton, gbc_addConversionValueButton);

		// Conversion Table - Convert to panel - Apply change button
		final JButton applyConversionValueButton = new JButton("Apply change");
		final GridBagConstraints gbc_applyConversionValueButton = new GridBagConstraints();
		gbc_applyConversionValueButton.gridx = 2;
		gbc_applyConversionValueButton.gridy = 1;
		convertToPanel.add(applyConversionValueButton, gbc_applyConversionValueButton);

		conversionEnabledCB = new JCheckBox("Enable");
		final GridBagConstraints gbc_conversionEnabledCB = new GridBagConstraints();
		gbc_conversionEnabledCB.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_conversionEnabledCB.insets = new Insets(0, 0, 5, 5);
		gbc_conversionEnabledCB.gridx = 0;
		gbc_conversionEnabledCB.gridy = 0;
		conversionPanel.add(conversionEnabledCB, gbc_conversionEnabledCB);
		conversionEnabledCB.setSelected(true);

		final JPanel conversionOptionsPanel = new JPanel();
		final GridBagConstraints gbc_conversionOptionsPanel = new GridBagConstraints();
		gbc_conversionOptionsPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_conversionOptionsPanel.insets = new Insets(0, 0, 0, 5);
		gbc_conversionOptionsPanel.gridx = 0;
		gbc_conversionOptionsPanel.gridy = 1;
		conversionPanel.add(conversionOptionsPanel, gbc_conversionOptionsPanel);
		conversionOptionsPanel.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagLayout gbl_conversionOptionsPanel = new GridBagLayout();
		gbl_conversionOptionsPanel.columnWidths = new int[]
		{
				146, 0
		};
		gbl_conversionOptionsPanel.rowHeights = new int[]
		{
				0, 52, 0, 0
		};
		gbl_conversionOptionsPanel.columnWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		gbl_conversionOptionsPanel.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, Double.MIN_VALUE
		};
		conversionOptionsPanel.setLayout(gbl_conversionOptionsPanel);

		// Case sensitive checkbox
		conversionCaseSensitiveCB = new JCheckBox("Case-sensitive");
		final GridBagConstraints gbc_caseSensitiveCB = new GridBagConstraints();
		gbc_caseSensitiveCB.insets = new Insets(0, 0, 5, 0);
		gbc_caseSensitiveCB.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_caseSensitiveCB.gridx = 0;
		gbc_caseSensitiveCB.gridy = 0;
		conversionOptionsPanel.add(conversionCaseSensitiveCB, gbc_caseSensitiveCB);

		final JPanel conversionRatePanel = new JPanel();
		final GridBagConstraints gbc_convertRatePanel = new GridBagConstraints();
		gbc_convertRatePanel.insets = new Insets(0, 0, 5, 0);
		gbc_convertRatePanel.anchor = GridBagConstraints.PAGE_START;
		gbc_convertRatePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_convertRatePanel.gridx = 0;
		gbc_convertRatePanel.gridy = 1;
		gbc_convertRatePanel.ipadx = 100;
		conversionOptionsPanel.add(conversionRatePanel, gbc_convertRatePanel);
		conversionRatePanel.setBorder(new CompoundBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Convert rate (in percent)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(0, 0, 2, 2)));
		conversionRatePanel.setLayout(new BorderLayout(0, 0));

		conversionRateSpinner = new JSpinner();
		conversionRateSpinner.setModel(new SpinnerNumberModel(100, 0, 100, 1));
		conversionRatePanel.add(conversionRateSpinner, BorderLayout.CENTER);

		// Conversion Table - Conversion table presets panel
		final JPanel conversionTablePresetsPanel = new JPanel();
		final GridBagConstraints gbc_conversionTablePresetsPanel = new GridBagConstraints();
		gbc_conversionTablePresetsPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_conversionTablePresetsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_conversionTablePresetsPanel.gridx = 0;
		gbc_conversionTablePresetsPanel.gridy = 2;
		conversionOptionsPanel.add(conversionTablePresetsPanel, gbc_conversionTablePresetsPanel);
		conversionTablePresetsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Table Presets", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		conversionTablePresetsPanel.setLayout(new BorderLayout(0, 0));

		// Conversion Table - Conversion table presets panel - Conversion table presets combo box
		final JComboBox<ConversionTablePresets> conversionTablePresets = new JComboBox<>();
		conversionTablePresetsPanel.add(conversionTablePresets, BorderLayout.CENTER);

		final JPanel insertionPanel = new JPanel();
		insertionPanel.setBorder(new TitledBorder(null, "Insertion", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_insertionPanel = new GridBagConstraints();
		gbc_insertionPanel.fill = GridBagConstraints.BOTH;
		gbc_insertionPanel.gridx = 1;
		gbc_insertionPanel.gridy = 4;
		add(insertionPanel, gbc_insertionPanel);
		final GridBagLayout gbl_insertionPanel = new GridBagLayout();
		gbl_insertionPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_insertionPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_insertionPanel.columnWeights = new double[]
		{
				1.0, 1.0, Double.MIN_VALUE
		};
		gbl_insertionPanel.rowWeights = new double[]
		{
				0.0, 1.0, Double.MIN_VALUE
		};
		insertionPanel.setLayout(gbl_insertionPanel);

		final JPanel insertionTablePanel = new JPanel();
		insertionTablePanel.setBorder(new TitledBorder(null, "Insertion Table", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_insertionTablePanel = new GridBagConstraints();
		gbc_insertionTablePanel.gridheight = 2;
		gbc_insertionTablePanel.insets = new Insets(0, 0, 0, 5);
		gbc_insertionTablePanel.fill = GridBagConstraints.BOTH;
		gbc_insertionTablePanel.gridx = 0;
		gbc_insertionTablePanel.gridy = 0;
		insertionPanel.add(insertionTablePanel, gbc_insertionTablePanel);
		final GridBagLayout gbl_insertionTablePanel = new GridBagLayout();
		gbl_insertionTablePanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_insertionTablePanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_insertionTablePanel.columnWeights = new double[]
		{
				1.0, 0.0, 0.0, Double.MIN_VALUE
		};
		gbl_insertionTablePanel.rowWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		insertionTablePanel.setLayout(gbl_insertionTablePanel);

		final JScrollPane insertionTableScrollPane = new JScrollPane();
		final GridBagConstraints gbc_insertionTableScrollPane = new GridBagConstraints();
		gbc_insertionTableScrollPane.gridwidth = 3;
		gbc_insertionTableScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_insertionTableScrollPane.fill = GridBagConstraints.BOTH;
		gbc_insertionTableScrollPane.gridx = 0;
		gbc_insertionTableScrollPane.gridy = 0;
		insertionTablePanel.add(insertionTableScrollPane, gbc_insertionTableScrollPane);

		insertionTable = new JList<>();
		insertionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		insertionTableScrollPane.setViewportView(insertionTable);

		final JTextField insertionValueField = new JTextField();
		insertionValueField.setText("");
		insertionValueField.setColumns(10);
		final GridBagConstraints gbc_insertionValueField = new GridBagConstraints();
		gbc_insertionValueField.insets = new Insets(0, 0, 0, 5);
		gbc_insertionValueField.fill = GridBagConstraints.HORIZONTAL;
		gbc_insertionValueField.gridx = 0;
		gbc_insertionValueField.gridy = 1;
		insertionTablePanel.add(insertionValueField, gbc_insertionValueField);

		final JButton addInsertionValueButton = new JButton("Add");
		final GridBagConstraints gbc_addInsertionValueButton = new GridBagConstraints();
		gbc_addInsertionValueButton.insets = new Insets(0, 0, 0, 5);
		gbc_addInsertionValueButton.gridx = 1;
		gbc_addInsertionValueButton.gridy = 1;
		insertionTablePanel.add(addInsertionValueButton, gbc_addInsertionValueButton);

		final JButton applyInsertionValueButton = new JButton("Apply change");
		final GridBagConstraints gbc_applyInsertionValueButton = new GridBagConstraints();
		gbc_applyInsertionValueButton.gridx = 2;
		gbc_applyInsertionValueButton.gridy = 1;
		insertionTablePanel.add(applyInsertionValueButton, gbc_applyInsertionValueButton);

		insertionEnabledCB = new JCheckBox("Enable");
		final GridBagConstraints gbc_insertionEnabledCB = new GridBagConstraints();
		gbc_insertionEnabledCB.insets = new Insets(0, 0, 5, 0);
		gbc_insertionEnabledCB.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_insertionEnabledCB.gridx = 1;
		gbc_insertionEnabledCB.gridy = 0;
		insertionPanel.add(insertionEnabledCB, gbc_insertionEnabledCB);

		final JPanel insertionOptionsPanel = new JPanel();
		insertionOptionsPanel.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_insertionOptionsPanel = new GridBagConstraints();
		gbc_insertionOptionsPanel.fill = GridBagConstraints.BOTH;
		gbc_insertionOptionsPanel.gridx = 1;
		gbc_insertionOptionsPanel.gridy = 1;
		insertionPanel.add(insertionOptionsPanel, gbc_insertionOptionsPanel);
		final GridBagLayout gbl_insertionOptionsPanel = new GridBagLayout();
		gbl_insertionOptionsPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_insertionOptionsPanel.rowHeights = new int[]
		{
				0, 0, 0
		};
		gbl_insertionOptionsPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_insertionOptionsPanel.rowWeights = new double[]
		{
				0.0, 0.0, Double.MIN_VALUE
		};
		insertionOptionsPanel.setLayout(gbl_insertionOptionsPanel);

		final JPanel insertionMinMaxPanel = new JPanel();
		insertionMinMaxPanel.setBorder(new TitledBorder(null, "Min/Max", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_insertionMinMaxPanel = new GridBagConstraints();
		gbc_insertionMinMaxPanel.ipadx = 40;
		gbc_insertionMinMaxPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_insertionMinMaxPanel.insets = new Insets(0, 0, 5, 0);
		gbc_insertionMinMaxPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_insertionMinMaxPanel.gridx = 0;
		gbc_insertionMinMaxPanel.gridy = 0;
		insertionOptionsPanel.add(insertionMinMaxPanel, gbc_insertionMinMaxPanel);
		insertionMinMaxPanel.setLayout(new BoxLayout(insertionMinMaxPanel, BoxLayout.LINE_AXIS));

		insertionMinSpinner = new JSpinner();
		insertionMinSpinner.setModel(new SpinnerNumberModel(4, 1, 10000, 1));
		insertionMinMaxPanel.add(insertionMinSpinner);

		final JLabel insertionMinMaxLabel = new JLabel(" - ");
		insertionMinMaxPanel.add(insertionMinMaxLabel);

		insertionMaxSpinner = new JSpinner();
		insertionMaxSpinner.setModel(new SpinnerNumberModel(8, 1, 10000, 1));
		insertionMinMaxPanel.add(insertionMaxSpinner);

		final JPanel insertionTablePresetsPanel = new JPanel();
		insertionTablePresetsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Table Presets", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_insertionTablePresetsPanel = new GridBagConstraints();
		gbc_insertionTablePresetsPanel.anchor = GridBagConstraints.PAGE_START;
		gbc_insertionTablePresetsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_insertionTablePresetsPanel.gridx = 0;
		gbc_insertionTablePresetsPanel.gridy = 1;
		insertionOptionsPanel.add(insertionTablePresetsPanel, gbc_insertionTablePresetsPanel);
		insertionTablePresetsPanel.setLayout(new BorderLayout(0, 0));

		final JComboBox<InsertionTablePresets> insertionTablePresets = new JComboBox<>();
		insertionTablePresetsPanel.add(insertionTablePresets, BorderLayout.CENTER);

		// Convert Button
		convertButton.addActionListener(e ->
		{
			convertButton.setEnabled(false);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					doFancify();
				}
				catch (final Throwable t)
				{
					Main.exceptionMessageBox("Error while fancification", "Exception occurred during fancification", t);
				}
				finally
				{
					convertButton.setEnabled(true);
				}
			});
		});

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

		// ConvertTo list model
		convertTo.setModel(new AbstractListModel<String>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public int getSize()
			{
				final int selectedIndex = convertFrom.getSelectedIndex();

				return selectedIndex < 0 || selectedIndex > conversionMap.size() ? 0 : conversionMap.get(convertFrom.getSelectedIndex()).getValue().size();
			}

			@Override
			public String getElementAt(final int index)
			{
				return conversionMap.get(convertFrom.getSelectedIndex()).getValue().get(index);
			}
		});

		convertTo.addListSelectionListener(e ->
		{
			final int fromIndex = convertFrom.getSelectedIndex();
			final int toIndex = convertTo.getSelectedIndex();

			valueField.setText(fromIndex != -1 && toIndex != -1 ? conversionMap.get(fromIndex).getValue().get(toIndex) : "");

			valueField.updateUI();
		});

		addConversionValueButton.addActionListener(e ->
		{
			final int selectedIndex = convertFrom.getSelectedIndex();
			final String newCandidate = valueField.getText();

			if (!newCandidate.isEmpty() && !conversionMap.get(selectedIndex).getValue().contains(newCandidate))
				conversionMap.get(selectedIndex).getValue().add(newCandidate);

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

		// Conversion table presets combo box model
		conversionTablePresets.setModel(new DefaultComboBoxModel<>(ConversionTablePresets.values()));

		valueField.setText(convertFrom.getSelectedIndex() != -1 && convertTo.getSelectedIndex() != -1 ? conversionMap.get(convertFrom.getSelectedIndex()).getValue().get(convertTo.getSelectedIndex()) : "");

		conversionTablePresets.addActionListener(e ->
		{
			if (conversionTablePresets.getSelectedIndex() != -1)
			{
				initDefaultConversionMap((ConversionTablePresets) conversionTablePresets.getSelectedItem());
				convertFrom.updateUI();
				convertTo.updateUI();
			}
		});

		addInsertionValueButton.addActionListener(e ->
		{
			final String newCandidate = insertionValueField.getText();

			if (!newCandidate.isEmpty() && !insertionList.contains(newCandidate))
				insertionList.add(newCandidate);

			insertionTable.updateUI();
		});

		applyInsertionValueButton.addActionListener(e ->
		{
			if (insertionValueField.getText().isEmpty())
				insertionList.remove(insertionTable.getSelectedIndex());
			else
				insertionList.set(insertionTable.getSelectedIndex(), insertionValueField.getText());
			insertionTable.updateUI();
		});

		conversionEnabledCB.addActionListener(e ->
		{
			final boolean enabled = conversionEnabledCB.isSelected();
			conversionOptionsPanel.setEnabled(enabled);
			conversionCaseSensitiveCB.setEnabled(enabled);
			conversionRatePanel.setEnabled(enabled);
			conversionRateSpinner.setEnabled(enabled);
			conversionTablePresetsPanel.setEnabled(enabled);
			conversionTablePresets.setEnabled(enabled);

			conversionTabelPanel.setEnabled(enabled);
			convertFromPanel.setEnabled(enabled);
			convertFrom.setEnabled(enabled);
			addConversionKeyButton.setEnabled(enabled);
			addConversionValueButton.setEnabled(enabled);
			applyConversionKeyButton.setEnabled(enabled);
			applyConversionValueButton.setEnabled(enabled);
			convertToPanel.setEnabled(enabled);
			convertTo.setEnabled(enabled);
		});

		insertionEnabledCB.addActionListener(e ->
		{
			final boolean enabled = insertionEnabledCB.isSelected();
			insertionOptionsPanel.setEnabled(enabled);
			insertionMinMaxPanel.setEnabled(enabled);
			insertionMinSpinner.setEnabled(enabled);
			insertionMaxSpinner.setEnabled(enabled);
			insertionTablePresetsPanel.setEnabled(enabled);
			insertionTablePresets.setEnabled(enabled);

			insertionTablePanel.setEnabled(enabled);
			insertionTable.setEnabled(enabled);
			addInsertionValueButton.setEnabled(enabled);
			applyInsertionValueButton.setEnabled(enabled);
		});

		insertionTable.setModel(new AbstractListModel<String>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public int getSize()
			{
				return insertionList.size();
			}

			@Override
			public String getElementAt(final int index)
			{
				return insertionList.get(index);
			}
		});

		insertionTable.addListSelectionListener(e ->
		{
			final int index = insertionTable.getSelectedIndex();
			insertionValueField.setText(index == -1 ? "" : insertionList.get(index));
			insertionValueField.updateUI();
		});

		insertionValueField.setText(insertionTable.getSelectedIndex() == -1 ? "" : insertionList.get(insertionTable.getSelectedIndex()));

		insertionTablePresets.setModel(new DefaultComboBoxModel<>(InsertionTablePresets.values()));

		insertionTablePresets.addActionListener(e ->
		{
			if (insertionTablePresets.getSelectedIndex() != -1)
			{
				initDefaultInsertionMap((InsertionTablePresets) insertionTablePresets.getSelectedItem());
				insertionTable.updateUI();
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

	private void initDefaultInsertionMap(final InsertionTablePresets preset)
	{
		if (insertionList == null)
			insertionList = new Vector<>();
		else
			insertionList.clear();

		preset.apply(insertionList);
	}

	private static <K, V> Entry<K, V> createEntry(final K key, final V value)
	{
		return new SimpleImmutableEntry<>(key, value);
	}

	@SuppressWarnings("WeakerAccess")
	void doFancify()
	{
		final String input = textInputField.getText();
		final Random random = new Random();

		if (input == null || input.isEmpty())
		{
			textInputField.grabFocus();
			textInputField.setCaretColor(Color.red);
			return;
		}

		textInputField.setCaretColor(Color.black);

		final String output = performInsertion(performConversion(input, random), random);
		if (insertionEnabledCB.isSelected())
		{
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(output), null);
			textOutputField.setText("Copied to clipboard (Because Java swing glyph renderer doesn't properly support combining characters)");
		}
		else
			textOutputField.setText(output);
		textOutputField.updateUI();
	}

	private String performInsertion(final String input, final Random random)
	{
		if (!insertionEnabledCB.isSelected())
			return input;

		final int minimum = (int) insertionMinSpinner.getValue();
		final int maximum = (int) insertionMinSpinner.getValue();

		final char[] charArray = input.toCharArray();
		final StringBuilder outputBuilder = new StringBuilder(charArray.length << 1);
		for (final char c : charArray)
		{
			outputBuilder.append(c);
			final int count = maximum > minimum ? minimum + random.nextInt(maximum - minimum) : minimum;
			for (int i = 0; i < count; i++)
				outputBuilder.append(insertionList.get(random.nextInt(insertionList.size())));
		}

		return outputBuilder.toString();
	}

	private String performConversion(final String input, final Random random)
	{
		if (!conversionEnabledCB.isSelected())
			return input;

		final boolean caseSensitive = conversionCaseSensitiveCB.isSelected();
		final double convertRate = (int) conversionRateSpinner.getValue() / 100.0;

		// TODO: I know this is the worst solution for the problem. Fix it later when the better solution found.
		final List<Entry<String, List<String>>> fixedConversionMap = new ArrayList<>(conversionMap.size());

		conversionMap.forEach(entry ->
		{
			final Optional<Entry<String, List<String>>> duplicateEntry = fixedConversionMap.stream().filter(otherEntry -> caseSensitive ? otherEntry.getKey().equals(entry.getKey()) : otherEntry.getKey().equalsIgnoreCase(entry.getKey())).findFirst();

			// Merge duplicate cases
			if (duplicateEntry.isPresent()) // If the key that have same name already exists, add all values onto it.
				entry.getValue().stream().filter(valueToAdd -> !duplicateEntry.get().getValue().contains(valueToAdd)).forEach(valueToAdd -> duplicateEntry.get().getValue().add(valueToAdd));
			else
				fixedConversionMap.add(new SimpleImmutableEntry<>(entry.getKey(), new ArrayList<>(entry.getValue()))); // Re-allocate is required because if we don't re-allocate it, it also TAMPER original conversion table.
		});

		// https://banana-media-lab.tistory.com/entry/JAVA-ArrayList-내-단어-길이-내림차순-정렬 [Banana Media Lab]
		fixedConversionMap.sort((entry1, entry2) -> Integer.compare(entry2.getKey().length(), entry1.getKey().length()));

		final int inputLength = input.length();
		final char[] charArray = input.toCharArray();
		final StringBuilder outputBuilder = new StringBuilder(charArray.length);

		for (int currentIndex = 0, maxIndex = charArray.length; currentIndex < maxIndex; currentIndex++)
		{
			final char currentChar = charArray[currentIndex];
			boolean changed = false;

			if (random.nextFloat() <= convertRate)
				for (final Entry<String, List<String>> entry : fixedConversionMap)
				{
					final String replaceFrom = entry.getKey();
					final int replaceFromLength = replaceFrom.length();

					final List<String> replaceToCandidates = entry.getValue();

					if (replaceFromLength > 1)
					{
						if (inputLength - currentIndex >= replaceFromLength) // Input string length check
						{
							final String checkBuffer = input.substring(currentIndex, currentIndex + replaceFromLength);

							if (caseSensitive ? checkBuffer.equals(replaceFrom) : checkBuffer.equalsIgnoreCase(replaceFrom))
							{
								final String replaceTo = replaceToCandidates.get(random.nextInt(replaceToCandidates.size()));

								outputBuilder.append(replaceTo);

								currentIndex += replaceFromLength - 1; // Skip already-read texts
								changed = true;
							}
						}
					}
					else
					{
						final char replaceFromChar = replaceFrom.charAt(0);

						if (!changed && (replaceFromChar == currentChar || !caseSensitive && (Character.toLowerCase(replaceFromChar) == Character.toLowerCase(currentChar) || Character.toUpperCase(replaceFromChar) == Character.toUpperCase(currentChar))))
						{
							outputBuilder.append(replaceToCandidates.get(random.nextInt(replaceToCandidates.size())));
							changed = true;
						}
					}
				}

			if (!changed)
				outputBuilder.append(currentChar);
		}

		return outputBuilder.toString();
	}
}
