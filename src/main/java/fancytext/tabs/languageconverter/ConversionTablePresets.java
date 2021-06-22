package fancytext.tabs.languageconverter;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import fancytext.Main;
import fancytext.utils.MultiThreading;

enum ConversionTablePresets
{
	LEET("1337 5p34k"),
	DIACRITICS("Ďiăcritičš"),
	FAKE_RUSSIAN("Ғakє ЯussiaЙ"),
	CURRENCY_SYMBOLS("₵urrenc¥ $yℳ₿ol$");

	private final String presetName;

	ConversionTablePresets(final String name)
	{
		presetName = name;
	}

	@Override
	public String toString()
	{
		return presetName;
	}

	public void apply(final Collection<? super Entry<String, List<String>>> conversionMap)
	{
		switch (this)
		{
			case LEET:
				apply1337(conversionMap);
				break;
			case DIACRITICS:
				applyDiacritics(conversionMap);
				break;
			case FAKE_RUSSIAN:
				applyFakeRussian(conversionMap);
				break;
			case CURRENCY_SYMBOLS:
				applyCurrencySymbols(conversionMap);
				break;
		}
	}

	private static void apply1337(final Collection<? super Entry<String, List<String>>> conversionMap)
	{
		final Collection<Runnable> work = new ArrayDeque<>(36);

		// <editor-fold desc="Alphabets">
		conversionMap.add(createEntry("A", createList("4", "/\\", "@", "/-\\", "^", "aye", "(L", "Д")));
		conversionMap.add(createEntry("B", createList("I3", "8", "13", "|3", "ß", "!3", "(3", "/3", ")3", "|-]", "j3", "6")));
		conversionMap.add(createEntry("D", createList(")", "|)", "(|", "[)", "I>", "|>", "T)", "I7", "cl", "|}", ">", "|]")));
		conversionMap.add(createEntry("E", createList("3", "&", "£", "€", "ë", "[-", "|=-")));
		conversionMap.add(createEntry("C", createList("[", "¢", "{", "<", "(", "©")));
		conversionMap.add(createEntry("F", createList("|=", "ƒ", "|#", "ph", "/=", "v")));
		conversionMap.add(createEntry("G", createList("&", "6", "(_,", "9", "C-", "gee", "(?,", "[,", "{,", "<-", "(.")));
		conversionMap.add(createEntry("H", createList("#", "/-/", "[-]", "]-[", ")-(", "(-)", ":-:", "|~|", "|-|", "]~[", "}{", "!-!", "1-1", "\\-/", "I,I", "/-\\")));
		conversionMap.add(createEntry("I", createList("1", "[]", "|", "!", "eye", "3y3", "][")));
		conversionMap.add(createEntry("J", createList(",_|", "_|", "._|", "._]", "_]", ",_]", "]", ";", "1")));
		conversionMap.add(createEntry("K", createList(">|", "|<", "/<", "1<", "|c", "|(", "|{")));
		conversionMap.add(createEntry("L", createList("1", "£", "7", "|_", "|")));
		conversionMap.add(createEntry("M", createList("/\\/\\", "/V\\", "JVI", "[V]", "[]V[]", "|\\/|", "^^", "<\\/>", "{V}", "(v)", "(V)", "|V|", "nn", "IVI", "|\\|\\", "]\\/[", "1^1", "ITI", "JTI")));
		conversionMap.add(createEntry("N", createList("^/", "|\\|", "/\\/", "[\\]", "<\\>", "{\\}", "|V", "/V", "И", "^", "ท")));
		conversionMap.add(createEntry("O", createList("0", "Q", "()", "oh", "[]", "p", "<>", "Ø")));
		conversionMap.add(createEntry("P", createList("|*", "|o", "|º", "?", "|^", "|>", "|\"", "9", "[]D", "|°", "|7")));
		conversionMap.add(createEntry("Q", createList("(_,)", "9", "()_", "2", "0_", "<|", "&")));
		conversionMap.add(createEntry("R", createList("I2", "|`", "|~", "|?", "/2", "|^", "lz", "|9", "2", "12", "®", "[z", "Я", ".-", "|2", "|-")));
		conversionMap.add(createEntry("S", createList("5", "$", "z", "§", "ehs", "es", "2")));
		conversionMap.add(createEntry("T", createList("7", "+", "-|-", "']['", "†", "\"|\"", "~|~")));
		conversionMap.add(createEntry("U", createList("(_)", "|_|", "v", "L|", "µ", "บ")));
		conversionMap.add(createEntry("V", createList("\\/", "|/", "\\|")));
		conversionMap.add(createEntry("W", createList("\\/\\/", "VV", "\\N", "'//", "\\\\'", "\\^/", "(n)", "\\V/", "\\X/", "\\|/", "\\_|_/", "\\_:_/", "Ш", "Щ", "uu", "2u", "\\\\//\\\\//", "พ", "v²")));
		conversionMap.add(createEntry("X", createList("><", "Ж", "}{", "ecks", "×", "?", ")(", "][")));
		conversionMap.add(createEntry("Y", createList("j", "`/", "Ч", "7", "\\|/", "¥", "\\//")));
		conversionMap.add(createEntry("Z", createList("2", "7_", "-/_", "%", ">_", "s", "~/_", "-\\_", "-|_")));
		// </editor-fold>

		// <editor-fold desc="Numbers">
		conversionMap.add(createEntry("1", createList("L", "I")));
		conversionMap.add(createEntry("2", createList("R", "Z")));
		conversionMap.add(createEntry("3", createList("E")));
		conversionMap.add(createEntry("4", createList("A")));
		conversionMap.add(createEntry("5", createList("S")));
		conversionMap.add(createEntry("6", createList("b", "G")));
		conversionMap.add(createEntry("7", createList("T", "L")));
		conversionMap.add(createEntry("8", createList("B")));
		conversionMap.add(createEntry("9", createList("g", "q")));
		conversionMap.add(createEntry("0", createList("o", "()", "[]", "Ø", "<>")));
		// </editor-fold>

		MultiThreading.submitRunnables(work);

		// https://www.gamehouse.com/blog/leet-speak-cheat-sheet/
	}

