package billiongoods.server.web.servlet.mvc.warehouse.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ParcelViewForm extends OrderViewForm {
	private Long parcelId;

	public ParcelViewForm() {
	}

	public ParcelViewForm(Long order, Long parcelId, String email) {
		super(order, email);
		this.parcelId = parcelId;
	}

	public Long getParcelId() {
		return parcelId;
	}

	public void setParcelId(Long parcelId) {
		this.parcelId = parcelId;
	}
}
