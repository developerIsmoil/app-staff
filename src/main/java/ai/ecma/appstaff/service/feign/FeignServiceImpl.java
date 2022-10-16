package ai.ecma.appstaff.service.feign;

import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.feign.AttachmentFeign;
import ai.ecma.appstaff.feign.AuthFeign;
import ai.ecma.appstaff.feign.BranchFeign;
import ai.ecma.appstaff.feign.FinanceFeign;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.UserDTO;
import ai.ecma.appstaff.payload.feign.*;
import ai.ecma.appstaff.payload.view.ViewDTOForFinanceService;

import ai.ecma.appstaff.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FeignServiceImpl implements FeignService {

    private final AuthFeign authFeign;
    private final BranchFeign branchFeign;
    private final AttachmentFeign attachmentFeign;
    private final FinanceFeign financeFeign;

    @Autowired
    public FeignServiceImpl(AuthFeign authFeign,
                            BranchFeign branchFeign,
                            AttachmentFeign attachmentFeign,
                            FinanceFeign financeFeign) {
        this.authFeign = authFeign;
        this.branchFeign = branchFeign;
        this.attachmentFeign = attachmentFeign;
        this.financeFeign = financeFeign;
    }


    @Override
    @Cacheable(key = "#employeeAttachmentIdList", value = "getAttachmentList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<AttachmentFeignDTO> getAttachmentList(List<String> employeeAttachmentIdList) {
        // log.info("ATTACHMENT SERVICE");
        String token = CommonUtils.getTokenFromRequest();
        ApiResult<List<AttachmentFeignDTO>> apiResult = attachmentFeign.getFileInfoByIdList(
                token,
                employeeAttachmentIdList
        );
        if (apiResult.getSuccess()) {
            // log.info("SUCCESS RESPONSE");

            return apiResult.getData();
        }
        return new ArrayList<>();
    }

    @Override
    @Cacheable(key = "#employeeAttachmentIdList", value = "safeGetAttachmentList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<AttachmentFeignDTO> safeGetAttachmentList(List<String> employeeAttachmentIdList) {
        try {
            String token = CommonUtils.getTokenFromRequest();
            // log.info("ATTACHMENT SERVICE");
            ApiResult<List<AttachmentFeignDTO>> apiResult = attachmentFeign.getFileInfoByIdList(
                    token,
                    employeeAttachmentIdList
            );
            if (apiResult.getSuccess()) {
                // log.info("SUCCESS RESPONSE");

                return apiResult.getData();
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Cacheable(key = "#branchIdList", value = "checkBranchList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public void checkBranchList(List<Long> branchIdList) {
        String token = CommonUtils.getTokenFromRequest();

        ApiResult<?> apiResult = branchFeign.checkBranchByIdList(
                token,
                branchIdList
        );

        if (!apiResult.getSuccess()) {
            throw RestException.restThrow(apiResult.getErrors());
        }
    }

    @Override
    @Cacheable(key = "#companyIdList", value = "checkCompanyList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public void checkCompanyList(List<Long> companyIdList) {

        String token = CommonUtils.getTokenFromRequest();
        ApiResult<?> apiResult = branchFeign.checkCompanyByIdList(
                token,
                companyIdList
        );

        if (!apiResult.getSuccess()) {
            throw RestException.restThrow(apiResult.getErrors());
        }
    }

    @Override
    @Cacheable(key = "#branchIdList", value = "getBranchByIdList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<BranchFeignDTO> getBranchByIdList(Collection<Long> branchIdList) {
        // log.info("BRANCH SERVICE");
        String token = CommonUtils.getTokenFromRequest();
        ApiResult<List<BranchFeignDTO>> apiResult = branchFeign.getAllBranchByIdList(
                token,
                branchIdList
        );

        if (apiResult.getSuccess()) {
            // log.info("SUCCESS RESPONSE");

            return apiResult.getData();
        }

        return new ArrayList<>();
    }

    @Override
    @Cacheable(key = "#branchIdList", value = "safeGetBranchByIdList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<BranchFeignDTO> safeGetBranchByIdList(Collection<Long> branchIdList) {
        try {
            // log.info("BRANCH SERVICE");
            String token = CommonUtils.getTokenFromRequest();
            ApiResult<List<BranchFeignDTO>> apiResult = branchFeign.getAllBranchByIdList(
                    token,
                    branchIdList
            );

            if (apiResult.getSuccess()) {
                // log.info("SUCCESS RESPONSE");

                return apiResult.getData();
            } else {
                return new ArrayList<>();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();

        }
    }

    @Override
    @Cacheable(value = "getBranchList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<BranchFeignDTO> getBranchList() {
        // log.info("BRANCH SERVICE");

        String token = CommonUtils.getTokenFromRequest();
        ApiResult<List<BranchFeignDTO>> apiResult = branchFeign.getAllBranchList(
                token
        );

        if (apiResult.getSuccess()) {
            // log.info("SUCCESS RESPONSE");

            return apiResult.getData();
        }

        return new ArrayList<>();
    }

    @Override
    @Cacheable(value = "safeGetBranchList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<BranchFeignDTO> safeGetBranchList() {
        try {
            // log.info("BRANCH SERVICE");
            String token = CommonUtils.getTokenFromRequest();
            ApiResult<List<BranchFeignDTO>> apiResult = branchFeign.getAllBranchList(
                    token
            );

            if (apiResult.getSuccess()) {
                // log.info("SUCCESS RESPONSE");

                return apiResult.getData();
            } else {
                return new ArrayList<>();
            }

        } catch (Exception e) {
            e.printStackTrace();

            return new ArrayList<>();
        }

    }

    @Override
    @Cacheable(value = "getCompanyList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<CompanyFeignDTO> getCompanyList() {
        String token = CommonUtils.getTokenFromRequest();
        ApiResult<List<CompanyFeignDTO>> apiResult = branchFeign.getAllCompanyList(
                token
        );

        if (apiResult.getSuccess()) {

            return apiResult.getData();
        }
        return new ArrayList<>();
    }

    @Override
    @Cacheable(value = "safeGetCompanyList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<CompanyFeignDTO> safeGetCompanyList() {
        try {
            String token = CommonUtils.getTokenFromRequest();
            ApiResult<List<CompanyFeignDTO>> apiResult = branchFeign.getAllCompanyList(
                    token
            );

            if (apiResult.getSuccess()) {

                return apiResult.getData();
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Cacheable(value = "getRoleList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<RoleFeignDTO> getRoleList() {
        // log.info("AUTH SERVICE");

        String token = CommonUtils.getTokenFromRequest();

        ApiResult<List<RoleFeignDTO>> apiResult = authFeign.getRoleList(token);

        if (apiResult.getSuccess()) {
            // log.info("SUCCESS RESPONSE");

            return apiResult.getData();
        }
        return new ArrayList<>();
    }

    @Override
    @Cacheable(value = "safeGetRoleList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<RoleFeignDTO> safeGetRoleList() {

        String token = CommonUtils.getTokenFromRequest();

        try {

            // log.info("AUTH SERVICE");
            ApiResult<List<RoleFeignDTO>> apiResult = authFeign.getRoleList(token);

            if (apiResult.getSuccess()) {
                // log.info("SUCCESS RESPONSE");

                return apiResult.getData();
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Cacheable(key = "#attachmentIdList", value = "checkAttachmentList", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public void checkAttachmentList(List<String> attachmentIdList) {
        // log.info("ATTACHMENT SERVICE");
        String token = CommonUtils.getTokenFromRequest();

        ApiResult<?> apiResult = attachmentFeign.checkAttachmentByIdList(
                token,
                attachmentIdList
        );

        if (!apiResult.getSuccess()) {
            throw RestException.restThrow(apiResult.getErrors());
        }
    }

    @Override
    @Cacheable(key = "#pageAddDTOList", value = "addPageToAuth", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public ApiResult<Boolean> addPageToAuth(List<PageAddDTO> pageAddDTOList) {
        // log.info("AUTH SERVICE");
        String token = CommonUtils.getTokenFromRequest();
        return authFeign.addPageToAuth(
                token,
                pageAddDTOList
        );
    }

    @Override
    @Cacheable(key = "#permissionAddDTOList", value = "addPermissionToAuth", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public ApiResult<Boolean> addPermissionToAuth(List<PermissionAddDTO> permissionAddDTOList) {
        // log.info("AUTH SERVICE");
        String token = CommonUtils.getTokenFromRequest();
        return authFeign.addPermissionToAuth(
                token,
                permissionAddDTOList
        );
    }

    @Override
    public UUID saveUserAndGetUserId(UserFeignDTO userFeignDTO) {
        log.info("AUTH userFeignDTO {}", userFeignDTO);
        String token = CommonUtils.getTokenFromRequest();
        ApiResult<UserFeignDTO> apiResult = authFeign.saveUserForCreateEmployee(
                token,
                userFeignDTO
        );

        if (apiResult.getSuccess()) {
            UserFeignDTO apiResultData = apiResult.getData();

            return apiResultData.getId();
        } else {
            throw RestException.restThrow(apiResult.getErrors());
        }
    }

    @Override
    public void editUser(UserFeignDTO userFeignDTO) {
        String token = CommonUtils.getTokenFromRequest();
        authFeign.editUserForEmployee(
                token,
                userFeignDTO.getId(),
                userFeignDTO
        );
    }

    @Override
    public UserFeignDTO editStaffBugFixPath(UserFeignDTO userFeignDTO) {
        String token = CommonUtils.getTokenFromRequest();
        ApiResult<UUID> apiResult = authFeign.editStaffBugFixPath(
                token,
                userFeignDTO
        );
        UserFeignDTO userFeignDTO1 = new UserFeignDTO();
        userFeignDTO1.setId(apiResult.getData());
        return userFeignDTO1;

    }

    @Override
    public void confirmedTimeSheetSendToFinanceService(List<ConfirmedTimeSheetDTO> confirmedTimeSheetDTOList) {
        // log.info("FINANCE SERVICE");
        String token = CommonUtils.getTokenFromRequest();
        financeFeign.sendToTimeSheetInfoFinanceService(
                token,
                confirmedTimeSheetDTOList
        );

    }

    /**
     * AUTH DAN USER MA'LUMOTLARINI OLIB KELISH KERAK (FULL NAME)
     *
     * @param userId userId
     * @return UserDTO
     */
    @Override
    @Cacheable(key = "#userId", value = "getUserById", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public UserDTO getUserById(UUID userId) {
        // log.info("AUTH SERVICE");

        String token = CommonUtils.getTokenFromRequest();

        ApiResult<UserDTO> apiResult = authFeign.getUserById(
                token,
                userId
        );

        if (apiResult.getSuccess()) {
            // log.info("SUCCESS RESPONSE");

            return apiResult.getData();
        } else {
            return new UserDTO();
        }
    }

    @Override
    @Cacheable(key = "#userId", value = "safeGetUserById", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public UserDTO safeGetUserById(UUID userId) {
        String token = CommonUtils.getTokenFromRequest();
        try {

            // log.info("AUTH SERVICE");
            ApiResult<UserDTO> apiResult = authFeign.getUserById(
                    token,
                    userId
            );

            if (apiResult.getSuccess()) {
                // log.info("SUCCESS RESPONSE");

                return apiResult.getData();
            } else {
                return new UserDTO();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new UserDTO();
        }
    }

    @Override
    @Cacheable(value = "getAllViewMemberFromAuthService", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<UserDTO> getAllViewMemberFromAuthService() {
        // log.info("AUTH SERVICE");
        String token = CommonUtils.getTokenFromRequest();

        ApiResult<List<UserDTO>> apiResult = authFeign.getAllViewMemberFromAuthService(
                token
        );
        if (apiResult.getSuccess()) {
            // log.info("SUCCESS RESPONSE");

            return apiResult.getData();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    @Cacheable(value = "safeGetAllViewMemberFromAuthService", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<UserDTO> safeGetAllViewMemberFromAuthService() {
        String token = CommonUtils.getTokenFromRequest();

        try {
            // log.info("AUTH SERVICE");
            ApiResult<List<UserDTO>> apiResult = authFeign.getAllViewMemberFromAuthService(
                    token
            );
            if (apiResult.getSuccess()) {
                // log.info("SUCCESS RESPONSE");

                return apiResult.getData();
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    @Cacheable(key = "#token", value = "checkPermission", sync = true, cacheManager = "userCacheManagerSTAFF")
    public UserDTO checkPermission(String token) {
        // log.info("AUTH SERVICE");

        ApiResult<UserDTO> apiResult = authFeign.checkPermission(token);
        // log.info("SUCCESS RESPONSE");

        return apiResult.getData();
    }

    @Override
    @Cacheable(key = "#viewDTO", value = "genericViewForTimesheetForFinance", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<String> genericViewForTimesheetForFinance(ViewDTOForFinanceService viewDTO) {
        // log.info("FINANCE SERVICE");
        String token = CommonUtils.getTokenFromRequest();
        ApiResult<List<String>> apiResult = financeFeign.genericViewForTimesheetForFinance(
                token,
                viewDTO
        );

        if (apiResult.getSuccess()) {
            // log.info("SUCCESS RESPONSE");

            return apiResult.getData();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    @Cacheable(value = "getAllOperators", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<UserDTO> getAllOperators() {
        // log.info("AUTH SERVICE");
        String token = CommonUtils.getTokenFromRequest();

        ApiResult<List<UserDTO>> apiResult = authFeign.getAllOperators(
                token
        );

        if (apiResult.getSuccess()) {
            // log.info("SUCCESS RESPONSE");

            return apiResult.getData();
        }

        return new ArrayList<>();
    }

    @Override
    @Cacheable(value = "getAllMentors", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<UserDTO> getAllMentors() {

        String token = CommonUtils.getTokenFromRequest();

        ApiResult<List<UserDTO>> apiResult = authFeign.getAllMentors(
                token
        );

        if (apiResult.getSuccess()) {

            return apiResult.getData();
        }

        return new ArrayList<>();
    }

    @Override
    @Cacheable(value = "safeGetAllOperators", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<UserDTO> safeGetAllOperators() {
        String token = CommonUtils.getTokenFromRequest();

        try {
            // log.info("AUTH SERVICE");
            ApiResult<List<UserDTO>> apiResult = authFeign.getAllOperators(
                    token
            );

            if (apiResult.getSuccess()) {
                // log.info("SUCCESS RESPONSE");

                return apiResult.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    @Cacheable(value = "safeGetAllMentors", sync = true, cacheManager = "defaultCacheManagerSTAFF")
    public List<UserDTO> safeGetAllMentors() {
        String token = CommonUtils.getTokenFromRequest();

        try {
            // log.info("AUTH SERVICE");
            ApiResult<List<UserDTO>> apiResult = authFeign.getAllMentors(
                    token
            );

            if (apiResult.getSuccess()) {
                // log.info("SUCCESS RESPONSE");

                return apiResult.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }


}
