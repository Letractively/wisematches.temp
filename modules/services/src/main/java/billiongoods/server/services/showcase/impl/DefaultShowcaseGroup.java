package billiongoods.server.services.showcase.impl;

import billiongoods.server.services.showcase.ShowcaseGroup;
import billiongoods.server.services.showcase.ShowcaseItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultShowcaseGroup implements ShowcaseGroup {
	private final List<ShowcaseItem> items;

	public DefaultShowcaseGroup(Collection<ShowcaseItem> values) {
		this.items = new ArrayList<>(values);
	}

	@Override
	public Collection<ShowcaseItem> getShowcaseItems() {
		return items;
	}
}
