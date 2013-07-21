package billiongoods.core.search.descriptive;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@code SearchableDescriptor} contains description of entity like it's searchable properties
 * and common info.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class SearchableDescriptor {
    private final Class<?> desiredEntityType;

    private final SearchableBean beanAnnotation;
    private final Map<String, SearchableProperty> propertyAnnotations = new HashMap<String, SearchableProperty>();

    private SearchableDescriptor(Class<?> desiredEntityType) {
        this.desiredEntityType = desiredEntityType;

        beanAnnotation = desiredEntityType.getAnnotation(SearchableBean.class);
        if (beanAnnotation == null) {
            throw new IllegalArgumentException("Type '" + desiredEntityType.getName() +
                    "' doesn't support SearchableBean annotation.");
        }

        final Field[] declaredFields = desiredEntityType.getDeclaredFields();
        for (Field field : declaredFields) {
            final SearchableProperty annotation = field.getAnnotation(SearchableProperty.class);
            if (annotation != null) {
                propertyAnnotations.put(field.getName(), annotation);
            }
        }

        final String key = beanAnnotation.uniformityProperty();
        if (!key.isEmpty()) {
            SearchableProperty searchableProperty = propertyAnnotations.get(key);
            if (searchableProperty == null) {
                throw new IllegalArgumentException("Uniformity property '" + key + "' not found in the type '" +
                        desiredEntityType.getName() + "'.");
            }
        }
    }

    /**
     * Returns original entity type represented by this manager.
     *
     * @return the original entity type represented by this manager.
     */
    public Class<?> getDesiredEntityType() {
        return desiredEntityType;
    }

    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(propertyAnnotations.keySet());
    }

    public SearchableBean getBeanAnnotation() {
        return beanAnnotation;
    }

    public SearchableProperty getProperty(String property) {
        return propertyAnnotations.get(property);
    }

    public SearchableProperty[] getProperties(String... property) {
        final SearchableProperty[] res = new SearchableProperty[property.length];
        for (int i = 0; i < property.length; i++) {
            res[i] = getProperty(property[i]);
        }
        return res;
    }

    public SearchableProperty getUniformityProperty() {
        if (beanAnnotation.uniformityProperty().isEmpty()) {
            return null;
        }
        return propertyAnnotations.get(beanAnnotation.uniformityProperty());
    }

    /**
     * Returns description for specified entity type
     *
     * @param desiredEntityType the entity type to be described
     * @return the descriptor
     * @throws NullPointerException     if specified type is null
     * @throws IllegalArgumentException if specified type doesn't have {@code SearchableBean}
     *                                  annotation.
     */
    public static SearchableDescriptor valueOf(Class<?> desiredEntityType) {
        // Internal cache by type can be created here
        return new SearchableDescriptor(desiredEntityType);
    }
}