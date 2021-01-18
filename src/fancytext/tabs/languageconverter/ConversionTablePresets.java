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

	public void apply(final List<? super Entry<String, List<String>>> conversionMap)
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

	private static void apply1337(final List<? super Entry<String, List<String>>> conversionMap)
	{
		final Collection<Runnable> work = new ArrayDeque<>(36);

		// <editor-fold desc="Alphabets">
		work.add(() -> conversionMap.add(createEntry("A", createList("4", "/\\", "@", "/-\\", "^", "aye", "(L", "Д"))));
		work.add(() -> conversionMap.add(createEntry("B", createList("I3", "8", "13", "|3", "ß", "!3", "(3", "/3", ")3", "|-]", "j3", "6"))));
		work.add(() -> conversionMap.add(createEntry("D", createList(")", "|)", "(|", "[)", "I>", "|>", "T)", "I7", "cl", "|}", ">", "|]"))));
		work.add(() -> conversionMap.add(createEntry("E", createList("3", "&", "£", "€", "ë", "[-", "|=-"))));
		work.add(() -> conversionMap.add(createEntry("C", createList("[", "¢", "{", "<", "(", "©"))));
		work.add(() -> conversionMap.add(createEntry("F", createList("|=", "ƒ", "|#", "ph", "/=", "v"))));
		work.add(() -> conversionMap.add(createEntry("G", createList("&", "6", "(_,", "9", "C-", "gee", "(?,", "[,", "{,", "<-", "(."))));
		work.add(() -> conversionMap.add(createEntry("H", createList("#", "/-/", "[-]", "]-[", ")-(", "(-)", ":-:", "|~|", "|-|", "]~[", "}{", "!-!", "1-1", "\\-/", "I,I", "/-\\"))));
		work.add(() -> conversionMap.add(createEntry("I", createList("1", "[]", "|", "!", "eye", "3y3", "]["))));
		work.add(() -> conversionMap.add(createEntry("J", createList(",_|", "_|", "._|", "._]", "_]", ",_]", "]", ");", "1"))));
		work.add(() -> conversionMap.add(createEntry("K", createList(">|", "|<", "/<", "1<", "|c", "|(", "|{"))));
		work.add(() -> conversionMap.add(createEntry("L", createList("1", "£", "7", "|_", "|"))));
		work.add(() -> conversionMap.add(createEntry("M", createList("/\\/\\", "/V\\", "JVI", "[V]", "[]V[]", "|\\/|", "^^", "<\\/>", "{V}", "(v)", "(V)", "|V|", "nn", "IVI", "|\\|\\", "]\\/[", "1^1", "ITI", "JTI"))));
		work.add(() -> conversionMap.add(createEntry("N", createList("^/", "|\\|", "/\\/", "[\\]", "<\\>", "{\\}", "|V", "/V", "И", "^", "ท"))));
		work.add(() -> conversionMap.add(createEntry("O", createList("0", "Q", "()", "oh", "[]", "p", "<>", "Ø"))));
		work.add(() -> conversionMap.add(createEntry("P", createList("|*", "|o", "|º", "?", "|^", "|>", "|\"", "9", "[]D", "|°", "|7"))));
		work.add(() -> conversionMap.add(createEntry("Q", createList("(_,)", "9", "()_", "2", "0_", "<|", "&"))));
		work.add(() -> conversionMap.add(createEntry("R", createList("I2", "|`", "|~", "|?", "/2", "|^", "lz", "|9", "2", "12", "®", "[z", "Я", ".-", "|2", "|-"))));
		work.add(() -> conversionMap.add(createEntry("S", createList("5", "$", "z", "§", "ehs", "es", "2"))));
		work.add(() -> conversionMap.add(createEntry("T", createList("7", "+", "-|-", "']['", "†", "\"|\"", "~|~"))));
		work.add(() -> conversionMap.add(createEntry("U", createList("(_)", "|_|", "v", "L|", "µ", "บ"))));
		work.add(() -> conversionMap.add(createEntry("V", createList("\\/", "|/", "\\|"))));
		work.add(() -> conversionMap.add(createEntry("W", createList("\\/\\/", "VV", "\\N", "'//", "\\\\'", "\\^/", "(n)", "\\V/", "\\X/", "\\|/", "\\_|_/", "\\_:_/", "Ш", "Щ", "uu", "2u", "\\\\//\\\\//", "พ", "v²"))));
		work.add(() -> conversionMap.add(createEntry("X", createList("><", "Ж", "}{", "ecks", "×", "?", ")(", "]["))));
		work.add(() -> conversionMap.add(createEntry("Y", createList("j", "`/", "Ч", "7", "\\|/", "¥", "\\//"))));
		work.add(() -> conversionMap.add(createEntry("Z", createList("2", "7_", "-/_", "%", ">_", "s", "~/_", "-\\_", "-|_"))));
		// </editor-fold>

		// <editor-fold desc="Numbers">
		work.add(() -> conversionMap.add(createEntry("1", createList("L", "I"))));
		work.add(() -> conversionMap.add(createEntry("2", createList("R", "Z"))));
		work.add(() -> conversionMap.add(createEntry("3", createList("E"))));
		work.add(() -> conversionMap.add(createEntry("4", createList("A"))));
		work.add(() -> conversionMap.add(createEntry("5", createList("S"))));
		work.add(() -> conversionMap.add(createEntry("6", createList("b", "G"))));
		work.add(() -> conversionMap.add(createEntry("7", createList("T", "L"))));
		work.add(() -> conversionMap.add(createEntry("8", createList("B"))));
		work.add(() -> conversionMap.add(createEntry("9", createList("g", "q"))));
		work.add(() -> conversionMap.add(createEntry("0", createList("o", "()", "[]", "Ø", "<>"))));
		// </editor-fold>

		MultiThreading.submitRunnables(work);

		// https://www.gamehouse.com/blog/leet-speak-cheat-sheet/
	}

	private static void applyDiacritics(final List<? super Entry<String, List<String>>> conversionMap)
	{
		final Collection<Runnable> work = new ArrayDeque<>(86);

		// <editor-fold desc="Capital-case alphabets"> - 36
		work.add(() -> conversionMap.add(createEntry("A", createList("Á", "Ă", "Ắ", "Ặ", "Ằ", "Ẳ", "Ẵ", "Ǎ", "Â", "Ấ", "Ậ", "Ầ", "Ẩ", "Ẫ", "Ä", "Ạ", "À", "Ả", "Ā", "Ą", "Å", "Ǻ", "Ã", "Ǟ", "Ǡ", "Ȁ", "Ȃ", "Ȧ", "Ⱥ", "Ʌ", "Ḁ"))));
		work.add(() -> conversionMap.add(createEntry("AE", createList("Æ", "Ǽ", "Ǣ"))));
		work.add(() -> conversionMap.add(createEntry("B", createList("Ḅ", "Ɓ", "ʚ", "ɞ", "Ƃ", "Ƅ", "Ƀ", "Ḃ", "Ḅ", "Ḇ"))));
		work.add(() -> conversionMap.add(createEntry("C", createList("Ć", "Č", "Ç", "Ĉ", "Ċ", "Ɔ", "ʗ", "Ƈ", "Ȼ", "Ḉ"))));
		work.add(() -> conversionMap.add(createEntry("D", createList("Ď", "Ḓ", "Ḍ", "Ɗ", "Ḏ", "Ḋ", "ḋ", "Ḑ", "ḑ", "Đ", "Ð", "Ɖ", "Ƌ", "ƿ"))));
		work.add(() -> conversionMap.add(createEntry("DZ", createList("Ǳ", "Ǆ"))));
		work.add(() -> conversionMap.add(createEntry("Dz", createList("ǲ", "ǅ"))));
		work.add(() -> conversionMap.add(createEntry("E", createList("É", "Ĕ", "Ě", "Ê", "Ế", "Ệ", "Ề", "Ể", "Ễ", "Ë", "Ė", "Ẹ", "È", "Ẻ", "Ē", "Ę", "Ẽ", "Ɛ", "Ə", "Ǝ", "Ʃ", "Ȅ", "Ȇ", "Ȩ", "Ɇ", "Ḕ", "Ḗ", "Ḙ", "Ḛ", "Ḝ"))));
		work.add(() -> conversionMap.add(createEntry("F", createList("Ƒ", "Ḟ"))));
		work.add(() -> conversionMap.add(createEntry("G", createList("Ǵ", "Ğ", "Ǧ", "Ģ", "Ĝ", "Ġ", "Ḡ", "ʛ", "Ɠ", "Ǥ"))));
		work.add(() -> conversionMap.add(createEntry("H", createList("Ḫ", "Ĥ", "Ḥ", "Ħ", "Ḣ", "Ḧ", "Ḩ"))));
		work.add(() -> conversionMap.add(createEntry("Hu", createList("Ƕ"))));
		work.add(() -> conversionMap.add(createEntry("Hb", createList("Њ"))));
		work.add(() -> conversionMap.add(createEntry("I", createList("Í", "Ĭ", "Ǐ", "Î", "Ï", "İ", "Ị", "Ì", "Ỉ", "Ī", "Į", "Ĩ", "Ɨ", "ǀ", "ǂ", "Ȉ", "Ȋ", "Ḭ", "Ḯ"))));
		work.add(() -> conversionMap.add(createEntry("IJ", createList("Ĳ"))));
		work.add(() -> conversionMap.add(createEntry("II", createList("ǁ"))));
		work.add(() -> conversionMap.add(createEntry("J", createList("Ĵ", "Ɉ"))));
		work.add(() -> conversionMap.add(createEntry("K", createList("Ķ", "Ḳ", "Ƙ", "Ḵ", "Ḱ"))));
		work.add(() -> conversionMap.add(createEntry("L", createList("Ĺ", "Ƚ", "Ľ", "Ļ", "Ḽ", "Ḷ", "Ḹ", "Ḻ", "Ŀ", "Ł"))));
		work.add(() -> conversionMap.add(createEntry("LJ", createList("Ǉ"))));
		work.add(() -> conversionMap.add(createEntry("Lj", createList("ǈ"))));
		work.add(() -> conversionMap.add(createEntry("M", createList("Ḿ", "Ṁ", "Ṃ", "Ɯ"))));
		work.add(() -> conversionMap.add(createEntry("N", createList("Ń", "Ň", "Ņ", "Ṋ", "Ṅ", "Ṇ", "Ǹ", "Ɲ", "Ŋ", "Ṉ", "Ñ", "Ƞ"))));
		work.add(() -> conversionMap.add(createEntry("NJ", createList("Ǌ"))));
		work.add(() -> conversionMap.add(createEntry("Nj", createList("ǋ"))));
		work.add(() -> conversionMap.add(createEntry("O", createList("Ó", "Ŏ", "Ǒ", "Ô", "Ố", "Ộ", "Ồ", "Ổ", "Ỗ", "Ö", "Ọ", "Ő", "Ò", "Ỏ", "Ơ", "Ớ", "Ợ", "Ờ", "Ở", "Ỡ", "Ō", "Ɵ", "Ǫ", "Ø", "Ǿ", "Õ" /* , "Œ", "ɶ" */, "Ǭ", "Ȍ", "Ȏ", "Ȫ", "Ȭ", "Ȯ", "Ȱ"))));
		work.add(() -> conversionMap.add(createEntry("OE", createList("Œ", "ɶ")))); // It's 'OE' don't be confused with 'CE'.
		work.add(() -> conversionMap.add(createEntry("P", createList("Þ", "Ƥ", "Ƿ"))));
		work.add(() -> conversionMap.add(createEntry("R", createList("Ŕ", "Ř", "Ŗ", "Ṙ", "Ṛ", "Ṝ", "Ṟ", "ʁ", "Ʀ", "Ȑ", "Ȓ", "Ɍ"))));
		work.add(() -> conversionMap.add(createEntry("S", createList("Ś", "Š", "Ş", "Ŝ", "Ș", "Ṡ", "Ṣ", "Ƨ"))));
		work.add(() -> conversionMap.add(createEntry("SS", createList("ẞ"))));
		work.add(() -> conversionMap.add(createEntry("T", createList("Ť", "Ţ", "Ṱ", "Ț", "Ṭ", "Ṯ", "Ŧ", "Ƭ", "Ʈ", "Ⱦ"))));
		work.add(() -> conversionMap.add(createEntry("U", createList("Ú", "Ŭ", "Ǔ", "Û", "Ü", "Ǘ", "Ǚ", "Ǜ", "Ǖ", "Ụ", "Ű", "Ù", "Ủ", "Ư", "Ứ", "Ự", "Ừ", "Ử", "Ữ", "Ū", "Ų", "Ů", "Ũ", "Ʊ", "Ʋ", "Ȕ", "Ȗ", "Ʉ"))));
		work.add(() -> conversionMap.add(createEntry("W", createList("Ẃ", "Ŵ", "Ẅ", "Ẁ"))));
		work.add(() -> conversionMap.add(createEntry("Y", createList("Ý", "Ŷ", "Ÿ", "Ẏ", "Ỵ", "Ỳ", "Ƴ", "Ỷ", "Ȳ", "Ỹ", "Ɣ", "Ɏ"))));
		work.add(() -> conversionMap.add(createEntry("Z", createList("Ź", "Ž", "Ż", "Ẓ", "Ẕ", "Ƶ", "Ȥ"))));
		// </editor-fold>

		// <editor-fold desc="Lower-case alphabets"> - 44
		work.add(() -> conversionMap.add(createEntry("a", createList("á", "ă", "ắ", "ặ", "ằ", "ẳ", "ẵ", "ǎ", "ḁ", "â", "ấ", "ậ", "ầ", "ẩ", "ẫ", "ä", "ạ", "à", "ả", "ā", "ą", "å", "ǻ", "ã", /* "æ", "ǽ", */ "ɑ", "ɐ", "ɒ", "ǟ", "ǡ", "ȁ", "ȃ", "ȧ"))));
		work.add(() -> conversionMap.add(createEntry("ae", createList("æ", "ǽ", "ǣ"))));
		work.add(() -> conversionMap.add(createEntry("b", createList("ḅ", "ɓ", "ß", "ƀ", "ƃ", "ƅ", "ḃ", "ḅ", "ḇ"))));
		work.add(() -> conversionMap.add(createEntry("c", createList("ć", "č", "ç", "ĉ", "ɕ", "ċ", "ȼ", "ḉ"))));
		work.add(() -> conversionMap.add(createEntry("ce", createList("œ"))));
		work.add(() -> conversionMap.add(createEntry("d", createList("ď", "ḓ", "ḍ", "ɗ", "ḏ", "đ", "ḋ", "ɖ", "ḑ", "ð", "ƌ", "ȡ"))));
		work.add(() -> conversionMap.add(createEntry("db", createList("ȸ"))));
		work.add(() -> conversionMap.add(createEntry("dz", createList("ʤ", "ǳ", "ʣ", "ʥ", "ǆ"))));
		work.add(() -> conversionMap.add(createEntry("e", createList("é", "ĕ", "ě", "ê", "ế", "ệ", "ề", "ể", "ḕ", "ḗ", "ḝ", "ḛ", "ḙ", "ễ", "ë", "ė", "ẹ", "è", "ẻ", "ē", "ę", "ẽ", "ʒ", "ǯ", "ʓ", "ɘ", "ɜ", "ɝ", "ə", "ɚ", "ʚ", "ɞ", "ǝ", "ȅ", "ȇ", "ȩ", "ɇ"))));
		work.add(() -> conversionMap.add(createEntry("f", createList("ƒ", "ſ", "ʃ", "ʆ", "ɟ", "ʄ", "ƭ", "ḟ"))));
		work.add(() -> conversionMap.add(createEntry("fn", createList("ʩ"))));
		work.add(() -> conversionMap.add(createEntry("fi", createList("ﬁ"))));
		work.add(() -> conversionMap.add(createEntry("fl", createList("ﬂ"))));
		work.add(() -> conversionMap.add(createEntry("g", createList("ǵ", "ğ", "ǧ", "ģ", "ĝ", "ġ", "ɠ", "ḡ", "ɡ", "ǥ"))));
		work.add(() -> conversionMap.add(createEntry("h", createList("ḫ", "ĥ", "ḥ", "ɦ", "ẖ", "ħ", "ɧ", "ɥ", "ʮ", "ʯ", "ḣ", "ḧ", "ḩ"))));
		work.add(() -> conversionMap.add(createEntry("hv", createList("ƕ"))));
		work.add(() -> conversionMap.add(createEntry("i", createList("í", "ĭ", "ǐ", "î", "ï", "ị", "ì", "ỉ", "ī", "į", "ɨ", "ĩ", "ɩ", "ı"/* , "ĳ", "ɟ" */, "ȉ", "ȋ", "ḭ", "ḯ"))));
		work.add(() -> conversionMap.add(createEntry("ij", createList("ĳ"))));
		work.add(() -> conversionMap.add(createEntry("j", createList("ǰ", "ĵ", "ʝ", "ȷ", "ɉ"))));
		work.add(() -> conversionMap.add(createEntry("k", createList("ķ", "ḳ", "ƙ", "ḵ", "ĸ", "ʞ", "ḱ"))));
		work.add(() -> conversionMap.add(createEntry("l", createList("ĺ", "ƚ", "ɬ", "ľ", "ļ", "ḽ", "ḷ", "ḹ", "ḻ", "ŀ", "ɫ", "ɭ", "ł", "ȴ"))));
		work.add(() -> conversionMap.add(createEntry("lz", createList("ɮ", "ʫ"))));
		work.add(() -> conversionMap.add(createEntry("lj", createList("ǉ"))));
		work.add(() -> conversionMap.add(createEntry("ls", createList("ʪ"))));
		work.add(() -> conversionMap.add(createEntry("m", createList("ḿ", "ṁ", "ṃ", "ɱ", "ɯ", "ɰ"))));
		work.add(() -> conversionMap.add(createEntry("n", createList("ŉ", "ń", "ň", "ņ", "ṋ", "ṅ", "ṇ", "ǹ", "ɲ", "ṉ", "ɳ", "ñ", "ŋ", "ƞ", "ȵ"))));
		work.add(() -> conversionMap.add(createEntry("nj", createList("ǌ"))));
		work.add(() -> conversionMap.add(createEntry("o", createList("ó", "ŏ", "ǒ", "ô", "ố", "ộ", "ồ", "ổ", "ỗ", "ö", "ọ", "ő", "ò", "ỏ", "ơ", "ớ", "ợ", "ờ", "ở", "ỡ", "ō", "ǫ", "ø", "ǿ", "õ", "ɛ", "ɔ", "ɵ", "ʘ"/* , "œ" */, "ǭ", "ȍ", "ȏ", "ȫ", "ȭ", "ȯ", "ȱ"))));
		work.add(() -> conversionMap.add(createEntry("oe", createList("œ"))));
		work.add(() -> conversionMap.add(createEntry("p", createList("ɸ", "þ", "ƥ"))));
		work.add(() -> conversionMap.add(createEntry("q", createList("ʠ", "Ƣ", "ƣ", "Ɋ", "ɋ"))));
		work.add(() -> conversionMap.add(createEntry("qp", createList("ȹ"))));
		work.add(() -> conversionMap.add(createEntry("r", createList("ŕ", "ř", "ŗ", "ṙ", "ṛ", "ṝ", "ɾ", "ṟ", "ɼ", "ɽ", "ɿ", "ɹ", "ɻ", "ɺ", "ȑ", "ȓ", "ɍ"))));
		work.add(() -> conversionMap.add(createEntry("s", createList("ś", "š", "ş", "ŝ", "ș", "ṡ", "ṣ", "ʂ", "ƨ", "ȿ"))));
		work.add(() -> conversionMap.add(createEntry("ss", createList("ß"))));
		work.add(() -> conversionMap.add(createEntry("t", createList("ť", "ţ", "ṱ", "ț", "ẗ", "ṭ", "ṯ", "ʈ", "ŧ", "ƫ", "ʇ", "ȶ"))));
		work.add(() -> conversionMap.add(createEntry("tc", createList("ʨ"))));
		work.add(() -> conversionMap.add(createEntry("ts", createList("ʦ"))));
		work.add(() -> conversionMap.add(createEntry("tf", createList("ʧ"))));
		work.add(() -> conversionMap.add(createEntry("u", createList("ʉ", "ú", "ŭ", "ǔ", "û", "ü", "ǘ", "ǚ", "ǜ", "ǖ", "ụ", "ű", "ù", "ủ", "ư", "ứ", "ự", "ừ", "ử", "ữ", "ū", "ų", "ů", "ũ", "ʊ", "ȕ", "ȗ"))));
		work.add(() -> conversionMap.add(createEntry("v", createList("ʋ", "ʌ"))));
		work.add(() -> conversionMap.add(createEntry("w", createList("ẃ", "ŵ", "ẅ", "ẁ", "ʍ"))));
		work.add(() -> conversionMap.add(createEntry("y", createList("ý", "ŷ", "ÿ", "ẏ", "ỵ", "ỳ", "ƴ", "ỷ", "ȳ", "ỹ", "ʎ", "ƛ", "ɣ", "ƛ", "ɏ"))));
		work.add(() -> conversionMap.add(createEntry("z", createList("ź", "ž", "ʑ", "ż", "ẓ", "ẕ", "ʐ", "ƶ", "ȥ", "ɀ"))));
		// </editor-fold>

		// <editor-fold desc="Numbers"> - 6
		work.add(() -> conversionMap.add(createEntry("0", createList("Ø", "ø", "⌀", "∅"))));
		work.add(() -> conversionMap.add(createEntry("1", createList("ı"))));
		work.add(() -> conversionMap.add(createEntry("2", createList("ƻ"))));
		work.add(() -> conversionMap.add(createEntry("3", createList("ʒ", "Ȝ", "Ʒ", "Ƹ", "ƹ", "ƺ", "Ǯ", "ǯ"))));
		work.add(() -> conversionMap.add(createEntry("5", createList("Ƽ", "ƽ", "ƾ"))));
		work.add(() -> conversionMap.add(createEntry("8", createList("Ȣ", "ȣ"))));
		// </editor-fold>

		MultiThreading.submitRunnables(work);

		// http://pinyin.info/unicode/diacritics.html //
		// https://unicode-table.com/en/#latin-extended-b //
		// https://unicode-table.com/en/#latin-extended-additional //
		// https://unicode-table.com/en/#greek-extended //
	}

	private static void applyFakeRussian(final List<? super Entry<String, List<String>>> conversionMap)
	{
		final Collection<Runnable> work = new ArrayDeque<>(51);

		// <editor-fold desc="Capital-case alphabets"> - 30
		work.add(() -> conversionMap.add(createEntry("A", createList("Д", "д", "Ѧ", "ѧ", "Ӑ", "Ӓ"))));
		work.add(() -> conversionMap.add(createEntry("AE", createList("Ӕ"))));
		work.add(() -> conversionMap.add(createEntry("B", createList("Б"))));
		work.add(() -> conversionMap.add(createEntry("C", createList("Ҫ", "ҫ"))));
		work.add(() -> conversionMap.add(createEntry("Co", createList("Ҩ"))));
		work.add(() -> conversionMap.add(createEntry("E", createList("Ԑ", "Є", "Э", "Ё", "Ӗ"))));
		work.add(() -> conversionMap.add(createEntry("G", createList("Ԍ", "ԍ"))));
		work.add(() -> conversionMap.add(createEntry("F", createList("Ӻ", "ӻ", "Ғ", "ғ"))));
		work.add(() -> conversionMap.add(createEntry("H", createList("Њ", "Ҥ", "ҥ", "Ӊ", "ӊ", "Ң", "ң", "Ӈ", "ӈ", "Ȟ", "Ԩ", "ԩ", "н"))));
		work.add(() -> conversionMap.add(createEntry("Hb", createList("Њ", "њ"))));
		work.add(() -> conversionMap.add(createEntry("Hu", createList("Ԋ", "ԋ"))));
		work.add(() -> conversionMap.add(createEntry("Hj", createList("Ԣ", "ԣ"))));
		work.add(() -> conversionMap.add(createEntry("I", createList("Ї"))));
		work.add(() -> conversionMap.add(createEntry("J", createList("Ј"))));
		work.add(() -> conversionMap.add(createEntry("JX", createList("Ԕ", "ԕ"))));
		work.add(() -> conversionMap.add(createEntry("K", createList("Қ", "қ", "Ҡ", "ҡ", "Ҝ", "ҝ", "Ԟ", "ԟ", "Ҟ", "Ќ", "к", "ќ", "Ӄ", "ӄ"))));
		work.add(() -> conversionMap.add(createEntry("M", createList("Ԡ", "ԡ", "м", "Ҧ", "ҧ", "Ӎ", "ӎ"))));
		work.add(() -> conversionMap.add(createEntry("N", createList("И", "Ѝ", "Й", "и", "й", "ѝ", "Ҋ", "ҋ", "Ӣ", "ӣ", "Ӥ", "ӥ"))));
		work.add(() -> conversionMap.add(createEntry("O", createList("Ф", "Ѳ", "Ѻ", "Ӧ", "Ө", "Ӫ"))));
		work.add(() -> conversionMap.add(createEntry("P", createList("Ҏ"))));
		work.add(() -> conversionMap.add(createEntry("R", createList("Я", "я"))));
		work.add(() -> conversionMap.add(createEntry("RE", createList("Ԙ"))));
		work.add(() -> conversionMap.add(createEntry("Re", createList("ԙ"))));
		work.add(() -> conversionMap.add(createEntry("T", createList("Г", "Ґ", "ґ", "Ҭ", "ҭ", "Ѓ", "т", "ѓ", "Ӷ", "ӷ"))));
		work.add(() -> conversionMap.add(createEntry("U", createList("Ц", "Џ", "џ", "Ҵ"))));
		work.add(() -> conversionMap.add(createEntry("V", createList("Ѵ", "Ѷ"))));
		work.add(() -> conversionMap.add(createEntry("W", createList("Ш", "Щ", "Ѱ"))));
		work.add(() -> conversionMap.add(createEntry("X", createList("Ӿ", "Ҳ", "Ӽ", "Ж", "Ӂ", "Ӝ", "ж", "Җ", "җ"))));
		work.add(() -> conversionMap.add(createEntry("Y", createList("Ч", "Ҷ", "ҷ", "Ӌ", "ӌ", "Ҹ", "ҹ", "Ұ", "ұ", "Ў"))));
		work.add(() -> conversionMap.add(createEntry("Oy", createList("Ѹ", "ѹ"))));

		// Lower-case characters - 21
		work.add(() -> conversionMap.add(createEntry("a", createList("ӑ", "ӓ"))));
		work.add(() -> conversionMap.add(createEntry("ae", createList("ӕ"))));
		work.add(() -> conversionMap.add(createEntry("b", createList("Ъ", "Ь", "ь", "Ѣ", "ѣ", "Ҍ", "ҍ"))));
		work.add(() -> conversionMap.add(createEntry("bl", createList("Ы", "ы", "Ӹ", "ӹ"))));
		work.add(() -> conversionMap.add(createEntry("co", createList("ҩ"))));
		work.add(() -> conversionMap.add(createEntry("d", createList("Ԃ", "ԃ", "Ԁ"))));
		work.add(() -> conversionMap.add(createEntry("e", createList("ԑ", "є", "э", "ё", "Ҽ", "ҽ", "ѐ", "Ҿ", "ҿ", "ё", "ӗ", "Ә", "ә", "Ӛ", "ӛ"))));
		work.add(() -> conversionMap.add(createEntry("h", createList("Һ", "һ", "ȟ", "Ԧ", "ԧ", "Ђ", "Ћ", "ћ", "ђ", "Ҕ", "ҕ"))));
		work.add(() -> conversionMap.add(createEntry("i", createList("ї"))));
		work.add(() -> conversionMap.add(createEntry("k", createList("ҟ"))));
		work.add(() -> conversionMap.add(createEntry("n", createList("л", "П", "п", "ԉ", "Л", "Ԉ", "Ԓ", "ԓ", "Ԥ", "ԥ", "Ԯ", "ԯ"))));
		work.add(() -> conversionMap.add(createEntry("nb", createList("љ"))));
		work.add(() -> conversionMap.add(createEntry("o", createList("ѳ", "ѻ", "ӧ", "ө", "ӫ"))));
		work.add(() -> conversionMap.add(createEntry("p", createList("ҏ"))));
		work.add(() -> conversionMap.add(createEntry("u", createList("ц", "ҵ"))));
		work.add(() -> conversionMap.add(createEntry("v", createList("ѵ", "ѷ"))));
		work.add(() -> conversionMap.add(createEntry("w", createList("ш", "щ", "ѡ", "ѱ"))));
		work.add(() -> conversionMap.add(createEntry("x", createList("ҳ", "ӽ", "ӿ", "ж", "ӂ", "ӝ"))));
		work.add(() -> conversionMap.add(createEntry("y", createList("У", "У̃", "Ӯ", "Ӱ", "Ӳ", "у", "у̃", "ӯ", "ӱ", "ӳ", "ч", "ў", "Ӵ", "ӵ"))));
		work.add(() -> conversionMap.add(createEntry("3", createList("ԑ", "є", "э", "З", "Ҙ", "ҙ", "Ӟ", "ӟ", "Ӡ", "ӡ", "Ӭ", "ӭ"))));
		work.add(() -> conversionMap.add(createEntry("6", createList("Б", "б"))));
		// </editor-fold>

		MultiThreading.submitRunnables(work);

		// https://jkirchartz.com/demos/fake_russian_generator.html
	}

	private static void applyCurrencySymbols(final List<? super Entry<String, List<String>>> conversionMap)
	{
		final Collection<Runnable> work = new ArrayDeque<>(23);

		// <editor-fold desc="Capital-case alphabets"> - 16
		work.add(() -> conversionMap.add(createEntry("A", createList("₳"))));
		work.add(() -> conversionMap.add(createEntry("B", createList("฿", "₿"))));
		work.add(() -> conversionMap.add(createEntry("C", createList("₵", "₡"))));
		work.add(() -> conversionMap.add(createEntry("CE", createList("₠"))));
		work.add(() -> conversionMap.add(createEntry("E", createList("€", "£", "₤"))));
		work.add(() -> conversionMap.add(createEntry("F", createList("₣"))));
		work.add(() -> conversionMap.add(createEntry("G", createList("₲"))));
		work.add(() -> conversionMap.add(createEntry("Rs", createList("₨"))));
		work.add(() -> conversionMap.add(createEntry("S", createList("$"))));
		work.add(() -> conversionMap.add(createEntry("K", createList("₭"))));
		work.add(() -> conversionMap.add(createEntry("M", createList("₼", "ℳ"))));
		work.add(() -> conversionMap.add(createEntry("N", createList("₦"))));
		work.add(() -> conversionMap.add(createEntry("P", createList("₱", "₽", "Ᵽ"))));
		work.add(() -> conversionMap.add(createEntry("T", createList("₮"))));
		work.add(() -> conversionMap.add(createEntry("W", createList("￦", "₩"))));
		work.add(() -> conversionMap.add(createEntry("Y", createList("¥"))));
		// </editor-fold>

		// <editor-fold desc="Lower-case alphabets"> - 7
		work.add(() -> conversionMap.add(createEntry("c", createList("¢"))));
		work.add(() -> conversionMap.add(createEntry("com", createList("сом"))));
		work.add(() -> conversionMap.add(createEntry("d", createList("₫"))));
		work.add(() -> conversionMap.add(createEntry("f", createList("ƒ"))));
		work.add(() -> conversionMap.add(createEntry("m", createList("₥"))));
		work.add(() -> conversionMap.add(createEntry("o", createList("¤"))));
		work.add(() -> conversionMap.add(createEntry("tt", createList("₶"))));
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
