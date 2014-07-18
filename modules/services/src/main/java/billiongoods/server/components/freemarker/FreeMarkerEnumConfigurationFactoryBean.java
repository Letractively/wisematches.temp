package billiongoods.server.components.freemarker;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FreeMarkerEnumConfigurationFactoryBean extends FreeMarkerConfigurationFactoryBean {
	private Collection<Class<? extends Enum>> exposeEnums;

	public FreeMarkerEnumConfigurationFactoryBean() {
	}

	@Override
	public Configuration createConfiguration() throws IOException, TemplateException {
		final Configuration configuration = super.createConfiguration();
		if (exposeEnums != null) {
			for (Class<? extends Enum> exposeEnum : exposeEnums) {
				FreeMarkerEnumMap view = FreeMarkerEnumMap.valueOf(exposeEnum);
				configuration.setSharedVariable(exposeEnum.getSimpleName(), view);
			}
		}
		return configuration;
	}

	public void setExposeEnums(Collection<Class<? extends Enum>> exposeEnums) {
		this.exposeEnums = exposeEnums;
	}
}
