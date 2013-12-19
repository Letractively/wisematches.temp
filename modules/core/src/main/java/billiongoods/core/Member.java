package billiongoods.core;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class Member extends Personality {
	private String email;
	private Passport passport;

	private static final long serialVersionUID = -3657252453631101842L;

	public Member(Long id, String email, Passport passport) {
		super(id);
		this.email = email;
		this.passport = passport;
	}

	public String getEmail() {
		return email;
	}

	public Passport getPassport() {
		return passport;
	}
}
