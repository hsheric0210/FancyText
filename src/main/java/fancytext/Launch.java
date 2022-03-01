package fancytext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Launch
{
	public static final Logger LOGGER = Logger.getLogger("FancyText Launcher");

	public static void main(final String... args)
	{
		final String providerName = "%BouncyCastleProvider%"; // Will be replaced by gradle
		final File extractedProviderPath = new File(System.getProperty("java.io.tmpdir"), providerName);

		if (extractedProviderPath.exists())
			LOGGER.info("BouncyCastle provider already exists in " + extractedProviderPath);
		else
		{
			LOGGER.info("Extracting BouncyCastle provider into " + extractedProviderPath);

			try
			{
				final InputStream resourceAsStream = Launch.class.getResourceAsStream('/' + providerName);
				if (resourceAsStream == null)
				{
					LOGGER.log(Level.SEVERE, "BouncyCastle provider (" + providerName + ") not found from JAR");
					return;
				}
				Files.copy(resourceAsStream, extractedProviderPath.toPath());
			}
			catch (final IOException e)
			{
				LOGGER.log(Level.SEVERE, "Failed to extract the BouncyCastle provider (" + providerName + ") into current directory", e);
			}
		}

		final List<String> inputArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
		final List<String> cmd = new ArrayList<>(6 + args.length + inputArgs.size());

		cmd.add(System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java ");

		cmd.addAll(inputArgs);

		cmd.add("-cp"); // Load BouncyCastle with class-path to register it as security provider without any errors/exceptions
		cmd.add(ManagementFactory.getRuntimeMXBean().getClassPath() + File.pathSeparatorChar + extractedProviderPath);

		if (!System.getProperty("java.version").startsWith("1.")) // Java version 8 and older doesn't support '--add-opens' parameter
		{
			// Workaround for InaccessibleObjectException in ClipboardAnalyzer since Java 9 and later
			cmd.add("--add-opens");
			cmd.add("java.datatransfer/java.awt.datatransfer=ALL-UNNAMED");
		}

		cmd.add("fancytext.Main");
		cmd.addAll(Arrays.asList(args));

		final StringJoiner joiner = new StringJoiner(" ");
		cmd.forEach(joiner::add);
		LOGGER.info("Commandline is \"" + joiner + "\"");

		try
		{
			System.exit(new ProcessBuilder(cmd).inheritIO().start().waitFor());
		}
		catch (final IOException e)
		{
			LOGGER.log(Level.SEVERE, "Failed to launch FancyText", e);
		}
		catch (final InterruptedException e)
		{
			LOGGER.log(Level.SEVERE, "Wait interrupted", e);
		}
	}

	private Launch()
	{
	}
}
