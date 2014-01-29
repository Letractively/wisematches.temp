package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.payment.OrdersSummary;
import billiongoods.server.services.price.PriceConverter;
import billiongoods.server.services.tracking.ProductTracking;
import billiongoods.server.services.tracking.ProductTrackingManager;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.warehouse.ProductPreview;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.TrackingSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain")
public class MaintenanceController extends AbstractController {
	private OrderManager orderManager;
	private ProductManager productManager;
	private PriceConverter priceConverter;
	private ProductTrackingManager trackingManager;

	public MaintenanceController() {
	}

	@RequestMapping("/main")
	public String mainPage(Model model) {
		model.addAttribute("priceConverter", priceConverter);

		final OrdersSummary ordersSummary = orderManager.getOrdersSummary();
		model.addAttribute("ordersSummary", ordersSummary);

		Map<ProductPreview, List<ProductTracking>> trackingSummary = new HashMap<>();
		final List<ProductTracking> attributeValue = trackingManager.searchEntities(null, null, null, null);
		for (ProductTracking pt : attributeValue) {
			final ProductPreview pd = productManager.getPreview(pt.getProductId());
			List<ProductTracking> productTrackings = trackingSummary.get(pd);
			if (productTrackings == null) {
				productTrackings = new ArrayList<>(3);
				trackingSummary.put(pd, productTrackings);
			}
			productTrackings.add(pt);
		}
		model.addAttribute("trackingSummary", new TrackingSummary(trackingSummary));

		return "/content/maintain/main";
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	@Autowired
	public void setPriceConverter(PriceConverter priceConverter) {
		this.priceConverter = priceConverter;
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	@Autowired
	public void setTrackingManager(ProductTrackingManager trackingManager) {
		this.trackingManager = trackingManager;
	}
}
