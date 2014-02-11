package billiongoods.server.web.services;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ProductSymbolicService {
	private static final String[][] ENCODING = new String[][]{
			{"а", "a"},
			{"б", "b"},
			{"в", "v"},
			{"г", "g"},
			{"д", "d"},
			{"е", "e"},
			{"ё", "yo"},
			{"ж", "zh"},
			{"з", "z"},
			{"и", "i"},
			{"й", "i"},
			{"к", "k"},
			{"л", "l"},
			{"м", "m"},
			{"н", "n"},
			{"о", "o"},
			{"п", "p"},
			{"р", "r"},
			{"с", "s"},
			{"т", "t"},
			{"у", "u"},
			{"ф", "f"},
			{"х", "h"},
			{"ц", "ts"},
			{"ч", "ch"},
			{"ш", "sh"},
			{"щ", "sh"},
			{"ъ", ""},
			{"ы", "i"},
			{"ь", ""},
			{"э", "e"},
			{"ю", "yu"},
			{"я", "ya"}
	};

    private static final int RUSSIAN_ENCODING_SHIFT = 1040;
    private static final String[] CHARS_INDEX = new String[ENCODING.length * 2 + 1];

    static {
		for (String[] strings : ENCODING) {
            final String s = strings[0];
            final String r = strings[1];
            final char chl = s.charAt(0);
            CHARS_INDEX[chl - RUSSIAN_ENCODING_SHIFT] = r;

            final char chu = Character.toUpperCase(chl);
            if (chu == 'Ё') {
                continue;
            }
            CHARS_INDEX[chu - RUSSIAN_ENCODING_SHIFT] = r.isEmpty() ? "" : Character.toUpperCase(r.charAt(0)) + r.substring(1);
        }
	}

	public ProductSymbolicService() {
	}

	public String generateSymbolic(String name) {
		String r = name;

		r = r.replaceAll("-", "_");
		r = r.replaceAll("\\.", "_");
		r = r.replaceAll("/", "-");
		r = r.replaceAll("№", "n");
		r = r.replaceAll(" ", "-");

		final StringBuilder b = new StringBuilder();
		final char[] chars = r.toCharArray();
		for (char ch : chars) {
			final int index = ((int) ch) - RUSSIAN_ENCODING_SHIFT;
			if (index >= 0 && index < CHARS_INDEX.length) {
				b.append(CHARS_INDEX[index]);
            } else if (ch == 'Ё') {
                b.append("Yo");
            } else {
				b.append(ch);
			}
		}
		return b.toString();
	}
}
