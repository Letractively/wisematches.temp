package billiongoods.core;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class Member extends Personality {
	private String email;
	private String username;
	private Settings settings;

	private static final long serialVersionUID = -3657252453631101842L;

	public Member(Long id, String email, String username, Settings settings) {
		super(id);
		this.email = email;
		this.username = username;
		this.settings = settings;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public Settings getSettings() {
		return settings;
	}

	@Override
	public final PersonalityType getType() {
		return PersonalityType.MEMBER;
	}
}
