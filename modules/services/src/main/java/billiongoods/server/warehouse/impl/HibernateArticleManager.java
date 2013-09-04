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

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateArticleManager extends EntitySearchManager<ArticleDescription, ArticleContext> implements ArticleManager {
	private AttributeManager attributeManager;

	private final Collection<ArticleListener> listeners = new CopyOnWriteArrayList<>();

	private static final int ONE_WEEK_MILLIS = 1000 * 60 * 60 * 24 * 7;

	public HibernateArticleManager() {
		super(HibernateArticleDescription.class);
	}

	@Override
	public void addArticleListener(ArticleListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removeArticleListener(ArticleListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public Article getArticle(Integer id) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateArticle article = (HibernateArticle) session.get(HibernateArticle.class, id);
		if (article != null) {
			article.initialize(attributeManager);
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
			article.initialize(attributeManager);
			return article;
		}
		return null;
	}

	@Override
	public ArticleDescription getDescription(Integer id) {
		final Session session = sessionFactory.getCurrentSession();
		return (HibernateArticleDescription) session.get(HibernateArticleDescription.class, id);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Article createArticle(ArticleEditor editor) {
		final HibernateArticle article = new HibernateArticle();
		updateArticle(article, editor);


		final Session session = sessionFactory.getCurrentSession();
		session.save(article);

		for (ArticleListener listener : listeners) {
			listener.articleCreated(article);
		}
		return article;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Article updateArticle(Integer id, ArticleEditor editor) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateArticle article = (HibernateArticle) session.get(HibernateArticle.class, id);
		if (article == null) {
			return null;
		}
		updateArticle(article, editor);
		session.update(article);

		for (ArticleListener listener : listeners) {
			listener.articleUpdated(article);
		}
		return article;
	}

	@Override
	public Article removeArticle(Integer id) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateArticle article = (HibernateArticle) session.get(HibernateArticle.class, id);
		if (article == null) {
			return null;
		}
		session.delete(article);

		for (ArticleListener listener : listeners) {
			listener.articleRemoved(article);
		}
		return article;
	}

	private void updateArticle(HibernateArticle article, ArticleEditor editor) {
		article.setName(editor.getName());
		article.setDescription(editor.getDescription());
		article.setCategory(editor.getCategoryId());
		article.setPrice(editor.getPrice());
		article.setWeight(editor.getWeight());
		article.setRestockInfo(editor.getStoreAvailable(), editor.getRestockDate());
		article.setPreviewImageId(editor.getPreviewImage());
		article.setImageIds(editor.getImageIds());
		article.setOptions(editor.getOptions());
		article.setProperties(editor.getProperties());
		article.setState(editor.getArticleState());
		article.setCommentary(editor.getCommentary());

		final HibernateSupplierInfo supplierInfo = article.getSupplierInfo();
		supplierInfo.setReferenceUri(editor.getReferenceUri());
		supplierInfo.setReferenceCode(editor.getReferenceCode());
		supplierInfo.setWholesaler(editor.getWholesaler());
		supplierInfo.setPrice(editor.getSupplierPrice());
	}

	@Override
	public void updateSold(Integer id, int quantity) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("update billiongoods.server.warehouse.impl.HibernateArticle a set a.stockInfo.sold=a.stockInfo.sold+:quantity where a.id=:id");
		query.setParameter("id", id);
		query.setParameter("quantity", quantity);
		query.executeUpdate();
	}

	@Override
	public void updatePrice(Integer id, Price price, Price supplierPrice) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("update billiongoods.server.warehouse.impl.HibernateArticle a " +
				"set " +
				"a.price.amount=:priceAmount, a.price.primordialAmount=:pricePrimordialAmount, " +
				"a.supplierInfo.price.amount=:supplierAmount, a.supplierInfo.price.primordialAmount=:supplierPrimordialAmount, " +
				"a.supplierInfo.validationDate=:validationDate " +
				"where a.id=:id");

		query.setParameter("id", id);
		query.setParameter("priceAmount", price.getAmount());
		query.setParameter("pricePrimordialAmount", price.getPrimordialAmount());
		query.setParameter("supplierAmount", supplierPrice.getAmount());
		query.setParameter("supplierPrimordialAmount", supplierPrice.getPrimordialAmount());
		query.setParameter("validationDate", new Date());
		query.executeUpdate();
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

			if (context.getArticleStates() != null) {
				criteria.add(Restrictions.in("state", context.getArticleStates()));
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

	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}
}
