package billiongoods.core.personality.player.profile;

import billiongoods.core.Language;

import java.io.Serializable;

/**
 * This is contry object. Each country has a code (for example, ...).
 * <p/>
 * Each country of {@code Comparable} and sorted by it's names.
 * <p/>
 * Two countries are equals if its have the same code. Locale of country (it's name) does not
 * used in comparation.
 *
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public final class Country implements Serializable, Comparable<Country> {
    private final String code;
    private final String name;
    private final Language language;

    public Country(String code, String name, Language language) {
        this.code = code;
        this.name = name;
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Language getLanguage() {
        return language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Country country = (Country) o;
        return code.equals(country.code);

    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return "Country{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public int compareTo(Country country) {
        return name.compareTo(country.name);
    }
}
