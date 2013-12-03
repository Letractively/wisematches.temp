package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.core.search.Orders;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderContext;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.payment.OrderState;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.maintain.form.OrderStateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
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
	public String viewOrder(@RequestParam("id") String id, @RequestParam("type") String type, @ModelAttribute("form") OrderStateForm form, Model model) {
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
		form.setState(order.getOrderState());
		form.setCommentary(order.getCommentary());

		model.addAttribute("order", order);
		return "/content/maintain/order";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "promote", method = RequestMethod.POST)
	public String promoteOrder(@ModelAttribute("form") OrderStateForm form, Errors errors, Model model) {
		final Long id = form.getId();
		final String value = form.getValue();
		final String comment = form.getCommentary();
		final OrderState state = form.getState();

		if (id == null) {
			errors.rejectValue("value", "order.state.id.empty");
		}
		if (state == null) {
			errors.rejectValue("value", "order.state.state.empty");
		}

		if (state != null && !errors.hasErrors()) {
			switch (state) {
				case PROCESSING:
					orderManager.processing(id, value, comment);
					break;
				case SHIPPING:
					orderManager.shipping(id, value, comment);
					break;
				case SHIPPED:
					orderManager.shipped(id, value, comment);
					break;
				case SUSPENDED:
					try {
						orderManager.suspend(id, value != null && !value.isEmpty() ? SIMPLE_DATE_FORMAT.parse(value) : null, comment);
					} catch (Exception ex) {
						errors.rejectValue("value", "order.state.date.incorrect");
					}
					break;
				case CANCELLED:
					orderManager.cancel(id, value, comment);
					break;
				case CLOSED:
					try {
						orderManager.close(id, value != null && !value.isEmpty() ? SIMPLE_DATE_FORMAT.parse(value) : null, comment);
					} catch (Exception ex) {
						errors.rejectValue("value", "order.state.date.incorrect");
					}
					break;
				default:
					errors.rejectValue("value", "order.state.state.incorrect");
			}
		}

		if (!errors.hasErrors()) {
			return "redirect:/maintain/order/view?id=" + id + "&type=id";
		}

		model.addAttribute("order", orderManager.getOrder(id));
		return "/content/maintain/order";
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
}
