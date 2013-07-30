package billiongoods.server.services.reclame.impl;

import billiongoods.core.Language;
import billiongoods.server.services.reclame.AdvertisementBlock;
import billiongoods.server.services.reclame.AdvertisementProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.UrlResource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class XMLAdvertisementManagerTest {
    private XMLAdvertisementManager advertisementManager;

    public XMLAdvertisementManagerTest() {
    }

    @Before
    public void setUp() throws IOException, SAXException, ParserConfigurationException {
        advertisementManager = new XMLAdvertisementManager();
        advertisementManager.setAdsResource(new UrlResource(getClass().getResource("/adsBlocks.xml")));
    }

    @Test
    public void test() {
        assertBlock(advertisementManager.getAdvertisementBlock("dashboard", Language.RU),
                "ca-pub-1510155976245826", "s1_ru", 1, 2);
        assertBlock(advertisementManager.getAdvertisementBlock("dashboard", Language.EN),
                "ca-pub-1510155976245826", "s1_en", 3, 4);
        assertBlock(advertisementManager.getAdvertisementBlock("playboard", Language.RU),
                "ca-pub-1510155976245826", "s2_ru", 5, 6);
        assertNull(advertisementManager.getAdvertisementBlock("playboard", Language.EN));
    }

    private void assertBlock(AdvertisementBlock block, String client, String slot, int width, int height) {
        assertEquals(AdvertisementProvider.GOOGLE, block.getProvider());
        assertEquals(client, block.getClient());
        assertEquals(slot, block.getSlot());
        assertEquals(width, block.getWidth());
        assertEquals(height, block.getHeight());
    }
}
