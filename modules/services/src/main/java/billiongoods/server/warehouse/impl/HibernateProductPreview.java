package billiongoods.server.warehouse.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_product")
public class HibernateProductPreview extends AbstractProduct {
	public HibernateProductPreview() {
	}
}
