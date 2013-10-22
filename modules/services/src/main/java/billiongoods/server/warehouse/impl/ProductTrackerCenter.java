package billiongoods.server.warehouse.impl;

import billiongoods.server.services.payment.*;
import billiongoods.server.warehouse.ProductManager;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ProductTrackerCenter {
	private OrderManager orderManager;
	private ProductManager productManager;

	private final OrderListener orderListener = new TheOrderListener();

	public ProductTrackerCenter() {
	}

	private void updateSoldQuantity(List<OrderItem> items) {
		for (OrderItem item : items) {
			updateSoldQuantity(item.getProduct().getId(), item.getQuantity());
		}
	}

	private void updateSoldQuantity(Integer id, int quantity) {
		productManager.updateSold(id, quantity);
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

	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	private class TheOrderListener implements OrderListener {
		private TheOrderListener() {
		}

		@Override
		public void orderStateChanged(Order order, OrderState oldState, OrderState newState) {
			if (newState == OrderState.PROCESSING) {
				updateSoldQuantity(order.getOrderItems());
			}
		}
	}
}
