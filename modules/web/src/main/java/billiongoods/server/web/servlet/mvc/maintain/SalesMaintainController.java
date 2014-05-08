package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.sales.SalesOperation;
import billiongoods.server.services.sales.SalesOperationManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.SalesOperationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/sales")
public class SalesMaintainController extends AbstractController {
	private SalesOperationManager salesOperationManager;

	public SalesMaintainController() {
	}

	@RequestMapping(value = "/operation", method = RequestMethod.GET)
	public String viewOperationDates(@ModelAttribute("form") SalesOperationForm form) {
		final SalesOperation salesOperation = salesOperationManager.getSalesOperation();
		if (salesOperation == null) {
			form.setStopSalesDate("");
			form.setStartSalesDate("");
		} else {
			form.setStopSalesDate(salesOperation.getStopSalesDate().format(DateTimeFormatter.ISO_DATE));
			form.setStartSalesDate(salesOperation.getStartSalesDate().format(DateTimeFormatter.ISO_DATE));
		}
		return "/content/maintain/operation";
	}

	@RequestMapping(value = "/operation", method = RequestMethod.POST)
	public String updateOperationDates(@ModelAttribute("form") SalesOperationForm form, Errors errors, Model model) {
		LocalDateTime stop = null;
		LocalDateTime start = null;

		if ("set".equals(form.getAction())) {
			final String stopSalesDate = form.getStopSalesDate();
			if (stopSalesDate != null && !stopSalesDate.isEmpty()) {
				try {
					stop = LocalDateTime.of(LocalDate.parse(stopSalesDate, DateTimeFormatter.ISO_DATE), LocalTime.MIDNIGHT);
				} catch (Exception ex) {
					ex.printStackTrace();
					errors.rejectValue("stopSalesDate", "incorrect.stopSalesDate", ex.getMessage());
				}
			}

			final String startSalesDate = form.getStartSalesDate();
			if (startSalesDate != null && !startSalesDate.isEmpty()) {
				try {
					start = LocalDateTime.of(LocalDate.parse(startSalesDate, DateTimeFormatter.ISO_DATE), LocalTime.MIDNIGHT);
				} catch (Exception ex) {
					ex.printStackTrace();
					errors.rejectValue("startSalesDate", "incorrect.startSalesDate", ex.getMessage());
				}
			}
		}

		if (!errors.hasErrors()) {
			try {
				if (stop != null && start != null && stop.isBefore(start)) {
					salesOperationManager.closeSales(stop, start);
				} else {
					salesOperationManager.openSales();
				}
			} catch (Exception ex) {
				errors.rejectValue("stopSalesDate", "error.internal", ex.getMessage());
				errors.rejectValue("startSalesDate", "error.internal", ex.getMessage());
			}
		}
		return viewOperationDates(form);
	}

	@Autowired
	public void setSalesOperationManager(SalesOperationManager salesOperationManager) {
		this.salesOperationManager = salesOperationManager;
	}
}
