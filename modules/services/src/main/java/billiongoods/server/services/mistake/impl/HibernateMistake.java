package billiongoods.server.services.mistake.impl;

import billiongoods.server.services.mistake.Mistake;
import billiongoods.server.services.mistake.MistakeScope;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "report_mistake")
public class HibernateMistake implements Mistake {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "productId")
	private Integer productId;

	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "description")
	private String description;

	@Column(name = "scope")
	private MistakeScope scope;

	@Column(name = "resolved")
	@Temporal(TemporalType.TIMESTAMP)
	private Date resolved;

	HibernateMistake() {
	}

	public HibernateMistake(Integer productId, String description, MistakeScope scope) {
		this.created = new Date();
		this.productId = productId;
		this.description = description;
		this.scope = scope;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public Integer getProductId() {
		return productId;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public MistakeScope getScope() {
		return scope;
	}

	@Override
	public Date getResolved() {
		return resolved;
	}

	void resolve() {
		this.resolved = new Date();
	}
}
