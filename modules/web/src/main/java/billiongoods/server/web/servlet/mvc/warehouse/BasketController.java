package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.services.basket.BasketManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.warehouse.form.BasketItemForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/basket")
public class BasketController extends AbstractController {
	private BasketManager basketManager;

	public BasketController() {
	}

	@RequestMapping("")
	public String viewBasket(Model model) {
		model.addAttribute("basket", basketManager.getBasket(getPrincipal()));

		return "/content/warehouse/basket";
	}


	@RequestMapping("add.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse addToBasket(@RequestBody BasketItemForm form) {
//		basketManager.addBasketItem(getPrincipal(), );

		return responseFactory.success();
	}

	@RequestMapping("remove.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse removeFromBasket(@RequestBody BasketItemForm form) {
		System.out.println(form);

		return responseFactory.success();
	}
}
