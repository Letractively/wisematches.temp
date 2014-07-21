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

    public void setParcelId(Long parcel) {
        this.parcel = parcel;
    }
}
