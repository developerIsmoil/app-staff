package ai.ecma.appstaff.feign;

import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import com.google.gson.Gson;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;

import java.io.IOException;

@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final Gson gson;

    @Autowired
    public CustomFeignErrorDecoder(Gson gson) {
        this.gson = gson;
    }


    @Override
    public Exception decode(final String methodKey, final Response response) {
        log.info("___ -___ -_- --__- -_methodKey {}", methodKey);
        log.info("___ -___ -_- --__- -_response {}", response);

        final String error = getResponseBodyAsString(response.body());
        log.error("Error in another server: {}", error);

        try {

            ApiResult<?> apiResult = gson.fromJson(error, ApiResult.class);
            log.info("___ -___ -_- --__- -_apiResult {}", apiResult);
            return RestException.restThrow(apiResult.getErrors(), HttpStatus.valueOf(response.status()));

        } catch (Exception e) {
            e.printStackTrace();
            return RestException.restThrow(
//                    "Error in another server",
                    error,
                    HttpStatus.BAD_REQUEST);
        }

    }

    private String getResponseBodyAsString(final Response.Body body) {
        try {
            byte[] bytes = StreamUtils.copyToByteArray(body.asInputStream());
            return new String(bytes);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
