package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.Supplier;
import billiongoods.server.warehouse.SupplierInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class HibernateSupplierInfo implements SupplierInfo {
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "buyPrice")),
			@AttributeOverride(name = "primordialAmount", column = @Column(name = "buyPrimordialPrice"))
	})
	private Price price;

	@Column(name = "referenceId")
	private String referenceId;

	@Column(name = "referenceCode")
	private String referenceCode;

	@Column(name = "wholesaler")
	@Enumerated(EnumType.ORDINAL)
	private Supplier wholesaler;

	@Column(name = "validationDate")
	private Date validationDate;

	HibernateSupplierInfo() {
	}

	public HibernateSupplierInfo(String referenceId, String referenceCode, Supplier wholesaler, Price price) {
		this.price = price;
		this.referenceId = referenceId;
		this.referenceCode = referenceCode;
		this.wholesaler = wholesaler;
		this.validationDate = new Date();
	}

	@Override
	public Price getPrice() {
		return price;
	}

	@Override
	public String getReferenceId() {
		return referenceId;
	}

	@Override
	public String getReferenceCode() {
		return referenceCode;
	}

	@Override
	public Supplier getWholesaler() {
		return wholesaler;
	}

	@Override
	public Date getValidationDate() {
		return validationDate;
	}

	void setPrice(Price price) {
		this.price = price;
	}

	void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	void setWholesaler(Supplier wholesaler) {
		this.wholesaler = wholesaler;
	}

	void setValidationDate(Date validationDate) {
		this.validationDate = validationDate;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateSupplierInfo{");
		sb.append("price=").append(price);
		sb.append(", referenceId='").append(referenceId).append('\'');
		sb.append(", referenceCode='").append(referenceCode).append('\'');
		sb.append(", wholesaler=").append(wholesaler);
		sb.append(", validationDate=").append(validationDate);
		sb.append('}');
		return sb.toString();
	}
}
