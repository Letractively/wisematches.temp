package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.core.search.Order;
import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import billiongoods.server.services.coupon.*;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.warehouse.ProductPreview;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.CouponForm;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/coupon")
public class CouponMaintainController extends AbstractController {
	private CouponManager couponManager;
	private ProductManager productManager;

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

	public CouponMaintainController() {
	}

	@RequestMapping(value = "/view")
	public String viewCoupon(@RequestParam(value = "code", required = false) String code, Model model) {
		final Coupon coupon = couponManager.getCoupon(code);
		model.addAttribute("coupon", coupon);
		return "/content/maintain/coupon/view";
	}

	@RequestMapping(value = "/search")
	public String viewCoupon(@ModelAttribute("form") CouponForm form, Model model) {
		if (form.getCode() != null && !form.getCode().isEmpty()) {
			final Coupon coupon = couponManager.getCoupon(form.getCode());
			if (coupon != null) {
				model.addAttribute("coupons", Collections.singleton(coupon));
			} else {
				model.addAttribute("coupons", Collections.<Coupon>emptyList());
			}
		} else if (form.getReference() != null && form.getReferenceType() != null) {
			final CouponContext context = new CouponContext(form.getReference(), form.getReferenceType());
			final List<Coupon> coupons = couponManager.searchEntities(context, null, null, null);
			model.addAttribute("coupons", coupons);
		} else {
			final List<Coupon> coupons = couponManager.searchEntities(null, null, Range.limit(10), Orders.of(Order.desc("creation")));
			model.addAttribute("coupons", coupons);
		}

		if (form.getReferenceType() == null) {
			form.setReferenceType(CouponReferenceType.EVERYTHING);
		}
		return "/content/maintain/coupon/search";
	}

	@RequestMapping(value = "/create")
	public String createCoupon(@ModelAttribute("form") CouponForm form, Model model) {
		if (form.getCode() == null) {
			form.setCode(generateCode());
		}
		form.setAmount(5.);
		form.setAllocatedCount(1);
		form.setAmountType(CouponAmountType.PERCENT);
		form.setReferenceType(CouponReferenceType.EVERYTHING);
		return "/content/maintain/coupon/create";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String createCouponAction(@ModelAttribute("form") CouponForm form, Errors errors, Model model) {
		final Coupon coupon = couponManager.getCoupon(form.getCode());
		if (coupon != null) {
			errors.rejectValue("code", "error.coupon.exist");
		} else {
			Date termination = null;
			try {
				final String formFinished = form.getTermination();
				if (formFinished != null && !formFinished.isEmpty()) {
					termination = DATE_FORMAT.parse(formFinished);
				}
			} catch (ParseException ignore) {
			}

			Coupon res = null;
			if (form.getReferenceType() == CouponReferenceType.EVERYTHING) {
				res = couponManager.createCoupon(form.getCode(), form.getAmount(), form.getAmountType(), form.getAllocatedCount(), termination);
			} else if (form.getReference() == null) {
				errors.rejectValue("reference", "error.coupon.reference.unknown");
			} else {
				if (form.getReferenceType() == CouponReferenceType.CATEGORY) {
					final Category category = categoryManager.getCategory(form.getReference());
					if (category == null) {
						errors.rejectValue("referenceId", "error.coupon.reference.category.unknown");
					} else {
						res = couponManager.createCoupon(form.getCode(), form.getAmount(), form.getAmountType(), category, form.getAllocatedCount(), termination);
					}
				} else if (form.getReferenceType() == CouponReferenceType.PRODUCT) {
					final ProductPreview preview = productManager.getPreview(form.getReference());
					if (preview == null) {
						errors.rejectValue("referenceId", "error.coupon.reference.preview.unknown");
					} else {
						res = couponManager.createCoupon(form.getCode(), form.getAmount(), form.getAmountType(), preview, form.getAllocatedCount(), termination);
					}
				} else {
					errors.rejectValue("referenceType", "error.coupon.reference.type.unknown");
				}
			}

			if (res != null) {
				return "redirect:/maintain/coupon/view?code=" + res.getCode();
			}
		}
		return "/content/maintain/coupon/create";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/close", method = RequestMethod.POST)
	public String closeCouponAction(@RequestParam("code") String code) {
		final Coupon coupon = couponManager.getCoupon(code);
		if (coupon != null) {
			couponManager.closeCoupon(code);
		}
		return "redirect:/maintain/coupon/view?code=" + code;
	}

	private String generateCode() {
		String code = null;
		while (code == null || couponManager.getCoupon(code) != null) {
			code = Integer.toHexString(Float.floatToIntBits((float) Math.random()));
		}
		return code;
	}

	@Autowired
	public void setCouponManager(CouponManager couponManager) {
		this.couponManager = couponManager;
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}
}
