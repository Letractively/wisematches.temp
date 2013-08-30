package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.warehouse.Group;
import billiongoods.server.warehouse.RelationshipManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.GroupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/group")
public class GroupMaintainController extends AbstractController {
	private RelationshipManager relationshipManager;

	public GroupMaintainController() {
	}

	@RequestMapping("")
	public String createGroupView(@ModelAttribute("form") GroupForm form, Model model) {
		if (form.getId() != null) {
			final Group group = relationshipManager.getGroup(form.getId());
			form.setName(group.getName());
			model.addAttribute("group", group);
		}
		return "/content/maintain/group";
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String createGroupAction(@ModelAttribute("form") GroupForm form, Errors errors, Model model) {
		if (form.getName() == null || form.getName().isEmpty()) {
			errors.rejectValue("name", "group.error.name.empty");
		}

		if (!errors.hasErrors()) {
			if ("remove".equalsIgnoreCase(form.getAction())) {
				relationshipManager.removeGroup(form.getId());
			} else {
				Group group;
				if ("update".equalsIgnoreCase(form.getAction())) {
					group = relationshipManager.updateGroup(form.getId(), form.getName());
				} else {
					group = relationshipManager.createGroup(form.getName());
				}
				return "redirect:/maintain/group?id=" + group.getId();
			}
		}
		return "/content/maintain/group";
	}

	@Autowired
	public void setRelationshipManager(RelationshipManager relationshipManager) {
		this.relationshipManager = relationshipManager;
	}
}
