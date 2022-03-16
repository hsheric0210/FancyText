package fancytext.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SimpleDocumentListener implements DocumentListener
{
	private final Runnable onChanged;

	public SimpleDocumentListener(Runnable onChanged)
	{
		this.onChanged = onChanged;
	}

	@Override
	public void insertUpdate(final DocumentEvent e)
	{
		onChanged.run();
	}

	@Override
	public void removeUpdate(final DocumentEvent e)
	{
		onChanged.run();
	}

	@Override
	public void changedUpdate(final DocumentEvent e)
	{
		onChanged.run();
	}
}
