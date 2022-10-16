package ai.ecma.appstaff.controller.otherServices;

import ai.ecma.appstaff.payload.*;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

@RequestMapping(TurniketController.TURNIKET_CONTROLLER_PATH)
public interface TurniketController {
    String TURNIKET_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/turniket/";
    String GET_IN_OUT_DATA_PATH = "in-out-data";
    String GET_BY_FILTER_PATH = "by-filter";
    String GET_ONE_DAY_OF_EMPLOYEE_PATH = "one-day";
    String DOWNLOAD_EXCEL_PATH = "download-excel";
    String DOWNLOAD_EXCEL_PATH_2 = "download-excel-2";

    @GetMapping(GET_IN_OUT_DATA_PATH)
    ApiResult<TurniketInOutInfoDTO> getInOutData();

    @PostMapping(GET_BY_FILTER_PATH)
    ApiResult<TurniketInOutInfoDTO> getByFilter(@RequestBody @Valid @NotEmpty List<@Valid TurniketFilterDTO> filters);

    @GetMapping(GET_ONE_DAY_OF_EMPLOYEE_PATH + "/{userDailyInOutId}")
    ApiResult<UserDailyInOutDTO> getOneDayDate(@PathVariable UUID userDailyInOutId);

    @PostMapping(GET_ONE_DAY_OF_EMPLOYEE_PATH)
    ApiResult<UserDailyInOutDTO> getOneDayData(@RequestBody OneDayTurniketDTO oneDayTurniketDTO);

    @GetMapping(DOWNLOAD_EXCEL_PATH + "/{filters}")
    ResponseEntity<Resource> downloadExcel(@PathVariable String filters, @RequestParam String token);

}
