package billiongoods.core;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class Localization {
	private final String momentAgo;
	private final String[][] timeDeclension;

	static Localization ENGLISH = new Localization("a moment", new String[][]{
			{"d", "day", "days", "days"},
			{"h", "hour", "hours", "hours"},
			{"m", "minute", "minutes", "minutes"}}) {
		@Override
		public String getNumeralEnding(int abs) {
			switch (abs) {
				case 1:
					return "st";
				case 2:
					return "nd";
				case 3:
					return "rd";
				default:
					return "th";
			}
		}

		@Override
		public String getWordEnding(int abs) {
			if (abs < 1) {
				return "";
			}
			return "s";
		}
	};

	static Localization RUSSIAN = new Localization("одно мгновенье", new String[][]{
			{"д", "день", "дня", "дней"},
			{"ч", "час", "часа", "часов"},
			{"м", "минута", "минуты", "минут"}}) {

		private final String[] NUMERALS = new String[]{"ый", "ый", "ой", "ий", "ый", "ый", "ой", "ой", "ой", "ый"};

		@Override
		public String getNumeralEnding(int abs) {
			if (abs >= 9 && abs <= 20) {
				return "ый";
			}
			return NUMERALS[abs % 10];
		}

		@Override
		public String getWordEnding(int abs) {
			if (abs == 1) {
				return "а";
			} else if (abs < 5) {
				return "ы";
			}
			return "";
		}
	};


	public Localization(String momentAgo, String[][] timeDeclension) {
		this.momentAgo = momentAgo;
		this.timeDeclension = timeDeclension;
	}

	public String getDaysCode() {
		return timeDeclension[0][0];
	}

	public String getHoursCode() {
		return timeDeclension[1][0];
	}

	public String getMinutesCode() {
		return timeDeclension[2][0];
	}


	public String getMomentAgoLabel() {
		return momentAgo;
	}

	public String getDaysLabel(int value) {
		return timeDeclension[0][timeDeclensionIndex(value)];
	}

	public String getHoursLabel(int value) {
		return timeDeclension[1][timeDeclensionIndex(value)];
	}

	public String getMinutesLabel(int value) {
		return timeDeclension[2][timeDeclensionIndex(value)];
	}

	public abstract String getWordEnding(int quantity);

	public abstract String getNumeralEnding(int value);


	private static int timeDeclensionIndex(int value) {
		int v = value % 100;
		if (v > 20) {
			v %= 10;
		}
		if (v == 1) {
			return 1;
		} else if (v > 1 && v < 5) {
			return 2;
		}
		return 3;
	}
}
