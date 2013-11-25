package billiongoods.server.web.security.social;

import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.social.security.provider.OAuth1AuthenticationService;
import org.springframework.social.security.provider.OAuth2AuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class SocialAuthenticationServiceRegistry extends ConnectionFactoryRegistry implements SocialAuthenticationServiceLocator {
	private final Map<String, SocialAuthenticationService<?>> cache = new LinkedHashMap<>();

	public SocialAuthenticationServiceRegistry() {
	}

	@Override
	public SocialAuthenticationService<?> getAuthenticationService(String providerId) {
		return cache.get(providerId);
	}

	@Override
	public Set<String> registeredAuthenticationProviderIds() {
		return cache.keySet();
	}

	@Override
	public void setConnectionFactories(List<ConnectionFactory<?>> connectionFactories) {
		super.setConnectionFactories(connectionFactories);

		for (ConnectionFactory<?> connectionFactory : connectionFactories) {
			SocialAuthenticationService<?> service = null;
			if (connectionFactory instanceof OAuth1ConnectionFactory<?>) {
				service = new OAuth1AuthenticationService<>((OAuth1ConnectionFactory<?>) connectionFactory);
			} else if (connectionFactory instanceof OAuth2ConnectionFactory<?>) {
				service = new OAuth2AuthenticationService<>((OAuth2ConnectionFactory<?>) connectionFactory);
			}

			if (service != null) {
				cache.put(connectionFactory.getProviderId(), service);
			}
		}
	}
}
