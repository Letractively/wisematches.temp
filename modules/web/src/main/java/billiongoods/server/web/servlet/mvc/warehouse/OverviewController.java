package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/store")
public class OverviewController extends AbstractController {
    public OverviewController() {
    }

    @RequestMapping(value = "overview")
    public String modifyAccountPage(Model model) {
        return "/content/warehouse/home";
    }
}
