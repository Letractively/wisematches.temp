package billiongoods.server.warehouse;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Category {
	Integer getId();

	String getName();

	String getDescription();


	int getPosition();


	boolean isFinal();

	boolean isActive();


	Category getParent();

	Genealogy getGenealogy();


	List<Category> getChildren();

	Collection<Parameter> getParameters();

	class Editor {
		private Integer id;
		private String name;
		private String description;
		private Category parent;
		private int position;
		private Set<Integer> attributes;

		public Editor(String name, String description, Category parent, int position, Set<Integer> attributes) {
			this(null, name, description, parent, position, attributes);
		}

		public Editor(Integer id, String name, String description, Category parent, int position, Set<Integer> attributes) {
			this.id = id;
			this.name = name;
			this.description = description;
			this.parent = parent;
			this.position = position;
			this.attributes = attributes;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Category getParent() {
			return parent;
		}

		public void setParent(Category parent) {
			this.parent = parent;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		public Set<Integer> getAttributes() {
			return attributes;
		}

		public void setAttributes(Set<Integer> attributes) {
			this.attributes = attributes;
		}
	}
}
