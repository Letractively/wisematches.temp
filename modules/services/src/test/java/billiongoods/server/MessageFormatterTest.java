package billiongoods.server;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MessageFormatterTest {
    public MessageFormatterTest() {
    }

    @Test
    public void asd() {
        String a = "google|smklimenko@gmail.com";

        final int i = a.indexOf('|');
        System.out.println(a.substring(0, i));
        System.out.println(a.substring(i + 1));
    }

    @Test
    public void testProductCode() {
        assertNull(MessageFormatter.extractProductId("124334"));
        assertNull(MessageFormatter.extractProductId("A24334"));
        assertNull(MessageFormatter.extractProductId("A1243343"));
        assertNull(MessageFormatter.extractProductId("AB43343"));
        assertNull(MessageFormatter.extractProductId("A433B43"));

        assertEquals((Integer) 0, MessageFormatter.extractProductId("A000000"));
        assertEquals((Integer) 124334, MessageFormatter.extractProductId("A124334"));
    }
}
