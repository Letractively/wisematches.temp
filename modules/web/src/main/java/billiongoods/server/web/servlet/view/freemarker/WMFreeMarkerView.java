package billiongoods.server.web.servlet.view.freemarker;

import billiongoods.core.Language;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.template.*;
import org.springframework.beans.BeansException;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class WMFreeMarkerView extends FreeMarkerView {
	private FreeMarkerConfig configuration;
	private Collection<Class<? extends Enum>> exposeEnums;

	public WMFreeMarkerView() {
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void processTemplate(Template template, SimpleHash model, HttpServletResponse response) throws IOException, TemplateException {
		model.put("locale", template.getLocale());
		model.put("language", Language.byLocale(template.getLocale()));

		if (exposeEnums != null) {
			for (Class<? extends Enum> exposeEnum : exposeEnums) {
				FreeMarkerEnumMap view = FreeMarkerEnumMap.valueOf(exposeEnum);
				model.put(exposeEnum.getSimpleName(), view);
			}
		}

		final HttpRequestParametersHashModel sm2 = (HttpRequestParametersHashModel) model.get(FreemarkerServlet.KEY_REQUEST_PARAMETERS);
		if ("true".equalsIgnoreCase(String.valueOf(sm2.get("plain"))) ||
				(model.get("plain") != null && ((TemplateBooleanModel) model.get("plain")).getAsBoolean())) {
			super.processTemplate(template, model, response);
		} else {
			model.put("templateName", getUrl());
			super.processTemplate(getTemplate("/content/billiongoods.ftl", template.getLocale()), model, response);
		}
	}

	@Override
	protected FreeMarkerConfig autodetectConfiguration() throws BeansException {
		return configuration;
	}

	public void setConfiguration(FreeMarkerConfig configuration) {
		this.configuration = configuration;
	}

	public void setExposeEnums(Collection<Class<? extends Enum>> exposeEnums) throws TemplateModelException {
		this.exposeEnums = exposeEnums;
	}
}
