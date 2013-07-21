package billiongoods.server.web.servlet.mvc.assistance;

import billiongoods.server.web.servlet.mvc.AbstractController;
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
@RequestMapping("/info")
public class InformationController extends AbstractController {
    public InformationController() {
    }

    @RequestMapping("/{pageName}")
    public String infoPages(@PathVariable String pageName,
                            @RequestParam(value = "plain", required = false) String plain,
                            Model model, Locale locale) throws InformationUnavailableException {
        if (!staticContentGenerator.generatePage(pageName, "features".equals(pageName), model, locale)) {
            throw new InformationUnavailableException(pageName, plain);
        }

        if (plain != null) {
            return "/content/assistance/static";
        } else {
            return "/content/assistance/help";
        }
    }

    @RequestMapping("/tip.ajax")
    public ServiceResponse loadTip(@RequestParam("s") String section, Locale locale) {
        return responseFactory.success(messageSource.getMessage("game.tip." + section, locale));
    }
}
