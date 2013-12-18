package billiongoods.server.services.tracking;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class TrackingPerson {
	private TrackingPerson() {
	}

	public static Member of(Long personId) {
		return new Member(personId);
	}

	public static Member of(billiongoods.core.Member member) {
		return new Member(member.getId());
	}

	public static Visitor of(String email) {
		return new Visitor(email);
	}


	public static final class Member extends TrackingPerson {
		private final Long personId;

		private Member(Long personId) {
			this.personId = personId;
		}

		public Long getPersonId() {
			return personId;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("Member{");
			sb.append("personId=").append(personId);
			sb.append('}');
			return sb.toString();
		}
	}

	public static final class Visitor extends TrackingPerson {
		private final String personEmail;

		private Visitor(String personEmail) {
			this.personEmail = personEmail;
		}

		public String getPersonEmail() {
			return personEmail;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("Visitor{");
			sb.append("personEmail='").append(personEmail).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}
}
