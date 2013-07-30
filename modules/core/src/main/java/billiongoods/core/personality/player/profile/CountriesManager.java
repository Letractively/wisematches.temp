package billiongoods.core.personality.player.profile;

import billiongoods.core.Language;

import java.util.Collection;

/**
 * {@code CountriesManager} allows your get a country or list of all known countries.
 *
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public interface CountriesManager {
    /**
     * Returns collection of locales which countries is supported by this manager.
     *
     * @return the unmodifiable collection of supported locales.
     */
    Collection<Language> getLanguages();

    /**
     * Return unmodifiable and sorted by name collection of countries for specified getLocale.
     *
     * @param language the getLocale
     * @return the unmodifiable and sorted by name collection of countries or {@code null} if there is no
     * countries for specified getLocale.
     */
    Collection<Country> getCountries(Language language);

    /**
     * Returns country by it's code in specified getLocale.
     *
     * @param code     the country code
     * @param language
     * @return the country by specified parameters or {@code null} if country is unknown or manager
     * does not have countries for specified getLocale.
     */
    Country getCountry(String code, Language language);
}
