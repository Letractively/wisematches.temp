package billiongoods.core.search.descriptive;

import java.lang.annotation.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SearchableBean {
    String uniformityProperty() default "";
}
