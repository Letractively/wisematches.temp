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
	private volatile int validatedArticles = 0;
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
	public int getValidatedArticles() {
		return validatedArticles;
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
		validatedArticles++;
	}

	void addRenewal(PriceRenewal renewal) {
		renewals.add(renewal);
	}

	void addBreakdown(Date date, Integer articleId, Exception exception) {
		breakdowns.add(new ThePriceBreakdown(date, articleId, exception));
	}

	void finalize(Date finish) {
		this.finishDate = finish;
	}

	void initialize(Date date) {
		this.startDate = date;
		validatedArticles = 0;
		renewals.clear();
		breakdowns.clear();
	}


	private static final class ThePriceBreakdown implements PriceBreakdown {
		private final Date timestamp;
		private final Integer articleId;
		private final Exception exception;

		private ThePriceBreakdown(Date timestamp, Integer articleId, Exception exception) {
			this.timestamp = timestamp;
			this.articleId = articleId;
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
		public Integer getArticleId() {
			return articleId;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("ThePriceBreakdown{");
			sb.append("timestamp=").append(timestamp);
			sb.append(", articleId=").append(articleId);
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
		sb.append(", validatedArticles=").append(validatedArticles);
		sb.append(", renewals=").append(renewals);
		sb.append(", breakdowns=").append(breakdowns);
		sb.append('}');
		return sb.toString();
	}
}
