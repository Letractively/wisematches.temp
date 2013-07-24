package billiongoods.server.web.servlet.mvc.assistance;

import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/assistance")
public class AssistanceController extends AbstractController {
	public AssistanceController() {
	}

	@RequestMapping({"", "/"})
	public String helpCenterPage() {
		return "/content/assistance/content";
	}

	@RequestMapping("/{pageName}")
	public String infoPages(@PathVariable String pageName, HttpSession session) {
		return "/content/assistance/page/" + pageName;
	}

	@RequestMapping("/tip.ajax")
	public ServiceResponse loadTip(@RequestParam("s") String section, Locale locale) {
		return responseFactory.success(messageSource.getMessage("game.tip." + section, locale));
	}
}
