package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.RelationshipType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_article_relationship")
public class HibernateRelationship {
	@EmbeddedId
	private Pk pk;

	@OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = false)
	@JoinColumn(name = "groupId")
	private HibernateGroup group;

	@Deprecated
	public HibernateRelationship() {
	}

	public HibernateRelationship(Integer articleId, RelationshipType relationshipType, HibernateGroup group) {
		this.pk = new Pk(articleId, relationshipType);
		this.group = group;
	}

	public HibernateGroup getGroup() {
		return group;
	}

	public RelationshipType getRelationshipType() {
		return pk.relationshipType;
	}

	public List<ArticleDescription> getDescriptions() {
		return group.getDescriptions();
	}

	void setGroup(HibernateGroup group) {
		this.group = group;
	}

	@Embeddable
	public static class Pk implements Serializable {
		@Column(name = "articleId")
		private Integer articleId;

		@Column(name = "type")
		@Enumerated(EnumType.ORDINAL)
		private RelationshipType relationshipType;

		public Pk() {
		}

		public Pk(Integer articleId, RelationshipType relationshipType) {
			this.articleId = articleId;
			this.relationshipType = relationshipType;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Pk)) return false;

			Pk pk = (Pk) o;

			if (!articleId.equals(pk.articleId)) return false;
			if (relationshipType != pk.relationshipType) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = articleId.hashCode();
			result = 31 * result + relationshipType.hashCode();
			return result;
		}
	}
}
