package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Category;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_category")
public class HibernateCategory implements Category {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Transient
    private int level = -1;

    @Column(name = "name")
    private String name;

    @Column(name = "position")
    private int position;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = true, targetEntity = HibernateCategory.class)
    @JoinColumn(name = "parent", nullable = true)
    private HibernateCategory parent;

    @OrderColumn(name = "position")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true, targetEntity = HibernateCategory.class)
    private List<HibernateCategory> children = new ArrayList<>();

    protected HibernateCategory() {
    }

    protected HibernateCategory(String name, HibernateCategory parent) {
        this.name = name;
        parent.addChild(this);
    }

    @Override
    public int getId() {
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
    public boolean isFinal() {
        return children.size() == 0;
    }

    @Override
    public Category getParent() {
        return parent;
    }

    @Override
    public List<HibernateCategory> getCatalogItems() {
        return children;
    }

    void addChild(HibernateCategory item) {
        if (item.parent != null) {
            throw new IllegalArgumentException("Item already has a parent");
        }
        item.parent = this;
        children.add(item);
    }

    void removeFromParent() {
        parent.removeChild(this);
    }

    void removeChild(HibernateCategory item) {
        if (item.parent != this) {
            throw new IllegalArgumentException("Item doesn't belong to this item");
        }
        item.parent = null;
        children.remove(item);
    }

    @Override
    public String toString() {
        return "HibernateCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
