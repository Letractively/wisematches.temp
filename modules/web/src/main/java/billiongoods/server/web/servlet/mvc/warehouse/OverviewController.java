package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse")
public class OverviewController extends AbstractController {
    public OverviewController() {
    }

    @RequestMapping(value = "/home")
    public String modifyAccountPage() {
        return "/content/warehouse/home";
    }
}