	private static void applyDiacritics(final Collection<? super Entry<String, List<String>>> conversionMap)
	{
		final Collection<Runnable> work = new ArrayDeque<>(86);

		// <editor-fold desc="Capital-case alphabets"> - 36
		conversionMap.add(createEntry("A", createList("Á", "Ă", "Ắ", "Ặ", "Ằ", "Ẳ", "Ẵ", "Ǎ", "Â", "Ấ", "Ậ", "Ầ", "Ẩ", "Ẫ", "Ä", "Ạ", "À", "Ả", "Ā", "Ą", "Å", "Ǻ", "Ã", "Ǟ", "Ǡ", "Ȁ", "Ȃ", "Ȧ", "Ⱥ", "Ʌ", "Ḁ")));
		conversionMap.add(createEntry("AE", createList("Æ", "Ǽ", "Ǣ")));
		conversionMap.add(createEntry("B", createList("Ḅ", "Ɓ", "ʚ", "ɞ", "Ƃ", "Ƅ", "Ƀ", "Ḃ", "Ḅ", "Ḇ")));
		conversionMap.add(createEntry("C", createList("Ć", "Č", "Ç", "Ĉ", "Ċ", "Ɔ", "ʗ", "Ƈ", "Ȼ", "Ḉ")));
		conversionMap.add(createEntry("D", createList("Ď", "Ḓ", "Ḍ", "Ɗ", "Ḏ", "Ḋ", "ḋ", "Ḑ", "ḑ", "Đ", "Ð", "Ɖ", "Ƌ", "ƿ")));
		conversionMap.add(createEntry("DZ", createList("Ǳ", "Ǆ")));
		conversionMap.add(createEntry("Dz", createList("ǲ", "ǅ")));
		conversionMap.add(createEntry("E", createList("É", "Ĕ", "Ě", "Ê", "Ế", "Ệ", "Ề", "Ể", "Ễ", "Ë", "Ė", "Ẹ", "È", "Ẻ", "Ē", "Ę", "Ẽ", "Ɛ", "Ə", "Ǝ", "Ʃ", "Ȅ", "Ȇ", "Ȩ", "Ɇ", "Ḕ", "Ḗ", "Ḙ", "Ḛ", "Ḝ")));
		conversionMap.add(createEntry("F", createList("Ƒ", "Ḟ")));
		conversionMap.add(createEntry("G", createList("Ǵ", "Ğ", "Ǧ", "Ģ", "Ĝ", "Ġ", "Ḡ", "ʛ", "Ɠ", "Ǥ")));
		conversionMap.add(createEntry("H", createList("Ḫ", "Ĥ", "Ḥ", "Ħ", "Ḣ", "Ḧ", "Ḩ")));
		conversionMap.add(createEntry("Hu", createList("Ƕ")));
		conversionMap.add(createEntry("Hb", createList("Њ")));
		conversionMap.add(createEntry("I", createList("Í", "Ĭ", "Ǐ", "Î", "Ï", "İ", "Ị", "Ì", "Ỉ", "Ī", "Į", "Ĩ", "Ɨ", "ǀ", "ǂ", "Ȉ", "Ȋ", "Ḭ", "Ḯ")));
		conversionMap.add(createEntry("IJ", createList("Ĳ")));
		conversionMap.add(createEntry("II", createList("ǁ")));
		conversionMap.add(createEntry("J", createList("Ĵ", "Ɉ")));
		conversionMap.add(createEntry("K", createList("Ķ", "Ḳ", "Ƙ", "Ḵ", "Ḱ")));
		conversionMap.add(createEntry("L", createList("Ĺ", "Ƚ", "Ľ", "Ļ", "Ḽ", "Ḷ", "Ḹ", "Ḻ", "Ŀ", "Ł")));
		conversionMap.add(createEntry("LJ", createList("Ǉ")));
		conversionMap.add(createEntry("Lj", createList("ǈ")));
		conversionMap.add(createEntry("M", createList("Ḿ", "Ṁ", "Ṃ", "Ɯ")));
		conversionMap.add(createEntry("N", createList("Ń", "Ň", "Ņ", "Ṋ", "Ṅ", "Ṇ", "Ǹ", "Ɲ", "Ŋ", "Ṉ", "Ñ", "Ƞ")));
		conversionMap.add(createEntry("NJ", createList("Ǌ")));
		conversionMap.add(createEntry("Nj", createList("ǋ")));
		conversionMap.add(createEntry("O", createList("Ó", "Ŏ", "Ǒ", "Ô", "Ố", "Ộ", "Ồ", "Ổ", "Ỗ", "Ö", "Ọ", "Ő", "Ò", "Ỏ", "Ơ", "Ớ", "Ợ", "Ờ", "Ở", "Ỡ", "Ō", "Ɵ", "Ǫ", "Ø", "Ǿ", "Õ" /* , "Œ", "ɶ" */, "Ǭ", "Ȍ", "Ȏ", "Ȫ", "Ȭ", "Ȯ", "Ȱ")));
		conversionMap.add(createEntry("OE", createList("Œ", "ɶ"))); // It's 'OE' don't be confused with 'CE'.
		conversionMap.add(createEntry("P", createList("Þ", "Ƥ", "Ƿ")));
		conversionMap.add(createEntry("R", createList("Ŕ", "Ř", "Ŗ", "Ṙ", "Ṛ", "Ṝ", "Ṟ", "ʁ", "Ʀ", "Ȑ", "Ȓ", "Ɍ")));
		conversionMap.add(createEntry("S", createList("Ś", "Š", "Ş", "Ŝ", "Ș", "Ṡ", "Ṣ", "Ƨ")));
		conversionMap.add(createEntry("SS", createList("ẞ")));
		conversionMap.add(createEntry("T", createList("Ť", "Ţ", "Ṱ", "Ț", "Ṭ", "Ṯ", "Ŧ", "Ƭ", "Ʈ", "Ⱦ")));
		conversionMap.add(createEntry("U", createList("Ú", "Ŭ", "Ǔ", "Û", "Ü", "Ǘ", "Ǚ", "Ǜ", "Ǖ", "Ụ", "Ű", "Ù", "Ủ", "Ư", "Ứ", "Ự", "Ừ", "Ử", "Ữ", "Ū", "Ų", "Ů", "Ũ", "Ʊ", "Ʋ", "Ȕ", "Ȗ", "Ʉ")));
		conversionMap.add(createEntry("W", createList("Ẃ", "Ŵ", "Ẅ", "Ẁ")));
		conversionMap.add(createEntry("Y", createList("Ý", "Ŷ", "Ÿ", "Ẏ", "Ỵ", "Ỳ", "Ƴ", "Ỷ", "Ȳ", "Ỹ", "Ɣ", "Ɏ")));
		conversionMap.add(createEntry("Z", createList("Ź", "Ž", "Ż", "Ẓ", "Ẕ", "Ƶ", "Ȥ")));
		// </editor-fold>

		// <editor-fold desc="Lower-case alphabets"> - 44
		conversionMap.add(createEntry("a", createList("á", "ă", "ắ", "ặ", "ằ", "ẳ", "ẵ", "ǎ", "ḁ", "â", "ấ", "ậ", "ầ", "ẩ", "ẫ", "ä", "ạ", "à", "ả", "ā", "ą", "å", "ǻ", "ã", /* "æ", "ǽ", */ "ɑ", "ɐ", "ɒ", "ǟ", "ǡ", "ȁ", "ȃ", "ȧ")));
		conversionMap.add(createEntry("ae", createList("æ", "ǽ", "ǣ")));
		conversionMap.add(createEntry("b", createList("ḅ", "ɓ", "ß", "ƀ", "ƃ", "ƅ", "ḃ", "ḅ", "ḇ")));
		conversionMap.add(createEntry("c", createList("ć", "č", "ç", "ĉ", "ɕ", "ċ", "ȼ", "ḉ")));
		conversionMap.add(createEntry("ce", createList("œ")));
		conversionMap.add(createEntry("d", createList("ď", "ḓ", "ḍ", "ɗ", "ḏ", "đ", "ḋ", "ɖ", "ḑ", "ð", "ƌ", "ȡ")));
		conversionMap.add(createEntry("db", createList("ȸ")));
		conversionMap.add(createEntry("dz", createList("ʤ", "ǳ", "ʣ", "ʥ", "ǆ")));
		conversionMap.add(createEntry("e", createList("é", "ĕ", "ě", "ê", "ế", "ệ", "ề", "ể", "ḕ", "ḗ", "ḝ", "ḛ", "ḙ", "ễ", "ë", "ė", "ẹ", "è", "ẻ", "ē", "ę", "ẽ", "ʒ", "ǯ", "ʓ", "ɘ", "ɜ", "ɝ", "ə", "ɚ", "ʚ", "ɞ", "ǝ", "ȅ", "ȇ", "ȩ", "ɇ")));
		conversionMap.add(createEntry("f", createList("ƒ", "ſ", "ʃ", "ʆ", "ɟ", "ʄ", "ƭ", "ḟ")));
		conversionMap.add(createEntry("fn", createList("ʩ")));
		conversionMap.add(createEntry("fi", createList("ﬁ")));
		conversionMap.add(createEntry("fl", createList("ﬂ")));
		conversionMap.add(createEntry("g", createList("ǵ", "ğ", "ǧ", "ģ", "ĝ", "ġ", "ɠ", "ḡ", "ɡ", "ǥ")));
		conversionMap.add(createEntry("h", createList("ḫ", "ĥ", "ḥ", "ɦ", "ẖ", "ħ", "ɧ", "ɥ", "ʮ", "ʯ", "ḣ", "ḧ", "ḩ")));
		conversionMap.add(createEntry("hv", createList("ƕ")));
		conversionMap.add(createEntry("i", createList("í", "ĭ", "ǐ", "î", "ï", "ị", "ì", "ỉ", "ī", "į", "ɨ", "ĩ", "ɩ", "ı"/* , "ĳ", "ɟ" */, "ȉ", "ȋ", "ḭ", "ḯ")));
		conversionMap.add(createEntry("ij", createList("ĳ")));
		conversionMap.add(createEntry("j", createList("ǰ", "ĵ", "ʝ", "ȷ", "ɉ")));
		conversionMap.add(createEntry("k", createList("ķ", "ḳ", "ƙ", "ḵ", "ĸ", "ʞ", "ḱ")));
		conversionMap.add(createEntry("l", createList("ĺ", "ƚ", "ɬ", "ľ", "ļ", "ḽ", "ḷ", "ḹ", "ḻ", "ŀ", "ɫ", "ɭ", "ł", "ȴ")));
		conversionMap.add(createEntry("lz", createList("ɮ", "ʫ")));
		conversionMap.add(createEntry("lj", createList("ǉ")));
		conversionMap.add(createEntry("ls", createList("ʪ")));
		conversionMap.add(createEntry("m", createList("ḿ", "ṁ", "ṃ", "ɱ", "ɯ", "ɰ")));
		conversionMap.add(createEntry("n", createList("ŉ", "ń", "ň", "ņ", "ṋ", "ṅ", "ṇ", "ǹ", "ɲ", "ṉ", "ɳ", "ñ", "ŋ", "ƞ", "ȵ")));
		conversionMap.add(createEntry("nj", createList("ǌ")));
		conversionMap.add(createEntry("o", createList("ó", "ŏ", "ǒ", "ô", "ố", "ộ", "ồ", "ổ", "ỗ", "ö", "ọ", "ő", "ò", "ỏ", "ơ", "ớ", "ợ", "ờ", "ở", "ỡ", "ō", "ǫ", "ø", "ǿ", "õ", "ɛ", "ɔ", "ɵ", "ʘ"/* , "œ" */, "ǭ", "ȍ", "ȏ", "ȫ", "ȭ", "ȯ", "ȱ")));
		conversionMap.add(createEntry("oe", createList("œ")));
		conversionMap.add(createEntry("p", createList("ɸ", "þ", "ƥ")));
		conversionMap.add(createEntry("q", createList("ʠ", "Ƣ", "ƣ", "Ɋ", "ɋ")));
		conversionMap.add(createEntry("qp", createList("ȹ")));
		conversionMap.add(createEntry("r", createList("ŕ", "ř", "ŗ", "ṙ", "ṛ", "ṝ", "ɾ", "ṟ", "ɼ", "ɽ", "ɿ", "ɹ", "ɻ", "ɺ", "ȑ", "ȓ", "ɍ")));
		conversionMap.add(createEntry("s", createList("ś", "š", "ş", "ŝ", "ș", "ṡ", "ṣ", "ʂ", "ƨ", "ȿ")));
		conversionMap.add(createEntry("ss", createList("ß")));
		conversionMap.add(createEntry("t", createList("ť", "ţ", "ṱ", "ț", "ẗ", "ṭ", "ṯ", "ʈ", "ŧ", "ƫ", "ʇ", "ȶ")));
		conversionMap.add(createEntry("tc", createList("ʨ")));
		conversionMap.add(createEntry("ts", createList("ʦ")));
		conversionMap.add(createEntry("tf", createList("ʧ")));
		conversionMap.add(createEntry("u", createList("ʉ", "ú", "ŭ", "ǔ", "û", "ü", "ǘ", "ǚ", "ǜ", "ǖ", "ụ", "ű", "ù", "ủ", "ư", "ứ", "ự", "ừ", "ử", "ữ", "ū", "ų", "ů", "ũ", "ʊ", "ȕ", "ȗ")));
		conversionMap.add(createEntry("v", createList("ʋ", "ʌ")));
		conversionMap.add(createEntry("w", createList("ẃ", "ŵ", "ẅ", "ẁ", "ʍ")));
		conversionMap.add(createEntry("y", createList("ý", "ŷ", "ÿ", "ẏ", "ỵ", "ỳ", "ƴ", "ỷ", "ȳ", "ỹ", "ʎ", "ƛ", "ɣ", "ƛ", "ɏ")));
		conversionMap.add(createEntry("z", createList("ź", "ž", "ʑ", "ż", "ẓ", "ẕ", "ʐ", "ƶ", "ȥ", "ɀ")));
		// </editor-fold>

		// <editor-fold desc="Numbers"> - 6
		conversionMap.add(createEntry("0", createList("Ø", "ø", "⌀", "∅")));
		conversionMap.add(createEntry("1", createList("ı")));
		conversionMap.add(createEntry("2", createList("ƻ")));
		conversionMap.add(createEntry("3", createList("ʒ", "Ȝ", "Ʒ", "Ƹ", "ƹ", "ƺ", "Ǯ", "ǯ")));
		conversionMap.add(createEntry("5", createList("Ƽ", "ƽ", "ƾ")));
		conversionMap.add(createEntry("8", createList("Ȣ", "ȣ")));
		// </editor-fold>

		MultiThreading.submitRunnables(work);

		// http://pinyin.info/unicode/diacritics.html //
		// https://unicode-table.com/en/#latin-extended-b //
		// https://unicode-table.com/en/#latin-extended-additional //
		// https://unicode-table.com/en/#greek-extended //
	}

