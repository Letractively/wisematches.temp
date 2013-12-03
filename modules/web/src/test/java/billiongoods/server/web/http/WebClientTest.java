package billiongoods.server.web.http;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class WebClientTest {
    public WebClientTest() {
    }

    @Test
    public void testOpera() {
        assertClient(UserAgent.OPERA, 12, "Opera/9.80 (Windows NT 6.1; U; es-ES) Presto/2.9.181 Version/12.00");
        assertClient(UserAgent.OPERA, 11, "Opera/9.80 (Android 2.3.3; Linux; Opera Mobi/ADR-1107051709; U; ru) Presto/2.8.149 Version/11.10");
        assertClient(UserAgent.OPERA, 9, "Opera/9.99 (Windows NT 5.1; U; pl) Presto/9.9.9");
        assertClient(UserAgent.OPERA, 9, "Mozilla/5.0 (Windows NT 5.1; U; en-GB; rv:1.8.1) Gecko/20061208 Firefox/2.2.0 Opera 9.61");
        assertClient(UserAgent.OPERA, 8, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) Opera 8.60 [en]");
    }

    @Test
    public void testJava() {
        assertClient(UserAgent.JAVA, 5, "Java/1.5.0_22");
    }

    @Test
    public void testGoogleAdSense() {
        assertClient(UserAgent.GOOGLEADSENSE, 0, "Mediapartners-Google");
        assertClient(UserAgent.GOOGLEADSENSE, 2, "Mediapartners-Google/2.1");
    }

    @Test
    public void testSafari() {
        assertNull(WebClient.detect("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_1) AppleWebKit/535.1 (KHTML, like Gecko) Safari/535.1"));
        assertClient(UserAgent.SAFARI, 5, "Mozilla/5.0 (Windows; U; Windows NT 6.1; tr-TR) AppleWebKit/533.20.25 (KHTML, like Gecko) Version/5.0.4 Safari/533.20.27");
        assertClient(UserAgent.SAFARI, 4, "Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10");
    }

    @Test
    public void testMSIE() {
        assertNull(WebClient.detect("Mozilla/4.0 (compatible- MSIE 6.0- Windows NT 5.1- SV1- .NET CLR 1.1.4322"));
    }

    @Test
    public void testRobots() {
        assertNull(WebClient.detect("Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)"));
        assertNull(WebClient.detect("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)"));
        assertNull(WebClient.detect("Mozilla/5.0 (compatible; news bot /2.1)"));
    }

    private void assertClient(UserAgent u, int v, String a) {
        final WebClient detect = WebClient.detect(a);
        Assert.assertEquals(u, detect.getUserAgent());
        Assert.assertEquals(v, detect.getMajorVersion());
    }
}
