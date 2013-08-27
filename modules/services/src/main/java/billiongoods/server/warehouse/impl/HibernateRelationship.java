package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.Relationship;
import billiongoods.server.warehouse.RelationshipType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_article_relationship")
public class HibernateRelationship implements Relationship {
	@EmbeddedId
	private Pk pk;

	@Deprecated
	public HibernateRelationship() {
	}

	public HibernateRelationship(HibernateGroup group, RelationshipType type, Integer articleId) {
		this.pk = new Pk(articleId, type, group);
	}

	@Override
	public RelationshipType getType() {
		return pk.type;
	}

	@Override
	public HibernateGroup getGroup() {
		return pk.group;
	}

	@Override
	public List<ArticleDescription> getDescriptions() {
		return pk.group.getDescriptions();
	}

	public static class Pk implements Serializable {
		@Column(name = "articleId")
		private Integer articleId;

		@Column(name = "type")
		@Enumerated(EnumType.ORDINAL)
		private RelationshipType type;

		@OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = false)
		@JoinColumn(name = "groupId")
		private HibernateGroup group;

		@Deprecated
		public Pk() {
		}

		public Pk(Integer articleId, RelationshipType type, HibernateGroup group) {
			this.articleId = articleId;
			this.type = type;
			this.group = group;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Pk)) return false;

			Pk pk = (Pk) o;

			if (articleId != null ? !articleId.equals(pk.articleId) : pk.articleId != null) return false;
			if (group != null ? !group.equals(pk.group) : pk.group != null) return false;
			if (type != pk.type) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = articleId != null ? articleId.hashCode() : 0;
			result = 31 * result + (type != null ? type.hashCode() : 0);
			result = 31 * result + (group != null ? group.hashCode() : 0);
			return result;
		}
	}
}
