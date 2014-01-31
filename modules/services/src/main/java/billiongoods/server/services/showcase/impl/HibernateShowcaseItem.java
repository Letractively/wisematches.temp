package billiongoods.server.services.showcase.impl;

import billiongoods.server.services.showcase.ShowcaseItem;
import billiongoods.server.warehouse.CategoryManager;
import billiongoods.server.warehouse.ProductContext;
import billiongoods.server.warehouse.StockState;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_showcase")
public class HibernateShowcaseItem implements ShowcaseItem, Comparable<HibernateShowcaseItem> {
	@EmbeddedId
	private Pk pk;

	@Column(name = "name")
	private String name;

	@Column(name = "uri")
	private String uri;

	@Column(name = "category")
	private Integer category;

	@Column(name = "arrival")
	private boolean arrival;

	@Column(name = "subcategories")
	private boolean subCategories;

	@Transient
	private ProductContext context = null;

	@Deprecated
	HibernateShowcaseItem() {
	}

	Integer getSection() {
		return pk.section;
	}

	Integer getPosition() {
		return pk.position;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getMoreInfoUri() {
		return uri;
	}

	@Override
	public ProductContext getProductContext() {
		return context;
	}

	void initialize(CategoryManager manager) {
		context = new ProductContext(manager.getCategory(category), subCategories, null, arrival, ProductContext.ACTIVE_ONLY, StockState.IN_STOCK);
	}

	@Override
	public int compareTo(HibernateShowcaseItem o) {
		return pk.compareTo(o.pk);
	}

	@Embeddable
	public static class Pk implements Serializable, Comparable<Pk> {
		@Column(name = "section")
		private Integer section;

		@Column(name = "position")
		private Integer position;

		public Pk() {
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Pk)) return false;

			Pk pk = (Pk) o;

			if (section != null ? !section.equals(pk.section) : pk.section != null) return false;
			if (position != null ? !position.equals(pk.position) : pk.position != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = section != null ? section.hashCode() : 0;
			result = 31 * result + (position != null ? position.hashCode() : 0);
			return result;
		}

		@Override
		public int compareTo(Pk o) {
			int a = section.compareTo(o.section);
			if (a != 0) {
				return a;
			}
			return position.compareTo(o.position);
		}
	}
}
