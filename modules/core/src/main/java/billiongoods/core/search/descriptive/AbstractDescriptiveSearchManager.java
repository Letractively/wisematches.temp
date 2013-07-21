package billiongoods.core.search.descriptive;

import billiongoods.core.Personality;
import billiongoods.core.search.Order;
import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToBeanResultTransformer;

import java.util.List;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class AbstractDescriptiveSearchManager<T, C> implements DescriptiveSearchManager<T, C> {
    private SessionFactory sessionFactory;

    private final boolean sql;
    private final SearchableDescriptor entityDescriptor;

    protected AbstractDescriptiveSearchManager(Class<T> type) {
        this(type, false);
    }

    protected AbstractDescriptiveSearchManager(Class<T> type, boolean sql) {
        this.sql = sql;
        entityDescriptor = SearchableDescriptor.valueOf(type);
    }

    @Override
    public SearchableDescriptor getEntityDescriptor() {
        return entityDescriptor;
    }

    @Override
    public <Ctx extends C> int getTotalCount(Personality person, Ctx context) {
        final Session session = sessionFactory.getCurrentSession();
        final StringBuilder query = new StringBuilder();
        query.append("select ");

        query.append("count(");
        final SearchableProperty da = entityDescriptor.getUniformityProperty();
        if (da != null) {
            query.append("distinct ");
            query.append(da.column());
        } else {
            query.append("*");
        }
        query.append(")");
        query.append(" from ");
        query.append(getEntitiesList(context));

        String whereCriterias = getWhereCriterias(context);
        if (whereCriterias != null) {
            query.append(" where ");
            query.append(whereCriterias);
        }

        final Query query1 = sql ? session.createSQLQuery(query.toString()) : session.createQuery(query.toString());
        query1.setCacheable(true);
        initParameters(person, context, query1);
        return ((Number) query1.uniqueResult()).intValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Ctx extends C> List<T> searchEntities(Personality person, Ctx context, Orders orders, Range range) {
        final Session session = sessionFactory.getCurrentSession();
        final StringBuilder query = new StringBuilder();
        query.append("select ");

        final SearchableBean bean = entityDescriptor.getBeanAnnotation();
        final SearchableProperty attribute = entityDescriptor.getUniformityProperty();
        if (attribute != null) {
            query.append("distinct ");
            query.append(attribute.column());
            query.append(" as ");
            query.append(bean.uniformityProperty());
            query.append(", ");
        }

        final Set<String> propertyNames = entityDescriptor.getPropertyNames();
        for (String name : propertyNames) {
            final SearchableProperty value = entityDescriptor.getProperty(name);
            query.append(value.column());
            query.append(" as ");
            query.append(name);
            query.append(", ");
        }
        query.setLength(query.length() - 2);

        query.append(" from ");
        query.append(getEntitiesList(context));

        String whereCriterias = getWhereCriterias(context);
        if (whereCriterias != null) {
            query.append(" where ");
            query.append(whereCriterias);
        }

        String groupCriterias = getGroupCriterias(context);
        if (groupCriterias != null) {
            query.append(" group by ");
            query.append(groupCriterias);
        }

        if (orders != null) {
            query.append(" order by ");
            for (Order o : orders) {
                final SearchableProperty a = entityDescriptor.getProperty(o.getPropertyName());
                query.append(a.column());
                query.append(o.isAscending() ? " asc" : " desc");
                query.append(", ");
            }
            query.setLength(query.length() - 2);
        }

        final Query query1;
        if (sql) {
            SQLQuery q = session.createSQLQuery(query.toString());
            q.addEntity(entityDescriptor.getDesiredEntityType());
            query1 = q;
        } else {
            query1 = session.createQuery(query.toString());
            query1.setResultTransformer(new AliasToBeanResultTransformer(entityDescriptor.getDesiredEntityType()));
        }
        query1.setCacheable(true);
        initParameters(person, context, query1);
        if (range != null) {
            range.apply(query1);
        }
        return query1.list();
    }

    private <Ctx extends C> void initParameters(Personality person, Ctx context, Query query1) {
        final String[] namedParameters = query1.getNamedParameters();
        for (String namedParameter : namedParameters) {
            if ("pid".equals(namedParameter)) {
                query1.setParameter("pid", person != null ? person.getId() : null);
            } else {
                query1.setParameter(namedParameter, getNamedParameter(context, namedParameter));
            }
        }
    }

    protected abstract String getEntitiesList(final C context);

    protected abstract String getWhereCriterias(final C context);

    protected abstract String getGroupCriterias(final C context);

    protected abstract Object getNamedParameter(final C context, final String name);

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
