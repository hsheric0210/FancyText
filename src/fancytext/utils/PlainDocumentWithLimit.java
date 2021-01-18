package fancytext.utils;

import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class PlainDocumentWithLimit extends PlainDocument
{
	private static final long serialVersionUID = 4694888591943366080L;
	private int limit;

	public final void setLimit(final int newLimit)
	{
		limit = newLimit;
	}

//	public final int getLimit()
//	{
//		return limit;
//	}

	@Override
	public final void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException
	{
		if (str == null)
			return;

		if (limit < 1 || getLength() + str.length() <= limit)
			super.insertString(offs, str, a);
		else if (getLength() + str.length() > limit)
			super.insertString(offs, str.substring(0, limit - getLength()), a);
	}

	private void readObject(final ObjectInputStream in) throws ClassNotFoundException, NotSerializableException
	{
		throw new NotSerializableException("fancytext.utils.PlainDocumentWithLimit");
	}

	private void writeObject(final ObjectOutputStream out) throws NotSerializableException
	{
		throw new NotSerializableException("fancytext.utils.PlainDocumentWithLimit");
	}
}
