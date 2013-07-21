package billiongoods.server.web.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class WebClient {
    private final UserAgent userAgent;
    private final int majorVersion;
    private final String fullVersion;

    private static final Logger log = LoggerFactory.getLogger("billiongoods.web.http.WebClient");

    public WebClient(UserAgent userAgent, int majorVersion, String fullVersion) {
        this.userAgent = userAgent;
        this.majorVersion = majorVersion;
        this.fullVersion = fullVersion;
    }

    public String getFullVersion() {
        return fullVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WebClient other = (WebClient) obj;
        if (this.userAgent != other.userAgent) {
            return false;
        }
        if (this.majorVersion != other.majorVersion) {
            return false;
        }
        if ((this.fullVersion == null) ? (other.fullVersion != null) : !this.fullVersion.equals(other.fullVersion)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.userAgent != null ? this.userAgent.hashCode() : 0);
        hash = 37 * hash + this.majorVersion;
        hash = 37 * hash + (this.fullVersion != null ? this.fullVersion.hashCode() : 0);
        return hash;
    }


    public static WebClient detect(HttpServletRequest req) {
        return detect(req.getHeader("User-Agent"));
    }

    public static WebClient detect(String userAgentString) {
        UserAgent ua = null;
        String ver = null;
        if (userAgentString != null && !userAgentString.isEmpty()) {
            try {
                if (userAgentString.contains("Wisematches/")) {
                    ua = UserAgent.WISEMATCHES;
                    ver = userAgentString.substring(userAgentString.indexOf("Wisematches/") + 12);
                } else if (userAgentString.contains("Yahoo! Slurp")) {
                    ua = UserAgent.YAHOO_SLURP;
                } else if (userAgentString.contains("Googlebot/")) {
                    ua = UserAgent.GOOGLEBOT;
                    ver = userAgentString.substring(userAgentString.indexOf("Googlebot/") + 10);
                    ver = ver.substring(0, (ver.indexOf(";") > 0 ? ver.indexOf(";") : ver.length())).trim();
                } else if (userAgentString.contains("msnbot/")) {
                    ua = UserAgent.MSNBOT;
                    ver = userAgentString.substring(userAgentString.indexOf("msnbot/") + 7);
                    ver = ver.substring(0, (ver.indexOf(" ") > 0 ? ver.indexOf(" ") : ver.length())).trim();
                } else if (userAgentString.contains("Chrome/")) {
                    ua = UserAgent.CHROME;
                    ver = userAgentString.substring(userAgentString.indexOf("Chrome/") + 7);
                    ver = ver.substring(0, ver.indexOf(" ")).trim();
                } else if (userAgentString.contains("Safari/")) {
                    ua = UserAgent.SAFARI;
                    final int i = userAgentString.indexOf("Version/");
                    if (i != -1) {
                        ver = userAgentString.substring(i + 8);
                        ver = ver.substring(0, (ver.indexOf(" ") > 0 ? ver.indexOf(" ") : ver.length())).trim();
                    }
                } else if (userAgentString.contains("Opera Mini/")) {
                    ua = UserAgent.OPERA_MINI;
                    ver = userAgentString.substring(userAgentString.indexOf("Opera Mini/") + 11);
                    ver = ver.substring(0, (ver.indexOf("/") > 0 ? ver.indexOf("/") : ver.length())).trim();
                } else if (userAgentString.contains("Opera")) {
                    ua = UserAgent.OPERA;
                    int a = userAgentString.indexOf("Version/");
                    if (a >= 0) {
                        ver = userAgentString.substring(a + 8).trim();
                    } else {
                        int i = userAgentString.indexOf("Opera/");
                        if (i >= 0) {
                            ver = userAgentString.substring(userAgentString.indexOf("Opera/") + 6);
                            ver = ver.substring(0, ver.indexOf(" ")).trim();
                        } else {
                            ver = userAgentString.substring(userAgentString.lastIndexOf("Opera ") + 6);
                            ver = ver.substring(0, (ver.indexOf(" ") > 0 ? ver.indexOf(" ") : ver.length())).trim();
                        }
                    }
                } else if (userAgentString.contains("Firefox/")) {
                    ua = UserAgent.FIREFOX;
                    ver = userAgentString.substring(userAgentString.indexOf("Firefox/") + 8);
                    ver = ver.substring(0, (ver.indexOf(" ") > 0 ? ver.indexOf(" ") : ver.length())).trim();
                } else if (userAgentString.contains("MSIE ")) {
                    ua = UserAgent.IE;
                    ver = userAgentString.substring(userAgentString.indexOf("MSIE ") + 5);
                    final int endIndex = ver.indexOf(";");
                    ver = ver.substring(0, endIndex).trim();
                } else if (userAgentString.contains("Java")) {
                    ua = UserAgent.JAVA;
                    ver = userAgentString.substring(userAgentString.indexOf("/1.") + 3);
                } else if (userAgentString.contains("Mediapartners-Google")) {
                    ua = UserAgent.GOOGLEADSENSE;
                    int i = userAgentString.indexOf("/");
                    if (i != -1) {
                        ver = userAgentString.substring(i + 1);
                    } else {
                        ver = "0.0";
                    }
                }
                if (ver != null && ua != null) {
                    return new WebClient(ua, Integer.parseInt(ver.substring(0, ver.indexOf("."))), ver);
                }
            } catch (Exception ex) {
                log.info("WebClient can't be parsed for: {}", userAgentString);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return userAgent + " " + fullVersion;
    }
}
