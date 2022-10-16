package ai.ecma.appstaff.service.feign;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.UserDTO;
import ai.ecma.appstaff.payload.feign.*;
import ai.ecma.appstaff.payload.view.ViewDTOForFinanceService;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface FeignService {

    ApiResult<Boolean> addPageToAuth(List<PageAddDTO> pageAddDTOList);

    ApiResult<Boolean> addPermissionToAuth(List<PermissionAddDTO> permissionAddDTOList);

    UUID saveUserAndGetUserId(UserFeignDTO userFeignDTO);

    List<AttachmentFeignDTO> getAttachmentList(List<String> employeeAttachmentIdList);

    List<AttachmentFeignDTO> safeGetAttachmentList(List<String> employeeAttachmentIdList);

    void checkAttachmentList(List<String> attachmentIdList);

    void checkBranchList(List<Long> branchIdList);

    void checkCompanyList(List<Long> companyIdList);

    List<BranchFeignDTO> getBranchByIdList(Collection<Long> branchIdList);

    List<BranchFeignDTO> safeGetBranchByIdList(Collection<Long> branchIdList);

    List<BranchFeignDTO> getBranchList();

    List<BranchFeignDTO> safeGetBranchList();

    List<CompanyFeignDTO> getCompanyList();

    List<CompanyFeignDTO> safeGetCompanyList();

    List<RoleFeignDTO> getRoleList();

    List<RoleFeignDTO> safeGetRoleList();

    void editUser(UserFeignDTO userFeignDTO);

    UserFeignDTO editStaffBugFixPath(UserFeignDTO userFeignDTO);

    void confirmedTimeSheetSendToFinanceService(List<ConfirmedTimeSheetDTO> confirmedTimeSheetDTOList);

    UserDTO getUserById(UUID userId);

    UserDTO safeGetUserById(UUID userId);

    List<UserDTO> getAllViewMemberFromAuthService();

    List<UserDTO> safeGetAllViewMemberFromAuthService();

    UserDTO checkPermission(String token);

    List<String> genericViewForTimesheetForFinance(ViewDTOForFinanceService viewDTO);

    List<UserDTO> getAllOperators();

    List<UserDTO> safeGetAllOperators();

    List<UserDTO> getAllMentors();

    List<UserDTO> safeGetAllMentors();


}
