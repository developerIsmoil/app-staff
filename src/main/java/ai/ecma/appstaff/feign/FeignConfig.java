package ai.ecma.appstaff.feign;

import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.RestConstants;
import feign.Logger;
import feign.RequestInterceptor;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FeignConfig {

    @Value("${service.username}")
    private String serviceUsername;

    private final Environment environment;

    @Autowired
    public FeignConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(RestConstants.AUTHORIZATION_HEADER, CommonUtils.getTokenFromRequest());
            requestTemplate.header(RestConstants.SERVICE_USERNAME_HEADER, serviceUsername);
            requestTemplate.header(RestConstants.SERVICE_PASSWORD_HEADER, environment.getProperty(serviceUsername));
            requestTemplate.header("Accept", ContentType.APPLICATION_JSON.getMimeType());
        };
    }

    // log uchun
    // feigndagi hamma loglar uchun kerak
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
