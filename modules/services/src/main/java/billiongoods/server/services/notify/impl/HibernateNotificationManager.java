package billiongoods.server.services.notify.impl;

import billiongoods.core.Player;
import billiongoods.server.services.notify.NotificationDescriptor;
import billiongoods.server.services.notify.NotificationManager;
import billiongoods.server.services.notify.NotificationScope;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateNotificationManager implements NotificationManager, InitializingBean {
    private SessionFactory sessionFactory;
    private final Map<String, NotificationDescriptor> descriptorsMap = new HashMap<>();

    public HibernateNotificationManager() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final Session session = sessionFactory.openSession();
        checkTable(session, "notification_settings");
//        checkTable(session, "notification_timestamp");
    }

    @Override
    public Set<String> getNotificationCodes() {
        return Collections.unmodifiableSet(descriptorsMap.keySet());
    }

    @Override
    public NotificationDescriptor getDescriptor(String code) {
        return descriptorsMap.get(code);
    }

    @Override
    public NotificationScope getNotificationScope(String code, Player member) {
        final NotificationDescriptor descriptor = descriptorsMap.get(code);
        if (descriptor == null) {
            return null;
        }
        final Session session = sessionFactory.getCurrentSession();
        final Query sqlQuery = session.createSQLQuery("SELECT `" + code + "` FROM notification_settings WHERE pid=:pid");
        sqlQuery.setCacheable(true);
        sqlQuery.setParameter("pid", member.getId());

        final List list = sqlQuery.list();
        if (list.isEmpty()) {
            return descriptor.getScope();
        }

        final Number scopeOrdinal = (Number) list.get(0);
        return scopeOrdinal == null ? null : NotificationScope.values()[scopeOrdinal.intValue()];
    }

    @Override
    public NotificationScope setNotificationScope(String code, Player player, NotificationScope scope) {
        final String query = "INSERT INTO notification_settings (`pid`, `" + code + "`) VALUES (:pid, :scope) ON DUPLICATE KEY UPDATE `" + code + "`=:scope";
        final Session session = sessionFactory.getCurrentSession();
        final Query sqlQuery = session.createSQLQuery(query).setParameter("pid", player.getId());
        sqlQuery.setCacheable(true);
        sqlQuery.setParameter("pid", player.getId());
        sqlQuery.setParameter("scope", scope == null ? null : scope.ordinal());
        sqlQuery.executeUpdate();
        return null;
    }

    private void checkTable(Session session, String table) {
        final SQLQuery query = session.createSQLQuery("DESCRIBE " + table);

        final List list = query.list();
        final Set<String> names = new HashSet<>();

        for (Object row : list) {
            names.add((String) ((Object[]) row)[0]);
        }
        if (!names.containsAll(descriptorsMap.keySet())) {
            final Set<String> unknown = new HashSet<>(descriptorsMap.keySet());
            unknown.removeAll(names);
            throw new IllegalStateException("Table doesn't have columns for fields: " + unknown + " for " + table);
        }
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setNotificationDescriptors(Collection<NotificationDescriptor> descriptors) {
        final Map<String, NotificationDescriptor> desc = new HashMap<>();
        if (descriptors != null) {
            for (NotificationDescriptor d : descriptors) {
                if (desc.put(d.getCode(), d) != null) {
                    throw new IllegalArgumentException("Specified name already contains d for " + d.getCode());
                }
            }
        }
        this.descriptorsMap.clear();

        if (descriptors != null) {
            this.descriptorsMap.putAll(desc);
        }
    }
}
