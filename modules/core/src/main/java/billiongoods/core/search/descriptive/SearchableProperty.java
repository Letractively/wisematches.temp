package billiongoods.core.search.descriptive;

import java.lang.annotation.*;

/**
 * {@code SearchAttribute} describes attributes of {@code SearchableEntity} which is provided by search
 * system.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchableProperty {
    /**
     * Returns appropriate column for the attribute.
     *
     * @return the appropriate column name.
     */
    String column() default "";

    /**
     * Indicates is attribute is sortable or not.
     *
     * @return {@code true} attribute is sortable; {@code false} - otherwise.
     */
    boolean sortable() default true;
}
