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
	public Article getArticle(Integer id) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateArticle article = (HibernateArticle) session.get(HibernateArticle.class, id);
		if (article != null) {
			article.initialize(catalogManager, attributeManager);
		}
		return article;
	}

	@Override
	public ArticleDescription getDescription(Integer id) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateArticleDescription article = (HibernateArticleDescription) session.get(HibernateArticleDescription.class, id);
		if (article != null) {
			article.initialize(catalogManager, attributeManager);
		}
		return article;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Article createArticle(String name, String description, Category category,
								 float price, Float primordialPrice, Date restockDate,
								 String previewImage, List<String> imageIds, List<ArticleDescription> accessories,
								 List<Option> options, List<Property> properties,
								 String referenceId, String referenceCode, Supplier wholesaler,
								 float supplierPrice, Float supplierPrimordialPrice) {

		final HibernateArticle article = new HibernateArticle();
		updateArticle(article, name, description, category, price, primordialPrice,
				restockDate, previewImage, imageIds, accessories, options, properties,
				referenceId, referenceCode, wholesaler, supplierPrice, supplierPrimordialPrice);


		final Session session = sessionFactory.getCurrentSession();
		session.save(article);
		return article;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Article updateArticle(Integer id, String name, String description, Category category,
								 float price, Float primordialPrice, Date restockDate,
								 String previewImage, List<String> imageIds, List<ArticleDescription> accessories,
								 List<Option> options, List<Property> properties,
								 String referenceId, String referenceCode, Supplier wholesaler,
								 float supplierPrice, Float supplierPrimordialPrice) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateArticle article = (HibernateArticle) session.get(HibernateArticle.class, id);
		if (article == null) {
			return null;
		}

		updateArticle(article, name, description, category, price, primordialPrice,
				restockDate, previewImage, imageIds, accessories, options, properties,
				referenceId, referenceCode, wholesaler, supplierPrice, supplierPrimordialPrice);

		session.update(article);
		return article;
	}

	private void updateArticle(HibernateArticle article,
							   String name, String description, Category category,
							   float price, Float primordialPrice, Date restockDate,
							   String previewImage, List<String> imageIds, List<ArticleDescription> accessories,
							   List<Option> options, List<Property> properties,
							   String referenceId, String referenceCode, Supplier wholesaler,
							   float supplierPrice, Float supplierPrimordialPrice) {
		article.setName(name);
		article.setDescription(description);
		article.setCategory(category);
		article.setPrice(price);
		article.setPrimordialPrice(primordialPrice);
		article.setRestockDate(restockDate);
		article.setPreviewImageId(previewImage);
		article.setImageIds(imageIds);
		article.setAccessories(accessories);
		article.setOptions(options);
		article.setProperties(properties);

		final HibernateSupplierInfo supplierInfo = article.getSupplierInfo();
		supplierInfo.setReferenceId(referenceId);
		supplierInfo.setReferenceCode(referenceCode);
		supplierInfo.setWholesaler(wholesaler);
		supplierInfo.setPrice(supplierPrice);
		supplierInfo.setPrimordialPrice(supplierPrimordialPrice);
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
