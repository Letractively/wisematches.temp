package billiongoods.server.web.servlet.mvc.warehouse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/catalog")
public class CatalogController extends WarehouseController {
	public CatalogController() {
	}

	@RequestMapping(value = {"", "/"})
	public String showRootCategory() {
		return "/content/warehouse/catalog";
	}
}
