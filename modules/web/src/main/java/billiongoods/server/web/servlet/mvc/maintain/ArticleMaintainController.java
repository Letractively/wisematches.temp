package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.image.ImageManager;
import billiongoods.server.services.image.ImageResolver;
import billiongoods.server.services.image.ImageSize;
import billiongoods.server.warehouse.*;
import billiongoods.server.warehouse.impl.BanggoodArticlesImporter;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.ArticleForm;
import billiongoods.server.web.servlet.mvc.maintain.form.ImportArticlesForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/article")
public class ArticleMaintainController extends AbstractController {
    private ImageManager imageManager;
    private ImageResolver imageResolver;
    private ArticleManager articleManager;
    private CategoryManager categoryManager;
    private AttributeManager attributeManager;
    private BanggoodArticlesImporter articlesImporter;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

    public ArticleMaintainController() {
    }

    @RequestMapping(value = "/import", method = RequestMethod.GET)
    public String importArticlesView(Model model) {
        model.addAttribute("catalog", categoryManager.getCatalog());
        return "/content/maintain/import";
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public String importArticlesAction(Model model, ImportArticlesForm uploadItem, BindingResult result) {
        if (result.hasErrors()) {
            final List<ObjectError> allErrors = result.getAllErrors();
            for (ObjectError allError : allErrors) {
                System.out.println(allError);
            }
        }

        try {
            final Category category = categoryManager.getCategory(uploadItem.getCategory());
            articlesImporter.importArticles(category, uploadItem.getFile().getInputStream());
            model.addAttribute("result", true);
        } catch (IOException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("result", false);
        }
        model.addAttribute("catalog", categoryManager.getCatalog());
        return "/content/maintain/import";
    }


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewArticle(Model model, @ModelAttribute("form") ArticleForm form) throws IOException {
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
            form.setViewImages(imageManager.getImageCodes(article));
            form.setEnabledImages(article.getImageIds());

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
            final Integer[] ids = new Integer[accessories.size()];
            for (ArticleDescription accessory : accessories) {
                ids[index++] = accessory.getId();
            }
            form.setAccessories(ids);
        }

        if (form.getCategory() != null) {
            model.addAttribute("category", categoryManager.getCategory(form.getCategory()));
        }
        model.addAttribute("article", article);
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

        final List<Option> options = new ArrayList<>();

        final Integer[] optionIds = form.getOptionIds();
        final String[] optionValues = form.getOptionValues();
        if (optionIds != null) {
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
        }

        final List<Property> properties = new ArrayList<>();
        final Integer[] propertyIds = form.getPropertyIds();
        final String[] propertyValues = form.getPropertyValues();
        if (propertyIds != null) {
            for (int i = 0; i < propertyIds.length; i++) {
                Integer propertyId = propertyIds[i];
                String propertyValue = propertyValues[i];
                properties.add(new Property(attributeManager.getAttribute(propertyId), propertyValue));
            }
        }

        final Integer[] accessories = form.getAccessories();
        final List<ArticleDescription> descriptions = new ArrayList<>();
        if (accessories != null) {
            for (Integer accessory : accessories) {
                final ArticleDescription articleDescription = articleManager.getDescription(accessory);
                if (articleDescription == null) {
                    errors.rejectValue("accessories", "maintain.article.accessories.err.unknown", new Object[]{accessory}, null);
                    break;
                }
                descriptions.add(articleDescription);
            }
        }

        try {
            if (!errors.hasErrors()) {
                final Article article;
                if (form.getId() == null) {
                    article = articleManager.createArticle(form.getName(), form.getDescription(), category,
                            form.getPrice(), form.getPrimordialPrice(), restockDate,
                            form.getPreviewImage(), form.getEnabledImages(), descriptions, options, properties,
                            form.getSupplierReferenceId(), form.getSupplierReferenceCode(), Supplier.BANGGOOD, form.getSupplierPrice(),
                            form.getSupplierPrimordialPrice());
                } else {
                    article = articleManager.updateArticle(form.getId(), form.getName(), form.getDescription(), category,
                            form.getPrice(), form.getPrimordialPrice(), restockDate,
                            form.getPreviewImage(), form.getEnabledImages(), descriptions, options, properties,
                            form.getSupplierReferenceId(), form.getSupplierReferenceCode(), Supplier.BANGGOOD, form.getSupplierPrice(),
                            form.getSupplierPrimordialPrice());
                }
                return "redirect:/maintain/article?id=" + article.getId();
            }
        } catch (Exception ex) {
            errors.reject("internal.error", ex.getMessage());
        }

        if (form.getCategory() != null) {
            model.addAttribute("category", categoryManager.getCategory(form.getCategory()));
        }
        model.addAttribute("catalog", categoryManager.getCatalog());
        model.addAttribute("attributes", attributeManager.getAttributes());
        return "/content/maintain/article";
    }

    @RequestMapping(value = "/addimg", method = RequestMethod.POST)
    public ServiceResponse upload(@RequestParam("id") Integer id, MultipartHttpServletRequest request) throws IOException {
        final Article article = articleManager.getArticle(id);
        if (article == null) {
            throw new IllegalArgumentException("Article is not specified");
        }

        final MultipartFile file = request.getFile("files[]");

        String originalFilename = file.getOriginalFilename();
        originalFilename = originalFilename.substring(0, originalFilename.indexOf("."));
        imageManager.addImage(article, originalFilename, file.getInputStream());


        final Map<String, String> uri = new HashMap<>();
        uri.put("original", imageResolver.resolveURI(article, originalFilename, null));
        uri.put("small", imageResolver.resolveURI(article, originalFilename, ImageSize.SMALL));
        uri.put("tiny", imageResolver.resolveURI(article, originalFilename, ImageSize.TINY));
        uri.put("medium", imageResolver.resolveURI(article, originalFilename, ImageSize.MEDIUM));
        uri.put("large", imageResolver.resolveURI(article, originalFilename, ImageSize.LARGE));

        final Map<String, Object> res = new HashMap<>();
        res.put("code", originalFilename);
        res.put("uri", uri);

        return responseFactory.success(res);
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

    @Autowired
    public void setImageManager(ImageManager imageManager) {
        this.imageManager = imageManager;
    }

    @Autowired
    public void setImageResolver(ImageResolver imageResolver) {
        this.imageResolver = imageResolver;
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

    @Autowired
    public void setArticlesImporter(BanggoodArticlesImporter articlesImporter) {
        this.articlesImporter = articlesImporter;
    }
}
