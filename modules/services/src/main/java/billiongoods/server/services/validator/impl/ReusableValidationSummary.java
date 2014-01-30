package billiongoods.server.services.validator.impl;

import billiongoods.server.services.validator.ValidatingProduct;
import billiongoods.server.services.validator.ValidationChange;
import billiongoods.server.services.validator.ValidationSummary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ReusableValidationSummary implements ValidationSummary {
	private volatile Date startDate;
	private volatile Date finishDate;

	private volatile int iteration = 0;

	private volatile int totalCount = 0;
	private volatile int processedProducts = 0;

	private Collection<ValidatingProduct> brokenProducts = new ConcurrentLinkedQueue<>();
	private final Collection<ValidationChange> updatedProducts = new ConcurrentLinkedQueue<>();

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
	public int getTotalCount() {
		return totalCount;
	}

	@Override
	public int getProcessedProducts() {
		return processedProducts;
	}

	void incrementProcessed() {
		processedProducts++;
	}

	void registerBroken(ValidatingProduct product) {
		brokenProducts.add(product);
	}

	void registerValidation(ValidationChange validation) {
		updatedProducts.add(validation);
	}

	void initialize(Date date, int totalCount) {
		this.startDate = date;
		this.finishDate = null;

		this.iteration = 0;

		this.totalCount = totalCount;
		this.processedProducts = 0;

		brokenProducts.clear();
		updatedProducts.clear();
	}

	@Override
	public Collection<ValidationChange> getUpdatedProducts() {
		return updatedProducts;
	}

	@Override
	public Collection<ValidatingProduct> getBrokenProducts() {
		return brokenProducts;
	}

	@Override
	public int getIteration() {
		return iteration;
	}

	List<ValidatingProduct> startNextIteration() {
		iteration++;

		List<ValidatingProduct> res = new ArrayList<>(brokenProducts);
		brokenProducts.clear();
		processedProducts -= res.size();
		return res;
	}

	void finalize(Date finish) {
		this.finishDate = finish;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ReusableValidationSummary{");
		sb.append("startDate=").append(startDate);
		sb.append(", finishDate=").append(finishDate);
		sb.append(", iteration=").append(iteration);
		sb.append(", totalCount=").append(totalCount);
		sb.append(", brokenProducts=").append(brokenProducts);
		sb.append(", processedProducts=").append(processedProducts);
		sb.append('}');
		return sb.toString();
	}
}
