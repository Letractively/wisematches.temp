package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.showcase.ShowcaseManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.ShowcaseForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String showcaseMainPage(Model model) {
		model.addAttribute("showcase", showcaseManager.getShowcase());
		return "/content/maintain/showcase";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String createShowcaseItem(@ModelAttribute("form") ShowcaseForm form, Model model) {
		showcaseManager.createItem(form.getSection(), form.getPosition(), form.getName(), form.getCategory(), form.isArrival(), form.isSubCategories());
		return "redirect:/maintain/showcase";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	public String removeShowcaseItem(@RequestParam("section") Integer section, @RequestParam("position") Integer position) {
		showcaseManager.removeItem(section, position);
		return "redirect:/maintain/showcase";
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
