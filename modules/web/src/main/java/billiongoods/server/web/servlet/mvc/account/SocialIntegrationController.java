package billiongoods.server.web.servlet.mvc.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

import javax.inject.Inject;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/account")
public class SocialIntegrationController {
	private final ConnectionFactoryLocator connectionFactoryLocator;
	private final UsersConnectionRepository usersConnectionRepository;

	private final ConnectSupport webSupport = new ConnectSupport();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.web.mvc.AccountSocialController");

	@Inject
	public SocialIntegrationController(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository) {
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.usersConnectionRepository = usersConnectionRepository;

		this.webSupport.setUseAuthenticateUrl(true);
	}

	@RequestMapping(value = "/signin/{providerId}", method = RequestMethod.POST)
	public String signIn(@PathVariable String providerId, NativeWebRequest request) {
		ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
		try {
			return "redirect:" + webSupport.buildOAuthUrl(connectionFactory, request);
		} catch (Exception e) {
			return null;
//			return redirect(URIBuilder.fromUri(signInUrl).queryParam("error", "provider").build().toString());
		}
	}

}