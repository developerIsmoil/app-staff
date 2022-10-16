package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.HolidayDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RequestMapping(path = HolidayController.HOLIDAY_CONTROLLER_PATH)
public interface HolidayController {
    String HOLIDAY_CONTROLLER_PATH = RestConstants.BASE_PATH_V1 + "/holiday";

    String ADD_HOLIDAY_PATH = "/add";
    String EDIT_HOLIDAY_PATH = "/edit/{id}";
    String GET_ALL_HOLIDAY_PATH = "/get-all";
    String GET_ALL_HOLIDAY_FOR_SELECT_PATH = "/get-all/select";
    String GET_ONE_HOLIDAY_PATH = "/get/{id}";
    String DELETE_HOLIDAY_PATH = "/delete/{id}";


    @PostMapping(path = ADD_HOLIDAY_PATH)
    ApiResult<HolidayDTO> addHoliday(
            @RequestBody @Valid HolidayDTO holidayDTO
    );


    @PutMapping(path = EDIT_HOLIDAY_PATH)
    ApiResult<HolidayDTO> editHoliday(
            @PathVariable(name = "id") UUID id,
            @RequestBody HolidayDTO holidayDTO
    );


    @GetMapping(path = GET_ALL_HOLIDAY_PATH)
    ApiResult<List<HolidayDTO>> getAllHoliday(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );


    @GetMapping(path = GET_ALL_HOLIDAY_FOR_SELECT_PATH)
    ApiResult<List<HolidayDTO>> getAllHolidayForSelect(
            @RequestParam(name = "page", required = false, defaultValue = RestConstants.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "size", required = false, defaultValue = RestConstants.DEFAULT_SIZE) Integer size
    );

    @GetMapping(path = GET_ONE_HOLIDAY_PATH)
    ApiResult<HolidayDTO> getOneHoliday(
            @PathVariable(name = "id") UUID id
    );


    @DeleteMapping(path = DELETE_HOLIDAY_PATH)
    ApiResult<?> deleteHoliday(
            @PathVariable(name = "id") UUID id
    );


}
