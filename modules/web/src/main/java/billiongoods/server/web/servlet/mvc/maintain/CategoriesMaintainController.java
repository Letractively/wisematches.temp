package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.warehouse.CategoryManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/category")
public class CategoriesMaintainController extends AbstractController {
    private CategoryManager categoryManager;

    public CategoriesMaintainController() {
    }

    @RequestMapping("/view")
    public String mainPage(Model model) {
        model.addAttribute("catalog", categoryManager.getCatalog());
        return "/content/maintain/warehouse/view";
    }

    @RequestMapping("add")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String addAttribute(@RequestParam("n") String name, @RequestParam("d") String desc, @RequestParam("a") Integer[] attrs, Model model) {
//        categoryManager.addCategory()
        return "redirect:/maintain/attributes/view";
    }

    @Autowired
    public void setCategoryManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }
}
