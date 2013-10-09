package billiongoods.server.services.validator.impl;

import billiongoods.server.services.validator.ProductValidation;
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
	private volatile int totalCount = 0;
	private volatile int brokenProducts = 0;
	private volatile int validatedProducts = 0;
	private final Collection<ProductValidation> validations = new ConcurrentLinkedQueue<>();

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
	public int getValidProducts() {
		return validations.size() - brokenProducts;
	}

	@Override
	public int getValidatedProducts() {
		return validatedProducts;
	}

	@Override
	public Collection<ProductValidation> getProductValidations() {
		return validations;
	}

	void incrementValidated() {
		validatedProducts++;
	}

	void addProductValidation(ProductValidation validation) {
		if (validation.getErrorMessage() != null) {
			brokenProducts++;
		}
		validations.add(validation);
	}

	void initialize(Date date, int totalCount) {
		this.startDate = date;
		this.finishDate = null;
		this.totalCount = totalCount;
		brokenProducts = 0;
		validatedProducts = 0;
		validations.clear();
	}

	void finalize(Date finish) {
		this.finishDate = finish;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ReusableValidationSummary{");
		sb.append("startDate=").append(startDate);
		sb.append(", finishDate=").append(finishDate);
		sb.append(", validatedProducts=").append(validatedProducts);
		sb.append(", validations=").append(validations);
		sb.append('}');
		return sb.toString();
	}
}
