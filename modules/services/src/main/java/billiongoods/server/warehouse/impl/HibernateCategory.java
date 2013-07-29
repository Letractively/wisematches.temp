package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Genealogy;

import javax.persistence.*;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_category")
public class HibernateCategory implements Category {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "parent")
    private Integer parentId;

    @Transient
    private int level = -1;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "position")
    private int position;

    @Column(name = "active")
    private boolean active;

    @Column(name = "attributeId")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "store_category_attribute", joinColumns = @JoinColumn(name = "categoryId"))
    private Set<Integer> attributeIds = new HashSet<>();

    @Transient
    private Set<Attribute> attributes = new HashSet<>();

    @Transient
    private transient Genealogy genealogy;

    @Transient
    private transient HibernateCategory parent;

    @Transient
    private transient List<Category> children = new ArrayList<>();

    protected HibernateCategory() {
    }

    protected HibernateCategory(String name, String description, HibernateCategory parent) {
        this.name = name;
        this.description = description;
        if (parent != null) {
            this.parentId = parent.id;
            preInit(parent);
        }
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public int getLevel() {
        if (level == -1) {
            int i = 0;
            Category p = parent;
            while (p != null) {
                i++;
                p = p.getParent();
            }
            level = i;
        }
        return level;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isFinal() {
        return children.size() == 0;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Category getParent() {
        return parent;
    }

    @Override
    public Genealogy getGenealogy() {
        if (genealogy == null) {
            genealogy = new Genealogy(this);
        }
        return genealogy;
    }

    @Override
    public List<Category> getChildren() {
        return children;
    }

    @Override
    public Set<Attribute> getAttributes() {
        return attributes;
    }

    int getPosition() {
        return position;
    }

    Integer getParentId() {
        return parentId;
    }

    void preInit(HibernateCategory parentCategory) {
        if (parentCategory == null && this.parentId == null) {
            return;
        }

        if (parentCategory == null || this.parentId == null || !parentCategory.id.equals(this.parentId)) {
            throw new IllegalArgumentException("Incorrect parent id");
        }
        this.parent = parentCategory;
        parentCategory.children.add(this);
    }

    void initialize(AttributeManager attributeManager) {
        Collections.sort(children, COMPARATOR);

        for (Integer attributeId : attributeIds) {
            final Attribute attribute = attributeManager.getAttribute(attributeId);
            if (attribute == null) {
                throw new IllegalStateException("Unknown attribute with id: " + attributeId);
            }
            attributes.add(attribute);
        }
    }

    void addAttribute(Attribute attribute) {
        attributeIds.add(attribute.getId());
        attributes.add(attribute);
    }

    void removeAttribute(Attribute attribute) {
        attributeIds.remove(attribute.getId());
        attributes.remove(attribute);
    }

    static final Comparator<Category> COMPARATOR = new Comparator<Category>() {
        @Override
        public int compare(Category o1, Category o2) {
            return ((HibernateCategory) o1).position - ((HibernateCategory) o2).position;
        }
    };

    @Override
    public String toString() {
        return "HibernateCategory{" + "id=" + id + ", parentId=" + parentId + ", name='" + name + '\'' + ", position=" + position + ", active=" + active + ", childrenCount=" + children.size() + '}';
    }
}
