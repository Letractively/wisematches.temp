package billiongoods.core.search;

import billiongoods.core.Personality;

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
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface SearchManager<E, C> {
    /**
     * Returns total items count for specified personality and
     *
     * @param person  the person who searches entities.
     * @param context the context for search.
     * @return number of items available for search.
     */
    <Ctx extends C> int getTotalCount(Personality person, Ctx context);

    /**
     * Searches and returns list of all entities for specified person in specified context and according
     * to specified criteria.
     * <p/>
     * The result will be sorted according to specified orders and will be limited according to specified
     * range.
     *
     * @param person  the person who searches entities.
     * @param context the context for search.
     * @param orders  orders of result
     * @param range   the range of returned entities.
     * @return list of entities or empty list if no entities are found.
     */
    <Ctx extends C> List<E> searchEntities(Personality person, Ctx context, Orders orders, Range range);
}
