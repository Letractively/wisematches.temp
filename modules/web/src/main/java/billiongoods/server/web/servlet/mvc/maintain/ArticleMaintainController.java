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
import java.util.*;

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

			int index = 0;
			final List<Option> options = article.getOptions();
			final Integer[] optIds = new Integer[options.size()];
			final String[] optValues = new String[options.size()];

			for (Option option : options) {
				optIds[index] = option.getAttribute().getId();
				optValues[index] = toString(option.getValues());
				index++;
			}

			form.setOptionIds(optIds);
			form.setOptionValues(optValues);

			final Map<Attribute, String> values = new HashMap<>();
			for (Category category : article.getCategory().getGenealogy()) {
				final Set<Attribute> attributes = category.getAttributes();
				for (Attribute attribute : attributes) {
					values.put(attribute, null);
				}
			}

			for (Property property : article.getProperties()) {
				values.put(property.getAttribute(), property.getValue());
			}

			index = 0;
			final Integer[] propIds = new Integer[values.size()];
			final String[] propValues = new String[values.size()];
			for (Map.Entry<Attribute, String> entry : values.entrySet()) {
				propIds[index] = entry.getKey().getId();
				propValues[index] = entry.getValue();
				index++;
			}
			form.setPropertyIds(propIds);
			form.setPropertyValues(propValues);

			index = 0;
			final List<ArticleDescription> accessories = article.getAccessories();
			final Long[] ids = new Long[accessories.size()];
			for (ArticleDescription accessory : accessories) {
				ids[index++] = accessory.getId();
			}
			form.setAccessories(ids);
		}

		if (form.getCategory() != null) {
			model.addAttribute("category", categoryManager.getCategory(form.getCategory()));
		}
		model.addAttribute("attributes", attributeManager.getAttributes());
		return "/content/maintain/article";
	}

	private String toString(List<String> strings) {
		StringBuilder b = new StringBuilder();
		for (Iterator<String> iterator = strings.iterator(); iterator.hasNext(); ) {
			String string = iterator.next();
			b.append(string);
			if (iterator.hasNext()) {
				b.append("; ");
			}
		}
		return b.toString();
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

		final List<Option> options = new ArrayList<>();

		final Integer[] optionIds = form.getOptionIds();
		final String[] optionValues = form.getOptionValues();
		for (int i = 0, optionIdsLength = optionIds.length; i < optionIdsLength; i++) {
			Integer optionId = optionIds[i];
			String optionValue = optionValues[i];

			final String[] split = optionValue.split(";");
			List<String> vals = new ArrayList<>();
			for (String s : split) {
				vals.add(s.trim());
			}
			options.add(new Option(attributeManager.getAttribute(optionId), vals));
		}

		final List<Property> properties = new ArrayList<>();
		final Integer[] propertyIds = form.getPropertyIds();
		final String[] propertyValues = form.getPropertyValues();
		for (int i = 0; i < propertyIds.length; i++) {
			Integer propertyId = propertyIds[i];
			String propertyValue = propertyValues[i];
			properties.add(new Property(attributeManager.getAttribute(propertyId), propertyValue));
		}

		final Long[] accessories = form.getAccessories();
		final List<ArticleDescription> descriptions = new ArrayList<>();
		for (Long accessory : accessories) {
			final ArticleDescription articleDescription = articleManager.getDescription(accessory);
			if (articleDescription == null) {
				errors.rejectValue("accessories", "maintain.article.accessories.err.unknown", new Object[]{accessory}, null);
				break;
			}
			descriptions.add(articleDescription);
		}

		if (!errors.hasErrors()) {
			final Article article;
			if (form.getId() == null) {
				article = articleManager.createArticle(form.getName(), form.getDescription(), category,
						form.getPrice(), form.getPrimordialPrice(), restockDate,
						form.getPreviewImage(), form.getViewImages(), descriptions, options, properties,
						form.getSupplierReferenceId(), form.getSupplierReferenceCode(), Supplier.BANGGOOD, form.getSupplierPrice(),
						form.getSupplierPrimordialPrice());
			} else {
				article = articleManager.updateArticle(form.getId(), form.getName(), form.getDescription(), category,
						form.getPrice(), form.getPrimordialPrice(), restockDate,
						form.getPreviewImage(), form.getViewImages(), descriptions, options, properties,
						form.getSupplierReferenceId(), form.getSupplierReferenceCode(), Supplier.BANGGOOD, form.getSupplierPrice(),
						form.getSupplierPrimordialPrice());
			}
			return "redirect:/maintain/article?id=" + article.getId();
		}

		if (form.getCategory() != null) {
			model.addAttribute("category", categoryManager.getCategory(form.getCategory()));
		}
		model.addAttribute("attributes", attributeManager.getAttributes());
		return "/content/maintain/article";
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
