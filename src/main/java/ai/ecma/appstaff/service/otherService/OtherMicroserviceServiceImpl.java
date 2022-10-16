package ai.ecma.appstaff.service.otherService;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.UserDTO;
import ai.ecma.appstaff.payload.feign.BranchAndDepartmentFeignDTO;
import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.DepartmentFeignDTO;
import ai.ecma.appstaff.payload.feign.EmploymentInfoFeignDTO;
import ai.ecma.appstaff.projection.IEmployee;
import ai.ecma.appstaff.service.DepartmentService;
import ai.ecma.appstaff.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtherMicroserviceServiceImpl implements OtherMicroserviceService {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    @Override
    public ApiResult<List<DepartmentFeignDTO>> getDepartmentsByUserId(UUID userId) {
        List<DepartmentFeignDTO> departmentsByUserId = departmentService.getDepartmentsByUserId(userId);
        return ApiResult.successResponse(departmentsByUserId);
    }

    @Override
    public ApiResult<List<BranchFeignDTO>> getBranchesByUserId(UUID userId) {
        List<BranchFeignDTO> branchesByUserId = employeeService.getBranchesByUserId(userId);
        return ApiResult.successResponse(branchesByUserId);
    }

    @Override
    public ApiResult<List<DepartmentFeignDTO>> getDepartments() {
        List<DepartmentFeignDTO> departmentFeignDTOList = departmentService.getAllActiveDepartmentFromDBForOtherMicroservice();
        return ApiResult.successResponse(departmentFeignDTOList);
    }

    @Override
    public ApiResult<List<DepartmentFeignDTO>> getDepartmentsById(List<UUID> idList) {
        List<DepartmentFeignDTO> departmentFromDBByIdList = departmentService.getAllActiveDepartmentFromDBByIdList(idList);
        return ApiResult.successResponse(departmentFromDBByIdList);

    }

    @Override
    public ApiResult<?> getEmploymentInfoById(List<UUID> uuidList) {

        List<EmploymentInfoFeignDTO> employmentInfoFeignDTOList = employeeService.getEmploymentInfoById(uuidList);

        return ApiResult.successResponse(employmentInfoFeignDTOList);
    }

    @Override
    public ApiResult<?> getEmploymentInfoFullInfoById(List<UUID> uuidList) {

        List<EmploymentInfoFeignDTO> employmentInfoFeignDTOList = employeeService.getEmploymentInfoFullInfoById(uuidList);

        return ApiResult.successResponse(employmentInfoFeignDTOList);
    }

    @Override
    public ApiResult<?> getAllOperators() {
        List<UserDTO> operators = employeeService.getAllOperators();

        return ApiResult.successResponse(operators);

    }

    @Override
    public ApiResult<?> getAllEmployee() {
        return employeeService.getAllEmployee();
    }

    @Override
    public ApiResult<BranchAndDepartmentFeignDTO> getBranchesAndDepartmentsByUserId(UUID userId) {
        List<BranchFeignDTO> branchesByUserId = employeeService.getBranchesByUserId(userId);
        List<DepartmentFeignDTO> departmentsByUserId = departmentService.getDepartmentsByUserId(userId);

        BranchAndDepartmentFeignDTO branchAndDepartmentFeignDTO = new BranchAndDepartmentFeignDTO(
                branchesByUserId,
                departmentsByUserId
        );

        return ApiResult.successResponse(branchAndDepartmentFeignDTO);
    }

    @Override
    public ApiResult<?> getEmployeesByBranch(Integer id) {
        List<IEmployee> employeesByBranch = employeeService.getAllMentorsByBranch(id);
        return ApiResult.successResponse(employeesByBranch);
    }

}
