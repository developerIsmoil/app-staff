package ai.ecma.appstaff.controller.otherServices;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.feign.BranchAndDepartmentFeignDTO;
import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.DepartmentFeignDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping(OtherMicroserviceController.OTHER_MICROSERVICE_CONTROLLER_PATH)
public interface OtherMicroserviceController {
    String OTHER_MICROSERVICE_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/other-microservice";

    String GET_DEPARTMENTS_BY_USER_ID_PATH = "/departments-by-user-id/{userId}";
    String GET_BRANCHES_BY_USER_ID_PATH = "/branches-by-user-id/{userId}";
    String GET_BRANCHES_DEPARTMENTS_BY_USER_ID_PATH = "/branches-departments/by-user-id/{userId}";
    String GET_DEPARTMENTS_PATH = "/departments";
    String GET_DEPARTMENTS_BY_IDS_PATH = "/departments/by-id";
    String GET_BRANCHES_DEPARTMENTS_PATH = "/branches-departments";
    String GET_EMPLOYMENT_INFO_BY_ID_PATH = "/employment-info-by-id";
    String GET_EMPLOYMENT_FULL_INFO_BY_ID_PATH = "/employment-full-info-by-id";
    String GET_ALL_OPERATORS = "/operator-list";
    String GET_ALL_MENTORS = "/operator-list";
    String GET_ALL_EMPLOYEE_PATH = "/get-all-employee";
    String GET_MENTORS_BY_BRANCH_PATH = "/mentors-by-branch/{id}";

    @GetMapping(value = GET_DEPARTMENTS_BY_USER_ID_PATH)
    ApiResult<List<DepartmentFeignDTO>> getDepartmentsByUserId(
            @PathVariable("userId") UUID userId
    );

    @GetMapping(value = GET_BRANCHES_BY_USER_ID_PATH)
    ApiResult<List<BranchFeignDTO>> getBranchesByUserId(
            @PathVariable("userId") UUID userId
    );

    @GetMapping(value = GET_BRANCHES_DEPARTMENTS_BY_USER_ID_PATH)
    ApiResult<BranchAndDepartmentFeignDTO> getBranchesAndDepartmentsByUserId(
            @PathVariable("userId") UUID userId
    );

    @GetMapping(value = GET_DEPARTMENTS_PATH)
    ApiResult<List<DepartmentFeignDTO>> getDepartments();

    @PostMapping(value = GET_DEPARTMENTS_BY_IDS_PATH)
    ApiResult<List<DepartmentFeignDTO>> getDepartmentsById(
            @RequestBody List<UUID> idList
    );

    @PostMapping(GET_EMPLOYMENT_INFO_BY_ID_PATH)
    ApiResult<?> getEmploymentInfoById(
            @RequestBody List<UUID> uuidList
    );

    @PostMapping(GET_EMPLOYMENT_FULL_INFO_BY_ID_PATH)
    ApiResult<?> getEmploymentInfoFullInfoById(
            @RequestBody List<UUID> uuidList
    );

    @GetMapping(GET_ALL_OPERATORS)
    ApiResult<?> getAllOperators();


    /**
     * Barcha hodimlar ro'yxatini olish uchun yo'l
     */
    @GetMapping(path = GET_ALL_EMPLOYEE_PATH)
    ApiResult<?> getAllEmployee();


    @GetMapping(path = GET_MENTORS_BY_BRANCH_PATH)
    ApiResult<?> getAllMentorsByBranch(
            @PathVariable Integer id
    );


}
