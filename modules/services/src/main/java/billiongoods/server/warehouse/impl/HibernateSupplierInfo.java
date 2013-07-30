package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Supplier;
import billiongoods.server.warehouse.SupplierInfo;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class HibernateSupplierInfo implements SupplierInfo {
    @Column(name = "buyPrice")
    private float price;

    @Column(name = "buyPrimordialPrice")
    private Float primordialPrice;

    @Column(name = "referenceId")
    private String referenceId;

    @Column(name = "referenceCode")
    private String referenceCode;

    @Column(name = "wholesaler")
    @Enumerated(EnumType.ORDINAL)
    private Supplier wholesaler;

    @Column(name = "validationDate")
    private Date validationDate;

    public HibernateSupplierInfo() {
    }

    public HibernateSupplierInfo(String referenceId, String referenceCode, Supplier wholesaler, float price, Float primordialPrice) {
        this.price = price;
        this.primordialPrice = primordialPrice;
        this.referenceId = referenceId;
        this.referenceCode = referenceCode;
        this.wholesaler = wholesaler;
        this.validationDate = new Date();
    }

    @Override
    public float getPrice() {
        return price;
    }

    @Override
    public Float getPrimordialPrice() {
        return primordialPrice;
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

    void validatePrices(float price, Float primordialPrice) {
        this.price = price;
        this.primordialPrice = primordialPrice;
        this.validationDate = new Date();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HibernateSupplierInfo{");
        sb.append("price=").append(price);
        sb.append(", primordialPrice=").append(primordialPrice);
        sb.append(", referenceId='").append(referenceId).append('\'');
        sb.append(", referenceCode='").append(referenceCode).append('\'');
        sb.append(", wholesaler=").append(wholesaler);
        sb.append(", validationDate=").append(validationDate);
        sb.append('}');
        return sb.toString();
    }
}
