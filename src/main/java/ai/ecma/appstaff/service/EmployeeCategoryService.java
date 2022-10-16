package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.EmployeeCategory;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EmployeeCategoryDTO;

import java.util.List;
import java.util.UUID;

public interface EmployeeCategoryService {

    ApiResult<EmployeeCategoryDTO> addEmployeeCategory(EmployeeCategoryDTO employeeCategoryDTO);

    ApiResult<EmployeeCategoryDTO> editEmployeeCategory(UUID id, EmployeeCategoryDTO employeeCategoryDTO);

    ApiResult<List<EmployeeCategoryDTO>> getAllEmployeeCategory(Integer page, Integer size);

    ApiResult<List<EmployeeCategoryDTO>> getAllEmployeeCategoryForSelect(UUID departmentId, UUID positionId, Integer page, Integer size);

    ApiResult<EmployeeCategoryDTO> getOneEmployeeCategory(UUID id);

    ApiResult<?> deleteEmployeeCategory(UUID id);

    ApiResult<?> deleteEmployeeCategoryByIdList(List<UUID> id);

    ApiResult<?> changeActiveEmployeeCategory(UUID id);

    List<EmployeeCategoryDTO> getAllActiveEmployeeCategoryFromDB();

    ApiResult<EmployeeCategoryDTO> getFormEmployeeCategory();

    EmployeeCategory getEmployeeCategoryFromDB(UUID id, boolean onlyActive);

    void existsByDepartmentId(UUID departmentId);

    void existsByEmployeeCategoryTypeId(UUID id);

    void existsByPositionId(UUID id);
}
