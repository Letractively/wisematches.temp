package billiongoods.server.services.payment;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum ParcelState {
    PROCESSING(false), // 0
    SHIPPING(false), // 1
    SHIPPED(false), // 2
    SUSPENDED(false), // 3
    CANCELLED(true), // 4
    CLOSED(true); // 5

	private final String code;
	private final boolean finished;

	private static Map<ParcelState, EnumSet<ParcelState>> allowed = new HashMap<>();

	static {
		allowed.put(PROCESSING, EnumSet.of(SHIPPING, SHIPPED, SUSPENDED, CANCELLED));
		allowed.put(SHIPPING, EnumSet.of(SHIPPED));
		allowed.put(SHIPPED, EnumSet.of(CANCELLED, CLOSED));
		allowed.put(SUSPENDED, EnumSet.of(SHIPPING, SHIPPED, SUSPENDED, CANCELLED));
		allowed.put(CANCELLED, EnumSet.noneOf(ParcelState.class));
		allowed.put(CLOSED, EnumSet.noneOf(ParcelState.class));
	}

	ParcelState(boolean finished) {
		this.code = name().toLowerCase();
		this.finished = finished;
	}

	public String getCode() {
		return code;
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean allowed(ParcelState state) {
		return allowed.get(this).contains(state);
	}
}
