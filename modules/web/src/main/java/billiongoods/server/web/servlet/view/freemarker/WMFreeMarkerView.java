package billiongoods.server.web.servlet.view.freemarker;

import billiongoods.core.Language;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import org.springframework.beans.BeansException;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class WMFreeMarkerView extends FreeMarkerView {
	private FreeMarkerConfig configuration;

	public WMFreeMarkerView() {
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void processTemplate(Template template, SimpleHash model, HttpServletResponse response) throws IOException, TemplateException {
		model.put("locale", template.getLocale());
		model.put("language", Language.byLocale(template.getLocale()));

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
}
