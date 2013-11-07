package billiongoods.core.search;

import org.hibernate.Criteria;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class Orders implements Iterable<Order> {
	private final Order[] orders;

	private Orders(Order[] orders) {
		this.orders = orders;
	}

	public static Orders all(Order... all) {
		return new Orders(all);
	}

	public static Orders of(Order o) {
		return new Orders(new Order[]{o});
	}

	public static Orders of(Order o1, Order o2) {
		return new Orders(new Order[]{o1, o2});
	}

	public static Orders of(Order o1, Order o2, Order o3) {
		return new Orders(new Order[]{o1, o2, o3});
	}

	public static Orders of(Order o1, Order o2, Order o3, Order o4) {
		return new Orders(new Order[]{o1, o2, o3, o4});
	}

	public static Orders of(Order o1, Order... res) {
		Order[] orders1 = new Order[res.length + 1];
		orders1[0] = o1;
		System.arraycopy(res, 0, orders1, 1, res.length);
		return new Orders(orders1);
	}

	public Orders map(Map<String, String> maps) {
		int index = 0;
		final Order[] res = new Order[orders.length];
		for (Order order : orders) {
			final String s = maps.get(order.getPropertyName());
			if (s != null) {
				res[index] = order.isAscending() ? Order.asc(s) : Order.desc(s);
			} else {
				res[index] = order;
			}
			index++;
		}
		return Orders.all(res);
	}

	public String apply(String query) {
		StringBuilder b = new StringBuilder(query);
		apply(b);
		return b.toString();
	}

	public void apply(StringBuilder query) {
		if (orders.length != 0) {
			query.append(" order by ");
			for (Order order : orders) {
				query.append(order.getPropertyName()).append(" ").append(order.isAscending() ? "asc" : "desc").append(", ");
			}
			query.setLength(query.length() - 2); // remove last quote
		}
	}

	public void apply(Criteria criteria) {
		for (Order order : orders) {
			criteria.addOrder(order.isAscending() ?
					org.hibernate.criterion.Order.asc(order.getPropertyName()) :
					org.hibernate.criterion.Order.desc(order.getPropertyName()));
		}
	}

	public String toSqlOrders() {
		final StringBuilder res = new StringBuilder();
		apply(res);
		return res.toString();
	}

	@Override
	public Iterator<Order> iterator() {
		return new Iterator<Order>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < orders.length;
			}

			@Override
			public Order next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return orders[index++];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Not implemented");
			}
		};
	}

	@Override
	public String toString() {
		return Arrays.toString(orders);
	}
}
