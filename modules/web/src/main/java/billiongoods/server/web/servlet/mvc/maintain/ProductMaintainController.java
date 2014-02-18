package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.image.ImageManager;
import billiongoods.server.services.image.ImageResolver;
import billiongoods.server.services.image.ImageSize;
import billiongoods.server.services.price.PriceConverter;
import billiongoods.server.warehouse.*;
import billiongoods.server.web.services.ProductSymbolicService;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.ProductForm;
import billiongoods.server.web.servlet.mvc.maintain.form.ReplaceForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
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
@RequestMapping("/maintain/product")
public class ProductMaintainController extends AbstractController {
	private ImageManager imageManager;
	private ImageResolver imageResolver;
	private PriceConverter priceConverter;
	private ProductManager productManager;
	private RelationshipManager relationshipManager;

	private ProductSymbolicService symbolicConverter;

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

	public ProductMaintainController() {
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String viewProduct(Model model, @ModelAttribute("form") ProductForm form) throws IOException {
		Product product = null;
		if (form.getId() != null) {
			product = productManager.getProduct(form.getId());
		}

		if (product != null) {
			final Category category = categoryManager.getCategory(product.getCategoryId());

			form.setName(product.getName());

			if (product.getSymbolic() == null || product.getSymbolic().isEmpty()) {
				form.setSymbolic(symbolicConverter.generateSymbolic(product.getName()));
			} else {
				form.setSymbolic(product.getSymbolic());
			}
			form.setDescription(product.getDescription());
			form.setCategoryId(category.getId());
			form.setPrice(product.getPrice().getAmount());
			form.setPrimordialPrice(product.getPrice().getPrimordialAmount());
			form.setWeight(product.getWeight());
			form.setCommentary(product.getCommentary());
			form.setProductState(product.getState());
			form.setStoreAvailable(product.getStockInfo().getLeftovers());

			if (product.getStockInfo().getRestockDate() != null) {
				form.setRestockDate(SIMPLE_DATE_FORMAT.format(product.getStockInfo().getRestockDate()));
			}

			final SupplierInfo supplierInfo = product.getSupplierInfo();
			form.setSupplierPrice(supplierInfo.getPrice().getAmount());
			form.setSupplierPrimordialPrice(supplierInfo.getPrice().getPrimordialAmount());
			form.setSupplierReferenceId(supplierInfo.getReferenceUri());
			form.setSupplierReferenceCode(supplierInfo.getReferenceCode());

			form.setPreviewImage(product.getPreviewImageId());
			form.setViewImages(imageManager.getImageCodes(product));
			form.setEnabledImages(product.getImageIds());

			int index = 0;
			final List<Option> options = product.getOptions();
			final Integer[] optIds = new Integer[options.size()];
			final String[] optValues = new String[options.size()];

			for (Option option : options) {
				optIds[index] = option.getAttribute().getId();
				optValues[index] = toString(option.getValues());
				index++;
			}

			form.setOptionIds(optIds);
			form.setOptionValues(optValues);

			final Map<Attribute, String> values = createAttributesMap(category);

			for (Property property : product.getProperties()) {
				final Attribute attribute = property.getAttribute();
				values.put(attribute, String.valueOf(property.getValue()));
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
			final List<Group> groups = relationshipManager.getGroups(product.getId());
			final String[] participatedNames = new String[groups.size()];
			final Integer[] participatedGroups = new Integer[groups.size()];
			for (Group group : groups) {
				participatedNames[index] = group.getName();
				participatedGroups[index++] = group.getId();
			}
			form.setParticipatedNames(participatedNames);
			form.setParticipatedGroups(participatedGroups);

			index = 0;
			final List<Relationship> relationships = relationshipManager.getRelationships(product.getId());
			final String[] relationshipNames = new String[relationships.size()];
			final Integer[] relationshipGroups = new Integer[relationships.size()];
			final RelationshipType[] relationshipTypes = new RelationshipType[relationships.size()];
			for (Relationship relationship : relationships) {
				relationshipNames[index] = relationship.getGroup().getName();
				relationshipGroups[index] = relationship.getGroup().getId();
				relationshipTypes[index++] = relationship.getType();
			}
			form.setRelationshipNames(relationshipNames);
			form.setRelationshipTypes(relationshipTypes);
			form.setRelationshipGroups(relationshipGroups);
		}

		model.addAttribute("attributes", attributeManager.getAttributes());
		model.addAttribute("priceConverter", priceConverter);
		return "/content/maintain/product";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String updateProduct(Model model, @Valid @ModelAttribute("form") ProductForm form, Errors errors) {
		final Category category = categoryManager.getCategory(form.getCategoryId());
		if (category == null) {
			errors.rejectValue("category", "maintain.product.category.err.unknown");
		}

		Date restockDate = null;
		if (form.getRestockDate() != null && !form.getRestockDate().trim().isEmpty()) {
			try {
				restockDate = SIMPLE_DATE_FORMAT.parse(form.getRestockDate().trim());
			} catch (ParseException ex) {
				errors.rejectValue("restockDate", "maintain.product.date.err.format");
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
				if (!propertyValue.isEmpty()) {
					properties.add(new Property(attributeManager.getAttribute(propertyId), propertyValue));
				}
			}
		}

		final Integer productId = form.getId();
		if (productId != null) {
			final List<Integer> groups = new ArrayList<>();
			for (Group group : relationshipManager.getGroups(productId)) {
				groups.add(group.getId());
			}
			final List<Integer> participatedGroups = new ArrayList<>();
			if (form.getParticipatedGroups() != null) {
				participatedGroups.addAll(Arrays.asList(form.getParticipatedGroups()));
			}

			final List<Integer> removedGroups = new ArrayList<>(groups);
			removedGroups.removeAll(participatedGroups);
			for (Integer removedGroup : removedGroups) {
				relationshipManager.removeGroupItem(removedGroup, productId);
			}

			final List<Integer> addedGroups = new ArrayList<>(participatedGroups);
			addedGroups.removeAll(groups);
			for (Integer addedGroup : addedGroups) {
				relationshipManager.addGroupItem(addedGroup, productId);
			}

			final Integer[] relationshipGroups = form.getRelationshipGroups();
			final RelationshipType[] relationshipTypes = form.getRelationshipTypes();
			final List<Relationship> relationships = new ArrayList<>(relationshipManager.getRelationships(productId));
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
						relationshipManager.addRelationship(productId, group, type);
					}
				}
			}

			for (Relationship r : relationships) {
				relationshipManager.removeRelationship(productId, r.getGroup().getId(), r.getType());
			}
		}

		try {
			if (!errors.hasErrors()) {
				final ProductEditor editor = new ProductEditor();
				editor.setName(form.getName().trim());
				editor.setSymbolic(form.getSymbolic() != null ? form.getSymbolic().trim() : null);
				editor.setDescription(form.getDescription().trim());
				editor.setCategoryId(category.getId());
				editor.setPrice(form.createPrice());
				editor.setWeight(form.getWeight());
				editor.setRestockDate(restockDate);
				editor.setStoreAvailable(form.getStoreAvailable());
				editor.setPreviewImage(form.getPreviewImage());
				editor.setImageIds(form.getEnabledImages());
				editor.setOptions(options);
				editor.setProperties(properties);
				editor.setReferenceUri(form.getSupplierReferenceId());
				editor.setReferenceCode(form.getSupplierReferenceCode());
				editor.setWholesaler(Supplier.BANGGOOD);
				editor.setSupplierPrice(form.createSupplierPrice());
				editor.setProductState(form.getProductState());
				editor.setCommentary(form.getCommentary());

				final Product product;
				if (productId == null) {
					product = productManager.createProduct(editor);
				} else {
					product = productManager.updateProduct(productId, editor);
				}
				return "redirect:/maintain/product?id=" + product.getId();
			}
		} catch (Exception ex) {
			errors.reject("internal.error", ex.getMessage());
		}

		model.addAttribute("attributes", attributeManager.getAttributes());
		model.addAttribute("priceConverter", priceConverter);
		return "/content/maintain/product";
	}

