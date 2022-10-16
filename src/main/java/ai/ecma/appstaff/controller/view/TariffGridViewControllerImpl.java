package ai.ecma.appstaff.controller.view;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.view.GenericViewResultDTO;
import ai.ecma.appstaff.payload.view.InitialViewTypesDTO;
import ai.ecma.appstaff.payload.view.ViewDTO;
import ai.ecma.appstaff.service.TariffGridService;
import ai.ecma.appstaff.service.view.ViewService;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TariffGridViewControllerImpl implements TariffGridViewController {
    private final ViewService viewService;
    private final TariffGridService tariffGridService;


    @CheckAuth(
//            permission = PermissionEnum.VIEW_UNIVERSITY_GRAND
    )
    @Override
    public ApiResult<InitialViewTypesDTO> getViewTypes() {
        return viewService.getViewTypes(TableNameConstant.TARIFF_GRID);
    }


    @CheckAuth(
//            permission = PermissionEnum.VIEW_UNIVERSITY_GRAND
    )
    @Override
    public ApiResult<?> getViewDataByIdList(UUID viewId, List<String> idList) {

        if (idList.size() > RestConstants.MAX_GENERIC_VALUE_SIZE)
            throw RestException.restThrow("MAX GENERIC VALUE SIZE MUST BE LESS THAN : " + RestConstants.MAX_GENERIC_VALUE_SIZE, HttpStatus.BAD_REQUEST);

        return viewService.getViewDataByIdList(viewId, idList);
    }


    @CheckAuth(
//            permission = PermissionEnum.VIEW_UNIVERSITY_GRAND
    )
    @Override
    public ApiResult<?> genericView(int page, ViewDTO viewDTO, String statusId) {

        GenericViewResultDTO result = new GenericViewResultDTO();

        return viewService.genericView(page, viewDTO, statusId, new ArrayList<>());

    }


    @CheckAuth(
//            permission = PermissionEnum.VIEW_UNIVERSITY_GRAND
    )
    @Override
    public ApiResult<ViewDTO> getViewById(UUID viewId) {
        return viewService.getViewById(viewId);
    }


    @CheckAuth(
//            permission = PermissionEnum.EDIT_UNIVERSITY_GRAND
    )
    @Override
    public ApiResult<?> editViewRowData(UUID viewId, UUID rowId, Map<String, Object> map) {
        return tariffGridService.editViewRowData(viewId, rowId, map);
    }


}