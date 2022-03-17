package fancytext.gui.fancify;

import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import fancytext.utils.MultiThreading;

enum ConversionTablePresets
{
	LEET_SIMPLIFIED("S1mplif3ed L33TSp3@k"),
	LEET("13375p34k"),
	DIACRITICS_SIMPLIFIED("Ŝïṁpľïﬁȅḍ ḋìǎčŕįṱǐćṣ"),
	DIACRITICS("Ðỉãçŕĩţíĉș"),
	FAKE_RUSSIAN_SIMPLIFIED("Simҏlified Fӓkє Rцssїan"),
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
			case LEET_SIMPLIFIED:
				applyLeetspeakSimplified(conversionMap);
				break;
			case LEET:
				applyLeetspeak(conversionMap);
				break;
			case DIACRITICS_SIMPLIFIED:
				applyDiacriticsSimplified(conversionMap);
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

	private static Map<String, List<String>> leetspeak_simplified;
	private static Map<String, List<String>> leetspeak;
	private static Map<String, List<String>> diacritics_simplified;
	private static Map<String, List<String>> diacritics;

	private static void initializeLeetspeak()
	{
		// https://www.gamehouse.com/blog/leet-speak-cheat-sheet/

		if (leetspeak_simplified == null)
		{
			leetspeak_simplified = new HashMap<>(37);

			// <editor-fold desc="Alphabets">
			leetspeak_simplified.put("A", createList("4", "^", "Д"));
			leetspeak_simplified.put("B", createList("8", "ß"));
			leetspeak_simplified.put("D", createList(")", "|>", ">", "|]"));
			leetspeak_simplified.put("E", createList("3", "&", "£", "€", "ë"));
			leetspeak_simplified.put("C", createList("[", "{", "<", "("));
			leetspeak_simplified.put("G", createList("&", "6"));
			leetspeak_simplified.put("H", createList("#", "/-/", "[-]", "]-[", ")-(", "(-)"));
			leetspeak_simplified.put("I", createList("1", "|", "!"));
			leetspeak_simplified.put("J", createList("]", "1"));
			leetspeak_simplified.put("K", createList("|<", "/<"));
			leetspeak_simplified.put("L", createList("£"));
			leetspeak_simplified.put("M", createList("^^", "|V|", "nn", "IVI"));
			leetspeak_simplified.put("N", createList("|\\|", "/\\/", "И"));
			leetspeak_simplified.put("O", createList("0", "Q", "p", "Ø"));
			leetspeak_simplified.put("P", createList("|*", "|º", "9", "|°"));
			leetspeak_simplified.put("Q", createList("9", "2", "<|", "&"));
			leetspeak_simplified.put("R", createList("I2", "Я", "|2"));
			leetspeak_simplified.put("S", createList("5", "$", "z", "§"));
			leetspeak_simplified.put("T", createList("7", "+", "†"));
			leetspeak_simplified.put("V", createList("\\/"));
			leetspeak_simplified.put("W", createList("\\/\\/", "Ш", "Щ", "พ", "v²"));
			leetspeak_simplified.put("X", createList("Ж"));
			leetspeak_simplified.put("Y", createList("j", "¥"));
			leetspeak_simplified.put("Z", createList("2", "%", ">_", "s"));

			leetspeak_simplified.put("a", createList("@", "(L"));
			leetspeak_simplified.put("b", createList("6"));
			leetspeak_simplified.put("c", createList("¢", "©"));
			leetspeak_simplified.put("f", createList("ƒ"));
			leetspeak_simplified.put("j", createList(";"));
			leetspeak_simplified.put("l", createList("1", "7", "|"));
			leetspeak_simplified.put("n", createList("ท"));
			leetspeak_simplified.put("o", createList("p"));
			leetspeak_simplified.put("r", createList("®"));
			leetspeak_simplified.put("u", createList("µ", "บ"));
			leetspeak_simplified.put("x", createList("×"));
			leetspeak_simplified.put("y", createList("Ч"));
			// </editor-fold>

			// <editor-fold desc="Numbers">
			leetspeak_simplified.put("0", createList("Ø"));
			// </editor-fold>
		}

		if (leetspeak == null)
		{
			final Map<String, List<String>> _leetspeak = new HashMap<>(36);

			_leetspeak.put("A", createList("/\\", "/-\\", "aye"));
			_leetspeak.put("B", createList("I3", "13", "|3", "!3", "(3", "/3", ")3", "|-]", "j3"));
			_leetspeak.put("D", createList("|)", "(|", "[)", "I>", "T)", "I7", "cl", "|}"));
			_leetspeak.put("E", createList("[-", "|=-"));
			_leetspeak.put("F", createList("|=", "|#", "ph", "/="));
			_leetspeak.put("G", createList("(_,", "9", "C-", "gee", "(?,", "[,", "{,", "<-", "(."));
			_leetspeak.put("H", createList(":-:", "|~|", "|-|", "]~[", "}{", "!-!", "1-1", "\\-/", "I,I", "/-\\"));
			_leetspeak.put("I", createList("[]", "eye", "3y3", "]["));
			_leetspeak.put("J", createList(",_|", "_|", "._|", "._]", "_]", ",_]"));
			_leetspeak.put("K", createList(">|", "1<", "|c", "|(", "|{"));
			_leetspeak.put("L", createList("|_"));
			_leetspeak.put("M", createList("/\\/\\", "/V\\", "JVI", "[V]", "[]V[]", "|\\/|", "<\\/>", "{V}", "(v)", "(V)", "|\\|\\", "]\\/[", "1^1", "ITI", "JTI"));
			_leetspeak.put("N", createList("^/", "[\\]", "<\\>", "{\\}", "|V", "/V"));
			_leetspeak.put("O", createList("()", "oh", "[]", "<>"));
			_leetspeak.put("P", createList("|o", "|^", "|>", "|\"", "[]D", "|7"));
			_leetspeak.put("Q", createList("(_,)", "()_", "0_"));
			_leetspeak.put("R", createList("|`", "|~", "/2", "|^", "lz", "|9", "12", "[z", ".-", "|-"));
			_leetspeak.put("S", createList("ehs", "es", "2"));
			_leetspeak.put("T", createList("-|-", "']['", "\"|\"", "~|~"));
			_leetspeak.put("U", createList("(_)", "|_|", "L|"));
			_leetspeak.put("V", createList("|/", "\\|"));
			_leetspeak.put("W", createList("VV", "\\N", "'//", "\\\\'", "\\^/", "(n)", "\\V/", "\\X/", "\\|/", "\\_|_/", "\\_:_/", "uu", "2u", "\\\\//\\\\//"));
			_leetspeak.put("X", createList("><", "}{", "ecks", ")(", "]["));
			_leetspeak.put("Y", createList("`/", "7", "\\|/", "\\//"));
			_leetspeak.put("Z", createList("7_", "-/_", "~/_", "-\\_", "-|_"));

			_leetspeak.put("u", createList("v"));

			_leetspeak.put("1", createList("L", "I"));
			_leetspeak.put("2", createList("R", "Z"));
			_leetspeak.put("3", createList("E"));
			_leetspeak.put("4", createList("A"));
			_leetspeak.put("5", createList("S"));
			_leetspeak.put("6", createList("b", "G"));
			_leetspeak.put("7", createList("T", "L"));
			_leetspeak.put("8", createList("B"));
			_leetspeak.put("9", createList("g", "q"));
			_leetspeak.put("0", createList("o", "()", "[]", "<>"));

			leetspeak = mergeMap(leetspeak_simplified, _leetspeak);
		}
	}

