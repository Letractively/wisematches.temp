package billiongoods.server.web.servlet.mvc.maintain;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */

import billiongoods.server.services.validator.ValidationManager;
import billiongoods.server.web.services.ProductSymbolicService;
import billiongoods.server.web.servlet.mvc.AbstractController;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
import java.util.List;
import java.util.StringTokenizer;

@Controller
@RequestMapping("/maintain/service")
public class ServiceController extends AbstractController {
	private SessionFactory sessionFactory;
	private ValidationManager validationManager;
	private ProductSymbolicService symbolicConverter;

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
		} else if ("broken".equalsIgnoreCase(action)) {
			if (!validationManager.isInProgress()) {
				validationManager.validateBroken();
			}
		}
		return "redirect:/maintain/service/validation";
	}

	@RequestMapping(value = "convert")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String validateImages() throws Exception {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("select id, name from billiongoods.server.warehouse.impl.HibernateCategory");
		final List list = query.list();
		for (Object o : list) {
			final Object[] v = (Object[]) o;

			final Integer id = (Integer) v[0];
			final String name = (String) v[1];

			final String s = symbolicConverter.generateSymbolic(name);

			final Query query1 = session.createQuery("update billiongoods.server.warehouse.impl.HibernateCategory set symbolic=:n where id=:id");
			query1.setParameter("id", id);
			query1.setParameter("n", s);
			query1.executeUpdate();
		}
		return "/content/maintain/main";
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Autowired
	public void setValidationManager(ValidationManager validationManager) {
		this.validationManager = validationManager;
	}

	@Autowired
	public void setSymbolicConverter(ProductSymbolicService symbolicConverter) {
		this.symbolicConverter = symbolicConverter;
	}
}
