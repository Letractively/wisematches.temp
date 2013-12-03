package billiongoods.server.services.showcase.impl;

import billiongoods.server.services.showcase.Showcase;
import billiongoods.server.services.showcase.ShowcaseGroup;
import billiongoods.server.services.showcase.ShowcaseItem;

import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultShowcase implements Showcase {
	private final List<ShowcaseGroup> showcaseGroups = new ArrayList<>();

	public DefaultShowcase() {
	}

	public DefaultShowcase(List<HibernateShowcaseItem> showcaseItems) {
		Map<Integer, Map<Integer, ShowcaseItem>> groups = new TreeMap<>();

		for (HibernateShowcaseItem item : showcaseItems) {
			final int integer = item.getSection();
			Map<Integer, ShowcaseItem> group = groups.get(integer);
			if (group == null) {
				group = new TreeMap<>();
				groups.put(integer, group);
			}
			group.put(item.getPosition(), item);
		}

		for (Map<Integer, ShowcaseItem> map : groups.values()) {
			showcaseGroups.add(new DefaultShowcaseGroup(map.values()));
		}
	}

	@Override
	public Collection<ShowcaseGroup> getShowcaseGroups() {
		return showcaseGroups;
	}
}
