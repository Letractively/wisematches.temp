package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.warehouse.Group;
import billiongoods.server.warehouse.RelationshipManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.GroupForm;
import billiongoods.server.web.servlet.mvc.maintain.form.GroupItemForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/group")
public class RelationshipsMaintainController extends AbstractController {
	private RelationshipManager relationshipManager;

	public RelationshipsMaintainController() {
	}

	@RequestMapping("")
	public String viewGroup(@ModelAttribute("form") GroupForm form, Model model) {
		if (form.getId() != null) {
			final Group group = relationshipManager.getGroup(form.getId());
			form.setName(group.getName());
			form.setType(group.getType());
			form.setCategoryId(group.getCategoryId());
			model.addAttribute("group", group);
		}
		return "/content/maintain/group";
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String updateGroup(@ModelAttribute("form") GroupForm form, Errors errors, Model model) {
		if (form.getName() == null || form.getName().isEmpty()) {
			errors.rejectValue("name", "group.error.name.empty");
		}

		if (!errors.hasErrors()) {
			if ("search".equalsIgnoreCase(form.getAction())) {
				model.addAttribute("groups", relationshipManager.searchGroups(form.getName()));
			} else if ("remove".equalsIgnoreCase(form.getAction())) {
				relationshipManager.removeGroup(form.getId());
			} else {
				Group group;
				if ("update".equalsIgnoreCase(form.getAction())) {
					group = relationshipManager.updateGroup(form.getId(), form.getName(), form.getType(), form.getCategoryId());
				} else {
					group = relationshipManager.createGroup(form.getName(), form.getType(), form.getCategoryId());
				}
				return "redirect:/maintain/group?id=" + group.getId();
			}
		}
		return "/content/maintain/group";
	}

	@RequestMapping("relationship.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse processGroupActionAjax(@RequestParam("action") String action, @RequestBody GroupItemForm form) {
		Group group = null;
		if ("remove".equalsIgnoreCase(action)) {
			group = relationshipManager.removeGroupItem(form.getGroupId(), form.getProductId());
		} else if ("add".equalsIgnoreCase(action)) {
			group = relationshipManager.addGroupItem(form.getGroupId(), form.getProductId());
		}
		return group == null ? responseFactory.failure("Not updated") : responseFactory.success();
	}

	@Autowired
	public void setRelationshipManager(RelationshipManager relationshipManager) {
		this.relationshipManager = relationshipManager;
	}
}
