package billiongoods.server.web.servlet.mvc;

import billiongoods.server.services.paypal.PayPalException;
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
public class CentralController extends AbstractController {
	public CentralController() {
	}

	@RequestMapping(value = {"/", "/index"})
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

		model.addAttribute("title", getTitle(request));
		model.addAttribute("principal", getPrincipal());
		model.addAttribute("department", getDepartment(request));

		model.addAttribute("errorCode", errorCode);
		model.addAttribute("errorTemplate", template);
		model.addAttribute("errorArguments", arguments);

		hideWhereabouts(model);
		hideNavigation(model);

		res.addAllObjects(model.asMap());

		return res;
	}
}
