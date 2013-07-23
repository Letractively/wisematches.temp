package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Genealogy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

	@Transient
	private transient Genealogy genealogy;

	@Transient
	private transient HibernateCategory parent;

	@Transient
	private transient List<Category> children = new ArrayList<>();

	protected HibernateCategory() {
	}

	protected HibernateCategory(String name, HibernateCategory parent) {
		this.name = name;
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

	void postInit() {
		Collections.sort(children, COMPARATOR);
	}

	String toString(int depth) {
		final StringBuilder b = new StringBuilder();
		toString(0, depth, b, "   ");
		return b.toString();
	}

	private void toString(int level, int depth, StringBuilder b, String spaces) {
		if (level >= depth) {
			return;
		}

		for (Category child : children) {
			for (int i = 0; i < level; i++) {
				b.append(spaces);
			}

			b.append(child.toString());

			toString(level + 1, depth, b, spaces);
		}
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
