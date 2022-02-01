package fancytext;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import fancytext.gui.ClipboardAnalyzer;
import fancytext.gui.encode.Base64Coder;
import fancytext.gui.Hasher;
import fancytext.gui.encrypt.PublicKeyCipher;
import fancytext.gui.encrypt.SymmetricKeyCipher;
import fancytext.gui.fancify.TextFancifyTab;
import fancytext.utils.MultiThreading;

public final class Main extends JFrame
{
	private static final long serialVersionUID = -7701915105049741216L;

	public static final Logger LOGGER = Logger.getLogger("FancyText");
	public static final String lineSeparator = System.lineSeparator();

	private static final Dimension preferredSize = new Dimension(1500, 1000);

	private static final Pattern crPattern = Pattern.compile("\r", Pattern.LITERAL);
	private static final Pattern lfPattern = Pattern.compile("\n", Pattern.LITERAL);

	static Main mainFrame;
	private final JPanel contentPane;

	private static File recentChooserDirectory;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args)
	{
		EventQueue.invokeLater(() ->
		{
			try
			{
				// <editor-fold desc="Look-and-Feel setup">
				try
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
				{
					exceptionMessageBox("Look-and-Feel setup failed", null, e);
				}
				// </editor-fold>

				mainFrame = new Main();
			}
			catch (final Throwable t)
			{
				exceptionMessageBox(t.getClass().getCanonicalName(), null, t);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private Main()
	{
		final RuntimeMXBean runtimeMan = ManagementFactory.getRuntimeMXBean();
		LOGGER.info("FancyText by eric0210");
		LOGGER.info(String.format("Running on JVM %s version: %s vendor: %s", runtimeMan.getVmName(), runtimeMan.getVmVersion(), runtimeMan.getVmVendor()));

		// <editor-fold desc="Main frame title setup">
		setTitle("Fancy Text Generator");
		// </editor-fold>

		// <editor-fold desc="Main frame size setup">
		setMinimumSize(preferredSize);
		setPreferredSize(preferredSize);
		// </editor-fold>

		// <editor-fold desc="BouncyCastle setup">
		/* https://stackoverflow.com/questions/6442012/whats-the-best-way-to-integrate-the-bouncy-castle-provider-in-a-java-program */
		try
		{
			Security.addProvider((Provider) Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider").getConstructor().newInstance());
		}
		catch (final NoClassDefFoundError | ClassNotFoundException | SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
		{
			exceptionMessageBox("Failed to register/load BouncyCastle library", null, e);
			dispose();
		}
		// </editor-fold>

		// <editor-fold desc="Main frame close operation setup">
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		// </editor-fold>

		// <editor-fold desc="Main frame bound setup">
		setBounds(100, 100, 450, 300);
		// </editor-fold>

		// <editor-fold desc="Main content pane setup">
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		// </editor-fold>

		// <editor-fold desc="Main content pane layout setup">
		final GridBagLayout contentPaneLayout = new GridBagLayout();
		contentPaneLayout.columnWidths = new int[]
		{
				0
		};
		contentPaneLayout.rowHeights = new int[]
		{
				0, 0
		};
		contentPaneLayout.columnWeights = new double[]
		{
				1.0
		};
		contentPaneLayout.rowWeights = new double[]
		{
				Double.MIN_VALUE, 0.0
		};
		contentPane.setLayout(contentPaneLayout);
		// </editor-fold>

		LOGGER.info("Loading tabs...");

		// <editor-fold desc="Load tabs">
		final JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP);

		final int scrollSize = 10;

		final long was = System.nanoTime();

		final Collection<Runnable> tasks = new ArrayDeque<>(6);

		tasks.add(() ->
		{
			final JScrollPane scroll = new JScrollPane(new TextFancifyTab());
			scroll.getHorizontalScrollBar().setUnitIncrement(scrollSize);
			scroll.getVerticalScrollBar().setUnitIncrement(scrollSize);
			EventQueue.invokeLater(() -> tabs.addTab("Translater", scroll));
		});
		tasks.add(() ->
		{
			final JScrollPane scroll = new JScrollPane(new Base64Coder());
			scroll.getHorizontalScrollBar().setUnitIncrement(scrollSize);
			scroll.getVerticalScrollBar().setUnitIncrement(scrollSize);
			EventQueue.invokeLater(() -> tabs.addTab("Base64 Coder", scroll));
		});
		tasks.add(() ->
		{
			final JScrollPane scroll = new JScrollPane(new SymmetricKeyCipher());
			scroll.getHorizontalScrollBar().setUnitIncrement(scrollSize);
			scroll.getVerticalScrollBar().setUnitIncrement(scrollSize);
			EventQueue.invokeLater(() -> tabs.addTab("Symmetric-key Algorithm Cipher", scroll));
		});
		tasks.add(() ->
		{
			final JScrollPane scroll = new JScrollPane(new PublicKeyCipher());
			scroll.getHorizontalScrollBar().setUnitIncrement(scrollSize);
			scroll.getVerticalScrollBar().setUnitIncrement(scrollSize);
			EventQueue.invokeLater(() -> tabs.addTab("Public-key Algorithm Cipher", scroll));
		});
		tasks.add(() ->
		{
			final JScrollPane scroll = new JScrollPane(new Hasher());
			scroll.getHorizontalScrollBar().setUnitIncrement(scrollSize);
			scroll.getVerticalScrollBar().setUnitIncrement(scrollSize);
			EventQueue.invokeLater(() -> tabs.addTab("Hasher", scroll));
		});
		tasks.add(() ->
		{
			final JScrollPane scroll = new JScrollPane(new ClipboardAnalyzer());
			scroll.getHorizontalScrollBar().setUnitIncrement(scrollSize);
			scroll.getVerticalScrollBar().setUnitIncrement(scrollSize);
			EventQueue.invokeLater(() -> tabs.addTab("Clipboard DataFlavor Analyzer", scroll));
		});

		MultiThreading.submitRunnables(tasks);

		LOGGER.info("Queued tabs. Took " + timeDeltaToString(was));
		// </editor-fold>

		final GridBagConstraints tabsLayout = new GridBagConstraints();
		tabsLayout.insets = new Insets(0, 0, 5, 0);
		tabsLayout.fill = GridBagConstraints.BOTH;
		tabsLayout.gridx = 0;
		tabsLayout.gridy = 0;

		contentPane.add(tabs, tabsLayout);

		// <editor-fold desc="Author label">
		final JLabel byEric0210 = new JLabel("- by eric0210");
		byEric0210.setEnabled(false);
		final GridBagConstraints gbc_byEric0210 = new GridBagConstraints();
		gbc_byEric0210.anchor = GridBagConstraints.LAST_LINE_END;
		gbc_byEric0210.gridx = 0;
		gbc_byEric0210.gridy = 1;
		contentPane.add(byEric0210, gbc_byEric0210);
		// </editor-fold>

		Runtime.getRuntime().addShutdownHook(new Thread(MultiThreading::shutdownAll));

		pack();
		setVisible(true);
	}

	public static void notificationMessageBox(final String title, final String message, final int optionType, final int messageType, final String[] options, final String initialOption)
	{
		EventQueue.invokeLater(() -> JOptionPane.showOptionDialog(mainFrame, message, title, optionType, messageType, null, options, initialOption));
	}

	// <editor-fold desc="Message Box">
	public static Future<Integer> warningMessageBox(final String title, final String message, final int optionType, final int messageType, final String[] options, final String initialOption)
	{
		LOGGER.log(Level.WARNING, String.format("Warning:%s%s", lineSeparator, message));

		final CompletableFuture<Integer> result = new CompletableFuture<>();

		// on EDT, we don't need to queue the operation.
		if (EventQueue.isDispatchThread())
			result.complete(JOptionPane.showOptionDialog(mainFrame, message, title, optionType, messageType, null, options, initialOption));
		else
			EventQueue.invokeLater(() -> result.complete(JOptionPane.showOptionDialog(mainFrame, message, title, optionType, messageType, null, options, initialOption)));

		return result;
	}

	public static void exceptionMessageBox(final String title, final String message, final Throwable thrown)
	{
		EventQueue.invokeLater(() ->
		{
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			final boolean hasThrowable = thrown != null;

			if (hasThrowable)
				LOGGER.log(Level.SEVERE, String.format("Exception:%s%s", lineSeparator, message), thrown);
			else
				LOGGER.log(Level.SEVERE, String.format("Exception:%s%s", lineSeparator, message));

			pw.println();

			if (message != null)
			{
				pw.println(message);
				pw.println();
			}

			if (hasThrowable)
			{
				pw.println(thrown);
				pw.println();
				pw.println();
				pw.println("Do you want to see the full exception stack trace?");
			}
			pw.close();

			final String msg = sw.toString();

			final int option = hasThrowable ? JOptionPane.showOptionDialog(mainFrame, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]
			{
					"Yes!", "No."
			}, "No.") : JOptionPane.showOptionDialog(mainFrame, msg, title, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);

			if (option == 0 && hasThrowable)
			{
				final StringWriter stackTraceStringWriter = new StringWriter();
				final PrintWriter stackTraceWriter = new PrintWriter(stackTraceStringWriter);

				thrown.printStackTrace(stackTraceWriter);

				JOptionPane.showOptionDialog(mainFrame, stackTraceStringWriter.toString(), "Full exception stack-trace", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);

				stackTraceWriter.close();
			}
		});
	}
	// </editor-fold>

	public static String filterStringForPopup(final String message)
	{
		String filteredString = message;

		// Limit the message length by 64 bytes
		if (filteredString.length() > 64)
			filteredString = filteredString.substring(0, 64) + "(...)";

		// Strip any kinds of the line-separators from the message
		filteredString = crPattern.matcher(filteredString).replaceAll("");
		filteredString = lfPattern.matcher(filteredString).replaceAll("");

		return filteredString;
	}

	// <editor-fold desc="generateFindFileGUI">
	public static String generateFindFileGUI(final String inputFieldText)
	{
		final JFileChooser chooser = new JFileChooser();

		if (inputFieldText != null && !inputFieldText.isEmpty())
			chooser.setSelectedFile(new File(inputFieldText));

		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		Optional.ofNullable(recentChooserDirectory).ifPresent(chooser::setCurrentDirectory);

		final int result = chooser.showOpenDialog(mainFrame);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			recentChooserDirectory = chooser.getSelectedFile();
			return chooser.getSelectedFile().getAbsolutePath();
		}

		return null;
	}
	// </editor-fold>

	// <editor-fold desc="setBusyCursor">
	public static void setBusyCursor(final Component component, final boolean busy)
	{
		component.setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
	}
	// </editor-fold>

	// <editor-fold desc="timeDeltaToString">
	private static String timeDeltaToString(final long nanoTime)
	{
		final long delta = System.nanoTime() - nanoTime;
		final long microSeconds = TimeUnit.NANOSECONDS.toMicros(delta);
		final long milliSeconds = TimeUnit.NANOSECONDS.toMillis(delta);
		final long seconds = TimeUnit.NANOSECONDS.toSeconds(delta);
		return String.format("%ds, %dms, %dus, %dns", seconds, milliSeconds, microSeconds, delta);
	}
	// </editor-fold>
}
