package ai.ecma.appstaff.service.otherService;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.feign.BranchAndDepartmentFeignDTO;
import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.DepartmentFeignDTO;

import java.util.List;
import java.util.UUID;

public interface OtherMicroserviceService {

    ApiResult<List<DepartmentFeignDTO>> getDepartmentsByUserId(UUID userId);

    ApiResult<List<BranchFeignDTO>> getBranchesByUserId(UUID userId);

    ApiResult<List<DepartmentFeignDTO>> getDepartments();

    ApiResult<List<DepartmentFeignDTO>> getDepartmentsById(List<UUID> idList);

    ApiResult<?> getEmploymentInfoById(List<UUID> uuidList);

    ApiResult<?> getEmploymentInfoFullInfoById(List<UUID> uuidList);

    ApiResult<?> getAllOperators();

    ApiResult<?> getAllEmployee();

    ApiResult<BranchAndDepartmentFeignDTO> getBranchesAndDepartmentsByUserId(UUID userId);

    ApiResult<?> getEmployeesByBranch(Integer id);

}
