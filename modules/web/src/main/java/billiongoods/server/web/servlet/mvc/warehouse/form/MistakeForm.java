package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.server.services.mistake.MistakeScope;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MistakeForm {
	private Integer productId;
	private String description;
	private MistakeScope scope;

	public MistakeForm() {
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MistakeScope getScope() {
		return scope;
	}

	public void setScope(MistakeScope scope) {
		this.scope = scope;
	}
}
