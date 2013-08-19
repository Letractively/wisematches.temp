package billiongoods.server.warehouse.impl;

import billiongoods.core.search.Orders;
import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.warehouse.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateArticleManager extends EntitySearchManager<ArticleDescription, ArticleContext> implements ArticleManager {
	private CategoryManager catalogManager;
	private AttributeManager attributeManager;

	private static final int ONE_WEEK_MILLIS = 1000 * 60 * 60 * 24 * 7;

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
	@Transactional(propagation = Propagation.SUPPORTS)
	public Article getArticle(String sku) {
		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateArticle a where a.supplierInfo.referenceCode=:code");
		query.setParameter("code", sku);
		final List list = query.list();
		if (list.size() > 0) {
			final HibernateArticle article = (HibernateArticle) list.get(0);
			article.initialize(catalogManager, attributeManager);
			return article;
		}
		return null;
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
								 double price, Float primordialPrice, double weight, Date restockDate,
								 String previewImage, List<String> imageIds, List<ArticleDescription> accessories,
								 List<Option> options, List<Property> properties,
								 String referenceId, String referenceCode, Supplier wholesaler,
								 double supplierPrice, Float supplierPrimordialPrice) {

		final HibernateArticle article = new HibernateArticle();
		updateArticle(article, name, description, category, price, primordialPrice, weight,
				restockDate, previewImage, imageIds, accessories, options, properties,
				referenceId, referenceCode, wholesaler, supplierPrice, supplierPrimordialPrice);


		final Session session = sessionFactory.getCurrentSession();
		session.save(article);
		return article;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Article updateArticle(Integer id, String name, String description, Category category,
								 double price, Float primordialPrice, double weight, Date restockDate,
								 String previewImage, List<String> imageIds, List<ArticleDescription> accessories,
								 List<Option> options, List<Property> properties,
								 String referenceId, String referenceCode, Supplier wholesaler,
								 double supplierPrice, Float supplierPrimordialPrice) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateArticle article = (HibernateArticle) session.get(HibernateArticle.class, id);
		if (article == null) {
			return null;
		}

		updateArticle(article, name, description, category, price, primordialPrice, weight,
				restockDate, previewImage, imageIds, accessories, options, properties,
				referenceId, referenceCode, wholesaler, supplierPrice, supplierPrimordialPrice);

		session.update(article);
		return article;
	}

	private void updateArticle(HibernateArticle article,
							   String name, String description, Category category,
							   double price, Float primordialPrice, double weight, Date restockDate,
							   String previewImage, List<String> imageIds, List<ArticleDescription> accessories,
							   List<Option> options, List<Property> properties,
							   String referenceId, String referenceCode, Supplier wholesaler,
							   double supplierPrice, Float supplierPrimordialPrice) {
		article.setName(name);
		article.setDescription(description);
		article.setCategory(category);
		article.setPrice(price);
		article.setPrimordialPrice(primordialPrice);
		article.setWeight(weight);
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
	public void updateSold(Integer id, int quantity) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("update billiongoods.server.warehouse.impl.HibernateArticle a set a.soldCount=:quantity where a.id=:id");
		query.setParameter("id", id);
		query.setParameter("quantity", quantity);
		query.executeUpdate();
	}

	@Override
	public void updateState(Integer id, boolean active) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("update billiongoods.server.warehouse.impl.HibernateArticle a set a.active=:active where a.id=:id");
		query.setParameter("id", id);
		query.setParameter("active", active);
		query.executeUpdate();
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
	protected void applyOrders(Criteria criteria, Orders orders) {
		super.applyOrders(criteria, orders);
		criteria.addOrder(Order.asc("id"));// always sort by id at the end
	}

	@Override
	protected void applyRestrictions(Criteria criteria, ArticleContext context) {
		if (context != null) {
			final Category category = context.getCategory();
			if (category != null) {
				if (context.isSubCategories() && !category.isFinal()) {
					final List<Integer> ids = new ArrayList<>();

					final LinkedList<Category> categories = new LinkedList<>();
					categories.add(category);

					while (categories.size() != 0) {
						final Category c = categories.removeFirst();

						ids.add(c.getId());
						categories.addAll(c.getChildren());
					}
					criteria.add(Restrictions.in("categoryId", ids));
				} else {
					criteria.add(Restrictions.eq("categoryId", category.getId()));
				}
			}

			if (!context.isInactive()) {
				criteria.add(Restrictions.eq("active", Boolean.TRUE));
			}

			if (context.isArrival()) {
				criteria.add(Restrictions.ge("registrationDate", new java.sql.Date(System.currentTimeMillis() - ONE_WEEK_MILLIS)));
			}

			if (context.getName() != null && !context.getName().trim().isEmpty()) {
				criteria.add(
						Restrictions.or(
								Restrictions.like("name", "%" + context.getName() + "%")
						)
				);
			}
		}
	}

	public void setCatalogManager(CategoryManager catalogManager) {
		this.catalogManager = catalogManager;
	}


	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}
}
