package billiongoods.core;

/**
 * The {@code PersonalityManager} provides ability to get players and machineries by id.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Deprecated
public interface PersonalityManager {
	void addPersonalityListener(PersonalityListener listener);

	void removePersonalityListener(PersonalityListener listener);


	/**
	 * Returns {@code Player} by specified id. If there is no player with the id {@code null} will be returned.
	 * <p/>
	 * This method doesn't check robots and visitors and works only with players.
	 *
	 * @param id the of player
	 * @return the player by specified id or {@code null}.
	 */
	Member getMember(Long id);


	/**
	 * Returns {@code Robot}, {@code Visitor} or {@code Player} personality based on specified id.
	 * <p/>
	 * If there is no personality with specified id {@code null} will be returned.
	 *
	 * @param id the of person
	 * @return {@code Robot}, {@code Visitor} or {@code Player} personality based on specified id.
	 */
	@Deprecated
	Personality getPerson(Long id);

	/**
	 * Returns visitor player by specified language.
	 *
	 * @param language the visitor's language.
	 * @return the visitor player by specified language.
	 */
	@Deprecated
	Visitor getVisitor(Language language);
}