package ai.ecma.appstaff.controller.otherServices;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.feign.BranchAndDepartmentFeignDTO;
import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.DepartmentFeignDTO;
import ai.ecma.appstaff.service.otherService.OtherMicroserviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OtherMicroserviceControllerImpl implements OtherMicroserviceController {

    private final OtherMicroserviceService otherMicroserviceService;

    @Override
    public ApiResult<List<DepartmentFeignDTO>> getDepartmentsByUserId(UUID userId) {
        return otherMicroserviceService.getDepartmentsByUserId(userId);
    }

    @Override
    public ApiResult<List<BranchFeignDTO>> getBranchesByUserId(UUID userId) {
        return otherMicroserviceService.getBranchesByUserId(userId);
    }

    @Override
    public ApiResult<BranchAndDepartmentFeignDTO> getBranchesAndDepartmentsByUserId(UUID userId) {
        return otherMicroserviceService.getBranchesAndDepartmentsByUserId(userId);
    }

    @Override
    public ApiResult<List<DepartmentFeignDTO>> getDepartments() {
        return otherMicroserviceService.getDepartments();
    }

    @Override
    public ApiResult<List<DepartmentFeignDTO>> getDepartmentsById(List<UUID> idList) {
        return otherMicroserviceService.getDepartmentsById(idList);
    }

    @Override
    public ApiResult<?> getEmploymentInfoById(List<UUID> uuidList) {
        return otherMicroserviceService.getEmploymentInfoById(uuidList);
    }

    @Override
    public ApiResult<?> getEmploymentInfoFullInfoById(List<UUID> uuidList) {
        return otherMicroserviceService.getEmploymentInfoFullInfoById(uuidList);
    }

    @Override
    public ApiResult<?> getAllOperators() {
        return otherMicroserviceService.getAllOperators();
    }

    @Override
    public ApiResult<?> getAllEmployee() {
        return otherMicroserviceService.getAllEmployee();
    }

    @Override
    public ApiResult<?> getAllMentorsByBranch(Integer id) {
        return otherMicroserviceService.getEmployeesByBranch(id);
    }


}
