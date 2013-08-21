package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Article;
import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.Option;
import billiongoods.server.warehouse.Property;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_article")
public class HibernateArticle extends AbstractArticleDescription implements Article {
	@Column(name = "description")
	private String description;

	@Column(name = "imageId")
	@OrderColumn(name = "position")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "store_article_image", joinColumns = @JoinColumn(name = "articleId"))
	private List<String> imageIds = new ArrayList<>();

	@OrderColumn(name = "position")
	@ElementCollection(fetch = FetchType.EAGER, targetClass = HibernateArticleProperty.class)
	@CollectionTable(name = "store_article_option", joinColumns = @JoinColumn(name = "articleId"))
	private List<HibernateArticleProperty> optionIds = new ArrayList<>();

	@OrderColumn(name = "position")
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
	public List<Property> getProperties() {
		return properties;
	}

	@Override
	public HibernateSupplierInfo getSupplierInfo() {
		return supplierInfo;
	}

	void setDescription(String description) {
		this.description = description;
	}

	void setOptions(List<Option> options) {
		this.optionIds.clear();

		this.options = options;
		if (options != null) {
			for (Option option : options) {
				for (String value : option.getValues()) {
					this.optionIds.add(new HibernateArticleProperty(option.getAttribute(), value));
				}
			}
		}
	}

	void setProperties(List<Property> properties) {
		this.propertyIds.clear();

		this.properties = properties;
		if (properties != null) {
			for (Property property : properties) {
				this.propertyIds.add(new HibernateArticleProperty(property.getAttribute(), property.getValue()));
			}
		}
	}

	void setImageIds(List<String> imageIds) {
		this.imageIds = imageIds;
	}

	void initialize(AttributeManager attributeManager) {
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
