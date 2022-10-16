package ai.ecma.appstaff.controller.view;


import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import ai.ecma.appstaff.payload.view.*;
import ai.ecma.appstaff.service.view.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ViewControllerImpl implements ViewController {
    private final ViewService viewService;


    @CheckAuth
    @Override
    public ApiResult<ViewDTO> duplicateViewById(UUID viewId) {
        return viewService.duplicateViewById(viewId);
    }

    @CheckAuth(
//            permission = PermissionEnum.UPDATE_VIEW
    )
    @Override
    public ApiResult<ViewDTO> updateView(ViewDTO viewDTO) {
        return viewService.updateView(viewDTO);
    }

    @CheckAuth(
//            permission = PermissionEnum.EDIT_VIEW
    )
    @Override
    public ApiResult<ViewDTO> editView(ViewEditDTO viewEditDTO) {
        return viewService.editView(viewEditDTO);
    }

    @CheckAuth(
//            permission = PermissionEnum.ADD_VIEW
    )
    @Override
    public ApiResult<ViewDTO> addView(ViewAddDTO viewAddDTO) {
        return viewService.addView(viewAddDTO);
    }

    @CheckAuth(
//            permission = PermissionEnum.SHARING_PERMISSION_VIEW
    )
    @Override
    public ApiResult<ViewMemberHierarchy> sharingPermissions(UUID viewId) {
        return viewService.sharingPermissions(viewId);
    }

    @CheckAuth(
//            permission = PermissionEnum.CHANGE_MEMBER_PERMISSION_VIEW
    )
    @Override
    public ApiResult<ViewMemberDTO> changeMemberPermission(PermissionEditDTO permissionEditDTO) {
        return viewService.changeMemberPermission(permissionEditDTO);
    }

    @CheckAuth
    @Override
    public ApiResult<Boolean> deleteViewById(UUID viewId) {
        return viewService.deleteViewById(viewId);
    }


}
