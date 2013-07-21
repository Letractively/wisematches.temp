package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Option {
    CharacterType getType();

    List<String> getAllowedValues();
}
