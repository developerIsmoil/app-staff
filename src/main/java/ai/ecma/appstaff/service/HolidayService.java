package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.Holiday;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.CustomPage;
import ai.ecma.appstaff.payload.HolidayDTO;

import java.util.List;
import java.util.UUID;

public interface HolidayService {

    ApiResult<HolidayDTO> addHoliday(HolidayDTO departmentDTO);

    ApiResult<HolidayDTO> editHoliday(UUID id, HolidayDTO departmentDTO);

    ApiResult<List<HolidayDTO>> getAllHoliday(Integer page, Integer size);

    ApiResult<List<HolidayDTO>> getAllHolidayForSelect(Integer page, Integer size);

    ApiResult<HolidayDTO> getOneHoliday(UUID id);

    ApiResult<?> deleteHoliday(UUID id);

    ApiResult<?> deleteHolidayByIdList(List<UUID> id);

    ApiResult<?> changeActiveHoliday(UUID id);

    List<HolidayDTO> getAllActiveHolidayFromDB();

    Holiday getHolidayFromDB(UUID id, boolean onlyActive);

}
