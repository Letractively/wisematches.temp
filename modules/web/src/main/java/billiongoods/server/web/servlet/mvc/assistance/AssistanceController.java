package billiongoods.server.web.servlet.mvc.assistance;

import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String helpCenterPage(Model model) {
        return "/content/assistance/general";
    }

    @RequestMapping("/{pageName}")
    public String infoPages(@PathVariable String pageName, Model model, Locale locale) throws UnknownEntityException {
        if (!staticContentGenerator.generatePage(pageName, false, model, locale)) {
            throw new UnknownEntityException(pageName, "assistance");
        }
        return "/content/assistance/layout";
    }

    @RequestMapping("/tip.ajax")
    public ServiceResponse loadTip(@RequestParam("s") String section, Locale locale) {
        return responseFactory.success(messageSource.getMessage("game.tip." + section, locale));
    }
}
