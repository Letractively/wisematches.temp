package billiongoods.server.services.price.impl;

import billiongoods.server.services.price.PriceBreakdown;
import billiongoods.server.services.price.PriceRenewal;
import billiongoods.server.services.price.ValidationSummary;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ReusableValidationSummary implements ValidationSummary {
	private volatile Date startDate;
	private volatile Date finishDate;
	private volatile int validatedProducts = 0;
	private final Collection<PriceRenewal> renewals = new ConcurrentLinkedQueue<>();
	private final Collection<PriceBreakdown> breakdowns = new ConcurrentLinkedQueue<>();

	public ReusableValidationSummary() {
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getFinishDate() {
		return finishDate;
	}

	@Override
	public int getValidatedProducts() {
		return validatedProducts;
	}

	@Override
	public Collection<PriceRenewal> getPriceRenewals() {
		return renewals;
	}

	@Override
	public Collection<PriceBreakdown> getPriceBreakdowns() {
		return breakdowns;
	}

	void incrementValidated() {
		validatedProducts++;
	}

	void addRenewal(PriceRenewal renewal) {
		renewals.add(renewal);
	}

	void addBreakdown(Date date, Integer productId, Exception exception) {
		breakdowns.add(new ThePriceBreakdown(date, productId, exception));
	}

	void finalize(Date finish) {
		this.finishDate = finish;
	}

	void initialize(Date date) {
		this.startDate = date;
		validatedProducts = 0;
		renewals.clear();
		breakdowns.clear();
	}


	private static final class ThePriceBreakdown implements PriceBreakdown {
		private final Date timestamp;
		private final Integer productId;
		private final Exception exception;

		private ThePriceBreakdown(Date timestamp, Integer productId, Exception exception) {
			this.timestamp = timestamp;
			this.productId = productId;
			this.exception = exception;
		}

		@Override
		public Exception getException() {
			return exception;
		}

		@Override
		public Date getTimestamp() {
			return timestamp;
		}

		@Override
		public Integer getProductId() {
			return productId;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("ThePriceBreakdown{");
			sb.append("timestamp=").append(timestamp);
			sb.append(", productId=").append(productId);
			sb.append(", exception=").append(exception);
			sb.append('}');
			return sb.toString();
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ReusableValidationSummary{");
		sb.append("startDate=").append(startDate);
		sb.append(", finishDate=").append(finishDate);
		sb.append(", validatedProducts=").append(validatedProducts);
		sb.append(", renewals=").append(renewals);
		sb.append(", breakdowns=").append(breakdowns);
		sb.append('}');
		return sb.toString();
	}
}