	@RequestMapping(value = "/replace")
	public String descriptionReplace(@ModelAttribute("form") ReplaceForm form) {
		return "/content/maintain/replace";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/replace", method = RequestMethod.POST)
	public String descriptionReplaceAction(@ModelAttribute("form") ReplaceForm form, Model model, Errors errors) {
		if (form.getFrom() == null || form.getFrom().trim().isEmpty()) {
			errors.rejectValue("from", "maintain.replace.from.empty");
		}
		if (form.getTo() == null || form.getTo().trim().isEmpty()) {
			errors.rejectValue("to", "maintain.replace.to.empty");
		}

		if (!errors.hasErrors()) {
			model.addAttribute("updatedCount", productManager.updateDescriptions(form.getFrom(), form.getTo()));
		}
		return "/content/maintain/replace";
	}

	@RequestMapping(value = "/addimg", method = RequestMethod.POST)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse upload(@RequestParam("id") Integer id, MultipartHttpServletRequest request) throws IOException {
		final Product product = productManager.getProduct(id);
		if (product == null) {
			throw new IllegalArgumentException("Product is not specified");
		}

		final MultipartFile file = request.getFile("files[]");

		String originalFilename = file.getOriginalFilename();
		originalFilename = originalFilename.substring(0, originalFilename.indexOf("."));
		imageManager.addImage(product, originalFilename, file.getInputStream());

		final Map<String, String> uri = new HashMap<>();
		uri.put("original", imageResolver.resolveURI(product, originalFilename, null));
		uri.put("small", imageResolver.resolveURI(product, originalFilename, ImageSize.SMALL));
		uri.put("tiny", imageResolver.resolveURI(product, originalFilename, ImageSize.TINY));
		uri.put("medium", imageResolver.resolveURI(product, originalFilename, ImageSize.MEDIUM));
		uri.put("large", imageResolver.resolveURI(product, originalFilename, ImageSize.LARGE));

		final Map<String, Object> res = new HashMap<>();
		res.put("code", originalFilename);
		res.put("uri", uri);

		return responseFactory.success(res);
	}

	@RequestMapping(value = "/symbolic.ajax")
	public ServiceResponse generateSymbolic(@RequestParam("name") String name, Locale locale) {
		return responseFactory.success(symbolicConverter.generateSymbolic(name));
	}

	protected static Map<Attribute, String> createAttributesMap(Category category) {
		final Map<Attribute, String> values = new HashMap<>();
		Category ct = category;
		while (ct != null) {
			for (Parameter parameter : ct.getParameters()) {
				values.put(parameter.getAttribute(), null);
			}
			ct = ct.getParent();
		}
		return values;
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
	public void setPriceConverter(PriceConverter priceConverter) {
		this.priceConverter = priceConverter;
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	@Autowired
	public void setSymbolicConverter(ProductSymbolicService symbolicConverter) {
		this.symbolicConverter = symbolicConverter;
	}

	@Autowired
	public void setRelationshipManager(RelationshipManager relationshipManager) {
		this.relationshipManager = relationshipManager;
	}
}
