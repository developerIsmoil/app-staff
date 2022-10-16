package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(path = HrmController.HOLIDAY_CONTROLLER_PATH)
public interface HrmController {
    String HOLIDAY_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/hrm";

    String GET_ORG_STRUCTURE = "/org-structure";


    @GetMapping(path = GET_ORG_STRUCTURE)
    ApiResult<?> getOrgStructure();


}
