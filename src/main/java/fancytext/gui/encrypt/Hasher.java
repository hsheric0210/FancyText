package fancytext.gui.encrypt;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Optional;
import java.util.zip.Adler32;
import java.util.zip.CRC32;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import at.favre.lib.bytes.Bytes;
import fancytext.Main;
import fancytext.hash.HashAlgorithm;
import fancytext.utils.CRC16;
import fancytext.gui.EncodedIOPanel;
import fancytext.utils.encoding.Encoding;
import fancytext.utils.MultiThreading;

public final class Hasher extends JPanel
{
	private static final long serialVersionUID = 8738449172274570395L;
	private final JComboBox<HashAlgorithm> hashAlgorithmCB;
	private final JComboBox<Integer> hashDigestSizeBitsCB;
	private final JPanel hashStateSizeBitsPanel;
	private final JComboBox<Integer> hashStateSizeBitsCB;
	private final EncodedIOPanel inputPanel;
	private final EncodedIOPanel hashOutputPanel;

	public Hasher()
	{
		// <editor-fold desc="UI Setup">
		// Main border setup
		setBorder(new TitledBorder(null, "Hasher", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		setSize(1000, 1000);

		// Main layout setup
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]
		{
				0, 0
		};
		gridBagLayout.rowHeights = new int[]
		{
				0, 0, 0, 0, 0
		};
		gridBagLayout.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gridBagLayout.rowWeights = new double[]
		{
				0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE
		};
		setLayout(gridBagLayout);

		inputPanel = new EncodedIOPanel("Plain-text input", Encoding.UTF_8);
		final GridBagConstraints gbc_inputPanel = new GridBagConstraints();
		gbc_inputPanel.insets = new Insets(0, 0, 5, 0);
		gbc_inputPanel.fill = GridBagConstraints.BOTH;
		gbc_inputPanel.gridx = 0;
		gbc_inputPanel.gridy = 0;
		add(inputPanel, gbc_inputPanel);

		// Hash settings panel
		final JPanel hashSettingsPanel = new JPanel();
		hashSettingsPanel.setBorder(new TitledBorder(null, "Hash settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_hashSettingsPanel = new GridBagConstraints();
		gbc_hashSettingsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_hashSettingsPanel.fill = GridBagConstraints.BOTH;
		gbc_hashSettingsPanel.gridx = 0;
		gbc_hashSettingsPanel.gridy = 1;
		add(hashSettingsPanel, gbc_hashSettingsPanel);
		final GridBagLayout gbl_hashSettingsPanel = new GridBagLayout();
		gbl_hashSettingsPanel.columnWidths = new int[]
		{
				0, 0, 0, 0
		};
		gbl_hashSettingsPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_hashSettingsPanel.columnWeights = new double[]
		{
				0.0, 0.0, 0.0, Double.MIN_VALUE
		};
		gbl_hashSettingsPanel.rowWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		hashSettingsPanel.setLayout(gbl_hashSettingsPanel);

		// Hash settings panel - Hash algorithm panel
		final JPanel hashAlgorithmPanel = new JPanel();
		hashAlgorithmPanel.setBorder(new TitledBorder(null, "Hash algorithm", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_hashAlgorithmPanel = new GridBagConstraints();
		gbc_hashAlgorithmPanel.ipadx = 60;
		gbc_hashAlgorithmPanel.insets = new Insets(0, 0, 0, 5);
		gbc_hashAlgorithmPanel.fill = GridBagConstraints.BOTH;
		gbc_hashAlgorithmPanel.gridx = 0;
		gbc_hashAlgorithmPanel.gridy = 0;
		hashSettingsPanel.add(hashAlgorithmPanel, gbc_hashAlgorithmPanel);
		final GridBagLayout gbl_hashAlgorithmPanel = new GridBagLayout();
		gbl_hashAlgorithmPanel.columnWidths = new int[]
		{
				0, 0
		};
		gbl_hashAlgorithmPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_hashAlgorithmPanel.columnWeights = new double[]
		{
				1.0, Double.MIN_VALUE
		};
		gbl_hashAlgorithmPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		hashAlgorithmPanel.setLayout(gbl_hashAlgorithmPanel);

		// Hash settings panel - Hash algorithm panel - Hash algorithm combo box
		hashAlgorithmCB = new JComboBox<>();
		final GridBagConstraints gbc_hashAlgorithmCB = new GridBagConstraints();
		gbc_hashAlgorithmCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_hashAlgorithmCB.gridx = 0;
		gbc_hashAlgorithmCB.gridy = 0;
		hashAlgorithmPanel.add(hashAlgorithmCB, gbc_hashAlgorithmCB);

		// Hash button
		final JButton hashButton = new JButton("Hash");
		final GridBagConstraints gbc_hashButton = new GridBagConstraints();
		gbc_hashButton.ipadx = 40;
		gbc_hashButton.insets = new Insets(0, 0, 5, 0);
		gbc_hashButton.gridx = 0;
		gbc_hashButton.gridy = 2;
		add(hashButton, gbc_hashButton);

		hashStateSizeBitsPanel = new JPanel();
		hashStateSizeBitsPanel.setVisible(false);
		hashStateSizeBitsPanel.setBorder(new TitledBorder(null, "Hash state size bits", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		final GridBagConstraints gbc_hashStateSizeBitsPanel = new GridBagConstraints();
		gbc_hashStateSizeBitsPanel.ipadx = 100;
		gbc_hashStateSizeBitsPanel.insets = new Insets(0, 0, 0, 5);
		gbc_hashStateSizeBitsPanel.fill = GridBagConstraints.BOTH;
		gbc_hashStateSizeBitsPanel.gridx = 1;
		gbc_hashStateSizeBitsPanel.gridy = 0;
		hashSettingsPanel.add(hashStateSizeBitsPanel, gbc_hashStateSizeBitsPanel);
		final GridBagLayout gbl_hashStateSizeBitsPanel = new GridBagLayout();
		gbl_hashStateSizeBitsPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_hashStateSizeBitsPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_hashStateSizeBitsPanel.columnWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		gbl_hashStateSizeBitsPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		hashStateSizeBitsPanel.setLayout(gbl_hashStateSizeBitsPanel);

		hashStateSizeBitsCB = new JComboBox<>();
		final GridBagConstraints gbc_hashStateSizeBitsCB = new GridBagConstraints();
		gbc_hashStateSizeBitsCB.insets = new Insets(0, 0, 0, 5);
		gbc_hashStateSizeBitsCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_hashStateSizeBitsCB.gridx = 0;
		gbc_hashStateSizeBitsCB.gridy = 0;
		hashStateSizeBitsPanel.add(hashStateSizeBitsCB, gbc_hashStateSizeBitsCB);

		final JPanel hashDigestSizeBitsPanel = new JPanel();
		hashDigestSizeBitsPanel.setBorder(new TitledBorder(null, "Hash digest size bits", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		final GridBagConstraints gbc_hashSizeBitsPanel = new GridBagConstraints();
		gbc_hashSizeBitsPanel.ipadx = 100;
		gbc_hashSizeBitsPanel.fill = GridBagConstraints.BOTH;
		gbc_hashSizeBitsPanel.gridx = 2;
		gbc_hashSizeBitsPanel.gridy = 0;
		hashSettingsPanel.add(hashDigestSizeBitsPanel, gbc_hashSizeBitsPanel);
		final GridBagLayout gbl_hashSizeBitsPanel = new GridBagLayout();
		gbl_hashSizeBitsPanel.columnWidths = new int[]
		{
				0, 0, 0
		};
		gbl_hashSizeBitsPanel.rowHeights = new int[]
		{
				0, 0
		};
		gbl_hashSizeBitsPanel.columnWeights = new double[]
		{
				1.0, 0.0, Double.MIN_VALUE
		};
		gbl_hashSizeBitsPanel.rowWeights = new double[]
		{
				0.0, Double.MIN_VALUE
		};
		hashDigestSizeBitsPanel.setLayout(gbl_hashSizeBitsPanel);

		hashDigestSizeBitsCB = new JComboBox<>();
		final GridBagConstraints gbc_hashSizeBitsCB = new GridBagConstraints();
		gbc_hashSizeBitsCB.insets = new Insets(0, 0, 0, 5);
		gbc_hashSizeBitsCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_hashSizeBitsCB.gridx = 0;
		gbc_hashSizeBitsCB.gridy = 0;
		hashDigestSizeBitsPanel.add(hashDigestSizeBitsCB, gbc_hashSizeBitsCB);
		// </editor-fold>

		hashAlgorithmCB.setModel(new DefaultComboBoxModel<>(HashAlgorithm.values()));
		hashAlgorithmCB.setSelectedItem(HashAlgorithm.SHA2);

		hashDigestSizeBitsCB.setModel(new DefaultComboBoxModel<>(HashAlgorithm.SHA2.getAvailableDigestSizesBoxed()));
		hashDigestSizeBitsCB.setSelectedItem(256);

		hashStateSizeBitsCB.setModel(new DefaultComboBoxModel<>(HashAlgorithm.Skein.getAvailableDigestSizesBoxed()));

		final JLabel hashStateSizeBitsLabel = new JLabel("bit");
		final GridBagConstraints gbc_hashStateSizeBitsLabel = new GridBagConstraints();
		gbc_hashStateSizeBitsLabel.gridx = 1;
		gbc_hashStateSizeBitsLabel.gridy = 0;
		hashStateSizeBitsPanel.add(hashStateSizeBitsLabel, gbc_hashStateSizeBitsLabel);
		hashDigestSizeBitsCB.setSelectedItem(256);

		final JLabel hashDigestSizeBitsLabel = new JLabel("bit");
		final GridBagConstraints gbc_hashDigestSizeBitsLabel = new GridBagConstraints();
		gbc_hashDigestSizeBitsLabel.gridx = 1;
		gbc_hashDigestSizeBitsLabel.gridy = 0;
		hashDigestSizeBitsPanel.add(hashDigestSizeBitsLabel, gbc_hashDigestSizeBitsLabel);

		hashOutputPanel = new EncodedIOPanel("Hash output", Encoding.HEXADECIMAL);
		final GridBagConstraints gbc_hashOutputPanel = new GridBagConstraints();
		gbc_hashOutputPanel.fill = GridBagConstraints.BOTH;
		gbc_hashOutputPanel.gridx = 0;
		gbc_hashOutputPanel.gridy = 3;
		add(hashOutputPanel, gbc_hashOutputPanel);

		hashAlgorithmCB.addActionListener(e ->
		{
			final HashAlgorithm selected = Optional.ofNullable((HashAlgorithm) hashAlgorithmCB.getSelectedItem()).orElse(HashAlgorithm.SHA2);

			final boolean isDigestSizesAvailable = Optional.ofNullable(selected.getAvailableDigestSizes()).map(available -> available.length > 0).orElse(false);
			final boolean isSkein = selected == HashAlgorithm.Skein;

			final int stateSizeBits = (int) Optional.ofNullable(hashStateSizeBitsCB.getSelectedItem()).orElse(256);

			if (isSkein)
			{
				final Integer[] sizeBits;
				switch (stateSizeBits)
				{
					case 512:
						sizeBits = new Integer[]
						{
								128, 160, 224, 256, 384, 512
						};
						break;
					case 1024:
						sizeBits = new Integer[]
						{
								384, 512, 1024
						};
						break;
					default: // 256
						sizeBits = new Integer[]
						{
								128, 160, 224, 256
						};
						break;
				}

				final int lastSelected = (int) Optional.ofNullable(hashDigestSizeBitsCB.getSelectedItem()).orElse(256);
				hashDigestSizeBitsCB.setModel(new DefaultComboBoxModel<>(sizeBits));
				hashDigestSizeBitsCB.updateUI();

				hashDigestSizeBitsCB.setSelectedItem(lastSelected);
			}
			else if (isDigestSizesAvailable)
			{
				final int lastSelected = (int) Optional.ofNullable(hashDigestSizeBitsCB.getSelectedItem()).orElse(256);
				hashDigestSizeBitsCB.setModel(new DefaultComboBoxModel<>(selected.getAvailableDigestSizesBoxed()));
				hashDigestSizeBitsCB.updateUI();

				hashDigestSizeBitsCB.setSelectedItem(lastSelected);
			}
			hashDigestSizeBitsPanel.setEnabled(isDigestSizesAvailable);
			hashDigestSizeBitsCB.setEnabled(isDigestSizesAvailable);

			hashStateSizeBitsPanel.setVisible(isSkein);
		});

		hashStateSizeBitsCB.addActionListener(e ->
		{
			final int stateSizeBits = (int) Optional.ofNullable(hashStateSizeBitsCB.getSelectedItem()).orElse(256);

			final Integer[] sizeBits;
			switch (stateSizeBits)
			{
				case 512:
					sizeBits = new Integer[]
					{
							128, 160, 224, 256, 384, 512
					};
					break;
				case 1024:
					sizeBits = new Integer[]
					{
							384, 512, 1024
					};
					break;
				default: // 256
					sizeBits = new Integer[]
					{
							128, 160, 224, 256
					};
					break;
			}

			final int lastSelected = (int) Optional.ofNullable(hashDigestSizeBitsCB.getSelectedItem()).orElse(256);
			hashDigestSizeBitsCB.setModel(new DefaultComboBoxModel<>(sizeBits));
			hashDigestSizeBitsCB.updateUI();

			hashDigestSizeBitsCB.setSelectedItem(lastSelected);
		});

		hashButton.addActionListener(e ->
		{
			hashButton.setEnabled(false);
			Main.setBusyCursor(this, true);

			MultiThreading.getDefaultWorkers().submit(() ->
			{
				try
				{
					if (doHash(getInputBytes()))
						Main.notificationMessageBox("Successfully hashed!", "Successfully hashed the message!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null);
				}
				finally
				{
					// Reset buttons
					EventQueue.invokeLater(() ->
					{
						hashButton.setEnabled(true);
						Main.setBusyCursor(this, false);
					});
				}
			});
		});
		// </editor-fold>
	}

	private byte[] getInputBytes()
	{

		try
		{
			return inputPanel.read();
		}
		catch (final Throwable e)
		{
			Main.exceptionMessageBox(e.getClass().getCanonicalName(), "Exception occurred while reading, parsing and decoding input", e);
			return null;
		}
	}

	private void doSave(final byte[] bytes)
	{

		try
		{
			hashOutputPanel.write(bytes);
		}
		catch (final Throwable e)
		{
			Main.exceptionMessageBox(e.getClass().getCanonicalName(), "Exception occurred while encoding and writing output", e);
		}
	}

	private boolean doHash(final byte[] messageBytes)
	{
		if (messageBytes == null)
			return false;

		final byte[] hash;
		final HashAlgorithm hashAlgorithm = Optional.ofNullable((HashAlgorithm) hashAlgorithmCB.getSelectedItem()).orElse(HashAlgorithm.SHA2);
		final int messageBytesSize = messageBytes.length;

		final int stateSizeBits = (int) Optional.ofNullable(hashStateSizeBitsCB.getSelectedItem()).orElse(256);
		final int digestSizeBits = (int) Optional.ofNullable(hashDigestSizeBitsCB.getSelectedItem()).orElse(256);

		final String algorithmString = getAlgorithmString(hashAlgorithm, stateSizeBits, digestSizeBits);

		try
		{
			if (hashAlgorithm.getProviderName() != null)
			{
				// Message digest algorithms supported by security provider
				final MessageDigest md = MessageDigest.getInstance(algorithmString, hashAlgorithm.getProviderName());
				md.update(messageBytes);
				hash = md.digest();
			}
			else
			{
				// Check-sum algorithms supported by natively
				final long checksum;
				switch (hashAlgorithm)
				{
					case CRC_16:
					{
						final CRC16 crc16 = new CRC16();
						for (final byte b : messageBytes)
							crc16.update(b);
						checksum = crc16.getValue();
						break;
					}
					case CRC_32:
					{
						final CRC32 crc32 = new CRC32();
						crc32.update(messageBytes);
						checksum = crc32.getValue();
						break;
					}
					case ADLER_32:
					{
						final Adler32 adler32 = new Adler32();
						adler32.update(messageBytes);
						checksum = adler32.getValue();
						break;
					}
					default:
						throw new NoSuchAlgorithmException("Algorithm is not implemented");
				}

				hash = Bytes.from(checksum).array();
			}

			doSave(hash);
		}
		catch (final NoSuchAlgorithmException | RuntimeException | NoSuchProviderException e)
		{
			final StringBuilder messageBuilder = new StringBuilder("Failed to hash the given message.").append(Main.lineSeparator).append(Main.lineSeparator);

			// Print the cause of the problem
			if (e instanceof RuntimeException)
				messageBuilder.append("It seems your input is not supported by the specified hash algorithm.").append(Main.lineSeparator);
			else if (e instanceof NoSuchProviderException)
				messageBuilder.append("It seems your Java version is not supporting the specified security provider.").append(Main.lineSeparator);
			else
				messageBuilder.append("It seems your Java version is not supporting the specified hash algorithm.").append(Main.lineSeparator);

			// Hash algorithm provider
			final Provider provider = Security.getProvider(hashAlgorithm.getProviderName());
			final String providerInfo = provider != null ? provider.getInfo() : "null";
			messageBuilder.append("Hash algorithm provider: ").append(hashAlgorithm.getProviderName()).append("(").append(providerInfo).append(")").append(Main.lineSeparator);

			// Hash algorithm
			messageBuilder.append("Hash algorithm: ").append(algorithmString).append("(").append(hashAlgorithm).append(")").append(Main.lineSeparator);

			// Message to be hashed
			messageBuilder.append("Message to be hashed: ").append(Main.filterStringForPopup(new String(messageBytes, StandardCharsets.UTF_8))).append(" (byteArrayLength: ").append(messageBytesSize).append(")").append(Main.lineSeparator);

			Main.exceptionMessageBox(e.getClass().getCanonicalName(), messageBuilder.toString(), e);
		}

		return true;
	}

	static String getAlgorithmString(final HashAlgorithm algorithm, final int stateSizeBits, final int digestSizeBits)
	{
		final StringBuilder algorithmBuilder = new StringBuilder(algorithm.getId());

		if (Optional.ofNullable(algorithm.getAvailableDigestSizes()).map(available -> available.length > 1).orElse(true))
			switch (algorithm)
			{
				case Skein:
					algorithmBuilder.append("-").append(stateSizeBits).append("-").append(digestSizeBits); /* example: SKEIN-256-256 */
					break;
				case RIPEMD:
				case SHAKE:
					algorithmBuilder.append(digestSizeBits); /* example: SHAKE256 */
					break;
				default:
					algorithmBuilder.append("-").append(digestSizeBits); /* example: SHA-256 */
			}

		return algorithmBuilder.toString();
	}
}
