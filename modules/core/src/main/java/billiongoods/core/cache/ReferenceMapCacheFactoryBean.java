package billiongoods.core.cache;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ReferenceMapCacheFactoryBean implements FactoryBean<ReferenceMapCache>, BeanNameAware, InitializingBean {
    private String name = "";
    private ReferenceType referenceType = ReferenceType.SOFT;
    private ReferenceMapCache cache;

    public ReferenceMapCacheFactoryBean() {
    }

    /**
     * Specify the name of the cache.
     * <p>Default is "" (empty String).
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setBeanName(String beanName) {
        if (!StringUtils.hasLength(this.name)) {
            setName(beanName);
        }
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public void afterPropertiesSet() {
        this.cache = new ReferenceMapCache(name, referenceType);
    }

    public ReferenceMapCache getObject() {
        return this.cache;
    }

    public Class<?> getObjectType() {
        return ReferenceMapCache.class;
    }

    public boolean isSingleton() {
        return true;
    }

}
