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
    private RelationshipManager relationshipManager;
    private BanggoodArticlesImporter articlesImporter;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

    public ArticleMaintainController() {
    }

    @RequestMapping(value = "/import", method = RequestMethod.GET)
    public String importArticlesView(Model model) {
        return "/content/maintain/import";
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public String importArticlesAction(Model model, ImportArticlesForm uploadItem, BindingResult result) {
        try {
            final Category category = categoryManager.getCategory(uploadItem.getCategory());
            articlesImporter.importArticles(category, uploadItem.getDescription().getInputStream(), uploadItem.getImages().getInputStream());
            model.addAttribute("result", true);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("result", false);
        }
        return "/content/maintain/import";
    }


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewArticle(Model model, @ModelAttribute("form") ArticleForm form) throws IOException {
        Article article = null;
        if (form.getId() != null) {
            article = articleManager.getArticle(form.getId());
        }

        if (article != null) {
            final Category category = categoryManager.getCategory(article.getCategoryId());

            form.setName(article.getName());
            form.setDescription(article.getDescription());
            form.setCategory(category.getId());
            form.setPrice(article.getPrice().getAmount());
            form.setPrimordialPrice(article.getPrice().getPrimordialAmount());
            form.setWeight(article.getWeight());

            final SupplierInfo supplierInfo = article.getSupplierInfo();
            form.setSupplierPrice(supplierInfo.getPrice().getAmount());
            form.setSupplierPrimordialPrice(supplierInfo.getPrice().getPrimordialAmount());
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
            Category ct = category;
            while (ct != null) {
                for (Attribute attribute : ct.getAttributes()) {
                    values.put(attribute, null);
                }
                ct = ct.getParent();
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
        }

        if (form.getCategory() != null) {
            model.addAttribute("category", categoryManager.getCategory(form.getCategory()));
        }
        if (article != null) {
            model.addAttribute("groups", relationshipManager.getGroups(article.getId()));
            model.addAttribute("relationships", relationshipManager.getRelationships(article.getId()));
        }

        model.addAttribute("article", article);
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
                String propertyValue = "";
                try {
                    propertyValue = propertyValues[i].trim();
                } catch (Exception ignore) {
                }
                properties.add(new Property(attributeManager.getAttribute(propertyId), propertyValue));
            }
        }

        final Integer articleId = form.getId();
        if (articleId != null) {
            final List<Integer> groups = new ArrayList<>();
            for (Group group : relationshipManager.getGroups(articleId)) {
                groups.add(group.getId());
            }
            final List<Integer> participatedGroups = new ArrayList<>();
            if (form.getParticipatedGroups() != null) {
                participatedGroups.addAll(Arrays.asList(form.getParticipatedGroups()));
            }

            final List<Integer> removedGroups = new ArrayList<>(groups);
            removedGroups.removeAll(participatedGroups);
            for (Integer removedGroup : removedGroups) {
                relationshipManager.removeGroupItem(removedGroup, articleId);
            }

            final List<Integer> addedGroups = new ArrayList<>(participatedGroups);
            addedGroups.removeAll(groups);
            for (Integer addedGroup : addedGroups) {
                relationshipManager.addGroupItem(addedGroup, articleId);
            }

            final Integer[] relationshipGroups = form.getRelationshipGroups();
            final RelationshipType[] relationshipTypes = form.getRelationshipTypes();
            final List<Relationship> relationships = new ArrayList<>(relationshipManager.getRelationships(articleId));
            if (relationshipGroups != null) {
                for (int i = 0, relationshipGroupsLength = relationshipGroups.length; i < relationshipGroupsLength; i++) {
                    final Integer group = relationshipGroups[i];
                    final RelationshipType type = relationshipTypes[i];

                    Relationship rs = null;
                    for (Relationship relationship : relationships) {
                        if (relationship.getType() == type && relationship.getGroup().getId().equals(group)) {
                            rs = relationship;
                            break;
                        }
                    }

                    if (rs != null) {
                        relationships.remove(rs);
                    } else {
                        relationshipManager.addRelationship(articleId, group, type);
                    }
                }
            }

            for (Relationship r : relationships) {
                relationshipManager.removeRelationship(articleId, r.getGroup().getId(), r.getType());
            }
        }

        try {
            if (!errors.hasErrors()) {
                final Article article;
                if (articleId == null) {
                    article = articleManager.createArticle(form.getName().trim(), form.getDescription().trim(), category,
                            form.createPrice(), form.getWeight(), restockDate,
                            form.getPreviewImage(), form.getEnabledImages(), options, properties,
                            form.getSupplierReferenceId(), form.getSupplierReferenceCode(), Supplier.BANGGOOD, form.createSupplierPrice());
                } else {
                    article = articleManager.updateArticle(articleId, form.getName().trim(), form.getDescription().trim(), category,
                            form.createPrice(), form.getWeight(), restockDate,
                            form.getPreviewImage(), form.getEnabledImages(), options, properties,
                            form.getSupplierReferenceId(), form.getSupplierReferenceCode(), Supplier.BANGGOOD, form.createSupplierPrice());
                }
                return "redirect:/maintain/article?id=" + article.getId();
            }
        } catch (Exception ex) {
            errors.reject("internal.error", ex.getMessage());
        }

        if (form.getCategory() != null) {
            model.addAttribute("category", categoryManager.getCategory(form.getCategory()));
        }
        model.addAttribute("attributes", attributeManager.getAttributes());
        return "/content/maintain/article";
    }

    @RequestMapping(value = "/addimg", method = RequestMethod.POST)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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

    @RequestMapping(value = "/activate.ajax", method = RequestMethod.POST)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ServiceResponse changeState(@RequestParam("id") Integer id, @RequestParam("a") boolean active) {
        articleManager.updateState(id, active);
        return responseFactory.success();
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
    public void setArticlesImporter(BanggoodArticlesImporter articlesImporter) {
        this.articlesImporter = articlesImporter;
    }

    @Autowired
    public void setRelationshipManager(RelationshipManager relationshipManager) {
        this.relationshipManager = relationshipManager;
    }
}
