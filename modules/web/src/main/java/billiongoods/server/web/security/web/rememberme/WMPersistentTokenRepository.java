package billiongoods.server.web.security.web.rememberme;

import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;

/**
 * FIX FOR: https://jira.springsource.org/browse/SEC-1964
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class WMPersistentTokenRepository extends JdbcTokenRepositoryImpl {
	/**
	 * The default SQL used by <tt>removeUserTokens</tt>
	 */
	public static final String DEF_REMOVE_SERIES_SQL =
			"delete from persistent_logins where series = ?";

	public WMPersistentTokenRepository() {
	}

	public void removeToken(String seriesId) {
		getJdbcTemplate().update(DEF_REMOVE_SERIES_SQL, seriesId);
	}
}
