package ai.ecma.appstaff.aop.executor;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.UserDTO;
import ai.ecma.appstaff.service.feign.FeignService;
import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.RestConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static ai.ecma.appstaff.utils.CommonUtils.currentRequest;


@Slf4j
@Order(value = 1)
@Aspect
@Component
@RequiredArgsConstructor
@CacheConfig(cacheManager = "userCacheManagerSTAFF")
public class CheckAuthAspect {

    private final FeignService feignService;

    @Before(value = "@annotation(checkAuth)")
    public void checkAuthExecutor(CheckAuth checkAuth) {
        check(checkAuth);
    }


    public void check(CheckAuth checkAuth) {

        HttpServletRequest httpServletRequest = currentRequest();

        String token = getTokenFromRequest(httpServletRequest);


        String userIdFromRequest = CommonUtils.getUserIdFromRequest(httpServletRequest);

        if (userIdFromRequest == null || userIdFromRequest.isEmpty()) {
            UserDTO userDTO = feignService.checkPermission(token);
            PermissionEnum[] permission = checkAuth.permission();
            if (permission.length > 0 && notPermission(userDTO.getPermissions(), permission)) {
                throw RestException.restThrow("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
            httpServletRequest.setAttribute(RestConstants.REQUEST_ATTRIBUTE_CURRENT_USER, userDTO);
        } else {
            if (checkAuth.permission().length > 0 && notPermission(CommonUtils.getUserPermissionsFromRequest(httpServletRequest), checkAuth.permission()))
                throw RestException.restThrow("FORBIDDEN", HttpStatus.FORBIDDEN);

            setUserIdAndPermissionFromRequest(httpServletRequest);
        }
    }


    private String getTokenFromRequest(HttpServletRequest httpServletRequest) {
        try {
            String token = httpServletRequest.getHeader(RestConstants.AUTHORIZATION_HEADER);
            if (Objects.isNull(token) || token.isEmpty()) {
                throw RestException.restThrow("FORBIDDEN", HttpStatus.FORBIDDEN);
            }
            return token;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private boolean notPermission(List<String> hasPermission, PermissionEnum[] mustPermission) {
        if (Objects.isNull(hasPermission) || hasPermission.isEmpty()) {
            return true;
        }
        for (PermissionEnum permissionEnum : mustPermission) {
            if (hasPermission.contains(permissionEnum.name()))
                return false;
        }
        return true;
    }

    private boolean notPermission(String permission, PermissionEnum[] mustPermission) {
        if (permission == null || permission.isEmpty())
            return true;
        for (PermissionEnum permissionEnum : mustPermission) {
            if (permission.contains(permissionEnum.name()))
                return false;
        }
        return true;
    }

    private void setUserIdAndPermissionFromRequest(HttpServletRequest httpServletRequest) {
        String userId = CommonUtils.getUserIdFromRequest(httpServletRequest);
        String permissions = CommonUtils.getUserPermissionsFromRequest(httpServletRequest);

        httpServletRequest.setAttribute(RestConstants.REQUEST_ATTRIBUTE_CURRENT_USER_ID, userId);
        httpServletRequest.setAttribute(RestConstants.REQUEST_ATTRIBUTE_CURRENT_USER_PERMISSIONS, permissions);
    }
}
