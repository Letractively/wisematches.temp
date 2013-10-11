package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Attribute {
	Integer getId();

	String getName();

	String getUnit();

	int getPriority();

	String getDescription();

	AttributeType getAttributeType();

	class Editor {
		private Integer id;
		private String name;
		private String unit;
		private int priority;
		private String description;
		private AttributeType attributeType;

		public Editor() {
		}

		public Editor(String name, String unit, String description, AttributeType attributeType) {
			this(null, name, unit, description, attributeType);
		}

		public Editor(Integer id, String name, String unit, String description, AttributeType attributeType) {
			this.id = id;
			this.name = name;
			this.unit = unit;
			this.description = description;
			this.attributeType = attributeType;
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

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public AttributeType getAttributeType() {
			return attributeType;
		}

		public void setAttributeType(AttributeType attributeType) {
			this.attributeType = attributeType;
		}

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public void init(Attribute attribute) {
			this.id = attribute.getId();
			this.name = attribute.getName();
			this.unit = attribute.getUnit();
			this.priority = attribute.getPriority();
			this.description = attribute.getDescription();
			this.attributeType = attribute.getAttributeType();
		}
	}
}
