package billiongoods.core.search.descriptive;

import billiongoods.core.search.SearchManager;

/**
 * The {@code DescriptiveSearchManager} extends base {@code SearchManager} and
 * processed entities described by {@code SearchableBean} and {@code SearchableProperty}
 * annotations.
 * <p/>
 * The {@code DescriptiveSearchManager} allows get {@code SearchableDescriptor} for
 * supported entity and can be used for dynamic entities parsing or analyzing.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface DescriptiveSearchManager<E, C> extends SearchManager<E, C> {
    /**
     * Returns descriptor of entity.
     *
     * @return the entity descriptor.
     */
    SearchableDescriptor getEntityDescriptor();
}
