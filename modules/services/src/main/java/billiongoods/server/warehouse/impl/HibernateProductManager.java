package billiongoods.server.warehouse.impl;

import billiongoods.core.search.Orders;
import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.warehouse.*;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateProductManager extends EntitySearchManager<ProductPreview, ProductContext, ProductFilter> implements ProductManager {
	private AttributeManager attributeManager;

	private final Collection<ProductListener> listeners = new CopyOnWriteArrayList<>();
	private final Collection<ProductStateListener> stateListeners = new CopyOnWriteArrayList<>();

	private static final int ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;
	private static final int ONE_WEEK_MILLIS = ONE_DAY_MILLIS * 7;

	public HibernateProductManager() {
		super(HibernateProductPreview.class);
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
	public void addProductStateListener(ProductStateListener l) {
		if (l != null) {
			stateListeners.add(l);
		}
	}

	@Override
	public void removeProductStateListener(ProductStateListener l) {
		if (l != null) {
			stateListeners.remove(l);
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
	public Integer searchBySku(String sku) {
		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createQuery("select id from billiongoods.server.warehouse.impl.HibernateProductPreview a where a.supplierInfo.referenceCode=:sku");
		query.setParameter("sku", sku);
		final List list = query.list();
		if (list.size() > 0) {
			return (Integer) list.get(0);
		}
		return null;
	}

	@Override
	public ProductPreview getPreview(Integer id) {
		final Session session = sessionFactory.getCurrentSession();
		return (HibernateProductPreview) session.get(HibernateProductPreview.class, id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProductPreview> getPreviews(Integer... id) {
		if (id == null || id.length == 0) {
			return null;
		}
		final Session session = sessionFactory.getCurrentSession();

		final Criteria criteria = session.createCriteria(HibernateProductPreview.class);
		criteria.add(Restrictions.in("id", id));
		return criteria.list();
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
	@SuppressWarnings("unchecked")
	public Filtering getFilteringAbility(ProductContext context, ProductFilter filter) {
		final Session session = sessionFactory.getCurrentSession();

		Criteria countCriteria = session.createCriteria(HibernateProductPreview.class);
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

		final Criteria criteria = session.createCriteria(HibernateProductPreview.class, "product");
		applyRestrictions(criteria, context, null);
		applyProjections(criteria, context, null);

		final ProjectionList projection = Projections.projectionList();
		projection.add(Projections.groupProperty("props.attributeId")); //0
		projection.add(Projections.groupProperty("props.sValue")); // 1
		projection.add(Projections.groupProperty("props.bValue")); // 2
		projection.add(Projections.rowCount()); // 3
		projection.add(Projections.min("props.iValue")); // 4
		projection.add(Projections.max("props.iValue")); // 5

		criteria.createAlias("product.propertyIds", "props").setProjection(projection);

		final List<FilteringItem> items = new ArrayList<>();
		Map<Attribute, List<CountedValue>> attributeListMap = new HashMap<>();
		final List list = criteria.list();
		for (Object o : list) {
			Object[] oo = (Object[]) o;

			final Integer attributeId = ((Number) oo[0]).intValue();
			final Integer count = ((Number) oo[3]).intValue();

			final Attribute attribute = attributeManager.getAttribute(attributeId);
			final AttributeType type = attribute.getAttributeType();
			if (type == AttributeType.INTEGER) {
				final Integer min = oo[4] != null ? ((Number) oo[4]).intValue() : null;
				final Integer max = oo[5] != null ? ((Number) oo[5]).intValue() : null;
				items.add(new FilteringItem.Range(attribute,
						min != null ? new BigDecimal(min) : null,
						max != null ? new BigDecimal(max) : null));
			} else if (type == AttributeType.STRING) {
				final String sValue = (String) oo[1];
				List<CountedValue> filteringSummaries = attributeListMap.get(attribute);
				if (filteringSummaries == null) {
					filteringSummaries = new ArrayList<>();
					attributeListMap.put(attribute, filteringSummaries);
				}
				filteringSummaries.add(new CountedValue(count, sValue));
			} else if (type == AttributeType.BOOLEAN) {
				final Boolean bValue = (Boolean) oo[2];
				List<CountedValue> filteringSummaries = attributeListMap.get(attribute);
				if (filteringSummaries == null) {
					filteringSummaries = new ArrayList<>();
					attributeListMap.put(attribute, filteringSummaries);
				}
				filteringSummaries.add(new CountedValue(count, bValue));
			}
		}

		int filteredCount = getTotalCount(context, filter);
		for (Map.Entry<Attribute, List<CountedValue>> entry : attributeListMap.entrySet()) {
			final List<CountedValue> value = entry.getValue();
			Collections.sort(value);

			Map<Object, Integer> values = new LinkedHashMap<>(value.size());
			for (CountedValue v : value) {
				values.put(v.value, v.count);
			}
			items.add(new FilteringItem.Enum(entry.getKey(), values));
		}
		return new DefaultFiltering(totalCount, filteredCount, minPrice, maxPrice, items);
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

		final Price oldPrice = product.getPrice();
		final ProductState oldState = product.getState();
		final StockInfo oldStock = product.getStockInfo();

		updateProduct(product, editor);
		session.update(product);

		processProduceValidation(product, oldPrice, oldState, oldStock);

		for (ProductListener listener : listeners) {
			listener.productUpdated(product);
		}
		return product;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
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

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int updateDescriptions(String from, String to) {
		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createQuery("update billiongoods.server.warehouse.impl.HibernateProduct p set p.description=replace(p.description, :from, :to)");
		query.setString("to", to);
		query.setString("from", from);

		return query.executeUpdate();
	}

	private void updateProduct(HibernateProduct product, ProductEditor editor) {
		product.setName(editor.getName(), editor.getSymbolic());
		product.setDescription(editor.getDescription());
		product.setCategory(editor.getCategoryId());
		product.setPrice(editor.getPrice());
		product.setWeight(editor.getWeight());
		product.setStockInfo(new StockInfo(editor.getStoreAvailable(), editor.getRestockDate()));
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
	public void updateRecommendation(Integer id, boolean recommended) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("update billiongoods.server.warehouse.impl.HibernateProduct a set a.recommended=:recommended where a.id=:id");
		query.setParameter("id", id);
		query.setParameter("recommended", recommended);
		query.executeUpdate();
	}

	@Override
	public void updateProductInformation(Integer id, Price newPrice, Price newSupplierPrice, StockInfo newStockInfo) {
		if (newPrice == null && newSupplierPrice == null && newStockInfo == null) {
			return;
		}

		final Session session = sessionFactory.getCurrentSession();
		HibernateProductPreview product = (HibernateProductPreview) session.get(HibernateProductPreview.class, id);
		if (product == null) {
			return;
		}

		final Price oldPrice = product.getPrice();
		final ProductState oldState = product.getState();
		final StockInfo oldStock = product.getStockInfo();

		if (newPrice != null) {
			product.setPrice(newPrice);
		}

		if (newSupplierPrice != null) {
			product.getSupplierInfo().setPrice(newSupplierPrice);
		}

		if (newStockInfo != null) {
			product.setStockInfo(newStockInfo);
		}
		session.update(product);

		processProduceValidation(product, oldPrice, oldState, oldStock);
	}

	private void processProduceValidation(ProductPreview product, Price oldPrice, ProductState oldState, StockInfo oldStock) {
		if (oldPrice != null && !oldPrice.equals(product.getPrice())) {
			for (ProductStateListener validationListener : stateListeners) {
				validationListener.productPriceChanged(product, oldPrice, product.getPrice());
			}
		}
		if (oldState != null && !oldState.equals(product.getState())) {
			for (ProductStateListener validationListener : stateListeners) {
				validationListener.productStateChanged(product, oldState, product.getState());
			}
		}
		if (oldStock != null && !oldStock.equals(product.getStockInfo())) {
			for (ProductStateListener validationListener : stateListeners) {
				validationListener.productStockChanged(product, oldStock, product.getStockInfo());
			}
		}
	}

	@Override
	protected void applyProjections(Criteria criteria, ProductContext context, ProductFilter filter) {
	}

	@Override
	protected void applyOrders(Criteria criteria, ProductContext context, Orders orders) {
		if (context != null && context.getSearch() != null && !context.getSearch().trim().isEmpty() && orders == null) {
			criteria.addOrder(new SqlOrder(getMatchSentence(context), false));
		} else {
			super.applyOrders(criteria, context, orders);
		}
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

			if (context.getStockState() != null) {
				switch (context.getStockState()) {
					case IN_STOCK:
						criteria.add(Restrictions.isNull("stockInfo.leftovers"));
						criteria.add(Restrictions.isNull("stockInfo.restockDate"));
						break;
					case LIMITED_NUMBER:
						criteria.add(Restrictions.gt("stockInfo.leftovers", 0));
						break;
					case OUT_STOCK:
						criteria.add(Restrictions.isNotNull("stockInfo.restockDate"));
						break;
					case SOLD_OUT:
						criteria.add(Restrictions.eq("stockInfo.leftovers", 0));
						break;
				}
			}

			if (context.isArrival()) {
				criteria.add(Restrictions.ge("registrationDate", new java.sql.Date(System.currentTimeMillis() - ONE_WEEK_MILLIS)));
			}

			if (context.isOnlyRecommended()) {
				criteria.add(Restrictions.eq("recommended", Boolean.TRUE));
			}

			if (context.getSearch() != null && !context.getSearch().trim().isEmpty()) {
				criteria.add(Restrictions.sqlRestriction(getMatchSentence(context)));
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
				int index = 0;
				for (Attribute attribute : attributes) {
					String alias = "p" + (index++);
					DetachedCriteria props = DetachedCriteria.forClass(HibernateProductProperty.class, alias).setProjection(Projections.distinct(Projections.property(alias + ".productId")));
					final FilteringValue value = filter.getValue(attribute);
					if (value instanceof FilteringValue.Bool) {
						final FilteringValue.Bool v = (FilteringValue.Bool) value;
						final Boolean aBoolean = v.getValue();
						if (aBoolean != null) {
							props.add(Restrictions.and(
									Restrictions.eq(alias + ".attributeId", attribute.getId()),
									Restrictions.eq(alias + ".bValue", aBoolean)));
						}
					} else if (value instanceof FilteringValue.Enum) {
						final FilteringValue.Enum v = (FilteringValue.Enum) value;
						final Set<String> values = v.getValues();
						if (values != null) {
							props.add(Restrictions.and(
									Restrictions.eq(alias + ".attributeId", attribute.getId()),
									Restrictions.in(alias + ".sValue", values)));
						}
					} else if (value instanceof FilteringValue.Range) {
						final FilteringValue.Range v = (FilteringValue.Range) value;
						final BigDecimal min = v.getMin();
						final BigDecimal max = v.getMax();
						if (min != null && max != null) {
							props.add(Restrictions.and(
									Restrictions.eq(alias + ".attributeId", attribute.getId()),
									Restrictions.ge(alias + ".iValue", min),
									Restrictions.le(alias + ".iValue", max)));
						} else if (min != null) {
							props.add(Restrictions.and(
									Restrictions.eq(alias + ".attributeId", attribute.getId()),
									Restrictions.ge(alias + ".iValue", min)));
						} else if (max != null) {
							props.add(Restrictions.and(
									Restrictions.eq(alias + ".attributeId", attribute.getId()),
									Restrictions.le(alias + ".iValue", max)));
						}
					}
					criteria.add(Subqueries.propertyIn("id", props));
				}
			}
		}
	}

	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}

	private String getMatchSentence(ProductContext context) {
		return "MATCH(name, description) AGAINST(\"" + context.getSearch().replace("\"", "\\\"") + "\")";
	}

	private static final class SqlOrder extends Order {
		protected SqlOrder(String propertyName, boolean ascending) {
			super(propertyName, ascending);
		}

		@Override
		public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
			return getPropertyName() + (isAscending() ? " asc" : " desc");
		}
	}

	private static final class CountedValue implements Comparable<CountedValue> {
		private final int count;
		private final Object value;

		private CountedValue(int count, Object value) {
			this.count = count;
			this.value = value;
		}

		public int getCount() {
			return count;
		}

		public Object getValue() {
			return value;
		}

		@Override
		public int compareTo(CountedValue o) {
			return o.count - count;
		}
	}
}
