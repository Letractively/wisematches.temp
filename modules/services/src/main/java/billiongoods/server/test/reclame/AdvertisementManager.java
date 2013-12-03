package billiongoods.server.test.reclame;

import billiongoods.core.Language;

import java.util.Locale;

/**
 * This is ads provider manager that contains list of {@code AdvertisementBlock} blocks and provides
 * ability to get a block by it's name.
 * <p/>
 * The ads content can be loaded from many places. Please check implementations to get more info.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface AdvertisementManager {
	/**
	 * Returns an ads block by specified name and for specified language. It's very possible that
	 * provide can use different ads provides for different languages.
	 *
	 * @param name     the name of ads block
	 * @param language the required language
	 * @return the ads block or {@code null} if there is no ads block by specified name or for specified language.
	 */
	AdvertisementBlock getAdvertisementBlock(String name, Locale language);

	/**
	 * Returns an ads block by specified name and for specified language. It's very possible that
	 * provide can use different ads provides for different languages.
	 *
	 * @param name     the name of ads block
	 * @param language the required language
	 * @return the ads block or {@code null} if there is no ads block by specified name or for specified language.
	 */
	AdvertisementBlock getAdvertisementBlock(String name, Language language);
}