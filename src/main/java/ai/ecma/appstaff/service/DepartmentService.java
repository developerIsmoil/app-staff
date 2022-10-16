package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.Department;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.CustomPage;
import ai.ecma.appstaff.payload.DepartmentDTO;
import ai.ecma.appstaff.payload.feign.DepartmentFeignDTO;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {

    ApiResult<DepartmentDTO> addDepartment(DepartmentDTO departmentDTO);

    ApiResult<DepartmentDTO> editDepartment(UUID id, DepartmentDTO departmentDTO);

    ApiResult<List<DepartmentDTO>> getAllDepartment(Integer page, Integer size);

    ApiResult<List<DepartmentDTO>> getAllDepartmentForSelect(Integer page, Integer size);

    ApiResult<DepartmentDTO> getOneDepartment(UUID id);

    ApiResult<?> deleteDepartment(UUID id);

    List<DepartmentDTO> getAllActiveDepartmentFromDB();

    Department getDepartmentFromDB(UUID id, boolean onlyActive);

    List<DepartmentFeignDTO> getAllActiveDepartmentFromDBForOtherMicroservice();

    List<DepartmentFeignDTO> getDepartmentsByUserId(UUID userId);

    List<DepartmentFeignDTO> getAllActiveDepartmentFromDBByIdList(List<UUID> idList);

    List<DepartmentDTO> getAllDepartmentsFromDB();
}