	private static void applyFakeRussian(final Collection<? super Entry<String, List<String>>> conversionMap)
	{
		final Collection<Runnable> work = new ArrayDeque<>(51);

		// <editor-fold desc="Capital-case alphabets"> - 30
		conversionMap.add(createEntry("A", createList("Д", "д", "Ѧ", "ѧ", "Ӑ", "Ӓ")));
		conversionMap.add(createEntry("AE", createList("Ӕ")));
		conversionMap.add(createEntry("B", createList("Б")));
		conversionMap.add(createEntry("C", createList("Ҫ", "ҫ")));
		conversionMap.add(createEntry("Co", createList("Ҩ")));
		conversionMap.add(createEntry("E", createList("Ԑ", "Є", "Э", "Ё", "Ӗ")));
		conversionMap.add(createEntry("G", createList("Ԍ", "ԍ")));
		conversionMap.add(createEntry("F", createList("Ӻ", "ӻ", "Ғ", "ғ")));
		conversionMap.add(createEntry("H", createList("Њ", "Ҥ", "ҥ", "Ӊ", "ӊ", "Ң", "ң", "Ӈ", "ӈ", "Ȟ", "Ԩ", "ԩ", "н")));
		conversionMap.add(createEntry("Hb", createList("Њ", "њ")));
		conversionMap.add(createEntry("Hu", createList("Ԋ", "ԋ")));
		conversionMap.add(createEntry("Hj", createList("Ԣ", "ԣ")));
		conversionMap.add(createEntry("I", createList("Ї")));
		conversionMap.add(createEntry("J", createList("Ј")));
		conversionMap.add(createEntry("JX", createList("Ԕ", "ԕ")));
		conversionMap.add(createEntry("K", createList("Қ", "қ", "Ҡ", "ҡ", "Ҝ", "ҝ", "Ԟ", "ԟ", "Ҟ", "Ќ", "к", "ќ", "Ӄ", "ӄ")));
		conversionMap.add(createEntry("M", createList("Ԡ", "ԡ", "м", "Ҧ", "ҧ", "Ӎ", "ӎ")));
		conversionMap.add(createEntry("N", createList("И", "Ѝ", "Й", "и", "й", "ѝ", "Ҋ", "ҋ", "Ӣ", "ӣ", "Ӥ", "ӥ")));
		conversionMap.add(createEntry("O", createList("Ф", "Ѳ", "Ѻ", "Ӧ", "Ө", "Ӫ")));
		conversionMap.add(createEntry("P", createList("Ҏ")));
		conversionMap.add(createEntry("R", createList("Я", "я")));
		conversionMap.add(createEntry("RE", createList("Ԙ")));
		conversionMap.add(createEntry("Re", createList("ԙ")));
		conversionMap.add(createEntry("T", createList("Г", "Ґ", "ґ", "Ҭ", "ҭ", "Ѓ", "т", "ѓ", "Ӷ", "ӷ")));
		conversionMap.add(createEntry("U", createList("Ц", "Џ", "џ", "Ҵ")));
		conversionMap.add(createEntry("V", createList("Ѵ", "Ѷ")));
		conversionMap.add(createEntry("W", createList("Ш", "Щ", "Ѱ")));
		conversionMap.add(createEntry("X", createList("Ӿ", "Ҳ", "Ӽ", "Ж", "Ӂ", "Ӝ", "ж", "Җ", "җ")));
		conversionMap.add(createEntry("Y", createList("Ч", "Ҷ", "ҷ", "Ӌ", "ӌ", "Ҹ", "ҹ", "Ұ", "ұ", "Ў")));
		conversionMap.add(createEntry("Oy", createList("Ѹ", "ѹ")));

		// Lower-case characters - 21
		conversionMap.add(createEntry("a", createList("ӑ", "ӓ")));
		conversionMap.add(createEntry("ae", createList("ӕ")));
		conversionMap.add(createEntry("b", createList("Ъ", "Ь", "ь", "Ѣ", "ѣ", "Ҍ", "ҍ")));
		conversionMap.add(createEntry("bl", createList("Ы", "ы", "Ӹ", "ӹ")));
		conversionMap.add(createEntry("co", createList("ҩ")));
		conversionMap.add(createEntry("d", createList("Ԃ", "ԃ", "Ԁ")));
		conversionMap.add(createEntry("e", createList("ԑ", "є", "э", "ё", "Ҽ", "ҽ", "ѐ", "Ҿ", "ҿ", "ё", "ӗ", "Ә", "ә", "Ӛ", "ӛ")));
		conversionMap.add(createEntry("h", createList("Һ", "һ", "ȟ", "Ԧ", "ԧ", "Ђ", "Ћ", "ћ", "ђ", "Ҕ", "ҕ")));
		conversionMap.add(createEntry("i", createList("ї")));
		conversionMap.add(createEntry("k", createList("ҟ")));
		conversionMap.add(createEntry("n", createList("л", "П", "п", "ԉ", "Л", "Ԉ", "Ԓ", "ԓ", "Ԥ", "ԥ", "Ԯ", "ԯ")));
		conversionMap.add(createEntry("nb", createList("љ")));
		conversionMap.add(createEntry("o", createList("ѳ", "ѻ", "ӧ", "ө", "ӫ")));
		conversionMap.add(createEntry("p", createList("ҏ")));
		conversionMap.add(createEntry("u", createList("ц", "ҵ")));
		conversionMap.add(createEntry("v", createList("ѵ", "ѷ")));
		conversionMap.add(createEntry("w", createList("ш", "щ", "ѡ", "ѱ")));
		conversionMap.add(createEntry("x", createList("ҳ", "ӽ", "ӿ", "ж", "ӂ", "ӝ")));
		conversionMap.add(createEntry("y", createList("У", "У̃", "Ӯ", "Ӱ", "Ӳ", "у", "у̃", "ӯ", "ӱ", "ӳ", "ч", "ў", "Ӵ", "ӵ")));
		conversionMap.add(createEntry("3", createList("ԑ", "є", "э", "З", "Ҙ", "ҙ", "Ӟ", "ӟ", "Ӡ", "ӡ", "Ӭ", "ӭ")));
		conversionMap.add(createEntry("6", createList("Б", "б")));
		// </editor-fold>

		MultiThreading.submitRunnables(work);

		// https://jkirchartz.com/demos/fake_russian_generator.html
	}

