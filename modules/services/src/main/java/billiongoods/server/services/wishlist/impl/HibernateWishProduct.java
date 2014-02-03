package billiongoods.server.services.wishlist.impl;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "privacy_wishlist")
public class HibernateWishProduct {
	@EmbeddedId
	private Pk pk;

	public HibernateWishProduct() {
	}

	public HibernateWishProduct(Long person, Integer product) {
		this.pk = new Pk(person, product);
	}


	public Long getPerson() {
		return pk.person;
	}

	public Integer getProduct() {
		return pk.product;
	}

	@Embeddable
	public static class Pk implements Serializable {
		@Column(name = "person")
		private Long person;

		@Column(name = "product")
		private Integer product;

		public Pk() {
		}

		public Pk(Long person, Integer product) {
			this.person = person;
			this.product = product;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Pk)) return false;

			Pk pk = (Pk) o;

			if (!person.equals(pk.person)) return false;
			if (!product.equals(pk.product)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = person.hashCode();
			result = 31 * result + product.hashCode();
			return result;
		}
	}
}
