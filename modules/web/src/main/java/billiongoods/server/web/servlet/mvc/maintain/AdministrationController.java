package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain")
public class AdministrationController extends AbstractController {
    public AdministrationController() {
    }

    @RequestMapping("/main")
    public String mainPage() {
        return "/content/maintain/main";
    }

    @RequestMapping("/gc")
    public String gcAction() {
        System.gc();
        return "redirect:/maintain/main";
    }
}
