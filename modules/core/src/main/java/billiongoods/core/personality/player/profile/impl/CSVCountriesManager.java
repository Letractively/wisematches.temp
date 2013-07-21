package billiongoods.core.personality.player.profile.impl;


import billiongoods.core.Language;
import billiongoods.core.personality.player.profile.CountriesManager;
import billiongoods.core.personality.player.profile.Country;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public class CSVCountriesManager implements CountriesManager {
    private final Map<Language, List<Country>> countries = new HashMap<>();
    private final Map<Language, Map<String, Country>> countriesMap = new HashMap<>();

    private static final Pattern PATTERN = Pattern.compile("\"([^\"]+?)\",?|([^,]+),?|,");

    public CSVCountriesManager() {
    }

    public Collection<Country> getCountries(Language language) {
        List<Country> countryList = countries.get(language);
        if (countryList == null) {
            return countries.get(Language.DEFAULT);
        }
        return Collections.unmodifiableList(countryList);
    }

    public Country getCountry(String code, Language language) {
        Map<String, Country> map = countriesMap.get(language);
        if (map == null) {
            map = countriesMap.get(Language.DEFAULT);
        }
        return map.get(code);
    }

    public Collection<Language> getLanguages() {
        return Collections.unmodifiableCollection(countries.keySet());
    }

    public void setCsvCountriesFile(Resource csvCountriesFile) throws IOException {
        parseResource(csvCountriesFile);
    }

    private void parseResource(Resource csvCountriesFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvCountriesFile.getInputStream(), "UTF-8"))) {
            final Matcher matcher = PATTERN.matcher(br.readLine());// parse header with locales

            final List<Language> locales = parseLocales(matcher);
            for (Language language : locales) {
                countries.put(language, new ArrayList<Country>());
                countriesMap.put(language, new HashMap<String, Country>());
            }

            String countryLine = br.readLine();
            while (countryLine != null) {
                matcher.reset(countryLine);

                final boolean codeFound = matcher.find();
                if (!codeFound) {
                    throw new IOException("Countries line can't be parsed. " +
                            "No code found in line: " + countryLine);
                }

                int index = 0;
                final String code = matcher.group(1); //code
                while (matcher.find()) {
                    final String name = matcher.group(1);
                    final Language language = locales.get(index++);
                    final List<Country> countries = this.countries.get(language);
                    final Map<String, Country> map = countriesMap.get(language);

                    final Country country = new Country(code, name, language);
                    countries.add(country);
                    map.put(code, country);
                }
                countryLine = br.readLine();
            }

            //Sort this array.
            for (List<Country> countryList : countries.values()) {
                Collections.sort(countryList);
            }
        }
    }

    private List<Language> parseLocales(Matcher matcher) throws IOException {
        if (!matcher.find()) {
            throw new IOException("CSV file does not contains header record");
        }
        final List<Language> locales = new ArrayList<>();
        while (matcher.find()) {
            locales.add(Language.byCode(matcher.group(1)));
        }
        return locales;
    }
}