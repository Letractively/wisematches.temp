select count(*) as y0_
from
store_product this_ inner join store_product_property props1_
on this_.id=props1_.productId
where this_.categoryId=? and this_.state in (?, ?) and this_.price>=? and this_.price<=?

select props1_.attributeId as y0_, props1_.svalue as y1_, count(*) as y2_, min(this_.price) as y3_, max(this_.price) as y4_ from store_product this_ inner join store_product_property props1_ on this_.id=props1_.productId where this_.categoryId=? and this_.state in (?, ?) group by props1_.attributeId, props1_.svalue

select this_.id as id1_21_0_, this_.categoryId as category2_21_0_, this_.comment as comment3_21_0_, this_.name as name4_21_0_, this_.previewImageId as previewI5_21_0_, this_.price as price6_21_0_, this_.primordialPrice as primordi7_21_0_, this_.registrationDate as registra8_21_0_, this_.state as state9_21_0_, this_.stockAvailable as stockAv10_21_0_, this_.restockDate as restock11_21_0_, this_.stockSold as stockSo12_21_0_, this_.buyPrice as buyPric13_21_0_, this_.buyPrimordialPrice as buyPrim14_21_0_, this_.referenceCode as referen15_21_0_, this_.referenceUri as referen16_21_0_, this_.validationDate as validat17_21_0_, this_.wholesaler as wholesa18_21_0_, this_.weight as weight19_21_0_ from store_product this_ inner join store_product_property props1_ on this_.id=props1_.productId where this_.categoryId=? and this_.state in (?, ?) and this_.price>=? and this_.price<=? order by this_.stockSold desc, this_.id asc limit ?
