package ai.ecma.appstaff.feign;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.UserDTO;
import ai.ecma.appstaff.payload.feign.PageAddDTO;
import ai.ecma.appstaff.payload.feign.PermissionAddDTO;
import ai.ecma.appstaff.payload.feign.RoleFeignDTO;
import ai.ecma.appstaff.payload.feign.UserFeignDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = RestConstants.AUTH_SERVICE, configuration = FeignConfig.class)
public interface AuthFeign {

    String USER_BY_TOKEN_PATH = "/for-microservice/user/user-by-token";
    String ADD_STAFF_PATH = "/for-microservice/user/add-staff";
    String EDIT_STAFF_PATH = "/for-microservice/user/edit-staff/{id}";
    String EDIT_STAFF_BUG_FIX_PATH = "/for-microservice/user/edit-staff-bug-fix";
    String ROLE_LIST_PATH = "/role/role-list";
    String PAGE_PATH = "/for-microservice/other-microservice/page";
    String PERMISSION_PATH = "/for-microservice/other-microservice/permission";
    String ADMIN_LIST_PATH = "/for-microservice/user/admin-list";
    String USER_BY_ID_PATH = "/for-microservice/user/user-by-id/{userId}";
    String GET_ALL_OPERATORS = "/for-microservice/user/operator-list";
    String GET_ALL_MENTORS = "/for-microservice/user/mentor-list";

    /**
     * AUTH SERVICEDAN TIZIMDA KIRIB TURGAN USER TOKENINI TEKSHIRIB KELISH UCHUN YO'L
     *
     * @return UserDTO
     */
    @GetMapping(value = USER_BY_TOKEN_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<UserDTO> checkPermission(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token
    );

    @PostMapping(value = ADD_STAFF_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<UserFeignDTO> saveUserForCreateEmployee(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody UserFeignDTO userFeignDTO
    );

    @PutMapping(value = EDIT_STAFF_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<UserFeignDTO> editUserForEmployee(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @PathVariable(name = "id") UUID id,
            @RequestBody UserFeignDTO userFeignDTO
    );

    @PutMapping(value = EDIT_STAFF_BUG_FIX_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<UUID> editStaffBugFixPath(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody UserFeignDTO userFeignDTO
    );

    @GetMapping(value = ROLE_LIST_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<List<RoleFeignDTO>> getRoleList(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token
    );

    /**
     * AUTH GA O'ZIMIZDA QO'SHILAYOTGAN PAGELARNI BERIB YUBORISHIMIZ UCHUN YO'L
     *
     * @param pageAddDTOList RequestBody
     * @return ApiResult<List < UserInfoDTO>>
     */
    @PostMapping(value = PAGE_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<Boolean> addPageToAuth(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody List<PageAddDTO> pageAddDTOList
    );

    /**
     * AUTH GA O'ZIMIZDA QO'SHILAYOTGAN PERMISSIONLAR BERIB YUBORISHIMIZ UCHUN YO'L
     *
     * @param permissionAddDTOList RequestBody
     * @return ApiResult<List < UserInfoDTO>>
     */
    @PostMapping(value = PERMISSION_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<Boolean> addPermissionToAuth(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody List<PermissionAddDTO> permissionAddDTOList
    );

    @GetMapping(value = ADMIN_LIST_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<List<UserDTO>> getAllViewMemberFromAuthService(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token
    );

    @GetMapping(value = USER_BY_ID_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<UserDTO> getUserById(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @PathVariable("userId") UUID userId
    );


    @GetMapping(value = GET_ALL_OPERATORS, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<List<UserDTO>> getAllOperators(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token
    );

    @GetMapping(value = GET_ALL_MENTORS, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<List<UserDTO>> getAllMentors(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token
    );
}