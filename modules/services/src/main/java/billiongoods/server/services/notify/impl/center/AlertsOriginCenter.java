package billiongoods.server.services.notify.impl.center;


import billiongoods.core.account.Account;
import billiongoods.core.account.AccountListener;
import billiongoods.core.account.AccountManager;
import billiongoods.server.services.notify.NotificationService;
import billiongoods.server.services.notify.Recipient;
import billiongoods.server.services.notify.Sender;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderListener;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.payment.OrderState;
import billiongoods.server.services.price.PriceRenewal;
import billiongoods.server.services.price.PriceValidator;
import billiongoods.server.services.price.PriceValidatorListener;
import billiongoods.server.warehouse.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AlertsOriginCenter {
	private OrderManager orderManager;
	private AccountManager accountManager;
	private PriceValidator priceValidator;

	private NotificationService notificationService;

	private final TheOrderListener orderListener = new TheOrderListener();
	private final TheAccountListener accountListener = new TheAccountListener();
	private final ThePriceValidatorListener validatorListener = new ThePriceValidatorListener();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.alerts.OriginCenter");

	public AlertsOriginCenter() {
	}

	protected void raiseAlarm(String subj, String msg) {
		try {
			notificationService.raiseNotification(subj, Recipient.ALERTS_BOX, Sender.SUPPORT, msg);
		} catch (Exception ex) {
			log.error("Alerts can't be sent: subj=[{}], msg=[{}]", subj, msg);
		}
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
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

	public void setAccountManager(AccountManager accountManager) {
		if (this.accountManager != null) {
			this.accountManager.removeAccountListener(accountListener);
		}

		this.accountManager = accountManager;

		if (this.accountManager != null) {
			this.accountManager.addAccountListener(accountListener);
		}
	}

	public void setPriceValidator(PriceValidator priceValidator) {
		if (this.priceValidator != null) {
			this.priceValidator.removePriceValidatorListener(validatorListener);
		}

		this.priceValidator = priceValidator;

		if (this.priceValidator != null) {
			this.priceValidator.addPriceValidatorListener(validatorListener);
		}
	}

	private class TheOrderListener implements OrderListener {
		private TheOrderListener() {
		}

		@Override
		public void orderStateChange(Order order, OrderState oldState, OrderState newState) {
			if (newState == OrderState.ACCEPTED) {
				raiseAlarm("system.order", "New order created: " + order.getId() + "<br>" + order);
			}
		}
	}

	private class TheAccountListener implements AccountListener {
		private TheAccountListener() {
		}

		@Override
		public void accountCreated(Account account) {
			raiseAlarm("system.account", account.getUsername() + " (" + account.getEmail() + ")");
		}

		@Override
		public void accountRemove(Account account) {
		}

		@Override
		public void accountUpdated(Account oldAccount, Account newAccount) {
		}
	}

	private class ThePriceValidatorListener implements PriceValidatorListener {
		private ThePriceValidatorListener() {
		}

		@Override
		public void priceValidationStarted(Date date, int articlesCount) {
		}

		@Override
		public void priceValidated(Integer articleId, PriceRenewal renewal) {
		}

		@Override
		public void priceValidationFinished(Date date, int checkedArticled, List<PriceRenewal> renewals) {
			StringBuilder b = new StringBuilder();
			if (renewals.isEmpty()) {
				b.append("Проверенно ").append(checkedArticled).append(" товаров. Обновлений нет.");
			} else {
				b.append("Проверенно ").append(checkedArticled).append(" товаров.");
				b.append("<table>");
				b.append("<tr>");
				b.append("<th>Артикул</th><th>Старая цена</th><th>Новая цена</th><th>Изменение</th>" +
						"<th>Старая до скидки</th><th>Новая до скидки</th><th>Изменение</th>");
				for (PriceRenewal renewal : renewals) {
					b.append("<tr>");
					b.append("<td><a href=\"http://www.billiongoods.ru/warehouse/article/").append(renewal.getArticleId()).append("\">").append(renewal.getArticleId()).append("</a></td>");

					final Price oldPrice = renewal.getOldPrice();
					final Price oldSupplierPrice = renewal.getOldSupplierPrice();
					final Price newPrice = renewal.getNewPrice();
					final Price newSupplierPrice = renewal.getNewSupplierPrice();

					b.append("<td>").append(oldPrice.getAmount()).append("</td>");
					b.append("<td>").append(newPrice.getAmount()).append("</td>");
					b.append("<td>").append(newPrice.getAmount() - oldPrice.getAmount()).append("</td>");

					b.append("<td>").append(oldSupplierPrice.getAmount()).append("</td>");
					b.append("<td>").append(newSupplierPrice.getAmount()).append("</td>");
					b.append("<td>").append(newSupplierPrice.getAmount() - oldSupplierPrice.getAmount()).append("</td>");

					b.append("</tr>");
				}
				b.append("</tr>");
				b.append("</table>");
			}
			raiseAlarm("system.price", b.toString());
		}
	}
}
