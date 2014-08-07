package billiongoods.core;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class Localization {
	private final String no;
	private final String yes;
	private final String momentAgo;
	private final String[][] timeDeclension;

	static Localization ENGLISH = new Localization(
			"no", "yes",
			"a moment", new String[][]{
			{"d", "day", "days", "days"},
			{"h", "hour", "hours", "hours"},
			{"m", "minute", "minutes", "minutes"}}) {

		@Override
		public String getCountEnding(int value) {
			if (value == 1) {
				return "";
			}
			return "s";
		}

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

	static Localization RUSSIAN = new Localization(
			"нет", "да",
			"одно мгновенье", new String[][]{
			{"д", "день", "дня", "дней"},
			{"ч", "час", "часа", "часов"},
			{"м", "минута", "минуты", "минут"}}) {

		private final String[] NUMERALS = new String[]{"ый", "ый", "ой", "ий", "ый", "ый", "ой", "ой", "ой", "ый"};

		@Override
		public String getCountEnding(int value) {
			int k = value;
			if (k > 20) {
				k = k % 10;
			}

			if (k == 1) {
				return "";
			}
			if (k >= 2 && k <= 4) {
				return "а";
			}
			return "ов";
		}

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


	public Localization(String no, String yes, String momentAgo, String[][] timeDeclension) {
		this.no = no;
		this.yes = yes;
		this.momentAgo = momentAgo;
		this.timeDeclension = timeDeclension;
	}

	public String getNo() {
		return no;
	}

	public String getYes() {
		return yes;
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

	public abstract String getCountEnding(int value);

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
