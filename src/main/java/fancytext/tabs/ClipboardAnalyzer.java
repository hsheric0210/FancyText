package fancytext.tabs;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public final class ClipboardAnalyzer extends JPanel
{
	private static final long serialVersionUID = -6974918867363781757L;

	private final JTable dataFlavorTable;

	public ClipboardAnalyzer()
	{
		// <editor-fold desc="UI initialization">
		setBorder(BorderFactory.createTitledBorder(null, "Clipboard analyzer", TitledBorder.LEADING, TitledBorder.TOP));

		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{
				0, 0
		};
		gridBagLayout.rowHeights = new int[]
		{
				0, 0, 0
		};
		gridBagLayout.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gridBagLayout.rowWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		setLayout(gridBagLayout);

		final JPanel dataFlavorPanel = new JPanel();
		dataFlavorPanel.setBorder(BorderFactory.createTitledBorder(null, "Data", TitledBorder.LEADING, TitledBorder.TOP));
		final GridBagConstraints gbc_dataFlavorPanel = new GridBagConstraints();
		gbc_dataFlavorPanel.insets = new Insets(0, 0, 5, 0);
		gbc_dataFlavorPanel.fill = GridBagConstraints.BOTH;
		gbc_dataFlavorPanel.gridx = 0;
		gbc_dataFlavorPanel.gridy = 0;
		add(dataFlavorPanel, gbc_dataFlavorPanel);
		final GridBagLayout gbl_dataFlavorPanel = new GridBagLayout();
		gbl_dataFlavorPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_dataFlavorPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_dataFlavorPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_dataFlavorPanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		dataFlavorPanel.setLayout(gbl_dataFlavorPanel);

		final JScrollPane dataFlavorScrollPane = new JScrollPane();
		final GridBagConstraints gbc_dataFlavorScrollPane = new GridBagConstraints();
		gbc_dataFlavorScrollPane.fill = GridBagConstraints.BOTH;
		gbc_dataFlavorScrollPane.gridx = 0;
		gbc_dataFlavorScrollPane.gridy = 0;
		dataFlavorPanel.add(dataFlavorScrollPane, gbc_dataFlavorScrollPane);

		dataFlavorTable = new JTable();
		dataFlavorTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		dataFlavorTable.setFillsViewportHeight(true);
		dataFlavorTable.setCellSelectionEnabled(true);
		dataFlavorTable.setColumnSelectionAllowed(true);
		final GridBagConstraints gbc_dataFlavorTable = new GridBagConstraints();
		gbc_dataFlavorTable.insets = new Insets(0, 0, 5, 0);
		gbc_dataFlavorTable.fill = GridBagConstraints.BOTH;
		gbc_dataFlavorTable.gridx = 0;
		gbc_dataFlavorTable.gridy = 1;

		dataFlavorScrollPane.setViewportView(dataFlavorTable);

		final JButton analyzeButton = new JButton("Analyze now!");
		final GridBagConstraints gbc_analyzeButton = new GridBagConstraints();
		gbc_analyzeButton.gridx = 0;
		gbc_analyzeButton.gridy = 1;
		analyzeButton.setToolTipText("Analyze the current clip-board data and display supported data-flavor(s) and corresponding data");
		add(analyzeButton, gbc_analyzeButton);
		// </editor-fold>

		// <editor-fold desc="Models">
		dataFlavorTable.setModel(new DataFlavorTableModel(null));

		// </editor-fold>

		// <editor-fold desc="Lambdas">
		analyzeButton.addActionListener(e -> analyze());
		// </editor-fold>
	}

	private void analyze()
	{
		try
		{
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

			final Transferable contents = clipboard.getContents(null);

			final DataFlavor[] dataFlavors = contents.getTransferDataFlavors();

			final int dataFlavorsSize = dataFlavors.length;

			// <editor-fold desc="Initialize the table values">
			final Map<String, Map<Integer, String>> tableValues = new HashMap<>();
			tableValues.put(DataFlavorTableModel.MIME_TYPE, new HashMap<>());
			tableValues.put(DataFlavorTableModel.PRIMARY_TYPE, new HashMap<>());
			tableValues.put(DataFlavorTableModel.SUBTYPE, new HashMap<>());
			tableValues.put(DataFlavorTableModel.VALUE, new HashMap<>());
			// </editor-fold>

			// <editor-fold desc="Do the work">

			boolean dontAnalyzeParameters = false;

			for (int i = 0; i < dataFlavorsSize; i++)
			{
				final DataFlavor flavor = dataFlavors[i];

				final String mimeTypeString = flavor.getMimeType();
				final String primaryType = flavor.getPrimaryType();
				final String subtype = flavor.getSubType();

				tableValues.get(DataFlavorTableModel.MIME_TYPE).put(i, mimeTypeString);
				tableValues.get(DataFlavorTableModel.PRIMARY_TYPE).put(i, primaryType);
				tableValues.get(DataFlavorTableModel.SUBTYPE).put(i, subtype);

				if (!dontAnalyzeParameters)
					try
					{
						final Field mimeTypeField = flavor.getClass().getDeclaredField("mimeType");
						mimeTypeField.setAccessible(true);
						final Object mimeType = mimeTypeField.get(flavor);

						final Method MimeType_getParameters = mimeType.getClass().getDeclaredMethod("getParameters");
						MimeType_getParameters.setAccessible(true);
						final Object MimeTypeParameterList = MimeType_getParameters.invoke(mimeType);

						final Field parametersField = MimeTypeParameterList.getClass().getDeclaredField("parameters");
						parametersField.setAccessible(true);
						final Map<String, String> parameters = (Map<String, String>) parametersField.get(MimeTypeParameterList);

						for (final Entry<String, String> entry : parameters.entrySet())
						{
							final String key = entry.getKey();
							final String firstWordCapitalized = Character.toUpperCase(key.charAt(0)) + key.substring(1); // First word capitalized
							if (!tableValues.containsKey(firstWordCapitalized))
								tableValues.put(firstWordCapitalized, new HashMap<>());

							tableValues.get(firstWordCapitalized).put(i, entry.getValue());
						}
					}
					catch (final NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | RuntimeException e)
					{
						dontAnalyzeParameters = true;
						e.printStackTrace();
					}

				Charset charset = null;
				if (tableValues.containsKey("Charset") && tableValues.get("Charset").containsKey(i))
					try
					{
						charset = Charset.forName(tableValues.get("Charset").get(i));
					}
					catch (final UnsupportedCharsetException ignored)
					{

					}

				String stringValue;

				try
				{
					stringValue = toString(contents.getTransferData(flavor), charset);
				}
				catch (final UnsupportedFlavorException | IOException e)
				{
					stringValue = e.toString();
				}

				tableValues.get(DataFlavorTableModel.VALUE).put(i, stringValue);
			}
			// </editor-fold>

			// <editor-fold desc="Sort values">
			// LinkedHashMap은 HashMap과는 다르게 저장 당시에 값을 저장한 순서를 유지합니다 (HashMap은 순서를 유지하지 않고, 자동으로 정렬 처리함).
			final Map<String, Map<Integer, String>> tableValuesSorted = new LinkedHashMap<>();

			tableValuesSorted.put(DataFlavorTableModel.MIME_TYPE, tableValues.get(DataFlavorTableModel.MIME_TYPE));
			tableValuesSorted.put(DataFlavorTableModel.PRIMARY_TYPE, tableValues.get(DataFlavorTableModel.PRIMARY_TYPE));
			tableValuesSorted.put(DataFlavorTableModel.SUBTYPE, tableValues.get(DataFlavorTableModel.SUBTYPE));

			for (final Entry<String, Map<Integer, String>> entry : tableValues.entrySet())
				if (Stream.of(DataFlavorTableModel.MIME_TYPE, DataFlavorTableModel.PRIMARY_TYPE, DataFlavorTableModel.SUBTYPE, DataFlavorTableModel.VALUE).noneMatch(s -> s.equals(entry.getKey())))
					tableValuesSorted.put(entry.getKey(), entry.getValue());

			tableValuesSorted.put(DataFlavorTableModel.VALUE, tableValues.get(DataFlavorTableModel.VALUE));
			// </editor-fold>

			// <editor-fold desc="Apply the changes to the table model">
			dataFlavorTable.setModel(new DataFlavorTableModel(tableValuesSorted));

			// Increase the width of string value column
			dataFlavorTable.getColumnModel().getColumn(tableValuesSorted.size() - 1).setPreferredWidth(400);
			dataFlavorTable.getColumnModel().getColumn(tableValuesSorted.size() - 1).setMinWidth(200);

			// Increase the width of MIME type column
			dataFlavorTable.getColumnModel().getColumn(0).setPreferredWidth(400);
			dataFlavorTable.getColumnModel().getColumn(0).setMinWidth(200);

			dataFlavorTable.updateUI();
			// </editor-fold>
		}
		catch (final HeadlessException | SecurityException | IllegalArgumentException e)
		{
			e.printStackTrace(); // TODO
		}
	}

	private String toString(final Object data, final Charset charset)
	{
		if (data == null)
			return "null";

		// <editor-fold desc="If the data is CharSequence (String)">
		if (data instanceof CharSequence)
			return ((CharSequence) data).toString();
		// </editor-fold>

		// <editor-fold desc="If the data is an Input-stream">
		if (data instanceof InputStream)
		{
			final InputStream inputStream = (InputStream) data;

			try
			{
				final StringJoiner joiner = new StringJoiner(" ");

				final byte[] buffer = new byte[1024];
				final int actualRead;
				if ((actualRead = inputStream.read(buffer, 0, buffer.length)) > 0)
				{
					if (charset != null)
						return new String(buffer, 0, actualRead, charset);

					for (final byte b : buffer)
						joiner.add(String.format("%02X", b));
				}

				return joiner.toString();
			}
			catch (final OutOfMemoryError | IOException e)
			{
				return "Error: " + e;
			}
		}
		// </editor-fold>

		// <editor-fold desc="If the data is a Reader">
		if (data instanceof Reader)
		{
			final Reader reader = (Reader) data;

			try (final BufferedReader bReader = new BufferedReader(reader))
			{
				return bReader.lines().collect(Collectors.joining());
			}
			catch (final IOException e)
			{
				return "Error: " + e;
			}
		}
		// </editor-fold>

		// <editor-fold desc="If the data is a byte-buffer">
		if (data instanceof ByteBuffer)
		{
			final ByteBuffer buffer = (ByteBuffer) data;

			if (charset != null)
				try
				{
					return charset.decode(buffer).toString();
				}
				catch (final Throwable t)
				{
					return "Error: " + t;
				}

			final byte[] bytes = new byte[buffer.position()];
			buffer.get(bytes, 0, bytes.length);

			final StringJoiner joiner = new StringJoiner(" ");

			for (final byte b : bytes)
				joiner.add(String.format("%02X", b));

			return joiner.toString();
		}
		// </editor-fold>

		// <editor-fold desc="If the data is an array">
		final Class<?> dataClass = data.getClass();
		if (dataClass.isArray())
		{
			final StringJoiner joiner = new StringJoiner(" ");

			if (dataClass == byte[].class)
			{
				final byte[] bArr = (byte[]) data;

				if (charset != null)
					return new String(bArr, 0, bArr.length, charset);

				for (final byte b : bArr)
					joiner.add(String.format("%02X", b));
			}
			else if (dataClass == char[].class)
			{
				final char[] cArr = (char[]) data;
				return new String(cArr);
			}
			else if (dataClass == short[].class)
			{
				final short[] sArr = (short[]) data;
				for (final short s : sArr)
					joiner.add(String.format("%d", s));
			}
			else if (dataClass == int[].class)
			{
				final int[] iArr = (int[]) data;
				for (final int i : iArr)
					joiner.add(String.format("%d", i));
			}
			else if (dataClass == long[].class)
			{
				final long[] lArr = (long[]) data;
				for (final long l : lArr)
					joiner.add(String.format("%d", l));
			}

			return joiner.toString();
		}
		// </editor-fold>

		// <editor-fold desc="If the class of the data does not implements toString() method">
		Method toStringMethod = null;
		try
		{
			toStringMethod = dataClass.getMethod("toString");
		}
		catch (final NoSuchMethodException ignored)
		{

		}

		return toStringMethod != null && toStringMethod.getDeclaringClass() == Object.class ? dataClass.getCanonicalName() + " does not implements \"toString()\" method." : data.toString();
		// </editor-fold>

	}

	private static final class DataFlavorTableModel extends DefaultTableModel
	{
		private static final long serialVersionUID = 3061599991851125932L;
		private static final String MIME_TYPE = "MIME Type";
		private static final String PRIMARY_TYPE = "Primary Type";
		private static final String SUBTYPE = "Sub Type";
		private static final String VALUE = "Value";
		private static final String[] EMPTY_STRING_ARRAY = new String[0];

		private DataFlavorTableModel(final Map<String, Map<Integer, String>> tableValues)
		{
			final Object[][] dataVector;
			if (tableValues == null)
				dataVector = new Object[][]
				{
						null
				};
			else
			{
				dataVector = new Object[tableValues.get(MIME_TYPE).size()][tableValues.size()];
				tableValues.forEach((key, rowValues) ->
				{
					for (int i = 0, j = rowValues.size(); i < j; i++)
					{
						final List<String> tableValuesKeysList = new ArrayList<>(tableValues.keySet());
						final int column = tableValuesKeysList.indexOf(key);

						dataVector[i][column] = rowValues.get(i);
					}
				});
			}

			setDataVector(dataVector, tableValues == null ? new String[]
			{
					"", ""
			} : tableValues.keySet().toArray(EMPTY_STRING_ARRAY));
		}

		@Override
		public Class<String> getColumnClass(final int columnIndex)
		{
			return String.class;
		}

		@Override
		public boolean isCellEditable(final int row, final int column)
		{
			return false;
		}
	}
}
