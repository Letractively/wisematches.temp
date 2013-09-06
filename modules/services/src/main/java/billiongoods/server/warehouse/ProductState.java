package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum ProductState {
	/**
	 * Just created/imported product
	 */
	DISABLED,

	/**
	 * Product has name, filled properties and prices but description is not valid.
	 */
	PROMOTED,

	/**
	 * Product is fully ready
	 */
	ACTIVE,

	/**
	 * Indicates that product is discontinued. Depends on settings we can show or hide such products.
	 * <p/>
	 * Usually it's pre-removed state.
	 */
	DISCONTINUED,

	/**
	 * Product has been removed
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
