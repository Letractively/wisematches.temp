package billiongoods.server.web.servlet.mvc.maintain;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */

import billiongoods.server.services.validator.ValidationManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

@Controller
@RequestMapping("/maintain/service")
public class ServiceController extends AbstractController {
	private ValidationManager validationManager;

	public ServiceController() {
	}

	@RequestMapping("url")
	public String checkURL(@RequestParam(value = "url", required = false) String u,
						   @RequestParam(value = "params", required = false) String params, Model model) {
		if (u != null) {
			try {
				final URL url = new URL(u);
				final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setUseCaches(false);
				urlConnection.setDefaultUseCaches(false);
				urlConnection.setInstanceFollowRedirects(true);

//                urlConnection.setReadTimeout(3000);
//                urlConnection.setConnectTimeout(3000);

				if (params != null) {
					StringTokenizer st = new StringTokenizer(params, "\n\r");
					while (st.hasMoreTokens()) {
						final String s = st.nextToken();
						final String[] split = s.split(":");
						urlConnection.setRequestProperty(split[0].trim(), split[1].trim());
					}
				}

				try (final InputStream inputStream = urlConnection.getInputStream()) {
					final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					final StringBuilder sb = new StringBuilder();

					String s = reader.readLine();
					while (s != null) {
						sb.append(s.trim());
						s = reader.readLine();
					}
					model.addAttribute("response", sb.toString());
				} catch (IOException ex) {
					model.addAttribute("response", ex.getMessage());
				}
			} catch (IOException ex) {
				model.addAttribute("response", ex.getMessage());
			}
		}
		model.addAttribute("url", u);
		model.addAttribute("params", params);
		return "/content/maintain/url";
	}

	@RequestMapping("validation")
	public String validatePrices(Model model) {
		model.addAttribute("active", validationManager.isInProgress());
		model.addAttribute("summary", validationManager.getValidationSummary());
		return "/content/maintain/validation";
	}

	@RequestMapping(value = "validation", method = RequestMethod.POST)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String validatePricesAction(@RequestParam("action") String action) {
		if ("start".equalsIgnoreCase(action)) {
			if (!validationManager.isInProgress()) {
				validationManager.startValidation();
			}
		} else if ("stop".equalsIgnoreCase(action)) {
			if (validationManager.isInProgress()) {
				validationManager.cancelValidation();
			}
		} else if ("exchange".equalsIgnoreCase(action)) {
			if (!validationManager.isInProgress()) {
				validationManager.validateExchangeRate();
			}
		}
		return "redirect:/maintain/service/validation";
	}

	@Autowired
	public void setValidationManager(ValidationManager validationManager) {
		this.validationManager = validationManager;
	}
}
