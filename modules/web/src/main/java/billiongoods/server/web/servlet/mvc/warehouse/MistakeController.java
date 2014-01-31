package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.services.mistake.Mistake;
import billiongoods.server.services.mistake.MistakeManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.warehouse.form.MistakeForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/mistake")
public class MistakeController extends AbstractController {
	private MistakeManager mistakeManager;

	public MistakeController() {
	}

	@RequestMapping("/raise.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse viewOrderStatus(@RequestBody MistakeForm form, Locale locale) {
		final Mistake mistake = mistakeManager.raiseMistake(form.getProductId(), form.getDescription(), form.getScope());
		return responseFactory.success(mistake);
	}


	@Autowired
	public void setMistakeManager(MistakeManager mistakeManager) {
		this.mistakeManager = mistakeManager;
	}
}