	private static void applyCurrencySymbols(final Collection<? super Entry<String, List<String>>> conversionMap)
	{
		final Collection<Runnable> work = new ArrayDeque<>(23);

		// <editor-fold desc="Capital-case alphabets"> - 16
		conversionMap.add(createEntry("A", createList("₳")));
		conversionMap.add(createEntry("B", createList("฿", "₿")));
		conversionMap.add(createEntry("C", createList("₵", "₡")));
		conversionMap.add(createEntry("CE", createList("₠")));
		conversionMap.add(createEntry("E", createList("€", "£", "₤")));
		conversionMap.add(createEntry("F", createList("₣")));
		conversionMap.add(createEntry("G", createList("₲")));
		conversionMap.add(createEntry("Rs", createList("₨")));
		conversionMap.add(createEntry("S", createList("$")));
		conversionMap.add(createEntry("K", createList("₭")));
		conversionMap.add(createEntry("M", createList("₼", "ℳ")));
		conversionMap.add(createEntry("N", createList("₦")));
		conversionMap.add(createEntry("P", createList("₱", "₽", "Ᵽ")));
		conversionMap.add(createEntry("T", createList("₮")));
		conversionMap.add(createEntry("W", createList("￦", "₩")));
		conversionMap.add(createEntry("Y", createList("¥")));
		// </editor-fold>

		// <editor-fold desc="Lower-case alphabets"> - 7
		conversionMap.add(createEntry("c", createList("¢")));
		conversionMap.add(createEntry("com", createList("сом")));
		conversionMap.add(createEntry("d", createList("₫")));
		conversionMap.add(createEntry("f", createList("ƒ")));
		conversionMap.add(createEntry("m", createList("₥")));
		conversionMap.add(createEntry("o", createList("¤")));
		conversionMap.add(createEntry("tt", createList("₶")));
		// </editor-fold>

		MultiThreading.submitRunnables(work);

		// https://en.wikipedia.org/wiki/Currency_symbol //
	}

	private static <K, V> Entry<K, V> createEntry(final K key, final V value)
	{
		return new SimpleImmutableEntry<>(key, value);
	}

	@SafeVarargs
	private static <T> List<T> createList(final T... args)
	{
		return new ArrayList<>(Arrays.asList(args));
	}
}
