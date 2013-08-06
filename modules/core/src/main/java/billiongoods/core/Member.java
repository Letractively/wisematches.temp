package billiongoods.core;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class Member extends Personality {
	private String email;
	private String username;

	private static final long serialVersionUID = -3657252453631101842L;

	public Member(Long id, String username, String email) {
		super(id);
		this.email = email;
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public Language getLanguage() {
		return Language.RU;
	}

	@Override
	public final PersonalityType getType() {
		return PersonalityType.MEMBER;
	}
}
