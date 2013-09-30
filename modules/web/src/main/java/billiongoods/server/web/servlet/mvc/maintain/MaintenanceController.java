package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain")
public class MaintenanceController extends AbstractController {
	public MaintenanceController() {
	}

	@RequestMapping("/main")
	public String mainPage() {
		return "/content/maintain/main";
	}
}
