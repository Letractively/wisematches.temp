package billiongoods.server.web.servlet.mvc.maintain.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class SalesOperationForm {
	private String stopSalesDate;
	private String startSalesDate;

	private String action;

	public SalesOperationForm() {
	}

	public String getStopSalesDate() {
		return stopSalesDate;
	}

	public void setStopSalesDate(String stopSalesDate) {
		this.stopSalesDate = stopSalesDate;
	}

	public String getStartSalesDate() {
		return startSalesDate;
	}

	public void setStartSalesDate(String startSalesDate) {
		this.startSalesDate = startSalesDate;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
