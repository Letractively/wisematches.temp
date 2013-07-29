package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.*;
import org.hibernate.annotations.IndexColumn;

import javax.persistence.*;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_article")
public class HibernateArticle extends AbstractArticleDescription implements Article {
    @Column(name = "soldCount")
    private int soldCount;

    @Column(name = "description")
    private String description;

    @Column(name = "imageId")
    @IndexColumn(name = "order")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "store_article_image", joinColumns = @JoinColumn(name = "articleId"))
    private List<String> imageIds = new ArrayList<>();

    @Column(name = "accessoryId")
    @IndexColumn(name = "order")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "store_article_accessory", joinColumns = @JoinColumn(name = "articleId"))
    private List<Long> accessories = new ArrayList<>();

    @IndexColumn(name = "order")
    @ElementCollection(fetch = FetchType.EAGER, targetClass = HibernateArticleProperty.class)
    @CollectionTable(name = "store_article_option", joinColumns = @JoinColumn(name = "articleId"))
    private List<HibernateArticleProperty> optionIds = new ArrayList<>();

    @IndexColumn(name = "order")
    @ElementCollection(fetch = FetchType.EAGER, targetClass = HibernateArticleProperty.class)
    @CollectionTable(name = "store_article_property", joinColumns = @JoinColumn(name = "articleId"))
    private List<HibernateArticleProperty> propertyIds = new ArrayList<>();

    @Transient
    private List<Option> options = new ArrayList<>();

    @Transient
    private List<Property> properties = new ArrayList<>();

    @Embedded
    private HibernateSupplierInfo supplierInfo = new HibernateSupplierInfo();

    public HibernateArticle() {
    }

    public HibernateArticle(String name, String description, HibernateSupplierInfo supplierInfo, float price, Float primordialPrice, Category category, Date restockDate, boolean active) {
        super(name, price, primordialPrice, category, restockDate, active);
        this.description = description;
        this.supplierInfo = supplierInfo;
    }

    @Override
    public int getSoldCount() {
        return soldCount;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<Option> getOptions() {
        return options;
    }

    @Override
    public List<String> getImageIds() {
        return imageIds;
    }

    @Override
    public List<Long> getAccessories() {
        return accessories;
    }

    @Override
    public List<Property> getProperties() {
        return properties;
    }

    @Override
    public HibernateSupplierInfo getSupplierInfo() {
        return supplierInfo;
    }

    void incrementSoldCount() {
        this.soldCount++;
    }

    void setDescription(String description) {
        this.description = description;
    }

    @Override
    void initialize(CategoryManager manager, AttributeManager attributeManager) {
        super.initialize(manager, attributeManager);

        final Map<Integer, List<String>> values = new HashMap<>();
        for (HibernateArticleProperty optionId : optionIds) {
            List<String> strings = values.get(optionId.getAttributeId());
            if (strings == null) {
                strings = new ArrayList<>(4);
                values.put(optionId.getAttributeId(), strings);
            }
            strings.add(optionId.getValue());
        }

        for (Map.Entry<Integer, List<String>> entry : values.entrySet()) {
            options.add(new Option(attributeManager.getAttribute(entry.getKey()), entry.getValue()));
        }

        for (HibernateArticleProperty propertyId : propertyIds) {
            properties.add(new Property(attributeManager.getAttribute(propertyId.getAttributeId()), propertyId.getValue()));
        }
    }
}
