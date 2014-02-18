package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.supplier.DataLoadingException;
import billiongoods.server.services.supplier.ImportingSummary;
import billiongoods.server.services.supplier.ProductImporter;
import billiongoods.server.services.supplier.SupplierDataLoader;
import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.ImportProductsForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/product")
public class BangGoodImportController extends AbstractController {
	private ProductManager productManager;
	private ProductImporter productImporter;
	private SupplierDataLoader supplierDataLoader;

	public BangGoodImportController() {
	}

	@RequestMapping(value = "/import", method = RequestMethod.GET)
	public String importProductsView(@RequestParam(value = "c", required = false) Integer categoryId,
									 @ModelAttribute("form") ImportProductsForm form, Model model) {

		final ImportingSummary summary = productImporter.getImportingSummary();
		if (summary != null) {
			model.addAttribute("summary", summary);
		} else {
			if (categoryId != null) {
				final Category category = categoryManager.getCategory(categoryId);
				form.setCategory(categoryId);

				model.addAttribute("category", category);
				model.addAttribute("attributes", ProductMaintainController.createAttributesMap(category).keySet());
			}
		}
		return "/content/maintain/import";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/import", method = RequestMethod.POST)
	public String importProductsAction(Model model, @ModelAttribute("form") ImportProductsForm form) {
		try {
			List<Property> properties = null;
			final Integer[] propertyIds = form.getPropertyIds();
			final String[] propertyValues = form.getPropertyValues();

			if (propertyIds != null && propertyValues != null && propertyIds.length > 0) {
				properties = new ArrayList<>();
				for (int i = 0; i < propertyIds.length; i++) {
					try {
						final Integer p = propertyIds[i];
						final String v = propertyValues[i];
						if (!v.isEmpty()) {
							final Attribute attribute = attributeManager.getAttribute(p);
							properties.add(new Property(attribute, v));
						}
					} catch (Exception ignore) {
					}
				}
			}

			List<Integer> groups = null;
			if (form.getParticipatedGroups() != null) {
				groups = new ArrayList<>();
				for (Integer integer : form.getParticipatedGroups()) {
					if (integer != null) {
						groups.add(integer);
					}
				}
			}

			final Category category = categoryManager.getCategory(form.getCategory());
			productImporter.importProducts(category, properties, groups, form.getDescription().getInputStream(),
					form.getImages().getInputStream(), form.isValidatePrice());
			model.addAttribute("result", true);
			model.addAttribute("category", category);
			model.addAttribute("attributes", ProductMaintainController.createAttributesMap(category).keySet());
		} catch (Exception ex) {
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("result", false);
		}
		return "/content/maintain/import";
	}

	@RequestMapping(value = "/loadSupplierInfo.ajax")
	public ServiceResponse changeState(@RequestParam("id") Integer pid, Locale locale) {
		final SupplierInfo supplier = productManager.getSupplierInfo(pid);
		if (supplier == null) {
			responseFactory.failure("error.unknown.supplier", locale);
		}
		try {
			return responseFactory.success(supplierDataLoader.loadDescription(supplier));
		} catch (DataLoadingException ex) {
			return responseFactory.failure("error.bad.supplier", locale);
		}
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	@Autowired
	public void setProductImporter(ProductImporter productImporter) {
		this.productImporter = productImporter;
	}

	@Autowired
	public void setSupplierDataLoader(SupplierDataLoader supplierDataLoader) {
		this.supplierDataLoader = supplierDataLoader;
	}
}
