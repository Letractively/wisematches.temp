package billiongoods.server.web.servlet.sdo;

import org.springframework.http.HttpEntity;

/**
 * This is service response object that is returned by
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class ServiceResponse extends HttpEntity<ServiceResponse.ResponseBody> {
    public ServiceResponse(ResponseBody body) {
        super(body);
    }

    public static class ResponseBody {
        private final boolean success;

        private ResponseBody(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public static final class Failure extends ServiceResponse.ResponseBody {
        private final String code;
        private final String message;
        private final Object data;

        public Failure(Object data) {
            this(null, data, null);
        }

        public Failure(String code, String message) {
            this(code, null, message);
        }

        public Failure(String code, Object data, String message) {
            super(false);
            this.code = code;
            this.data = data;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public Object getData() {
            return data;
        }

        public String getMessage() {
            return message;
        }
    }

    public static final class Success extends ServiceResponse.ResponseBody {
        private final Object data;

        public Success(Object data) {
            super(true);
            this.data = data;
        }

        public Object getData() {
            return data;
        }
    }
}