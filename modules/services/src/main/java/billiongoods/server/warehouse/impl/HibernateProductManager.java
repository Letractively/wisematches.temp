package billiongoods.server.warehouse.impl;

import billiongoods.core.search.Orders;
import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.warehouse.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateProductManager extends EntitySearchManager<ProductDescription, ProductContext, ProductFilter> implements ProductManager {
	private AttributeManager attributeManager;

	private final Collection<ProductListener> listeners = new CopyOnWriteArrayList<>();

	private static final int ONE_WEEK_MILLIS = 1000 * 60 * 60 * 24 * 7;

	public HibernateProductManager() {
		super(HibernateProductDescription.class);
	}

	@Override
	public void addProductListener(ProductListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removeProductListener(ProductListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public Product getProduct(Integer id) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateProduct product = (HibernateProduct) session.get(HibernateProduct.class, id);
		if (product != null) {
			product.initialize(attributeManager);
		}
		return product;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public Product getProduct(String sku) {
		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateProduct a where a.supplierInfo.referenceCode=:code");
		query.setParameter("code", sku);
		final List list = query.list();
		if (list.size() > 0) {
			final HibernateProduct product = (HibernateProduct) list.get(0);
			product.initialize(attributeManager);
			return product;
		}
		return null;
	}

	@Override
	public boolean hasProduct(Integer productId) {
		if (productId == null) {
			return false;
		}
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("select 1 from billiongoods.server.warehouse.impl.HibernateProduct where id=:pid");
		query.setParameter("pid", productId);
		return query.uniqueResult() != null;
	}

	@Override
	public ProductDescription getDescription(Integer id) {
		final Session session = sessionFactory.getCurrentSession();
		return (HibernateProductDescription) session.get(HibernateProductDescription.class, id);
	}

	@Override
	public SupplierInfo getSupplierInfo(Integer id) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("select p.supplierInfo from billiongoods.server.warehouse.impl.HibernateProduct p where p.id=:id");
		query.setCacheable(true);
		query.setParameter("id", id);
		return (SupplierInfo) query.uniqueResult();
	}

	@Override
	public FilteringAbility getFilteringAbility(ProductContext context, ProductFilter filter) {
		final Session session = sessionFactory.getCurrentSession();

		Criteria countCriteria = session.createCriteria(HibernateProductDescription.class);
		applyRestrictions(countCriteria, context, null);

		final ProjectionList countProjection = Projections.projectionList();
		countProjection.add(Projections.rowCount());
		countProjection.add(Projections.min("price.amount"));
		countProjection.add(Projections.max("price.amount"));
		countCriteria.setProjection(countProjection);

		final Object[] countResult = (Object[]) countCriteria.uniqueResult();
		int totalCount = ((Number) countResult[0]).intValue();
		double minPrice = countResult[1] != null ? ((Number) countResult[1]).doubleValue() : 0;
		double maxPrice = countResult[2] != null ? ((Number) countResult[2]).doubleValue() : 10000;

		final Criteria criteria = session.createCriteria(HibernateProductDescription.class, "product");
		applyRestrictions(criteria, context, null);
		applyProjections(criteria, context, null);

		final ProjectionList projection = Projections.projectionList();
		projection.add(Projections.groupProperty("props.attributeId"));
		projection.add(Projections.groupProperty("props.sValue"));
		projection.add(Projections.rowCount());

		criteria.createAlias("product.propertyIds", "props").setProjection(projection);

		Map<Attribute, List<FilteringSummary>> attributeListMap = new HashMap<>();
		final List list = criteria.list();
		for (Object o : list) {
			Object[] oo = (Object[]) o;

			final Attribute attribute = attributeManager.getAttribute((Integer) oo[0]);

			List<FilteringSummary> filteringSummaries = attributeListMap.get(attribute);
			if (filteringSummaries == null) {
				filteringSummaries = new ArrayList<>();
				attributeListMap.put(attribute, filteringSummaries);
			}

			filteringSummaries.add(new FilteringSummary((String) oo[1], ((Number) oo[2]).intValue()));
		}
		int filteredCount = getTotalCount(context, filter);
		return new DefaultFilteringAbility(totalCount, filteredCount, minPrice, maxPrice, attributeListMap);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Product createProduct(ProductEditor editor) {
		final HibernateProduct product = new HibernateProduct();
		updateProduct(product, editor);

		final Session session = sessionFactory.getCurrentSession();
		session.save(product);

		for (ProductListener listener : listeners) {
			listener.productCreated(product);
		}
		return product;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Product updateProduct(Integer id, ProductEditor editor) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateProduct product = (HibernateProduct) session.get(HibernateProduct.class, id);
		if (product == null) {
			return null;
		}
		final Set<String> strings = updateProduct(product, editor);
		session.update(product);

		for (ProductListener listener : listeners) {
			listener.productUpdated(product, strings);
		}
		return product;
	}

	@Override
	public Product removeProduct(Integer id) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateProduct product = (HibernateProduct) session.get(HibernateProduct.class, id);
		if (product == null) {
			return null;
		}
		session.delete(product);

		for (ProductListener listener : listeners) {
			listener.productRemoved(product);
		}
		return product;
	}

	private Set<String> updateProduct(HibernateProduct product, ProductEditor editor) {
		final Set<String> res = new HashSet<>();
		final StockInfo stockInfo = product.getStockInfo();

		if (product.getState() != editor.getProductState()) {
			res.add("state");
		}

		if ((editor.getRestockDate() == null && stockInfo.getRestockDate() != null) ||
				(editor.getRestockDate() != null && (stockInfo.getRestockDate() == null || !editor.getRestockDate().equals(stockInfo.getRestockDate())))) {
			res.add("restockDate");
		}

		product.setName(editor.getName());
		product.setDescription(editor.getDescription());
		product.setCategory(editor.getCategoryId());
		product.setPrice(editor.getPrice());
		product.setWeight(editor.getWeight());
		product.setRestockInfo(editor.getStoreAvailable(), editor.getRestockDate());
		product.setPreviewImageId(editor.getPreviewImage());
		product.setImageIds(editor.getImageIds());
		product.setOptions(editor.getOptions());
		product.setProperties(editor.getProperties());
		product.setState(editor.getProductState());
		product.setCommentary(editor.getCommentary());

		final HibernateSupplierInfo supplierInfo = product.getSupplierInfo();
		supplierInfo.setReferenceUri(editor.getReferenceUri());
		supplierInfo.setReferenceCode(editor.getReferenceCode());
		supplierInfo.setWholesaler(editor.getWholesaler());
		supplierInfo.setPrice(editor.getSupplierPrice());

		return res;
	}

	@Override
	public void updateSold(Integer id, int quantity) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("update billiongoods.server.warehouse.impl.HibernateProduct a set a.soldCount=a.soldCount+:quantity where a.id=:id");
		query.setParameter("id", id);
		query.setParameter("quantity", quantity);
		query.executeUpdate();
	}

	@Override
	public void validated(Integer id, Price price, Price supplierPrice, StockInfo stockInfo) {
		if (price == null && supplierPrice == null && stockInfo == null) {
			return;
		}

		final StringBuilder b = new StringBuilder("update billiongoods.server.warehouse.impl.HibernateProduct a set ");
		if (price != null) {
			b.append("a.price.amount=:priceAmount, a.price.primordialAmount=:pricePrimordialAmount, ");
		}
		if (supplierPrice != null) {
			b.append("a.supplierInfo.price.amount=:supplierAmount, a.supplierInfo.price.primordialAmount=:supplierPrimordialAmount, ");
		}
		if (stockInfo != null) {
			b.append("a.stockInfo.leftovers=:leftovers, a.stockInfo.restockDate=:restockDate, ");
		}
		b.append("a.supplierInfo.validationDate=:validationDate where a.id=:id");

		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery(b.toString());

		query.setParameter("id", id);
		query.setParameter("validationDate", new Date());

		if (price != null) {
			query.setParameter("priceAmount", price.getAmount());
			query.setParameter("pricePrimordialAmount", price.getPrimordialAmount());
		}
		if (supplierPrice != null) {
			query.setParameter("supplierAmount", supplierPrice.getAmount());
			query.setParameter("supplierPrimordialAmount", supplierPrice.getPrimordialAmount());
		}
		if (stockInfo != null) {
			query.setParameter("leftovers", stockInfo.getLeftovers());
			query.setParameter("restockDate", stockInfo.getRestockDate());
		}
		query.executeUpdate();
	}

	@Override
	protected void applyProjections(Criteria criteria, ProductContext context, ProductFilter filter) {
	}

	@Override
	protected void applyOrders(Criteria criteria, Orders orders) {
		super.applyOrders(criteria, orders);
		criteria.addOrder(Order.asc("id"));// always sort by id at the end
	}

	@Override
	protected void applyRestrictions(Criteria criteria, ProductContext context, ProductFilter filter) {
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

			if (context.getProductStates() != null) {
				criteria.add(Restrictions.in("state", context.getProductStates()));
			}

			if (context.isArrival()) {
				criteria.add(Restrictions.ge("registrationDate", new java.sql.Date(System.currentTimeMillis() - ONE_WEEK_MILLIS)));
			}

			if (context.getSearch() != null && !context.getSearch().trim().isEmpty()) {
				criteria.add(Restrictions.sqlRestriction("MATCH(name, description) AGAINST(? IN BOOLEAN MODE)", context.getSearch(), StringType.INSTANCE));
			}
		}

		if (filter != null) {
			if (filter.getMinPrice() != null) {
				criteria.add(Restrictions.ge("price.amount", filter.getMinPrice()));
			}
			if (filter.getMaxPrice() != null) {
				criteria.add(Restrictions.le("price.amount", filter.getMaxPrice()));
			}

			final Set<Attribute> attributes = filter.getAttributes();
			if (attributes != null && !attributes.isEmpty()) {
				final Criteria props = criteria.createAlias("propertyIds", "props");
				for (Attribute attribute : attributes) {
					props.add(Restrictions.and(
							Restrictions.eq("props.attributeId", attribute.getId()),
							Restrictions.in("props.sValue", filter.getValues(attribute))));
				}
			}
		}
	}

	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}
}
