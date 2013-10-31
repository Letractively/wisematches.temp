package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Personality;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.coupon.Coupon;
import billiongoods.server.services.coupon.CouponManager;
import billiongoods.server.services.payment.ShipmentManager;
import billiongoods.server.services.payment.ShipmentRates;
import billiongoods.server.services.payment.ShipmentType;
import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.warehouse.ProductPreview;
import billiongoods.server.warehouse.Property;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.warehouse.form.BasketItemForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderCheckoutForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/basket")
public class BasketController extends AbstractController {
	private CouponManager couponManager;
	private ProductManager productManager;
	private ShipmentManager shipmentManager;

	public static final String BASKET_PARAM = "BASKET";
	public static final String ORDER_CHECKOUT_FORM_PARAM = "ORDER_CHECKOUT_FORM";

	private static final CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();

	public BasketController() {
		super(true, false);
	}

	@RequestMapping(value = {""}, method = RequestMethod.GET)
	public String viewBasket(@ModelAttribute("order") OrderCheckoutForm form, Model model) {
		final Basket basket = basketManager.getBasket(getPrincipal());
		return prepareBasketView(basket, form, model);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST, params = "action=clear")
	public String processBasket(@ModelAttribute("order") OrderCheckoutForm form, Model model) {
		final Personality principal = getPrincipal();
		basketManager.closeBasket(principal);

		final Basket basket = basketManager.getBasket(principal);
		return prepareBasketView(basket, form, model);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST, params = "action=update")
	public String validateBasket(@ModelAttribute("order") OrderCheckoutForm form, Errors errors, Model model) {
		final Personality principal = getPrincipal();
		final Basket basket = validateBasket(principal, form, errors);
		return prepareBasketView(basket, form, model);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST, params = "action=rollback")
	public String rollbackBasket(@ModelAttribute("order") OrderCheckoutForm form, Model model) {
		return viewBasket(form, model);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST, params = "action=checkout")
	public String checkoutBasket(@ModelAttribute("order") OrderCheckoutForm form, Errors errors, Model model, WebRequest request) {
		final Personality principal = getPrincipal();
		final Basket basket = validateBasket(principal, form, errors);
		if (basket == null) {
			return prepareBasketView(null, form, model);
		}

		checkAddressField("name", form.getName(), errors);
		checkAddressField("city", form.getCity(), errors);
		checkAddressField("region", form.getRegion(), errors);
		checkAddressField("postalCode", form.getPostalCode(), errors);
		checkAddressField("streetAddress", form.getStreetAddress(), errors);

		if (!form.getPostalCode().matches("\\d{6}+")) {
			errors.rejectValue("postalCode", "basket.error.postalCode.format");
		}

		if (!errors.hasErrors()) {
			request.setAttribute(ORDER_CHECKOUT_FORM_PARAM, form, RequestAttributes.SCOPE_REQUEST);
			request.setAttribute(BASKET_PARAM, basket, RequestAttributes.SCOPE_REQUEST);
			return "forward:/warehouse/order/checkout";
		}
		return prepareBasketView(basket, form, model);
	}

	@RequestMapping("rollback")
	public String rollbackOrder(@ModelAttribute("order") OrderCheckoutForm form, Model model) {
		model.addAttribute("rollback", Boolean.TRUE);
		return viewBasket(form, model);
	}

