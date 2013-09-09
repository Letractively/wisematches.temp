package billiongoods.server.web.servlet.mvc.warehouse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/article")
public class ArticleController {
    public ArticleController() {
    }

    @RequestMapping("/{articleId}")
    public View redirectToProduct(@PathVariable("articleId") String articleId) {
        RedirectView rv = new RedirectView("/warehouse/product/" + articleId);
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        rv.setExposeModelAttributes(false);
        rv.setExpandUriTemplateVariables(false);
        rv.setExposePathVariables(false);
        return rv;
    }
}
