package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.Group;

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

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = HibernateArticleDescription.class)
	@JoinTable(name = "store_group_item", joinColumns = @JoinColumn(name = "groupId"), inverseJoinColumns = @JoinColumn(name = "articleId"))
	private List<ArticleDescription> articles = new ArrayList<>();

	public HibernateGroup() {
	}

	public HibernateGroup(String name) {
		this.name = name;
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
	public List<ArticleDescription> getDescriptions() {
		return articles;
	}

	boolean addArticle(ArticleDescription article) {
		return this.articles.add(article);
	}

	boolean removeArticle(ArticleDescription article) {
		return this.articles.remove(article);
	}

	void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateGroup{");
		sb.append("id=").append(id);
		sb.append(", name='").append(name).append('\'');
		sb.append(", articles=").append(articles);
		sb.append('}');
		return sb.toString();
	}
}
