package billiongoods.server.services.validator.impl;

import billiongoods.server.services.validator.ValidationChange;
import billiongoods.server.services.validator.ValidationSummary;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ReusableValidationSummary implements ValidationSummary {
	private volatile Date startDate;
	private volatile Date finishDate;

	private volatile int iteration = 0;

	private volatile int totalCount = 0;
	private volatile int brokenProducts = 0;
	private volatile int processedProducts = 0;

	private final Collection<ValidationChange> validations = new ConcurrentLinkedQueue<>();

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
	public int getBrokenProducts() {
		return brokenProducts;
	}

	@Override
	public int getUpdateProducts() {
		return validations.size();
	}

	@Override
	public int getProcessedProducts() {
		return processedProducts;
	}

	void incrementBroken() {
		brokenProducts++;
	}

	void incrementProcessed() {
		processedProducts++;
	}

	void registerValidation(ValidationChange validation) {
		validations.add(validation);
	}

	void initialize(Date date, int totalCount) {
		this.startDate = date;
		this.finishDate = null;

		this.iteration = 0;

		this.totalCount = totalCount;
		this.brokenProducts = 0;
		this.processedProducts = 0;

		validations.clear();
	}

	@Override
	public Collection<ValidationChange> getValidationChanges() {
		return validations;
	}

	@Override
	public int getIteration() {
		return iteration;
	}

	void incrementIteration(int broken) {
		iteration++;

		brokenProducts -= broken;
		processedProducts -= broken;
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
		sb.append(", validations=").append(validations);
		sb.append('}');
		return sb.toString();
	}
}
