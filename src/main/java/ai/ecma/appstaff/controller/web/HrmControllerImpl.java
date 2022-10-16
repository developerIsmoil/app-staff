package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.service.HrmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HrmControllerImpl implements HrmController {

    private final HrmService hrmService;


    @Override
    @CheckAuth(permission = PermissionEnum.GET_ORG_STRUCTURE)
    public ApiResult<?> getOrgStructure() {
        return hrmService.getOrgStructure();
    }
}
