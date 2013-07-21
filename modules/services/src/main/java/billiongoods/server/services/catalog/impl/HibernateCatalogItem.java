package billiongoods.server.services.catalog.impl;

import billiongoods.server.services.catalog.CatalogItem;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_catalog")
public class HibernateCatalogItem implements CatalogItem {
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

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = true, targetEntity = HibernateCatalogItem.class)
    @JoinColumn(name = "parent", nullable = true)
    private HibernateCatalogItem parent;

    @OrderColumn(name = "position")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true, targetEntity = HibernateCatalogItem.class)
    private List<HibernateCatalogItem> children = new ArrayList<>();

    protected HibernateCatalogItem() {
    }

    protected HibernateCatalogItem(String name, HibernateCatalogItem parent) {
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
            CatalogItem p = parent;
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
    public CatalogItem getParent() {
        return parent;
    }

    @Override
    public List<HibernateCatalogItem> getCatalogItems() {
        return children;
    }

    void addChild(HibernateCatalogItem item) {
        if (item.parent != null) {
            throw new IllegalArgumentException("Item already has a parent");
        }
        item.parent = this;
        children.add(item);
    }

    void removeFromParent() {
        parent.removeChild(this);
    }

    void removeChild(HibernateCatalogItem item) {
        if (item.parent != this) {
            throw new IllegalArgumentException("Item doesn't belong to this item");
        }
        item.parent = null;
        children.remove(item);
    }

    @Override
    public String toString() {
        return "HibernateCatalogItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
