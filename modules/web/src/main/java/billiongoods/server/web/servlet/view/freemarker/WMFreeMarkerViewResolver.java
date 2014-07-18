package billiongoods.server.web.servlet.view.freemarker;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class WMFreeMarkerViewResolver extends FreeMarkerViewResolver {
	private FreeMarkerConfig configuration;
	private boolean exposeRedirectModelAttributes = false;

	public WMFreeMarkerViewResolver() {
	}

	@Override
	protected View createView(String viewName, Locale locale) throws Exception {
		View view = super.createView(viewName, locale);
		if (view instanceof RedirectView) {
			((RedirectView) view).setExposeModelAttributes(exposeRedirectModelAttributes);
		}
		return view;
	}

	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		final WMFreeMarkerView view = (WMFreeMarkerView) super.buildView(viewName);
		view.setConfiguration(configuration);
		return view;
	}

	public boolean isExposeRedirectModelAttributes() {
		return exposeRedirectModelAttributes;
	}

	public void setConfiguration(FreeMarkerConfig configuration) {
		this.configuration = configuration;
	}

	public void setExposeRedirectModelAttributes(boolean exposeRedirectModelAttributes) {
		this.exposeRedirectModelAttributes = exposeRedirectModelAttributes;
	}
}
