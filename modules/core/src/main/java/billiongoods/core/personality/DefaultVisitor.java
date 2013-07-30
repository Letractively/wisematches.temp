package billiongoods.core.personality;

import billiongoods.core.Language;
import billiongoods.core.Visitor;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class DefaultVisitor extends Visitor {
    public DefaultVisitor(Language language) {
        super(language);
    }
}
