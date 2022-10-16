package ai.ecma.appstaff.config.filter;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.ErrorData;
import ai.ecma.appstaff.utils.ResponseMessage;
import ai.ecma.appstaff.utils.RestConstants;
import com.google.gson.Gson;
import feign.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


@Configuration
@RequiredArgsConstructor
public class StaffFilter implements Filter {

    private final Environment environment;
    private final Gson gson;


    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        if ((checkIsNotSwagger(httpServletRequest) && checkIsNotActuatorRefresh(httpServletRequest) && checkUsernameOrPasswordIsNotValid(httpServletRequest))) {

            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            ApiResult<ErrorData> errorDataApiResult = ApiResult.errorResponse(ResponseMessage.ACCESS_IS_DENIED_DUE_TO_INVALID_CREDENTIALS, 403);
            httpServletResponse.getWriter().write(gson.toJson(errorDataApiResult));
            httpServletResponse.setStatus(403);
            httpServletResponse.setContentType("application/json");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private boolean checkIsNotSwagger(HttpServletRequest httpServletRequest) {
        return !httpServletRequest.getRequestURI().contains("swagger");
    }

    private boolean checkIsNotActuatorRefresh(HttpServletRequest httpServletRequest) {
        return !httpServletRequest.getRequestURI().contains("actuator/refresh");
    }

    private boolean checkUsernameOrPasswordIsNotValid(HttpServletRequest httpServletRequest) {

        String serviceUsername = httpServletRequest.getHeader(RestConstants.SERVICE_USERNAME_HEADER);
        String servicePassword = httpServletRequest.getHeader(RestConstants.SERVICE_PASSWORD_HEADER);

        System.err.println(serviceUsername);
        System.err.println(servicePassword);

        return isNull(serviceUsername) ||
                isNull(servicePassword) ||
                isNull(environment.getProperty(serviceUsername)) ||
                isNotEquals(environment.getProperty(serviceUsername), servicePassword);
    }

    private boolean isNull(String s) {
        return Objects.isNull(s);
    }

    private boolean isNotEquals(String s1, String s2) {
        if (isNull(s1) || isNull(s2)) {
            return false;
        }
        return !(Objects.equals(s1, s2));
    }
}
