package billiongoods.server.web.servlet.mvc.maintain;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/category")
public class CategoriesMaintainController {
	public CategoriesMaintainController() {
	}

	@RequestMapping("/view")
	public String mainPage() {
		return "/content/maintain/warehouse/view";
	}

}