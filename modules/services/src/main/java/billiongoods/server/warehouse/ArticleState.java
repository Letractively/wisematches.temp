package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum ArticleState {
	/**
	 * Just created/imported article
	 */
	DISABLED,

	/**
	 * Article has name, filled properties and prices but description is not valid.
	 */
	PROMOTED,

	/**
	 * Article is fully ready
	 */
	ACTIVE,

	/**
	 * Indicates that articled is discontinued. Depends on settings we can show or hide such articles.
	 * <p/>
	 * Usually it's pre-removed state.
	 */
	DISCONTINUED,

	/**
	 * Article has been removed
	 */
	REMOVED;

	public boolean isDisabled() {
		return this == DISABLED;
	}

	public boolean isPromoted() {
		return this == PROMOTED;
	}

	public boolean isActive() {
		return this == ACTIVE;
	}

	public boolean isDiscontinued() {
		return this == DISCONTINUED;
	}

	public boolean isRemoved() {
		return this == REMOVED;
	}
}
