package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.advise.ProductAdviseManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/recommends")
public class RecommendsController extends AbstractController {
	private ProductAdviseManager adviseManager;

	public RecommendsController() {
	}

	@RequestMapping("")
	public String showcaseMainPage(Model model) {
		model.addAttribute("recommendations", adviseManager.getRecommendations());
		return "/content/maintain/recommends";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/add.ajax")
	public ServiceResponse addRecommendation(@RequestParam("id") Integer pid, Locale locale) {
		adviseManager.addRecommendation(pid);
		return responseFactory.success();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/remove.ajax")
	public ServiceResponse removeRecommendation(@RequestParam("id") Integer pid, Locale locale) {
		adviseManager.removeRecommendation(pid);
		return responseFactory.success();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/reload", method = RequestMethod.POST)
	public String reloadMainPage() {
		adviseManager.reloadRecommendations();
		return "redirect:/maintain/recommends";
	}

	@Autowired
	public void setAdviseManager(ProductAdviseManager adviseManager) {
		this.adviseManager = adviseManager;
	}
}
