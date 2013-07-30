package billiongoods.server.web.servlet.mvc.account.form;

import billiongoods.server.services.notify.NotificationScope;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class NotificationsTreeView {
    private Map<String, Set<String>> sections = new HashMap<>();
    private Map<String, Set<String>> subsections = new HashMap<>();
    private Map<String, NotificationScope> scopes = new HashMap<>();

    public NotificationsTreeView(Map<String, NotificationScope> descriptors) {
        for (Map.Entry<String, NotificationScope> descriptor : descriptors.entrySet()) {
            final String code = descriptor.getKey();
            final String[] tokens = code.split("\\.");
            final String section = tokens[0] + "." + tokens[1];
            final String subsection = section + "." + (tokens.length > 3 ? tokens[2] : "common");

            Set<String> strings = sections.get(section);
            if (strings == null) {
                strings = new TreeSet<>();
                sections.put(section, strings);
            }
            strings.add(subsection);

            Set<String> strings2 = subsections.get(subsection);
            if (strings2 == null) {
                strings2 = new TreeSet<>();
                subsections.put(subsection, strings2);
            }
            strings2.add(code);

            scopes.put(code, descriptor.getValue());
        }
    }

    public Set<String> getSections() {
        return sections.keySet();
    }

    public Set<String> getSubsections(String section) {
        return sections.get(section);
    }

    public Set<String> getCodes(String subsection) {
        return subsections.get(subsection);
    }

    public NotificationScope getScope(String code) {
        return scopes.get(code);
    }
}
