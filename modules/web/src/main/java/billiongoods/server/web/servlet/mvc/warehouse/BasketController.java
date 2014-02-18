package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Member;
import billiongoods.core.Personality;
import billiongoods.server.services.address.AddressBook;
import billiongoods.server.services.address.AddressBookManager;
import billiongoods.server.services.address.AddressRecord;
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
import billiongoods.server.web.servlet.mvc.warehouse.form.BasketCheckoutForm;
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
	private AddressBookManager addressBookManager;

	public BasketController() {
		super(true, false);
	}

	@RequestMapping(value = {""}, method = RequestMethod.GET)
	public String viewBasket(@ModelAttribute("order") BasketCheckoutForm form, Model model) {
		final Basket basket = basketManager.getBasket(getPersonality());
		return prepareBasketView(basket, form, model);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST, params = "action=clear")
	public String processBasket(@ModelAttribute("order") BasketCheckoutForm form, Model model) {
		final Personality principal = getPersonality();
		basketManager.closeBasket(principal);

		final Basket basket = basketManager.getBasket(principal);
		return prepareBasketView(basket, form, model);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST, params = "action=update")
	public String validateBasket(@ModelAttribute("order") BasketCheckoutForm form, Errors errors, Model model) {
		final Personality principal = getPersonality();
		final Basket basket = validateBasket(principal, form, errors);
		return prepareBasketView(basket, form, model);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST, params = "action=rollback")
	public String rollbackBasket(@ModelAttribute("order") BasketCheckoutForm form, Model model) {
		return viewBasket(form, model);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST, params = "action=checkout")
	public String checkoutBasket(@ModelAttribute("order") BasketCheckoutForm form, Errors errors, Model model, WebRequest request) {
		final Personality principal = getPersonality();
		final Basket basket = validateBasket(principal, form, errors);
		if (basket == null) {
			return prepareBasketView(null, form, model);
		}

		AddressRecord address = null;
		if (form.isSelectionTab() && form.getId() != null && principal != null) {
			address = addressBookManager.getAddressBook(principal).getAddressRecord(form.getId());
		}

		if (address == null) {
			address = form.toAddressRecord();

			form.setSelectionTab(false);

			final Map<String, String> validate = form.validate();
			for (Map.Entry<String, String> entry : validate.entrySet()) {
				errors.rejectValue(entry.getKey(), entry.getValue());
			}
		}

		if (!errors.hasErrors()) {
			final Member member = getMember();
			if (!form.isSelectionTab() && form.isRemember() && member != null) {
				final AddressRecord addressRecord = addressBookManager.addAddress(member, new AddressRecord(address));
				if (addressRecord != null) {
					form.setSelectionTab(true);
					form.setId(addressRecord.getId());
				}
			}

			OrderCheckoutForm checkout = new OrderCheckoutForm(basket, address, form.getShipment(), form.isNotifications());
			request.setAttribute(OrderController.ORDER_CHECKOUT_FORM_NAME, checkout, RequestAttributes.SCOPE_REQUEST);
			return "forward:/warehouse/order/checkout";
		}
		return prepareBasketView(basket, form, model);
	}

	@RequestMapping("rollback")
	public String rollbackOrder(@ModelAttribute("order") BasketCheckoutForm form, Model model) {
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

			final Personality principal = getPersonality();
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

	private Basket validateBasket(Personality principal, BasketCheckoutForm form, Errors errors) {
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
				if (!coupon.getCode().equals(basket.getCoupon())) {
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

	private String prepareBasketView(Basket basket, BasketCheckoutForm form, Model model) {
		model.addAttribute("basket", basket);
		model.addAttribute("shipmentManager", shipmentManager);

		if (basket != null) {
			final Coupon coupon = couponManager.getCoupon(basket.getCoupon());
			final ShipmentRates shipmentRates = shipmentManager.getShipmentRates(basket);

			form.setCoupon(coupon == null ? null : coupon.getCode());

			model.addAttribute("coupon", coupon);
			model.addAttribute("shipmentRates", shipmentRates);

			final Member member = getMember();
			if (member != null) {
				final AddressBook addressBook = addressBookManager.getAddressBook(member);
				model.addAttribute("addressBook", addressBook);

				if (form.getId() == null) {
					final AddressRecord primary = addressBook.getPrimary();
					if (primary != null) {
						form.setId(primary.getId());
					}
				}
			}
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

	@Autowired
	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}
}
