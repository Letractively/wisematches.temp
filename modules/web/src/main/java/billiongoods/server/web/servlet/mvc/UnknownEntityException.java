package billiongoods.server.web.servlet.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UnknownEntityException extends RuntimeException {
    private final Object entityId;
    private final String entityType;

    public UnknownEntityException(Object entityId, String entityType) {
        super("Entity " + entityId + " of " + entityType + " not found");
        this.entityId = entityId;
        this.entityType = entityType;
    }

    public Object getEntityId() {
        return entityId;
    }

    public String getEntityType() {
        return entityType;
    }
}
