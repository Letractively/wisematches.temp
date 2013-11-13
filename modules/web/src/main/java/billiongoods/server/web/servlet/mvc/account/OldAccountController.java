package billiongoods.server.web.servlet.mvc.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@Deprecated
@RequestMapping("/account")
public class OldAccountController {
	public OldAccountController() {
	}

	@RequestMapping("/view")
	public String viewProfile() {
		return "/content/account/view";
	}
}
