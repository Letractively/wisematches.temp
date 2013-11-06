package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Group;
import billiongoods.server.warehouse.GroupType;
import billiongoods.server.warehouse.ProductPreview;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_group")
public class HibernateGroup implements Group {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	private GroupType type;

	@Column(name = "categoryId")
	private Integer categoryId;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = HibernateProductPreview.class)
	@JoinTable(name = "store_group_item", joinColumns = @JoinColumn(name = "groupId"), inverseJoinColumns = @JoinColumn(name = "productId"))
	private List<ProductPreview> products = new ArrayList<>();

	@Deprecated
	HibernateGroup() {
	}

	public HibernateGroup(String name, GroupType type, Integer categoryId) {
		this.name = name;
		this.type = type;
		this.categoryId = categoryId;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public GroupType getType() {
		return type;
	}

	@Override
	public Integer getCategoryId() {
		return categoryId;
	}

	@Override
	public List<ProductPreview> getProductPreviews() {
		return products;
	}

	boolean addProduct(ProductPreview product) {
		return this.products.add(product);
	}

	boolean removeProduct(ProductPreview product) {
		return this.products.remove(product);
	}

	void setName(String name) {
		this.name = name;
	}

	void setType(GroupType type) {
		this.type = type;
	}

	void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateGroup{");
		sb.append("id=").append(id);
		sb.append(", name='").append(name).append('\'');
		sb.append(", products=").append(products);
		sb.append('}');
		return sb.toString();
	}
}
