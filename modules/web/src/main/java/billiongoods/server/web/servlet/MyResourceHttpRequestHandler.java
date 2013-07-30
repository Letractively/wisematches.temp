package billiongoods.server.web.servlet;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MyResourceHttpRequestHandler extends ResourceHttpRequestHandler {
	public MyResourceHttpRequestHandler() {
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		checkAndPrepare(request, response, true);
		Resource resource = getResource(request);
		if (resource == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		MediaType mediaType = getMediaType(resource);
		if (new ServletWebRequest(request, response).checkNotModified(resource.lastModified())) {
			return;
		}
		setHeaders(response, resource, mediaType);

		// content phase
		if (METHOD_HEAD.equals(request.getMethod())) {
			return;
		}
		writeContent(response, resource);
	}
}