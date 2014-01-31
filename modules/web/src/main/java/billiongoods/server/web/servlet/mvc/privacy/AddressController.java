package billiongoods.server.web.servlet.mvc.privacy;

import billiongoods.server.services.address.AddressBook;
import billiongoods.server.services.address.AddressBookManager;
import billiongoods.server.services.address.AddressRecord;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.privacy.form.AddressForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/privacy/address")
public class AddressController extends AbstractController {
	private AddressBookManager addressBookManager;

	public AddressController() {
	}

	@RequestMapping("")
	public String viewAddressBook(Model model) {
		final AddressBook addressBook = addressBookManager.getAddressBook(getMember());
		model.addAttribute("addressBook", addressBook);
		return "/content/privacy/address";
	}

	@RequestMapping("/create.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse createAddressAjax(@RequestBody AddressForm form, Locale locale) {
		final Map<String, String> validate = validation(form, locale);
		if (!validate.isEmpty()) {
			return responseFactory.failure(validate);
		}
		addressBookManager.addAddress(getMember(), form.toAddressRecord());
		return responseFactory.success();
	}

	private Map<String, String> validation(AddressForm form, Locale locale) {
		final Map<String, String> validate = form.validate();
		for (Map.Entry<String, String> entry : validate.entrySet()) {
			entry.setValue(messageSource.getMessage(entry.getValue(), locale));
		}
		return validate;
	}

	@RequestMapping("/remove.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse removeAddressAjax(@RequestBody AddressForm form, Locale locale) {
		final AddressBook addressBook = addressBookManager.getAddressBook(getMember());
		final AddressRecord addressRecord = addressBook.getAddressRecord(form.getId());
		if (addressRecord == null) {
			return responseFactory.failure("privacy.address.err.unknown", locale);
		}

		addressBookManager.removeAddress(getMember(), addressRecord);
		return responseFactory.success();
	}

	@RequestMapping("/update.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse updateAddressAjax(@RequestBody AddressForm form, Locale locale) {
		final Map<String, String> validate = validation(form, locale);
		if (!validate.isEmpty()) {
			return responseFactory.failure(validate);
		}

		final AddressBook addressBook = addressBookManager.getAddressBook(getMember());

		final AddressRecord addressRecord = addressBook.getAddressRecord(form.getId());
		if (addressRecord == null) {
			return responseFactory.failure("privacy.address.err.unknown", locale);
		}

		addressBookManager.updateAddress(getMember(), addressRecord, form.toAddressRecord());
		return responseFactory.success();
	}

	@Autowired
	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}
}
