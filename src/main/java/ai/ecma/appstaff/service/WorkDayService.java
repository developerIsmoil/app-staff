package ai.ecma.appstaff.service;

import ai.ecma.appstaff.payload.ApiResult;

import java.util.UUID;

public interface WorkDayService {
    ApiResult<?> changeActive(UUID id);

    ApiResult<?> getAll();

    ApiResult<?> getAllActive();

    void saveAllWeekDaysToWorkDay();
}
