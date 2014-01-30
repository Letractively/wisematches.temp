package billiongoods.server.web.servlet.mvc.privacy;

import billiongoods.core.Member;
import billiongoods.server.services.wishlist.WishlistManager;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.warehouse.ProductPreview;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.privacy.form.WishlistForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/privacy/wishlist")
public class WishlistController extends AbstractController {
	private ProductManager productManager;
	private WishlistManager wishlistManager;

	public WishlistController() {
	}

	@RequestMapping("")
	public String privacy(Model model) {
		final List<Integer> integers = wishlistManager.searchEntities(getMember(), null, null, null);

		final List<ProductPreview> previews = productManager.getPreviews(integers.toArray(new Integer[integers.size()]));
		model.addAttribute("products", previews);
		return "/content/privacy/wishlist";
	}

	@RequestMapping("/add.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse addProductTracking(@RequestBody WishlistForm form, Locale locale) {
		final Member member = getMember();
		if (member != null) {
			wishlistManager.addWishProducts(member, form.getProduct());
			return responseFactory.success();
		}
		return responseFactory.failure("error.unregistered", locale);
	}

	@RequestMapping("/remove.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse removeProductTracking(@RequestBody WishlistForm form, Locale locale) {
		final Member member = getMember();
		if (member != null) {
			wishlistManager.removeWishProducts(member, form.getProduct());
			return responseFactory.success();
		}
		return responseFactory.failure("error.unregistered", locale);
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	@Autowired
	public void setWishlistManager(WishlistManager wishlistManager) {
		this.wishlistManager = wishlistManager;
	}
}