	private static void initializeDiacritics()
	{
		// http://pinyin.info/unicode/diacritics.html //
		// https://unicode-table.com/en/#latin-extended-b //
		// https://unicode-table.com/en/#latin-extended-additional //
		// https://unicode-table.com/en/#greek-extended //

		if (diacritics_simplified == null)
		{
			diacritics_simplified = new HashMap<>(77);

			// <editor-fold desc="Capital-case alphabets"> - 36
			diacritics_simplified.put("A", createList("Á", "Ă", "Ắ", "Ặ", "Ằ", "Ẳ", "Ẵ", "Ǎ", "Â", "Ấ", "Ậ", "Ầ", "Ẩ", "Ẫ", "Ä", "Ạ", "À", "Ả", "Ā", "Ą", "Å", "Ǻ", "Ã", "Ǟ", "Ǡ", "Ȁ", "Ȃ", "Ȧ", "Ⱥ", "Ḁ"));
			diacritics_simplified.put("AE", createList("Æ", "Ǽ", "Ǣ"));
			diacritics_simplified.put("B", createList("Ḅ", "Ɓ", "Ƀ", "Ḃ", "Ḅ", "Ḇ"));
			diacritics_simplified.put("C", createList("Ć", "Č", "Ç", "Ĉ", "Ċ", "Ȼ", "Ḉ"));
			diacritics_simplified.put("D", createList("Ď", "Ḓ", "Ḍ", "Ḏ", "Ḋ", "ḋ", "Ḑ", "ḑ", "Đ", "Ð", "Ɖ"));
			diacritics_simplified.put("DZ", createList("Ǳ", "Ǆ"));
			diacritics_simplified.put("Dz", createList("ǲ", "ǅ"));
			diacritics_simplified.put("E", createList("É", "Ĕ", "Ě", "Ê", "Ế", "Ệ", "Ề", "Ể", "Ễ", "Ë", "Ė", "Ẹ", "È", "Ẻ", "Ē", "Ę", "Ẽ", "Ȅ", "Ȇ", "Ȩ", "Ɇ", "Ḕ", "Ḗ", "Ḙ", "Ḛ", "Ḝ"));
			diacritics_simplified.put("F", createList("Ƒ", "Ḟ"));
			diacritics_simplified.put("G", createList("Ǵ", "Ğ", "Ǧ", "Ģ", "Ĝ", "Ġ", "Ḡ", "ʛ", "Ɠ", "Ǥ"));
			diacritics_simplified.put("H", createList("Ḫ", "Ĥ", "Ḥ", "Ħ", "Ḣ", "Ḧ", "Ḩ"));
			diacritics_simplified.put("Hu", createList("Ƕ"));
			diacritics_simplified.put("Hb", createList("Њ"));
			diacritics_simplified.put("I", createList("Í", "Ĭ", "Ǐ", "Î", "Ï", "İ", "Ị", "Ì", "Ỉ", "Ī", "Į", "Ĩ", "Ɨ", "Ȉ", "Ȋ"));
			diacritics_simplified.put("IJ", createList("Ĳ"));
			diacritics_simplified.put("II", createList("ǁ"));
			diacritics_simplified.put("J", createList("Ĵ", "Ɉ"));
			diacritics_simplified.put("K", createList("Ķ", "Ḳ", "Ḵ", "Ḱ", "ĸ"));
			diacritics_simplified.put("L", createList("Ĺ", "Ƚ", "Ľ", "Ļ", "Ḽ", "Ḷ", "Ḹ", "Ḻ", "Ŀ", "Ł"));
			diacritics_simplified.put("LJ", createList("Ǉ"));
			diacritics_simplified.put("Lj", createList("ǈ"));
			diacritics_simplified.put("M", createList("Ḿ", "Ṁ", "Ṃ"));
			diacritics_simplified.put("N", createList("Ń", "Ň", "Ņ", "Ṋ", "Ṅ", "Ṇ", "Ǹ", "Ɲ", "Ŋ", "Ṉ", "Ñ"));
			diacritics_simplified.put("NJ", createList("Ǌ"));
			diacritics_simplified.put("Nj", createList("ǋ"));
			diacritics_simplified.put("O", createList("Ó", "Ŏ", "Ǒ", "Ô", "Ố", "Ộ", "Ồ", "Ổ", "Ỗ", "Ö", "Ọ", "Ő", "Ò", "Ỏ", "Ơ", "Ớ", "Ợ", "Ờ", "Ở", "Ỡ", "Ō", "Ɵ", "Ǫ", "Ø", "Ǿ", "Õ" /* , "Œ", "ɶ" */, "Ǭ", "Ȍ", "Ȏ", "Ȫ", "Ȭ", "Ȯ", "Ȱ"));
			diacritics_simplified.put("OE", createList("Œ", "ɶ")); // It's 'OE' don't be confused with 'CE'.
			diacritics_simplified.put("P", createList("Ƥ"));
			diacritics_simplified.put("R", createList("Ŕ", "Ř", "Ŗ", "Ṙ", "Ṛ", "Ṝ", "Ṟ", "Ȑ", "Ȓ", "Ɍ"));
			diacritics_simplified.put("S", createList("Ś", "Š", "Ş", "Ŝ", "Ș", "Ṡ", "Ṣ"));
			diacritics_simplified.put("T", createList("Ť", "Ţ", "Ṱ", "Ț", "Ṭ", "Ṯ", "Ŧ", "Ⱦ"));
			diacritics_simplified.put("U", createList("Ú", "Ŭ", "Ǔ", "Û", "Ü", "Ǘ", "Ǚ", "Ǜ", "Ǖ", "Ụ", "Ű", "Ù", "Ủ", "Ư", "Ứ", "Ự", "Ừ", "Ử", "Ữ", "Ū", "Ų", "Ů", "Ũ", "Ȕ", "Ȗ", "Ʉ"));
			diacritics_simplified.put("W", createList("Ẃ", "Ŵ", "Ẅ", "Ẁ"));
			diacritics_simplified.put("Y", createList("Ý", "Ŷ", "Ÿ", "Ẏ", "Ỵ", "Ỳ", "Ƴ", "Ỷ", "Ȳ", "Ỹ", "Ɏ"));
			diacritics_simplified.put("Z", createList("Ź", "Ž", "Ż", "Ẓ", "Ẕ", "Ƶ"));
			// </editor-fold>

			// <editor-fold desc="Lower-case alphabets"> - 44
			diacritics_simplified.put("a", createList("á", "ă", "ắ", "ặ", "ằ", "ą", "ẳ", "ẵ", "ǎ", "ḁ", "â", "ấ", "ậ", "ầ", "ẩ", "ẫ", "ä", "ạ", "à", "ả", "ā", "å", "ǻ", "ã", "ǟ", "ǡ", "ȁ", "ȃ", "ȧ"));
			diacritics_simplified.put("ae", createList("æ", "ǽ", "ǣ"));
			diacritics_simplified.put("b", createList("ḅ", "ḃ", "ḅ", "ḇ"));
			diacritics_simplified.put("c", createList("ć", "č", "ç", "ĉ", "ċ", "ȼ", "ḉ"));
			diacritics_simplified.put("ce", createList("œ"));
			diacritics_simplified.put("d", createList("ď", "ḓ", "ḍ", "ḏ", "đ", "ḋ", "ḑ"));
			diacritics_simplified.put("dz", createList("ǳ", "ʣ", "ʥ", "ǆ"));
			diacritics_simplified.put("e", createList("é", "ĕ", "ě", "ê", "ế", "ệ", "ề", "ể", "ḕ", "ḗ", "ḝ", "ḛ", "ḙ", "ễ", "ë", "ė", "ẹ", "è", "ẻ", "ē", "ę", "ẽ", "ȅ", "ȇ", "ȩ", "ɇ"));
			diacritics_simplified.put("f", createList("ƒ", "ḟ"));
			diacritics_simplified.put("fi", createList("ﬁ"));
			diacritics_simplified.put("fl", createList("ﬂ"));
			diacritics_simplified.put("g", createList("ǵ", "ğ", "ǧ", "ģ", "ĝ", "ġ", "ḡ", "ɡ", "ǥ"));
			diacritics_simplified.put("h", createList("ḫ", "ĥ", "ḥ", "ẖ", "ħ", "ḣ", "ḧ", "ḩ"));
			diacritics_simplified.put("hv", createList("ƕ"));
			diacritics_simplified.put("i", createList("í", "ĭ", "ǐ", "î", "ï", "ị", "ì", "ỉ", "ī", "į", "ɨ", "ĩ", "ı", "ȉ", "ȋ", "ḭ", "ḯ"));
			diacritics_simplified.put("ij", createList("ĳ"));
			diacritics_simplified.put("j", createList("ǰ", "ĵ", "ȷ", "ɉ"));
			diacritics_simplified.put("k", createList("ķ", "ḳ", "ḵ", "ḱ"));
			diacritics_simplified.put("l", createList("ĺ", "ƚ", "ɬ", "ľ", "ļ", "ḽ", "ḷ", "ḹ", "ḻ", "ŀ", "ɫ", "ł"));
			diacritics_simplified.put("lz", createList("ʫ"));
			diacritics_simplified.put("lj", createList("ǉ"));
			diacritics_simplified.put("ls", createList("ʪ"));
			diacritics_simplified.put("m", createList("ḿ", "ṁ", "ṃ"));
			diacritics_simplified.put("n", createList("ŉ", "ń", "ň", "ņ", "ṋ", "ṅ", "ṇ", "ǹ", "ṉ", "ñ"));
			diacritics_simplified.put("nj", createList("ǌ"));
			diacritics_simplified.put("o", createList("ó", "ŏ", "ǒ", "ô", "ố", "ộ", "ồ", "ổ", "ỗ", "ö", "ọ", "ő", "ò", "ỏ", "ơ", "ớ", "ợ", "ờ", "ở", "ỡ", "ō", "ǫ", "ø", "ǿ", "õ", "ɵ", "ʘ", "ǭ", "ȍ", "ȏ", "ȫ", "ȭ", "ȯ", "ȱ"));
			diacritics_simplified.put("oe", createList("œ"));
			diacritics_simplified.put("r", createList("ŕ", "ř", "ŗ", "ṙ", "ṛ", "ṝ", "ṟ", "ɼ", "ȑ", "ȓ", "ɍ"));
			diacritics_simplified.put("s", createList("ś", "š", "ş", "ŝ", "ș", "ṡ", "ṣ"));
			diacritics_simplified.put("t", createList("ť", "ţ", "ṱ", "ț", "ẗ", "ṭ", "ṯ", "ʈ", "ŧ"));
			diacritics_simplified.put("ts", createList("ʦ"));
			diacritics_simplified.put("u", createList("ʉ", "ú", "ŭ", "ǔ", "û", "ü", "ǘ", "ǚ", "ǜ", "ǖ", "ụ", "ű", "ù", "ủ", "ư", "ứ", "ự", "ừ", "ử", "ữ", "ū", "ų", "ů", "ũ", "ȕ", "ȗ"));
			diacritics_simplified.put("w", createList("ẃ", "ŵ", "ẅ", "ẁ"));
			diacritics_simplified.put("y", createList("ý", "ŷ", "ÿ", "ẏ", "ỵ", "ỳ", "ƴ", "ỷ", "ȳ", "ỹ", "ɏ"));
			diacritics_simplified.put("z", createList("ź", "ž", "ʑ", "ż", "ẓ", "ẕ", "ƶ"));
			// </editor-fold>

			// <editor-fold desc="Numbers"> - 6
			diacritics_simplified.put("0", createList("Ø", "ø", "⌀", "∅"));
			diacritics_simplified.put("1", createList("ı"));
			diacritics_simplified.put("2", createList("ƻ"));
			diacritics_simplified.put("3", createList("ʒ", "Ʒ", "Ǯ", "ǯ"));
			diacritics_simplified.put("5", createList("Ƽ"));
			// </editor-fold>
		}

		// ADVANCED
		if (diacritics == null)
		{
			final Map<String, List<String>> _diacritics = new HashMap<>(50);

			_diacritics.put("A", createList("Ʌ"));
			_diacritics.put("B", createList("ʚ", "ɞ", "Ƃ", "Ƅ"));
			_diacritics.put("C", createList("Ɔ", "ʗ", "Ƈ"));
			_diacritics.put("D", createList("Ɗ", "Ƌ", "ƿ"));
			_diacritics.put("E", createList("Ɛ", "Ə", "Ǝ", "Ʃ"));
			_diacritics.put("I", createList("ǀ", "ǂ", "Ḭ", "Ḯ"));
			_diacritics.put("K", createList("Ƙ"));
			_diacritics.put("M", createList("Ɯ"));
			_diacritics.put("P", createList("Þ", "Ƿ"));
			_diacritics.put("R", createList("ʁ", "Ʀ"));
			_diacritics.put("S", createList("Ƨ"));
			_diacritics.put("SS", createList("ẞ"));
			_diacritics.put("T", createList("Ƭ", "Ʈ"));
			_diacritics.put("U", createList("Ʊ", "Ʋ"));
			_diacritics.put("Y", createList("Ɣ"));
			_diacritics.put("Z", createList("Ȥ"));

			_diacritics.put("a", createList("ɑ", "ɐ", "ɒ"));
			_diacritics.put("b", createList("ɓ", "ß", "ƀ", "ƃ", "ƅ"));
			_diacritics.put("c", createList("ɕ"));
			_diacritics.put("d", createList("ɗ", "ð", "ɖ", "ƌ", "ȡ"));
			_diacritics.put("db", createList("ȸ"));
			_diacritics.put("dz", createList("ʤ"));
			_diacritics.put("e", createList("ʒ", "ǯ", "ʓ", "ɘ", "ɜ", "ɝ", "ə", "ɚ", "ʚ", "ɞ", "ǝ"));
			_diacritics.put("f", createList("ſ", "ʃ", "ʆ", "ɟ", "ʄ", "ƭ"));
			_diacritics.put("fn", createList("ʩ"));
			_diacritics.put("g", createList("ɠ"));
			_diacritics.put("h", createList("ɦ", "ɧ", "ɥ", "ʮ", "ʯ"));
			_diacritics.put("i", createList("ɩ"));
			_diacritics.put("j", createList("ʝ"));
			_diacritics.put("k", createList("ƙ", "ʞ"));
			_diacritics.put("l", createList("ɭ", "ȴ"));
			_diacritics.put("lz", createList("ɮ"));
			_diacritics.put("m", createList("ɱ", "ɯ", "ɰ"));
			_diacritics.put("n", createList("ɲ", "ɳ", "ŋ", "ƞ", "ȵ", "Ƞ"));
			_diacritics.put("p", createList("þ", "ƥ"));
			_diacritics.put("q", createList("ʠ", "Ƣ", "ƣ", "Ɋ", "ɋ"));
			_diacritics.put("qp", createList("ȹ"));
			_diacritics.put("r", createList("ɾ", "ɽ", "ɿ", "ɹ", "ɻ", "ɺ"));
			_diacritics.put("s", createList("ʂ", "ƨ", "ȿ"));
			_diacritics.put("ss", createList("ß"));
			_diacritics.put("t", createList("ƫ", "ʇ", "ȶ"));
			_diacritics.put("tc", createList("ʨ"));
			_diacritics.put("tf", createList("ʧ"));
			_diacritics.put("u", createList("ʊ"));
			_diacritics.put("v", createList("ʋ", "ʌ"));
			_diacritics.put("w", createList("ʍ"));
			_diacritics.put("y", createList("ʎ", "ƛ", "ɣ", "ƛ"));
			_diacritics.put("z", createList("ʐ", "ȥ", "ɀ"));

			_diacritics.put("3", createList("Ȝ", "Ƹ", "ƹ", "ƺ"));
			_diacritics.put("5", createList("ƽ", "ƾ"));
			_diacritics.put("8", createList("Ȣ", "ȣ"));

			diacritics = mergeMap(diacritics_simplified, _diacritics);
		}
	}

