package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.ProductPreview;
import billiongoods.server.warehouse.Relationship;
import billiongoods.server.warehouse.RelationshipType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_product_relationship")
public class HibernateRelationship implements Relationship {
	@EmbeddedId
	private Pk pk;

	@Deprecated
	public HibernateRelationship() {
	}

	public HibernateRelationship(Integer product, HibernateGroup group, RelationshipType type) {
		this.pk = new Pk(product, type, group);
	}

	@Override
	public HibernateGroup getGroup() {
		return pk.group;
	}

	@Override
	public RelationshipType getType() {
		return pk.type;
	}

	@Override
	public List<ProductPreview> getProductPreviews() {
		return pk.group.getProductPreviews();
	}

	public static class Pk implements Serializable {
		@Column(name = "productId")
		private Integer productId;

		@Column(name = "type")
		@Enumerated(EnumType.ORDINAL)
		private RelationshipType type;

		@OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = false)
		@JoinColumn(name = "groupId")
		private HibernateGroup group;

		@Deprecated
		public Pk() {
		}

		public Pk(Integer productId, RelationshipType type, HibernateGroup group) {
			this.productId = productId;
			this.type = type;
			this.group = group;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Pk)) return false;

			Pk pk = (Pk) o;

			if (productId != null ? !productId.equals(pk.productId) : pk.productId != null) return false;
			if (group != null ? !group.equals(pk.group) : pk.group != null) return false;
			if (type != pk.type) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = productId != null ? productId.hashCode() : 0;
			result = 31 * result + (type != null ? type.hashCode() : 0);
			result = 31 * result + (group != null ? group.hashCode() : 0);
			return result;
		}
	}
}
