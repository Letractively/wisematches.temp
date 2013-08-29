package billiongoods.server.web.servlet.mvc;

import billiongoods.core.Personality;
import billiongoods.core.security.PersonalityContext;
import billiongoods.server.MessageFormatter;
import billiongoods.server.services.basket.BasketManager;
import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.Catalog;
import billiongoods.server.warehouse.CategoryManager;
import billiongoods.server.web.servlet.sdo.ServiceResponseFactory;
import billiongoods.server.web.servlet.view.StaticContentGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class AbstractController {
	protected BasketManager basketManager;
	protected CategoryManager categoryManager;
	protected AttributeManager attributeManager;

	protected MessageFormatter messageSource;
	protected ServiceResponseFactory responseFactory;
	protected StaticContentGenerator staticContentGenerator;

	private final boolean hideNavigation;
	private final boolean hideWhereabouts;

	protected AbstractController() {
		this(false, false);
	}

	protected AbstractController(boolean hideNavigation, boolean hideWhereabouts) {
		this.hideNavigation = hideNavigation;
		this.hideWhereabouts = hideWhereabouts;
	}

	@ModelAttribute
	public void initializeDefaultState(Model model) {
		if (hideNavigation) {
			hideNavigation(model);
		}

		if (hideWhereabouts) {
			hideWhereabouts(model);
		}
	}

	@ModelAttribute("title")
	public String getTitle(HttpServletRequest request) {
		final Object title = request.getAttribute("title");
		if (title != null) {
			return String.valueOf(title);
		}
		final String uri = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
		if (uri.length() <= 1) {
			return "title.default";
		}
		return "title." + uri.replaceAll("/", ".").substring(1);
	}

	public void setTitle(Model model, String title) {
		model.addAttribute("title", title);
	}

	protected void setTitleExtension(Model model, String value) {
		model.addAttribute("titleExtension", value);
	}

	@ModelAttribute("principal")
	public Personality getPrincipal() {
		return PersonalityContext.getPrincipal();
	}

	@ModelAttribute("catalog")
	public Catalog getCatalog() {
		return categoryManager.getCatalog();
	}

	@ModelAttribute("section")
	public String getSection(HttpServletRequest request) {
		final String pathInfo = request.getPathInfo();
		if (pathInfo == null) {
			return null;
		}
		final String[] split = pathInfo.split("/");
		return split.length > 1 ? split[1] : null;
	}

	@ModelAttribute("department")
	public Department getDepartment(HttpServletRequest request) {
		final String servletPath = request.getServletPath();
		try {
			return Department.valueOf(servletPath.toUpperCase().substring(1));
		} catch (IllegalArgumentException ex) {
			return Department.UNDEFINED;
		}
	}

	@ModelAttribute("basketQuantity")
	public int getBasketQuantity() {
		return basketManager.getBasketSize(getPrincipal());
	}

	protected void hideWhereabouts(Model model) {
		model.addAttribute("hideWhereabouts", Boolean.TRUE);
	}

	protected void hideNavigation(Model model) {
		model.addAttribute("hideNavigation", Boolean.TRUE);
	}

	protected boolean hasRole(String role) {
		return PersonalityContext.hasRole(role);
	}

	@Autowired
	public void setBasketManager(BasketManager basketManager) {
		this.basketManager = basketManager;
	}

	@Autowired
	public void setMessageSource(MessageFormatter messageSource) {
		this.messageSource = messageSource;
		this.responseFactory = new ServiceResponseFactory(messageSource);
	}

	@Autowired
	public void setStaticContentGenerator(StaticContentGenerator staticContentGenerator) {
		this.staticContentGenerator = staticContentGenerator;
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