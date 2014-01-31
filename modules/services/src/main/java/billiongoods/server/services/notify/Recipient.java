package billiongoods.server.services.notify;

import billiongoods.core.Member;
import billiongoods.core.Passport;
import billiongoods.core.account.Account;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class Recipient {
	private Recipient() {
	}

	public static Recipient get(String email) {
		return new Person(email, null);
	}

	public static Recipient get(String email, Passport passport) {
		return new Person(email, passport);
	}

	public static Recipient get(Member member) {
		return new Person(member.getEmail(), member.getPassport());
	}

	public static Recipient get(Account account) {
		return new Person(account.getEmail(), account.getPassport());
	}

	public static Recipient get(MailBox mailBox) {
		return new Application(mailBox, null);
	}

	public static Recipient get(MailBox mailBox, Recipient returnAddress) {
		return new Application(mailBox, returnAddress);
	}

	public static enum MailBox {
		SUPPORT("support"),
		MONITORING("monitoring");

		private final String code;

		MailBox(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}


		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("MailBox{");
			sb.append("code='").append(code).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}

	public static final class Person extends Recipient {
		private final String email;
		private final Passport passport;

		private Person(String email, Passport passport) {
			this.email = email;
			this.passport = passport;
		}

		public String getEmail() {
			return email;
		}

		public Passport getPassport() {
			return passport;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("Person{");
			sb.append("email='").append(email).append('\'');
			sb.append(", passport=").append(passport);
			sb.append('}');
			return sb.toString();
		}
	}

	public static final class Application extends Recipient {
		private final MailBox mailBox;
		private final Recipient returnAddress;

		private Application(MailBox mailBox, Recipient returnAddress) {
			this.mailBox = mailBox;
			this.returnAddress = returnAddress;
		}

		public MailBox getMailBox() {
			return mailBox;
		}

		public Recipient getReturnAddress() {
			return returnAddress;
		}


		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("Application{");
			sb.append("mailBox=").append(mailBox);
			sb.append(", returnAddress=").append(returnAddress);
			sb.append('}');
			return sb.toString();
		}
	}
}
