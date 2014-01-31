-- Update prices
update store_product p right join service_validation v on p.id=v.productId 
set p.price=v.np, p.primordialPrice=v.npp, p.buyPrice=v.nsp, p.buyPrimordialPrice=v.nspp,
p.stockLeftovers=v.na, p.stockRestockDate=v.nrd, p.validationDate=v.timestamp where v.timestamp>'2013-11-09';