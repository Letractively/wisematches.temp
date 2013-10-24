package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.coupon.CouponManager;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.CouponModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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

	@RequestMapping(value = "")
	public String viewCoupons(Model model) {


		return "/content/maintain/coupon/view";
	}

	@RequestMapping(value = "/create")
	public String createCoupons(@ModelAttribute("form") CouponModel form, Model model) {
		if (form.getCode() == null) {
			form.setCode(generateCode());
		}
		return "/content/maintain/coupon/create";
	}

	private String generateCode() {
		String code = null;
		while (code == null || couponManager.getCoupon(code) != null) {
			code = Long.toHexString(Double.doubleToLongBits(Math.random()));
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
