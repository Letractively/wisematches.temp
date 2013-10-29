package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.coupon.Coupon;
import billiongoods.server.services.coupon.CouponManager;
import billiongoods.server.services.coupon.ReferenceType;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.warehouse.ProductPreview;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.CouponForm;
import org.codehaus.jackson.map.util.ISO8601Utils;
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

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/coupon")
public class CouponMaintainController extends AbstractController {
	private CouponManager couponManager;
	private ProductManager productManager;

	public CouponMaintainController() {
	}

	@RequestMapping(value = "view")
	public String viewCoupon(@RequestParam(value = "code", required = false) String code, Model model) {
		if (code != null) {
			model.addAttribute("coupon", couponManager.getCoupon(code));
		}
		return "/content/maintain/coupon/view";
	}

	@RequestMapping(value = "/create")
	public String createCoupon(@ModelAttribute("form") CouponForm form, Model model) {
		if (form.getCode() == null) {
			form.setCode(generateCode());
		}
		return "/content/maintain/coupon/create";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String createCouponAction(@ModelAttribute("form") CouponForm form, Errors errors, Model model) {
		final Coupon coupon = couponManager.getCoupon(form.getCode());
		if (coupon != null) {
			errors.rejectValue("code", "error.coupon.exist");
		} else {
			final String formStarted = form.getStarted();
			final String formFinished = form.getFinished();
			final Date started = formStarted != null && !formStarted.isEmpty() ? ISO8601Utils.parse(formStarted) : null;
			final Date finished = formFinished != null && !formFinished.isEmpty() ? ISO8601Utils.parse(formFinished) : null;

			Coupon res = null;
			if (form.getReferenceType() == ReferenceType.CATEGORY) {
				final Category category = categoryManager.getCategory(form.getReferenceId());
				if (category == null) {
					errors.rejectValue("referenceId", "error.coupon.reference.category.unknown");
				} else {
					res = couponManager.createCoupon(form.getCode(), form.getAmount(), form.getCouponType(), category,
							form.getCount(), started, finished);
				}
			} else if (form.getReferenceType() == ReferenceType.PRODUCT) {
				final ProductPreview preview = productManager.getPreview(form.getReferenceId());
				if (preview == null) {
					errors.rejectValue("referenceId", "error.coupon.reference.preview.unknown");
				} else {
					res = couponManager.createCoupon(form.getCode(), form.getAmount(), form.getCouponType(), preview,
							form.getCount(), started, finished);
				}
			} else {
				errors.rejectValue("referenceType", "error.coupon.reference.type.unknown");
			}

			if (res != null) {
				return "redirect:/maintain/coupon/view?code=" + res.getCode();
			}
		}
		return "/content/maintain/coupon/create";
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
