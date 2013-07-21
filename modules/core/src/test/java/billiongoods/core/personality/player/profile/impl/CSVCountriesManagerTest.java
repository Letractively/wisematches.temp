package billiongoods.core.personality.player.profile.impl;

import billiongoods.core.Language;
import billiongoods.core.personality.player.profile.Country;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public class CSVCountriesManagerTest {
    public CSVCountriesManagerTest() {
    }

    @Test
    public void testParse() throws IOException {
        final CSVCountriesManager manager = new CSVCountriesManager();
        manager.setCsvCountriesFile(new ClassPathResource("/i18n/countries.csv"));

        assertEquals(2, manager.getLanguages().size());
        Assert.assertTrue(manager.getLanguages().contains(Language.EN));
        Assert.assertTrue(manager.getLanguages().contains(Language.RU));

        assertEquals(244, manager.getCountries(Language.EN).size());
        assertEquals(244, manager.getCountries(Language.RU).size());
        assertEquals(244, manager.getCountries(null).size());  // English getLocale

        final Collection<Language> localeCollection = manager.getLanguages();
        for (Language locale : localeCollection) {
            final List<Country> countries = new ArrayList<>(manager.getCountries(locale));
            Collections.sort(countries);
            assertEquals("Countries list for getLocale '" + locale + "' is unsorted.",
                    countries, manager.getCountries(locale));
        }

        assertEquals("Brazil", manager.getCountry("br", Language.EN).getName());
        assertEquals("Бразилия", manager.getCountry("br", Language.RU).getName());
        assertEquals("Бразилия", manager.getCountry("br", null).getName());
    }
}
