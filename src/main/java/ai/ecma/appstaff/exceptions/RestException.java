package ai.ecma.appstaff.exceptions;

import ai.ecma.appstaff.payload.ErrorData;
import ai.ecma.appstaff.utils.RestConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.List;


@Data
public class RestException extends RuntimeException {

    private String userMsg;
    private HttpStatus status;

    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    private List<ErrorData> errors;
    private Integer errorCode;

    private RestException(String resourceName, String fieldName, Object fieldValue, String userMsg, HttpStatus status) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.userMsg = userMsg;
        this.status = status;
    }

    public RestException(String userMsg, HttpStatus status) {
        super(userMsg);
        this.userMsg = userMsg;
        this.status = status;
    }

    private RestException(HttpStatus status, List<ErrorData> errors) {
        this.status = status;
        this.errors = errors;
    }

    public static RestException restThrow(String message, HttpStatus httpStatus) {
        return new RestException(message, httpStatus);
    }

    public static RestException restThrow(String message) {
        return new RestException(message, HttpStatus.BAD_REQUEST);
    }

    public static RestException restThrow(String resourceName, String fieldName, Object fieldValue, String message, HttpStatus status) {
        return new RestException(resourceName, fieldName, fieldValue, message, status);
    }

    public static RestException restThrow(List<ErrorData> errors, HttpStatus status) {
        return new RestException(status, errors);
    }

    public static RestException restThrow(List<ErrorData> errors) {
        return new RestException(HttpStatus.BAD_REQUEST, errors);
    }


    private RestException(String resourceName, String fieldName, Object fieldValue, String userMsg) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.userMsg = userMsg;
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = RestConstants.NO_ITEMS_FOUND;
    }
}
