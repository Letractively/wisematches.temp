package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.core.search.Orders;
import billiongoods.server.services.address.Address;
import billiongoods.server.services.payment.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.maintain.form.OrderChangeForm;
import billiongoods.server.web.servlet.mvc.maintain.form.OrderStateForm;
import billiongoods.server.web.servlet.mvc.maintain.form.ParcelForm;
import billiongoods.server.web.servlet.mvc.maintain.form.ParcelStateForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/order")
public class OrderMaintainController extends AbstractController {
	private OrderManager orderManager;

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");
	private static final Orders TIMESTAMP = Orders.of(billiongoods.core.search.Order.desc("timestamp"));

	public OrderMaintainController() {
	}

	@RequestMapping(value = "")
	public String viewOrders(@RequestParam(value = "state", defaultValue = "ACCEPTED") String state, Model model) {
		final EnumSet<OrderState> orderState = EnumSet.of(OrderState.valueOf(state));

		final List<Order> orders = orderManager.searchEntities(new OrderContext(orderState), null, null, TIMESTAMP);
		model.addAttribute("orders", orders);
		model.addAttribute("orderState", orderState);

		model.addAttribute("ordersSummary", orderManager.getOrdersSummary());

		return "/content/maintain/orders";
	}

	@RequestMapping(value = "view")
	public String viewOrder(@RequestParam("id") String id, @RequestParam(value = "type", required = false) String type, @ModelAttribute("form") OrderStateForm form, Model model) {
		Order order;
		if ("ref".equalsIgnoreCase(type)) {
			order = orderManager.getByReference(id);
		} else if ("token".equalsIgnoreCase(type)) {
			order = orderManager.getByToken(id);
		} else {
			order = orderManager.getOrder(Long.decode(id));
		}

		if (order == null) {
			throw new UnknownEntityException(id, "order");
		}

		form.setId(order.getId());
		form.setState(order.getState());
		form.setCommentary(order.getCommentary());

		model.addAttribute("order", order);
		return "/content/maintain/order";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "modify", method = RequestMethod.POST)
	public String processOrderModify(@ModelAttribute("form") OrderChangeForm form, Errors errors, Model model) {
		final Long orderId = form.getOrderId();

		final Integer[] items = form.getItems();
		final int[] quantities = form.getQuantities();

		if (items != null && quantities != null && items.length == quantities.length) {
			final OrderItemChange[] changes = new OrderItemChange[items.length];
			for (int i = 0; i < changes.length; i++) {
				changes[i] = new OrderItemChange(items[i], quantities[i]);
			}
			orderManager.modify(orderId, changes);
		}
		return "redirect:/maintain/order/view?id=" + orderId;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "action", method = RequestMethod.POST)
	public String processOrderAction(@ModelAttribute("form") ParcelStateForm form, Errors errors, Model model) {
		final Long orderId = form.getOrderId();
		final Long parcelId = form.getParcelId();
		final String value = form.getValue();
		final String comment = form.getCommentary();

		if ("REFUND".equalsIgnoreCase(form.getState())) {
			final int i = value.indexOf(":");
			final double amount = Double.parseDouble(value.substring(0, i));
			final String token = value.substring(i + 1);
			orderManager.refund(orderId, token, amount, comment);
		} else {
			if (parcelId == null) {
				final OrderState state = OrderState.valueOf(form.getState());
				switch (state) {
					case SUSPENDED:
						orderManager.suspend(orderId, comment);
						break;
					case CANCELLED:
						orderManager.cancel(orderId, comment);
						break;
				}
			} else {
				final ParcelState state = ParcelState.valueOf(form.getState());
				switch (state) {
					case SHIPPING:
						orderManager.shipping(orderId, parcelId, value, comment);
						break;
					case SHIPPED:
						orderManager.shipped(orderId, parcelId, value, comment);
						break;
					case SUSPENDED:
						orderManager.suspend(orderId, parcelId, LocalDate.parse(value, DateTimeFormatter.ISO_DATE).atStartOfDay(), comment);
						break;
					case CANCELLED:
						orderManager.cancel(orderId, parcelId, comment);
						break;
					case CLOSED:
						orderManager.close(orderId, parcelId, LocalDate.parse(value, DateTimeFormatter.ISO_DATE).atStartOfDay(), comment);
						break;
				}
			}
		}
		return "redirect:/maintain/order/view?id=" + orderId;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "updateParcel.ajax", method = RequestMethod.POST)
	public ServiceResponse createParcel(@RequestBody ParcelForm form) {
		final Long orderId = form.getOrderId();
		final Order order = orderManager.getOrder(orderId);
		if (order == null) {
			throw new UnknownEntityException(orderId, "order");
		}

		orderManager.process(orderId, new ParcelEntry(form.getNumber(), form.getItems()));

		return responseFactory.success();
	}

	@RequestMapping(value = "export")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public HttpEntity<byte[]> promoteOrder(@ModelAttribute("order") Long orderId, Errors errors, Model model) {
		final Order o = orderManager.getOrder(orderId);
		if (o == null) {
			throw new UnknownEntityException(orderId, "order");
		}

		final StringBuilder b = new StringBuilder();
		b.append("Buyer Country," + "Buyer Fullname," + "Product SKU," + "Quantity," + "Buyer Address 1," + "Buyer Address 2," + "Buyer State," + "Buyer City," + "Buyer Zip," + "Buyer Phone Number," + "Remark," + "Sale Record Id");
		b.append(System.getProperty("line.separator"));

		int recordId = 1;
		final Shipment s = o.getShipment();
		final Address a = s.getAddress();
		for (OrderItem i : o.getItems()) {
			b.append("\"RUSSIAN FEDERATION");
			b.append("\",\"");
			b.append(a.getFullName());
			b.append("\",\"");
			b.append(i.getProduct().getSupplierInfo().getReferenceCode());
			b.append("\",\"");
			b.append(i.getQuantity());
			b.append("\",\"");
			b.append(a.getLocation());
			b.append("\",\"");
			b.append(a.getRegion());
			b.append("\",\"");
			b.append(a.getCity());
			b.append("\",\"");
			b.append(a.getPostcode());
			b.append("\",\"");
			b.append("");
			b.append("\",\"");
			b.append("");
			b.append("\",\"");
			b.append(recordId++);
			b.append("\"");
			b.append(System.getProperty("line.separator"));
		}

		final byte[] bytes = b.toString().getBytes();

		final HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "csv"));
		header.set("Content-Disposition", "attachment; filename=bgorder_" + orderId + ".csv");
		header.setContentLength(bytes.length);
		return new HttpEntity<>(bytes, header);
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
}
