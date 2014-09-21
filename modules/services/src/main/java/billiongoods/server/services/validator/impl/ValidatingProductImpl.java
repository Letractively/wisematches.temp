package billiongoods.server.services.validator.impl;

import billiongoods.server.services.validator.ValidatingProduct;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.StockInfo;
import billiongoods.server.warehouse.SupplierInfo;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ValidatingProductImpl implements ValidatingProduct {
    private final Integer id;
    private final String name;
    private final Price price;
    private final StockInfo stockInfo;
    private final SupplierInfo supplierInfo;

    ValidatingProductImpl(Integer id, String name, Price price, StockInfo stockInfo, SupplierInfo supplierInfo) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockInfo = stockInfo;
        this.supplierInfo = supplierInfo;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Price getPrice() {
        return price;
    }

    @Override
    public StockInfo getStockInfo() {
        return stockInfo;
    }

    @Override
    public SupplierInfo getSupplierInfo() {
        return supplierInfo;
    }

    @Override
    public String toString() {
        return "ValidatingProductImpl{" + "id=" + id + ", name=" + name + ", price=" + price + ", stockInfo=" + stockInfo + ", supplierInfo=" + supplierInfo + '}';
    }
}