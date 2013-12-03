package billiongoods.server.test.reclame.impl;


import billiongoods.core.Language;
import billiongoods.server.test.reclame.AdvertisementBlock;
import billiongoods.server.test.reclame.AdvertisementManager;
import billiongoods.server.test.reclame.AdvertisementProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class XMLAdvertisementManager implements AdvertisementManager {
	private final Map<AdsBlockKey, AdvertisementBlock> advertisementBlocks = new HashMap<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.reclame.XMLAdvertisementManager");

	private static final DocumentBuilderFactory BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

	public XMLAdvertisementManager() {
	}

	@Override
	public AdvertisementBlock getAdvertisementBlock(String name, Locale language) {
		AdvertisementBlock advertisementBlock = advertisementBlocks.get(new AdsBlockKey(name, language));
		if (advertisementBlock == null) {
			log.error("No adds block for language {} by name {}", language, name);
		}
		return advertisementBlock;
	}

	@Override
	public AdvertisementBlock getAdvertisementBlock(String name, Language language) {
		return getAdvertisementBlock(name, language.getLocale());
	}

	public void setAdsResource(Resource resource) throws ParserConfigurationException, IOException, SAXException {
		advertisementBlocks.clear();

		if (resource != null && resource.exists() && resource.isReadable()) {
			final DocumentBuilder builder = BUILDER_FACTORY.newDocumentBuilder();
			final Document document = builder.parse(resource.getInputStream());

			final Element root = document.getDocumentElement();
			final String client = root.getAttribute("client");

			final NodeList blocks = root.getElementsByTagName("block");
			for (int i = 0; i < blocks.getLength(); i++) {
				final Element block = (Element) blocks.item(i);
				final String name = block.getAttribute("name");

				final NodeList items = block.getElementsByTagName("item");
				for (int j = 0; j < items.getLength(); j++) {
					final Element item = (Element) items.item(j);
					final String slot = item.getAttribute("slot");
					final Language language = Language.byCode(item.getAttribute("language"));
					final int width = Integer.valueOf(item.getAttribute("width"));
					final int height = Integer.valueOf(item.getAttribute("height"));

					advertisementBlocks.put(new AdsBlockKey(name, language), new AdvertisementBlock(client, slot, width, height, AdvertisementProvider.GOOGLE));
				}
			}
		}
	}

	private static final class AdsBlockKey {
		private final String name;
		private final Locale locale;

		private AdsBlockKey(String name, Language language) {
			this(name, language.getLocale());
		}

		private AdsBlockKey(String name, Locale locale) {
			this.name = name;
			this.locale = locale;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			AdsBlockKey that = (AdsBlockKey) o;
			return locale.equals(that.locale) && name.equals(that.name);
		}

		@Override
		public int hashCode() {
			int result = name.hashCode();
			result = 31 * result + locale.hashCode();
			return result;
		}
	}
}
