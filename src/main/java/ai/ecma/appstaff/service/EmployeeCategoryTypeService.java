package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.EmployeeCategoryType;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.CustomPage;
import ai.ecma.appstaff.payload.EmployeeCategoryTypeDTO;

import java.util.List;
import java.util.UUID;

public interface EmployeeCategoryTypeService {

    ApiResult<EmployeeCategoryTypeDTO> addEmployeeCategoryType(EmployeeCategoryTypeDTO employeeCategoryTypeDTO);

    ApiResult<EmployeeCategoryTypeDTO> editEmployeeCategoryType(UUID id, EmployeeCategoryTypeDTO employeeCategoryTypeDTO);

    ApiResult<List<EmployeeCategoryTypeDTO>> getAllEmployeeCategoryType(Integer page, Integer size);

    ApiResult<List<EmployeeCategoryTypeDTO>> getAllEmployeeCategoryTypeForSelect(Integer page, Integer size);

    ApiResult<EmployeeCategoryTypeDTO> getOneEmployeeCategoryType(UUID id);

    ApiResult<?> deleteEmployeeCategoryType(UUID id);

    ApiResult<?> deleteEmployeeCategoryTypeByIdList(List<UUID> id);

    ApiResult<?> changeActiveEmployeeCategoryType(UUID id);

    List<EmployeeCategoryTypeDTO> getAllActiveEmployeeCategoryTypeFromDB();

    EmployeeCategoryType getEmployeeCategoryTypeFromDB(UUID id, boolean onlyActive);
}
