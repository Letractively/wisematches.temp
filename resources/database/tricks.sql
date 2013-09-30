-- Revert prices
update service_price_renewal r
left join store_product p on p.id=r.productId
set
p.price=r.oldPrice, p.primordialPrice=r.oldPrimordialPrice,
p.buyPrice=r.oldSupplierPrice, p.buyPrimordialPrice=r.oldSupplierPrimordialPrice
WHERE timestamp like '2013-09-28%';

