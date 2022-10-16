package ai.ecma.appstaff.controller.view;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import ai.ecma.appstaff.payload.view.*;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static ai.ecma.appstaff.controller.customField.CustomFieldController.FINANCE_EDIT_CUSTOM_FIELD_PATH;


@RequestMapping(ViewController.VIEW_CONTROLLER_PATH)
public interface ViewController {
    String VIEW_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/view/";

    String DUPLICATE_VIEW_PATH = "duplicate-view";
    String DELETE_VIEW_PATH = "delete-view";
    String EDIT_VIEW_NAME_PATH = "edit-view";
    String ADD_VIEW_PATH = "add-view";
    String UPDATE_VIEW_PATH = "update-view";
    String SHARING_PERMISSION_PATH = "sharing-permission";
    String CHANGE_MEMBER_PERMISSION_OR_ADD_MEMBER_OR_REMOVE_MEMBER_IN_VIEW_PATH = "change-member-permission-or-add-member-or-remove-member-in-view";


    /**
     * KELGANID LI VIEWNI DUPLICATE QILIP, DUPLACATE QILINGAN  VIEW NI QAYTARIB BERADI
     *
     * @param viewId
     * @return
     */
    @GetMapping(DUPLICATE_VIEW_PATH + "/{viewId}")
    ApiResult<ViewDTO> duplicateViewById(@PathVariable UUID viewId);


    /**
     * VIEW DAGI SORT, FILTER,.. LARNI O'ZGARTIRISH UCHUN YO'L.
     *
     * @param viewDTO
     * @return
     */
    @PutMapping(UPDATE_VIEW_PATH)
    ApiResult<ViewDTO> updateView(@RequestBody @Valid ViewDTO viewDTO);


    /**
     * VIEW NI NOMINI TAXRIRLASH,
     * VIEW NI FAVOURITE QILINADI YOKI FAVOURITE DAN OLIB TASHLAYDI,
     * VIEW NI PUBLIC YOKI PRIVATE GA
     *
     * @param viewEditDTO
     * @return
     */
    @PutMapping(EDIT_VIEW_NAME_PATH)
    ApiResult<ViewDTO> editView(@RequestBody @Valid ViewEditDTO viewEditDTO);


    /**
     * YANGIN VIEW YARATISH(DEFAULT VIEWNI  NUSXALAB, YANGI VIEW NI QAYTARADI )
     *
     * @param
     * @return
     */
    @PostMapping(ADD_VIEW_PATH)
    ApiResult<ViewDTO> addView(@Valid @RequestBody ViewAddDTO viewAddDTO);


    /**
     * VIEW DAGI BARCHA MEMBER LAR VA TEAM LAR RO'YXATINI QAYTARADI
     * 1.1 AGAR VIEW PUBLIC BO'LSA TEAM BO'SH BO'LADI BARCHASI MEMBER LIST DA QAYTADI
     * 1.2 AGAR VIEW PRIVATE BO'LSA USER VIEW DA BORLAR MEMBER LISTDA, YO'QLAR ESA TEAM DA QAYTADI
     *
     * @param viewId
     * @return
     */
    @GetMapping(SHARING_PERMISSION_PATH + "/{viewId}")
    ApiResult<ViewMemberHierarchy> sharingPermissions(@PathVariable UUID viewId);


    /**
     * 1.1 MEMBER NI HUQUQINI O'ZGARTIRADI, AGAR addMember NULL BO'LSA, permission NULL BO'LMASLIGI KERAK
     * MEMBER NI VIEW GA QO'SHADI, AGAR addMember TRUE BO'LSA, permission NULL BO'LMASLIGI KERAK
     * MEMBER NI VIEW DAN O'CHIRADI, AGAR addMember FALSE BO'LSA
     *
     * @param permissionEditDTO
     * @return
     */
    @PutMapping(CHANGE_MEMBER_PERMISSION_OR_ADD_MEMBER_OR_REMOVE_MEMBER_IN_VIEW_PATH)
    ApiResult<ViewMemberDTO> changeMemberPermission(@RequestBody @Valid PermissionEditDTO permissionEditDTO);


    @DeleteMapping(DELETE_VIEW_PATH + "/{viewId}")
    ApiResult<Boolean> deleteViewById(@PathVariable UUID viewId);



}
