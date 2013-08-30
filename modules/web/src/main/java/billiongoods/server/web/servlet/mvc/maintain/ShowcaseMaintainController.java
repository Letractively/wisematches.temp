package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.showcase.ShowcaseManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/showcase")
public class ShowcaseMaintainController extends AbstractController {
	private ShowcaseManager showcaseManager;

	public ShowcaseMaintainController() {
	}

	@RequestMapping("")
	public String showcaseMainPage() {
		return "/content/maintain/showcase";
	}

	@RequestMapping(value = "/reload", method = RequestMethod.POST)
	public String reloadMainPage() {
		showcaseManager.reloadShowcase();
		return "redirect:/maintain/showcase";
	}

	@Autowired
	public void setShowcaseManager(ShowcaseManager showcaseManager) {
		this.showcaseManager = showcaseManager;
	}
}
