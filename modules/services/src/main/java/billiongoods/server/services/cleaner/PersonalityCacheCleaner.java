package billiongoods.server.services.cleaner;

import billiongoods.core.Personality;
import billiongoods.server.services.state.PersonalityStateListener;
import billiongoods.server.services.state.PersonalityStateManager;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PersonalityCacheCleaner {
	private CacheManager cacheManager;
	private PersonalityStateManager stateManager;

	private final ThePersonalityStateListener stateListener = new ThePersonalityStateListener();

	public PersonalityCacheCleaner() {
	}

	private void clearPersonalityCaches(Personality person) {
		if (cacheManager != null) {
			final Collection<String> cacheNames = cacheManager.getCacheNames();
			for (String cacheName : cacheNames) {
				final Cache cache = cacheManager.getCache(cacheName);
				if (cache != null) {
					cache.evict(person);
				}
			}
		}
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setStateManager(PersonalityStateManager stateManager) {
		if (this.stateManager != null) {
			this.stateManager.removePlayerStateListener(stateListener);
		}

		this.stateManager = stateManager;

		if (this.stateManager != null) {
			this.stateManager.addPersonalityStateListener(stateListener);
		}
	}

	private final class ThePersonalityStateListener implements PersonalityStateListener {
		private ThePersonalityStateListener() {
		}

		@Override
		public void playerOnline(Personality person) {
		}

		@Override
		public void playerAlive(Personality person) {
		}

		@Override
		public void playerOffline(Personality person) {
			clearPersonalityCaches(person);
		}
	}
}
