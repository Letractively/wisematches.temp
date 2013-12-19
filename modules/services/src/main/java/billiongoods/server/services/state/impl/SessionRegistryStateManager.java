package billiongoods.server.services.state.impl;

import billiongoods.core.Personality;
import billiongoods.core.secure.MemberContainer;
import billiongoods.server.services.state.PersonalityStateListener;
import billiongoods.server.services.state.PersonalityStateManager;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class SessionRegistryStateManager extends SessionRegistryImpl implements PersonalityStateManager {
	private final Collection<PersonalityStateListener> listeners = new CopyOnWriteArraySet<>();

	public SessionRegistryStateManager() {
	}

	@Override
	public void addPersonalityStateListener(PersonalityStateListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removePlayerStateListener(PersonalityStateListener l) {
		listeners.remove(l);
	}

	@Override
	public boolean isPersonalityOnline(Personality personality) {
		return !getAllSessions(personality, false).isEmpty();
	}

	@Override
	public Date getLastActivityDate(Personality personality) {
		Date date = null;
		final List<SessionInformation> allSessions = getAllSessions(personality, true);
		for (SessionInformation session : allSessions) {
			final Date lastRequest = session.getLastRequest();
			if (date == null) {
				date = lastRequest;
			} else if (date.before(lastRequest)) {
				date = lastRequest;
			}
		}
		return date;
	}

	@Override
	public void registerNewSession(String sessionId, Object principal) {
		final Object personality = personality(principal);

		super.registerNewSession(sessionId, personality);

		final SessionInformation info = getSessionInformation(sessionId);
		if (info != null && info.getPrincipal() instanceof Personality) {
			processPersonalityOnline((Personality) info.getPrincipal());
		}
	}

	@Override
	public void refreshLastRequest(String sessionId) {
		super.refreshLastRequest(sessionId);

		final SessionInformation info = getSessionInformation(sessionId);
		if (info != null && info.getPrincipal() instanceof Personality) {
			processPersonalityAlive((Personality) info.getPrincipal());
		}
	}

	@Override
	public void removeSessionInformation(String sessionId) {
		final SessionInformation info = getSessionInformation(sessionId);

		super.removeSessionInformation(sessionId);

		if (info != null && info.getPrincipal() instanceof Personality) {
			final Personality player = (Personality) info.getPrincipal();
			// notify listeners only about last session
			if (getAllSessions(player, true).isEmpty()) {
				processPersonalityOffline(player);
			}
		}
	}

	private Object personality(Object info) {
		if (info instanceof MemberContainer) {
			return ((MemberContainer) info).getMember();
		}
		return info;
	}

	protected void processPersonalityOnline(Personality player) {
		// notify listeners only about first session
		if (getAllSessions(player, true).size() == 1) {
			for (PersonalityStateListener listener : listeners) {
				listener.playerOnline(player);
			}
		}
	}

	protected void processPersonalityAlive(Personality player) {
		for (PersonalityStateListener listener : listeners) {
			listener.playerAlive(player);
		}
	}

	protected void processPersonalityOffline(Personality player) {
		for (PersonalityStateListener listener : listeners) {
			listener.playerOffline(player);
		}
	}
}
