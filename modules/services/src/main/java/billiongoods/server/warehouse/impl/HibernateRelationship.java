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
	}
}
