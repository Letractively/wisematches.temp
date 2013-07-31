package billiongoods.server.warehouse.impl;

import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.warehouse.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateArticleManager extends EntitySearchManager<ArticleDescription, ArticleContext> implements ArticleManager {
	private CategoryManager catalogManager;
	private AttributeManager attributeManager;

	public HibernateArticleManager() {
		super(HibernateArticleDescription.class);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public Article getArticle(Long id) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateArticle article = (HibernateArticle) session.get(HibernateArticle.class, id);
		if (article != null) {
			article.initialize(catalogManager, attributeManager);
		}
		return article;
	}

	@Override
	public Article createArticle(String name, String description, Category category, float price, Float primordialPrice, Date restockDate, String referenceId, String referenceCode, Supplier wholesaler, float supplierPrice, Float supplierPrimordialPrice) {
		final HibernateSupplierInfo info = new HibernateSupplierInfo(referenceId, referenceCode, wholesaler, supplierPrice, supplierPrimordialPrice);

		final HibernateArticle article = new HibernateArticle(name, description, category, price, primordialPrice, restockDate, info, true);

		final Session session = sessionFactory.getCurrentSession();
		session.save(article);
		return article;
	}

	@Override
	public Article updateArticle(Long id, String name, String description) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateArticle article = (HibernateArticle) session.get(HibernateArticle.class, id);
		if (article == null) {
			return null;
		}

		article.setName(name);
		article.setDescription(description);

		session.update(article);
		return article;
	}

	@Override
	protected void initializeEntities(List<ArticleDescription> list) {
		for (ArticleDescription description : list) {
			((HibernateArticleDescription) description).initialize(catalogManager, attributeManager);
		}
	}

	@Override
	protected void applyProjections(Criteria criteria, ArticleContext context) {
	}

	@Override
	protected void applyRestrictions(Criteria criteria, ArticleContext context) {
		if (context != null && context.getCategory() != null) {
			criteria.add(Restrictions.eq("categoryId", context.getCategory().getId()));
		}
	}

	public void setCatalogManager(CategoryManager catalogManager) {
		this.catalogManager = catalogManager;
	}


	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}
}
