package billiongoods.server.services.account;

import billiongoods.core.account.Account;
import billiongoods.core.account.AccountListener;
import billiongoods.core.account.AccountManager;
import billiongoods.server.services.coupon.CouponManager;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.tracking.ProductTrackingManager;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class NewcomerProcessingCenter {
	private OrderManager orderManager;
	private CouponManager couponManager;
	private AccountManager accountManager;
	private ProductTrackingManager trackingManager;

	private static final long EXPIRATION_DATE = 2592000000L; // 30 days

	private final TheAccountListener accountListener = new TheAccountListener();

	public NewcomerProcessingCenter() {
	}

	public void importAccountData(Account account) {
		final int ordersCount = orderManager.importAccountOrders(account);

		final int trackingCount = trackingManager.importAccountTracking(account);

//		couponManager.createCoupon("nb" + account.getId(), 3d, CouponAmountType.PERCENT, 1, new Date(System.currentTimeMillis() + EXPIRATION_DATE));
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public void setCouponManager(CouponManager couponManager) {
		this.couponManager = couponManager;
	}

	public void setTrackingManager(ProductTrackingManager trackingManager) {
		this.trackingManager = trackingManager;
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

	private final class TheAccountListener implements AccountListener {
		private TheAccountListener() {
		}

		@Override
		public void accountCreated(Account account) {
			importAccountData(account);
		}

		@Override
		public void accountUpdated(Account oldAccount, Account newAccount) {
			importAccountData(newAccount);
		}

		@Override
		public void accountRemove(Account account) {
		}
	}
}
