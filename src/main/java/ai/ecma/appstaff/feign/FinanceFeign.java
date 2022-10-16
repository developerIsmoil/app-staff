package ai.ecma.appstaff.feign;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.feign.AttachmentFeignDTO;
import ai.ecma.appstaff.payload.feign.ConfirmedTimeSheetDTO;
import ai.ecma.appstaff.payload.view.ViewDTOForFinanceService;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = RestConstants.FINANCE_SERVICE, configuration = FeignConfig.class)
public interface FinanceFeign {

    String GET_FILE_INFO_BY_ID_LIST_PATH = "/attachment/get-file-info-by-id-list";
    String CONFIRMED_TIMESHEET_INFO_PATH = "/other-microservice/confirmed-timesheet-info";
//    String CONFIRMED_TIMESHEET_INFO_PATH = "/staff/confirmed-timeSheet-info";
    String GENERIC_VEW_PATH = "/view-for-staff/generic-view";

    @GetMapping(value = GET_FILE_INFO_BY_ID_LIST_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<List<AttachmentFeignDTO>> getFileInfoByIdList(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody List<UUID> fileIdList
    );

    @PostMapping(value = CONFIRMED_TIMESHEET_INFO_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<?> sendToTimeSheetInfoFinanceService(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody List<ConfirmedTimeSheetDTO> confirmedTimeSheetDTOList
    );

    @PostMapping(value = GENERIC_VEW_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<List<String>> genericViewForTimesheetForFinance(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody ViewDTOForFinanceService viewDTOForFinance);

}
