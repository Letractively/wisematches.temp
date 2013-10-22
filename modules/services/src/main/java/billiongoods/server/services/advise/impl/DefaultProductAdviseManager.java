package billiongoods.server.services.advise.impl;

import billiongoods.server.services.advise.ProductAdviseManager;
import billiongoods.server.warehouse.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultProductAdviseManager implements ProductAdviseManager, InitializingBean {
	private SessionFactory sessionFactory;
	private ProductManager productManager;
	private CategoryManager categoryManager;

	private final List<ProductDescription> recommendations = new ArrayList<>();

	public DefaultProductAdviseManager() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final Session session = sessionFactory.openSession();
		TransactionSynchronizationManager.bindResource(sessionFactory, session);
		try {
			reloadRecommendations();
		} finally {
			TransactionSynchronizationManager.unbindResource(sessionFactory);
			session.flush();
			session.close();
		}
	}

	@Override
	public void addRecommendation(Integer pid) {
		final ProductDescription description = productManager.getDescription(pid);
		if (description != null) {
			productManager.updateRecommendation(pid, true);
			recommendations.add(description);
		}
	}

	@Override
	public void removeRecommendation(Integer pid) {
		final ProductDescription description = productManager.getDescription(pid);
		if (description != null) {
			productManager.updateRecommendation(pid, false);
			recommendations.remove(description);
		}
	}

	@Override
	public List<ProductDescription> getRecommendations() {
		return recommendations;
	}

	@Override
	public List<ProductDescription> getRecommendations(Category category, int count) {
		List<ProductDescription> res = new ArrayList<>();

		for (Iterator<ProductDescription> iterator = recommendations.iterator(); iterator.hasNext() && res.size() < count; ) {
			final ProductDescription p = iterator.next();
			final Category ct = categoryManager.getCategory(p.getCategoryId());
			if (category == null || category.isAssignableFrom(ct)) {
				res.add(p);
			}
		}
		return res;
	}

	@Override
	public void reloadRecommendations() {
		recommendations.clear();

		final ProductContext ctx = new ProductContext(null, true, null, false, true, ProductContext.ACTIVE_ONLY, StockState.IN_STOCK);
		recommendations.addAll(productManager.searchEntities(ctx, null, null, null));
		Collections.shuffle(recommendations);
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	public void setCategoryManager(CategoryManager categoryManager) {
		this.categoryManager = categoryManager;
	}
}