	@RequestMapping("add.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse addToBasket(@RequestBody BasketItemForm form, Locale locale) {
		try {
			final List<Property> options = new ArrayList<>();

			final Integer[] optionIds = form.getOptionIds();
			final String[] optionValues = form.getOptionValues();
			if (optionIds != null) {
				for (int i = 0; i < optionIds.length; i++) {
					final Integer optionId = optionIds[i];
					if (optionId != null) {
						final Attribute attribute = attributeManager.getAttribute(optionId);
						if (attribute == null) {
							return responseFactory.failure("unknown.attribute", new Object[]{optionIds[i]}, locale);
						}
						options.add(new Property(attribute, optionValues[i]));
					}
				}
			}

			final int quantity = form.getQuantity();
			if (quantity <= 0) {
				return responseFactory.failure("illegal.quantity", new Object[]{quantity}, locale);
			}

			final ProductPreview product = productManager.getPreview(form.getProduct());
			if (product == null) {
				return responseFactory.failure("unknown.product", new Object[]{form.getProduct()}, locale);
			}

			final Personality principal = getPrincipal();

			final Basket basket = basketManager.getBasket(principal);
			if (basket != null) {
				final List<BasketItem> basketItems = basket.getBasketItems();
				for (BasketItem basketItem : basketItems) {
					if (basketItem.getProduct().getId().equals(product.getId()) && basketItem.getOptions().equals(options)) {
						basketManager.updateBasketItem(principal, basketItem.getNumber(), basketItem.getQuantity() + quantity);
						return responseFactory.success();
					}
				}
			}

			basketManager.addBasketItem(principal, product, options, quantity);
			return responseFactory.success();
		} catch (Exception ex) {
			return responseFactory.failure("internal.error", locale);
		}
	}

	private void checkAddressField(String name, String value, Errors errors) {
		if (value == null || value.trim().isEmpty() || !asciiEncoder.canEncode(value)) {
			errors.rejectValue(name, "basket.error." + name + ".empty");
		}
	}

	private Basket validateBasket(Personality principal, OrderCheckoutForm form, Errors errors) {
		Basket basket = basketManager.getBasket(principal);
		if (basket == null || basket.getBasketItems() == null) {
			return null;
		}

		final Set<Integer> numbers = new HashSet<>();
		for (BasketItem basketItem : basket.getBasketItems()) {
			numbers.add(basketItem.getNumber());
		}
		final Integer[] itemNumbers = form.getItemNumbers();
		if (itemNumbers == null || itemNumbers.length == 0) {
			basketManager.closeBasket(principal);
			return null;
		}
		for (Integer itemNumber : itemNumbers) {
			numbers.remove(itemNumber);
		}

		for (Integer number : numbers) {
			basketManager.removeBasketItem(principal, number);
		}

		for (int i = 0; i < itemNumbers.length; i++) {
			final Integer number = itemNumbers[i];
			final int quantity = form.getItemQuantities()[i];

			if (quantity != basket.getBasketItem(number).getQuantity()) {
				basketManager.updateBasketItem(principal, number, quantity);
			}
		}

		final ShipmentType shipmentType = form.getShipment();
		if (shipmentType == null) {
			errors.rejectValue("shipment", "basket.error.shipment.empty");
		}

		final String couponCode = form.getCoupon();
		if (couponCode != null && !couponCode.isEmpty()) {
			final Coupon coupon = couponManager.getCoupon(couponCode);
			if (coupon == null) {
				errors.rejectValue("coupon", "basket.error.coupon.empty");
			} else if (!coupon.isActive()) {
				errors.rejectValue("coupon", "basket.error.coupon.inactive");
			} else {
				if (!coupon.getId().equals(basket.getCoupon())) {
					basketManager.applyCoupon(principal, coupon);
				}
			}
		} else {
			if (basket.getCoupon() != null) {
				basketManager.applyCoupon(principal, null);
			}
		}
		return basketManager.getBasket(principal);
	}

	private String prepareBasketView(Basket basket, OrderCheckoutForm form, Model model) {
		model.addAttribute("basket", basket);

		if (basket != null) {
			final Coupon coupon = couponManager.getCoupon(basket.getCoupon());
			final ShipmentRates shipmentRates = shipmentManager.getShipmentRates(basket);

			form.setCoupon(coupon == null ? null : coupon.getCode());

			model.addAttribute("coupon", coupon);
			model.addAttribute("shipmentRates", shipmentRates);
		}
		return "/content/warehouse/basket/view";
	}

	@Autowired
	public void setCouponManager(CouponManager couponManager) {
		this.couponManager = couponManager;
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	@Autowired
	public void setShipmentManager(ShipmentManager shipmentManager) {
		this.shipmentManager = shipmentManager;
	}
}
