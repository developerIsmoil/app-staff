package ai.ecma.appstaff.controller.web;

import ai.ecma.appstaff.aop.annotation.CheckAuth;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.CustomPage;
import ai.ecma.appstaff.payload.HolidayDTO;
import ai.ecma.appstaff.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class HolidayControllerImpl implements HolidayController {

    private final HolidayService holidayService;

    @CheckAuth
    @Override
    public ApiResult<HolidayDTO> addHoliday(HolidayDTO holidayDTO) {
        return holidayService.addHoliday(holidayDTO);
    }


    @CheckAuth
    @Override
    public ApiResult<HolidayDTO> editHoliday(UUID id, HolidayDTO holidayDTO) {
        return holidayService.editHoliday(id, holidayDTO);
    }


    @CheckAuth
    @Override
    public ApiResult<List<HolidayDTO>> getAllHoliday(Integer page, Integer size) {
        return holidayService.getAllHoliday(page, size);
    }

    @CheckAuth
    @Override
    public ApiResult<List<HolidayDTO>> getAllHolidayForSelect(Integer page, Integer size) {
        return holidayService.getAllHolidayForSelect(page, size);
    }


    @CheckAuth
    @Override
    public ApiResult<HolidayDTO> getOneHoliday(UUID id) {
        return holidayService.getOneHoliday(id);
    }


    @CheckAuth
    @Override
    public ApiResult<?> deleteHoliday(UUID id) {
        return holidayService.deleteHoliday(id);
    }

}
