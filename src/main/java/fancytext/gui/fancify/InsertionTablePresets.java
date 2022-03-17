package fancytext.gui.fancify;

import java.util.Collection;

enum InsertionTablePresets
{
	COMBINING_DIACRITICAL_MARKS("Combining Diacritical Marks"),
	// COMBINING_DIACRITICAL_MARKS_EXTENDED("Combining Diacritical Marks Extended"), // UNSUPPORTED
	COMBINING_DIACRITICAL_MARKS_SUPPLEMENT("Combining Diacritical Marks Supplement"),
	// COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS("Combining Diacritical Marks for Symbols"),
	// COMBINING_HALF_MARKS("Combining Half Marks"),
	ALL("All");

	private final String presetName;

	InsertionTablePresets(final String name)
	{
		presetName = name;
	}

	@Override
	public String toString()
	{
		return presetName;
	}

	public void apply(final Collection<? super String> insertionList)
	{
		switch (this)
		{
			case COMBINING_DIACRITICAL_MARKS:
				applyDiacriticalMarks(insertionList);
				break;
			// case COMBINING_DIACRITICAL_MARKS_EXTENDED:
			// 	applyDiacriticalMarksExtended(insertionList);
			// 	break;
			case COMBINING_DIACRITICAL_MARKS_SUPPLEMENT:
				applyDiacriticalMarksSupplement(insertionList);
				break;
			// case COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS:
			// 	applyDiacriticalMarks4Symbols(insertionList);
			// 	break;
			// case COMBINING_HALF_MARKS:
			// 	applyHalfMarks(insertionList);
			// 	break;
			case ALL:
				applyAll(insertionList);
				break;
		}
	}

	private static void applyDiacriticalMarks(final Collection<? super String> insertionList)
	{
		for (char c = '\u0300'; c < '\u0370'; c++)
			insertionList.add(String.valueOf(c));
	}

	// private static void applyDiacriticalMarksExtended(final Collection<? super String> insertionList)
	// {
	// 	for (char c = '\u1AB0'; c < '\u1ABF'; c++)
	// 		insertionList.add(String.valueOf(c));
	// }

	private static void applyDiacriticalMarksSupplement(final Collection<? super String> insertionList)
	{
		for (char c = '\u1DC0'; c < '\u1DCB'; c++)
			insertionList.add(String.valueOf(c));
		for (char c = '\u1DFE'; c < '\u1E00'; c++)
			insertionList.add(String.valueOf(c));
	}

	// private static void applyDiacriticalMarks4Symbols(final Collection<? super String> insertionList)
	// {
	// 	for (char c = '\u20D0'; c <= '\u20F0'; c++)
	// 		insertionList.add(String.valueOf(c));
	// }
	//
	// private static void applyHalfMarks(final Collection<? super String> insertionList)
	// {
	// 	for (char c = '\uFE20'; c < '\uFE30'; c++)
	// 		insertionList.add(String.valueOf(c));
	// }

	private static void applyAll(final Collection<? super String> insertionList)
	{
		applyDiacriticalMarks(insertionList);
		// applyDiacriticalMarksExtended(insertionList);
		applyDiacriticalMarksSupplement(insertionList);
		// applyDiacriticalMarks4Symbols(insertionList);
		// applyHalfMarks(insertionList);
	}
}
