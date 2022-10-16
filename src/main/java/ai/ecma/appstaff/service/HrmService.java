package ai.ecma.appstaff.service;

import ai.ecma.appstaff.payload.ApiResult;


public interface HrmService {
    ApiResult<?> getOrgStructure();

}