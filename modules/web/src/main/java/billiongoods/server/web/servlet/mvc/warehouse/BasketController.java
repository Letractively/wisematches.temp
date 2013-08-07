package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.services.basket.BasketManager;
import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.warehouse.form.BasketItemForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/basket")
public class BasketController extends AbstractController {
	private BasketManager basketManager;
	private ArticleManager articleManager;
	private AttributeManager attributeManager;

	public BasketController() {
	}

	@RequestMapping("")
	public String viewBasket(Model model) {
		hideNavigation(model);

		model.addAttribute("basket", basketManager.getBasket(getPrincipal()));

		return "/content/warehouse/basket";
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

			final ArticleDescription article = articleManager.getDescription(form.getArticle());
			if (article == null) {
				return responseFactory.failure("unknown.article", new Object[]{form.getArticle()}, locale);
			}

			final int quantity = form.getQuantity();
			if (quantity <= 0) {
				return responseFactory.failure("illegal.quantity", new Object[]{quantity}, locale);
			}

			basketManager.addBasketItem(getPrincipal(), article, options, quantity);

			return responseFactory.success();
		} catch (Exception ex) {
			return responseFactory.failure("internal.error", locale);
		}
	}

	@RequestMapping("remove.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse removeFromBasket(@RequestBody BasketItemForm form) {
		System.out.println(form);

		return responseFactory.success();
	}

	@Autowired
	public void setBasketManager(BasketManager basketManager) {
		this.basketManager = basketManager;
	}

	@Autowired
	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}

	@Autowired
	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}
}
