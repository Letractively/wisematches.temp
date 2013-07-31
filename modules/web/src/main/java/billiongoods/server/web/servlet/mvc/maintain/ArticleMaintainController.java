package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.ArticleForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/article")
public class ArticleMaintainController extends AbstractController {
	private ArticleManager articleManager;
	private CategoryManager categoryManager;
	private AttributeManager attributeManager;

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

	public ArticleMaintainController() {
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String viewArticle(Model model, @ModelAttribute("form") ArticleForm form) {
		Article article = null;
		if (form.getId() != null) {
			article = articleManager.getArticle(form.getId());
		}

		if (article != null) {
			form.setName(article.getName());
			form.setDescription(article.getDescription());
			form.setCategory(article.getCategory().getId());
			form.setPrice(article.getPrice());
			form.setPrimordialPrice(article.getPrimordialPrice());

			final SupplierInfo supplierInfo = article.getSupplierInfo();
			form.setSupplierPrice(supplierInfo.getPrice());
			form.setSupplierPrimordialPrice(supplierInfo.getPrimordialPrice());
			form.setSupplierReferenceId(supplierInfo.getReferenceId());
			form.setSupplierReferenceCode(supplierInfo.getReferenceCode());

			form.setPreviewImage(article.getPreviewImageId());

			form.setViewImages(article.getImageIds());

/*
			if (article.getParent() != null) {
				form.setParent(article.getParent().getId());
			} else {
				form.setParent(null);
			}

			int index = 0;
			final Set<Attribute> attributes = article.getAttributes();
			final Integer[] attrs = new Integer[attributes.size()];
			for (Attribute attribute : attributes) {
				attrs[index++] = attribute.getId();
			}
			form.setAttributes(attrs);
*/
		}

		model.addAttribute("article", article);

		if (form.getCategory() != null) {
			model.addAttribute("category", categoryManager.getCategory(form.getCategory()));
		}

		model.addAttribute("catalog", categoryManager.getCatalog());
		model.addAttribute("attributes", attributeManager.getAttributes());
		return "/content/maintain/article";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String updateArticle(Model model, @Valid @ModelAttribute("form") ArticleForm form, Errors errors) {
		final Category category = categoryManager.getCategory(form.getCategory());
		if (category == null) {
			errors.rejectValue("category", "maintain.article.category.err.unknown");
		}

		Date restockDate = null;
		if (form.getRestockDate() != null && !form.getRestockDate().trim().isEmpty()) {
			try {
				restockDate = SIMPLE_DATE_FORMAT.parse(form.getRestockDate().trim());
			} catch (ParseException ex) {
				errors.rejectValue("restockDate", "maintain.article.date.err.format");
			}
		}

		if (!errors.hasErrors()) {
			final Article article;
			if (form.getId() == null) {
				article = articleManager.createArticle(form.getName(), form.getDescription(), category,
						form.getPrice(), form.getPrimordialPrice(), restockDate, form.getSupplierReferenceId(),
						form.getSupplierReferenceCode(), Supplier.BANGGOOD, form.getSupplierPrice(),
						form.getSupplierPrimordialPrice());
			} else {
				article = articleManager.updateArticle(form.getId(), form.getName(), form.getDescription());
			}
			return "redirect:/maintain/article?id=" + article.getId();
		}
		return viewArticle(model, form);
	}


	@Autowired
	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}

	@Autowired
	public void setCategoryManager(CategoryManager categoryManager) {
		this.categoryManager = categoryManager;
	}

	@Autowired
	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}
}
