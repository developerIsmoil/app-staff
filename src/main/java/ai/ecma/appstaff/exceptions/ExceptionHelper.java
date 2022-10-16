package ai.ecma.appstaff.exceptions;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.ErrorData;
import ai.ecma.appstaff.payload.ExceptionMessageDTO;
import ai.ecma.appstaff.payload.UserDTO;
import ai.ecma.appstaff.service.rabbitmq.RabbitMQProducerService;
import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.ResponseMessage;
import ai.ecma.appstaff.utils.RestConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionHelper {

    private final RabbitMQProducerService rabbitMQProducerService;

    private static final String NOT_FOUND = "_NOT_FOUND";
    private static final String NOT_NULL = "_NOT_NULL";
    private static final String UK_CONSTRAINT = "_UK_CONSTRAINT";

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private boolean isDev() {
        return activeProfile == null || activeProfile.equals("dev") || activeProfile.equals("ser");
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(MethodArgumentNotValidException ex) {
        sendMessageToTelegramChannel(ex);
        List<ErrorData> errors = new ArrayList<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.forEach(fieldError -> errors.add(new ErrorData(fieldError.getDefaultMessage(), fieldError.getField(), RestConstants.REQUIRED)));
        return new ResponseEntity<>(ApiResult.errorResponse(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {TypeMismatchException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(TypeMismatchException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(HttpMessageNotReadableException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ex.getMessage(), RestConstants.CONFLICT),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(MissingServletRequestParameterException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ServletRequestBindingException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(ServletRequestBindingException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MissingServletRequestPartException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(MissingServletRequestPartException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BindException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(BindException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(NoSuchElementException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ex.getMessage(), 400),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleExceptionAccessDenied(AccessDeniedException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ResponseMessage.EXCEPTION_FORBIDDEN, RestConstants.ACCESS_DENIED),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {MissingPathVariableException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleExceptionNotFound(MissingPathVariableException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ResponseMessage.EXCEPTION_PATH_NOTFOUND, RestConstants.NOT_FOUND),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(NoHandlerFoundException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ex.getMessage(), 404),
                HttpStatus.NOT_FOUND);
    }

    //METHOD XATO BO'LSA
    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(HttpRequestMethodNotSupportedException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ResponseMessage.EXCEPTION_METHOD_NOT_ALLOWED, 405),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = {HttpMediaTypeNotAcceptableException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleExceptionHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ResponseMessage.EXCEPTION_NOT_ACCEPTABLE, 406),
                HttpStatus.NOT_ACCEPTABLE);
    }

    //METHOD XATO BO'LSA
    @ExceptionHandler(value = {HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleExceptionHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ResponseMessage.EXCEPTION_UNSUPPORTED_MEDIA_TYPE, 415),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = {ConversionNotSupportedException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(ConversionNotSupportedException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ex.getMessage(), RestConstants.SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {HttpMessageNotWritableException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(HttpMessageNotWritableException ex) {
        sendMessageToTelegramChannel(ex);
        return new ResponseEntity<>(
                ApiResult.errorResponse(ex.getMessage(), RestConstants.SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(Exception ex) {
        sendMessageToTelegramChannel(ex);
        ex.printStackTrace();
        return new ResponseEntity<>(
                ApiResult.errorResponse(ResponseMessage.SERVER_ERROR, RestConstants.SERVER_ERROR, ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {AsyncRequestTimeoutException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(AsyncRequestTimeoutException ex) {
        sendMessageToTelegramChannel(ex);
        ex.printStackTrace();
        return new ResponseEntity<>(
                ApiResult.errorResponse(ex.getMessage(), 503),
                HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(DataIntegrityViolationException ex) {
        sendMessageToTelegramChannel(ex);
        if (isDev())
            ex.printStackTrace();
        try {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex.getCause();
            SQLException sqlException = ((ConstraintViolationException) ex.getCause()).getSQLException();
            String message = sqlException.getMessage();


            String sqlState = sqlException.getSQLState();

            //"23503" foreign key ulanolmasa
            //"23502" null property uchun

            //agar biror column dan nullable false qo'yilganda tushadigan exception
            if (sqlState.equals("23502")) {
                String columnName = constraintViolationException.getConstraintName().toUpperCase(Locale.ROOT);
                String clientMessage = columnName + NOT_NULL;

                System.out.println(clientMessage);

                return new ResponseEntity<>(
                        ApiResult.errorResponse(clientMessage.toUpperCase(), 400),
                        HttpStatus.BAD_REQUEST
                );
            }
            if (sqlState.equals("23503")) {


                String detail = "Detail:";

                //DETAIL: SO'ZINI INDEKSINI ANIQLAB OLYAPMAN ARENTIR SIFATIDA
                int arentir = message.indexOf(detail);

                //DETAIL SO'ZIDAN KEYINGI OCHILGAN 1-QAVS NI INDEXINI OLIB +1 QO'SHTIM
                int fromColumnName = message.indexOf("(", arentir) + 1;

                //DETAIL SO'ZIDAN KEYINGI YOPILGAN 1-QAVS NI INDEXINI OLIB -3 AYIRDIM
                int toColumnName = message.indexOf(")", fromColumnName) - 3;

                //MESSAGEDAN COLUMN NAME NI QIRQIB OLIB UNI UPPER_CASE QILINDI
                String columnName = message.substring(fromColumnName, toColumnName).toUpperCase(Locale.ROOT);

                //MESSAGE_BY_LANG GA BERISH UCHUN
                String clientMessage = columnName + NOT_FOUND;
                return new ResponseEntity<>(
                        ApiResult.errorResponse(clientMessage.toUpperCase(), 400),
                        HttpStatus.BAD_REQUEST
                );
            } else if (sqlState.equals("23505")) {

                //MESSAGE_BY_LANG GA BERISH UCHUN
                String clientMessage = constraintViolationException.getConstraintName().toUpperCase(Locale.ROOT) + UK_CONSTRAINT;
                return new ResponseEntity<>(
                        ApiResult.errorResponse(clientMessage.toUpperCase(), 400),
                        HttpStatus.BAD_REQUEST
                );
            }
        } catch (Exception exception) {
            if (isDev()) {
                exception.printStackTrace();
            }
            return new ResponseEntity<>(
                    ApiResult.errorResponse(ResponseMessage.SERVER_ERROR, RestConstants.SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(
                ApiResult.errorResponse(ResponseMessage.SERVER_ERROR, RestConstants.SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // FOYDALANUVCHI TOMONIDAN XATO SODIR BO'LGANDA
    @ExceptionHandler(value = {RestException.class})
    public ResponseEntity<ApiResult<ErrorData>> handleException(RestException ex) {
        sendMessageToTelegramChannel(ex);
        ex.printStackTrace();

        //AGAR RESOURSE TOPILMAGANLIGI XATOSI BO'LSA CLIENTGA QAYSI TABLEDA NIMA TOPILMAGANLIGI HAQIDA XABAR QAYTADI
        if (ex.getFieldName() != null)
            return new ResponseEntity<>(ApiResult.errorResponse(ex.getUserMsg(), ex.getErrorCode()), ex.getStatus());
        //AKS HOLDA DOIMIY EXCEPTIONLAR ISHLAYVERADI
        if (ex.getErrors() != null)
            return new ResponseEntity<>(ApiResult.errorResponse(ex.getErrors()), ex.getStatus());
        return new ResponseEntity<>(ApiResult.errorResponse(ex.getUserMsg(), ex.getErrorCode() != null ? ex.getErrorCode() : ex.getStatus().value()), ex.getStatus());
    }


    //////////////////////////
    ///                    ///
    ///   JPA EXCEPTIONS   ///
    ///                    ///
    //////////////////////////

//    @ExceptionHandler(value = {RestException.class})
//    public ResponseEntity<ApiResult<ErrorData>> handleException(IllegalArgumentException ex) {
//        ex.printStackTrace();
//
//        return new ResponseEntity<>(
//                ApiResult.errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()),
//                HttpStatus.BAD_REQUEST);
//
//    }

    private void sendMessageToTelegramChannel(Exception ex) {

        if (isDev()) {
            return;
        }

        log.info("sendMessageToTelegramChannel");
        String message = ex.getMessage();

        UserDTO currentUserOrNull = CommonUtils.getCurrentUserOrNull();

        ServletUriComponentsBuilder servletUriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        String url = servletUriComponentsBuilder.toUriString();

        ExceptionMessageDTO exceptionMessageDTO = new ExceptionMessageDTO();
        exceptionMessageDTO.setUser(currentUserOrNull);
        exceptionMessageDTO.setMessage(message);
        exceptionMessageDTO.setUrl(url);
        exceptionMessageDTO.setServiceName("staff");

        rabbitMQProducerService.sendExceptions(exceptionMessageDTO);
    }


}

