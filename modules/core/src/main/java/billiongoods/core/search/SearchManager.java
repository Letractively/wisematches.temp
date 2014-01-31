package billiongoods.core.search;

import java.util.List;

/**
 * The {@code SearchManager} allows do search or some objects supported by appropriate manager.
 * <p/>
 * The {@code SearchManager} introduces common interface for iterating through entities that provides
 * additional functionality like get total or filtered items count.
 * <p/>
 * The {@code SearchManager} takes a {@code Personality} into account and some realization can
 * return differ result for differ personalities.
 *
 * @param <E> the entity type that is supported by search manager.
 * @param <C> the context type that is supported by search manager.
 * @param <F> the filter for searching or {@code Void} if filter is not required.
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface SearchManager<E, C, F> {
	/**
	 * Returns total items count for specified personality and
	 *
	 * @param context the context for search.
	 * @return number of items available for search.
	 */
	<Ctx extends C> int getTotalCount(Ctx context);

	/**
	 * Returns total items count for specified personality and
	 *
	 * @param context the context for search.
	 * @return number of items available for search.
	 */
	<Ctx extends C, Ftl extends F> int getTotalCount(Ctx context, Ftl filter);

	/**
	 * Searches and returns list of all entities for specified person in specified context and according
	 * to specified criteria.
	 * <p/>
	 * The result will be sorted according to specified orders and will be limited according to specified
	 * range.
	 *
	 * @param context the context for search.
	 * @param filter  the filter to filter search result.
	 * @param range   the range of returned entities.
	 * @param orders  orders of result   @return list of entities or empty list if no entities are found.
	 */
	<Ctx extends C, Ftl extends F> List<E> searchEntities(Ctx context, Ftl filter, Range range, Orders orders);
}
