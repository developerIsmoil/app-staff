package ai.ecma.appstaff.service;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.TemplateForSickDTO;

import java.util.List;
import java.util.UUID;

public interface TemplateForSickService {

    ApiResult<TemplateForSickDTO> addTemplateForSick(TemplateForSickDTO departmentDTO);

    ApiResult<TemplateForSickDTO> editTemplateForSick(UUID id, TemplateForSickDTO departmentDTO);

    ApiResult<?> getAllTemplateForSick(Integer page, Integer size);

    ApiResult<?> getAllTemplateForSickForSelect(Integer page, Integer size);

    ApiResult<TemplateForSickDTO> getOneTemplateForSick(UUID id);

    ApiResult<?> deleteTemplateForSick(UUID id);

    ApiResult<?> deleteTemplateForSickByIdList(List<UUID> id);

    ApiResult<?> changeActiveTemplateForSick(UUID id);

    ApiResult<?> getFormTemplateForSick();

    void existsByEmployeePrivilegeTypeId(UUID id);
}
