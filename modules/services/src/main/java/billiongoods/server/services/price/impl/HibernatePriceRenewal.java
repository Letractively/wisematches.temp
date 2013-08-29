package billiongoods.server.services.price.impl;

import billiongoods.server.services.price.PriceRenewal;
import billiongoods.server.warehouse.Price;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "service_price_renewal")
public class HibernatePriceRenewal implements PriceRenewal {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer articleId;

    private Date timestamp;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "oldPrice")),
            @AttributeOverride(name = "primordialAmount", column = @Column(name = "oldPrimordialPrice"))
    })
    private Price oldPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "oldSupplierPrice")),
            @AttributeOverride(name = "primordialAmount", column = @Column(name = "oldSupplierPrimordialPrice"))
    })
    private Price oldSupplierPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "newPrice")),
            @AttributeOverride(name = "primordialAmount", column = @Column(name = "newPrimordialPrice"))
    })
    private Price newPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "newSupplierPrice")),
            @AttributeOverride(name = "primordialAmount", column = @Column(name = "newSupplierPrimordialPrice"))
    })
    private Price newSupplierPrice;

    @Deprecated
    HibernatePriceRenewal() {
    }

    public HibernatePriceRenewal(Integer articleId, Date timestamp, Price oldPrice, Price oldSupplierPrice, Price newPrice, Price newSupplierPrice) {
        this.articleId = articleId;
        this.timestamp = timestamp;
        this.oldPrice = oldPrice;
        this.oldSupplierPrice = oldSupplierPrice;
        this.newPrice = newPrice;
        this.newSupplierPrice = newSupplierPrice;
    }

    @Override
    public Integer getArticleId() {
        return articleId;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public Price getOldPrice() {
        return oldPrice;
    }

    @Override
    public Price getOldSupplierPrice() {
        return oldSupplierPrice;
    }

    @Override
    public Price getNewPrice() {
        return newPrice;
    }

    @Override
    public Price getNewSupplierPrice() {
        return newSupplierPrice;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HibernatePriceRenewal{");
        sb.append("id=").append(id);
        sb.append(", articleId=").append(articleId);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", priceChange=").append(oldPrice).append("->").append(newPrice);
        sb.append(", supplierPriceChange=").append(oldSupplierPrice).append("->").append(newSupplierPrice);
        sb.append('}');
        return sb.toString();
    }
}
