package billiongoods.server.web.servlet.mvc.assistance;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AssistanceUnavailableException extends Exception {
	public AssistanceUnavailableException(String pageName, String plain) {
	}
}
