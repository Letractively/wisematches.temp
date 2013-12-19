package billiongoods.core;

import java.io.Serializable;

/**
 * {@code Personality} is main element of the Wisematches. It identifies account, players and so on. Any actions,
 * data and so one belongs to a personality. For example, only {@code Personality} can play a game, have a rating,
 * statistic and so on.
 * <p/>
 * Each personality must have id to be identified. Two personality with the same id are always equals.
 * Any other attributes are ignored.
 * <p/>
 * {@code Personality} is the Hibernate entity and {@code id} field is always mapped to {@code id} column and must
 * be unique, can't be null and updated.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class Personality implements Serializable {
	private final Long id;

	private final int hashCode;

	/**
	 * Creates new personality with specified id.
	 *
	 * @param id the id for this personality.
	 */
	Personality(Long id) {
		this.id = id;
		hashCode = (int) (id ^ (id >>> 32));
	}

	/**
	 * Returns personality id.
	 *
	 * @return the id of the personality.
	 */
	public final Long getId() {
		return id;
	}

	public final boolean idem(Long id) {
		return this.id.equals(id);
	}

	/**
	 * Returns hash code of the personality calculated by formula:
	 * {@code (int) (id ^ (id >>> 32))}
	 *
	 * @return the hash code
	 */
	@Override
	public final int hashCode() {
		return hashCode;
	}

	/**
	 * Two personalities are equals if they ids are equals. Any subclasses of {@code Personality} are acceptable.
	 *
	 * @param o the object to be checked.
	 * @return {@code true} if specified object is subclass of {@code Personality} and has the same id;
	 * {@code false} - otherwise.
	 */
	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Personality)) return false;
		final Personality that = (Personality) o;
		return id.equals(that.id);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append("{id=").append(id).append('}');
		return sb.toString();
	}
}