	private static Map<String, List<String>> mergeMap(final Map<String, List<String>> firstSource, final Map<String, List<String>> secondSource)
	{
		final Map<String, List<String>> result = new HashMap<>();

		for (final Entry<String, List<String>> entry : firstSource.entrySet())
			result.put(entry.getKey(), new ArrayList<>(entry.getValue()));

		for (final Entry<String, List<String>> entry : secondSource.entrySet())
			if (result.containsKey(entry.getKey()))
				result.get(entry.getKey()).addAll(entry.getValue());
			else
				result.put(entry.getKey(), entry.getValue());

		return result;
	}

	private static void applyLeetspeakSimplified(final Collection<? super Entry<String, List<String>>> conversionMap)
	{
		initializeLeetspeak();
		conversionMap.addAll(leetspeak_simplified.entrySet());
	}

	private static void applyLeetspeak(final Collection<? super Entry<String, List<String>>> conversionMap)
	{
		initializeLeetspeak();
		conversionMap.addAll(leetspeak.entrySet());
	}

	private static void applyDiacriticsSimplified(final Collection<? super Entry<String, List<String>>> conversionMap)
	{
		initializeDiacritics();
		conversionMap.addAll(diacritics_simplified.entrySet());
	}

	private static void applyDiacritics(final Collection<? super Entry<String, List<String>>> conversionMap)
	{
		initializeDiacritics();
		conversionMap.addAll(diacritics.entrySet());
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
