package billiongoods.server.web.servlet.mvc.warehouse.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ParcelViewForm extends OrderViewForm {
    private Long parcel;

	public ParcelViewForm() {
	}

    public ParcelViewForm(Long order, Long parcel, String email) {
        super(order, email);
        this.parcel = parcel;
    }

    public Long getParcel() {
        return parcel;
    }

	public void setParcel(Long parcel) {
		this.parcel = parcel;
    }

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ParcelViewForm{");
		sb.append("parcel=").append(parcel);
		sb.append('}');
		return sb.toString();
	}
}
