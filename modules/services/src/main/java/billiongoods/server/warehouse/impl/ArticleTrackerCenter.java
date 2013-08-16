package billiongoods.server.warehouse.impl;

import billiongoods.server.services.payment.*;
import billiongoods.server.warehouse.ArticleManager;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ArticleTrackerCenter {
	private OrderManager orderManager;
	private ArticleManager articleManager;

	private final OrderListener orderListener = new TheOrderListener();

	public ArticleTrackerCenter() {
	}

	private void updateSoldQuantity(List<OrderItem> items) {
		for (OrderItem item : items) {
			updateSoldQuantity(item.getArticle(), item.getQuantity());
		}
	}

	private void updateSoldQuantity(Integer id, int quantity) {
		articleManager.updateSold(id, quantity);
	}

	public void setOrderManager(OrderManager orderManager) {
		if (this.orderManager != null) {
			this.orderManager.removeOrderListener(orderListener);
		}

		this.orderManager = orderManager;

		if (this.orderManager != null) {
			this.orderManager.addOrderListener(orderListener);
		}
	}

	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}

	private class TheOrderListener implements OrderListener {
		private TheOrderListener() {
		}

		@Override
		public void orderStateChange(Order order, OrderState oldState, OrderState newState) {
			if (newState == OrderState.PROCESSING) {
				updateSoldQuantity(order.getOrderItems());
			}
		}
	}
}
