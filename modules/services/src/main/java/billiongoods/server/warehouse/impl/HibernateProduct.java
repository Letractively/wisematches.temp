package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_product")
public class HibernateProduct extends AbstractProductDescription implements Product {
	@Column(name = "description")
	private String description;

	@Column(name = "imageId")
	@OrderColumn(name = "position")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "store_product_image", joinColumns = @JoinColumn(name = "productId"))
	private List<String> imageIds = new ArrayList<>();

	@OrderColumn(name = "position")
	@ElementCollection(fetch = FetchType.EAGER, targetClass = HibernateProductProperty.class)
	@CollectionTable(name = "store_product_option", joinColumns = @JoinColumn(name = "productId"))
	private List<HibernateProductProperty> optionIds = new ArrayList<>();

	@OrderColumn(name = "position")
	@ElementCollection(fetch = FetchType.EAGER, targetClass = HibernateProductProperty.class)
	@CollectionTable(name = "store_product_property", joinColumns = @JoinColumn(name = "productId"))
	private List<HibernateProductProperty> propertyIds = new ArrayList<>();

	@Transient
	private List<Option> options = new ArrayList<>();

	@Transient
	private List<Property> properties = new ArrayList<>();

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "price")),
			@AttributeOverride(name = "primordialAmount", column = @Column(name = "primordialPrice"))
	})
	private HibernateSupplierInfo supplierInfo = new HibernateSupplierInfo();

	public HibernateProduct() {
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
					this.optionIds.add(new HibernateProductProperty(option.getAttribute(), value));
				}
			}
		}
	}

	void setProperties(List<Property> properties) {
		this.propertyIds.clear();

		this.properties = properties;
		if (properties != null) {
			for (Property property : properties) {
				this.propertyIds.add(new HibernateProductProperty(property.getAttribute(), property.getValue()));
			}
		}
	}

	void setImageIds(List<String> imageIds) {
		this.imageIds = imageIds;
	}

	void initialize(AttributeManager attributeManager) {
		final Map<Integer, List<String>> values = new HashMap<>();
		for (HibernateProductProperty optionId : optionIds) {
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

		for (HibernateProductProperty propertyId : propertyIds) {
			if (propertyId != null) {
				final Attribute attribute = attributeManager.getAttribute(propertyId.getAttributeId());
				if (attribute != null) {
					properties.add(new Property(attribute, propertyId.getValue()));
				}
			}
		}
	}
}
