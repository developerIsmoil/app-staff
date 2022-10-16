package ai.ecma.appstaff;

import ai.ecma.appstaff.feign.CustomFeignErrorDecoder;
import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.RestConstants;
import com.google.gson.Gson;
import feign.Feign;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
@RestController
@RequestMapping(path = RestConstants.BASE_PATH)
@RequiredArgsConstructor
public class StaffServiceApplication {
    private final Environment environment;

    @Value("${spring.profiles.active}")
    String profile;


    public static void main(String[] args) {

        SpringApplication.run(StaffServiceApplication.class, args);

    }

    @Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder(gson());
    }





    /**
     * BUNGA TEGMA. CONFIG SERVERDA CONFIGLARNI OLISH UCHUN
     *
     */
    @PostMapping("/actuator/refresh")
    public void refreshEnvironment(@RequestBody Object object) {

        CommonUtils.DOMAIN = environment.getProperty("attachment.main.domain." + profile);
        CommonUtils.ATTACHMENT_DOWNLOAD_PATH = environment.getProperty("attachment.download.path");
        CommonUtils.ATTACHMENT_MEDIUM_VIEW_PATH = environment.getProperty("attachment.view.medium.path");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<?> entity = new HttpEntity<>(headers);
        restTemplate.exchange(RestConstants.ACTUATOR_PATH, HttpMethod.POST, entity, Object.class);
    }
}
