package billiongoods.server.web.servlet.mvc;

import billiongoods.core.Member;
import billiongoods.core.Personality;
import billiongoods.server.MessageFormatter;
import billiongoods.server.services.paypal.PayPalException;
import billiongoods.server.warehouse.CategoryManager;
import billiongoods.server.web.security.context.PersonalityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@ControllerAdvice
public class CentralController {
	private CategoryManager categoryManager;
	private MessageFormatter messageFormatter;
	private PersonalityContext personalityContext;

	public CentralController() {
	}

	@RequestMapping(value = {"/", "/index", "/warehouse"})
	public final String mainPage() {
		return "forward:/warehouse/catalog";
	}

	@RequestMapping(value = "/assistance/error")
	public ModelAndView processException(HttpServletRequest request, HttpServletResponse response) {
		return processException(String.valueOf(response.getStatus()), null, request, request.getRequestURI());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ModelAndView processAccessException(Exception exception, HttpServletRequest request) {
		return processException("access", null, request);
	}

	@ExceptionHandler(UnknownEntityException.class)
	public ModelAndView processUnknownEntity(UnknownEntityException exception, HttpServletRequest request) {
		return processException("unknown." + exception.getEntityType(), null, request, exception.getEntityId());
	}

	@ExceptionHandler(ExpiredParametersException.class)
	public ModelAndView processExpiredParametersException(ExpiredParametersException exception, HttpServletRequest request) {
		return processException("expired", "expired.ftl", request, exception);
	}

	@ExceptionHandler(PayPalException.class)
	public ModelAndView processPayPalException(PayPalException exception, HttpServletRequest request) {
		return processException("paypal", "paypal/failed.ftl", request, exception);
	}

	@ExceptionHandler(CookieTheftException.class)
	public String cookieTheftException(CookieTheftException ex) {
		return "forward:/account/loginAuth?error=insufficient";
	}

	private ModelAndView processException(String errorCode, String template, HttpServletRequest request, Object... arguments) {
		final Model model = new ExtendedModelMap();
		final ModelAndView res = new ModelAndView("/content/assistance/errors");

		model.addAttribute("errorCode", errorCode);
		model.addAttribute("errorTemplate", template);
		model.addAttribute("errorArguments", arguments);

		model.addAttribute("catalog", categoryManager.getCatalog());
		model.addAttribute("department", Department.ASSISTANCE);

		model.addAttribute("hideWhereabouts", Boolean.TRUE);
		model.addAttribute("hideNavigation", Boolean.TRUE);

		model.addAttribute("title", messageFormatter.getMessage("error." + errorCode + ".label", request.getLocale()));

		final Personality principal = personalityContext.getPrincipal();
		if (principal instanceof Member) {
			model.addAttribute("member", principal);
		}

		res.addAllObjects(model.asMap());

		return res;
	}

	@Autowired
	public void setPersonalityContext(PersonalityContext personalityContext) {
		this.personalityContext = personalityContext;
	}

	@Autowired
	public void setMessageSource(MessageFormatter messageSource) {
		this.messageFormatter = messageSource;
	}

	@Autowired
	public void setCategoryManager(CategoryManager categoryManager) {
		this.categoryManager = categoryManager;
	}
}
