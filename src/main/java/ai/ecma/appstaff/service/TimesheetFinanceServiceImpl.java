package ai.ecma.appstaff.service;

import ai.ecma.appstaff.payload.view.InitialViewTypesDTO;
import ai.ecma.appstaff.payload.view.ViewDTO;
import ai.ecma.appstaff.payload.ApiResult;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * The type Timesheet service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimesheetFinanceServiceImpl implements TimesheetFinanceService {

    private final TimesheetService timesheetService;

    @Override
    public ApiResult<ViewDTO> getViewById(UUID viewId) {
        return timesheetService.getViewById(viewId);
    }

    @Override
    public ApiResult<InitialViewTypesDTO> getViewTypes(String tableName) {
        return timesheetService.getViewTypes(tableName);
    }

    @Override
    public ApiResult<?> genericView(int page, ViewDTO viewDTO, String statusId) {
        return timesheetService.genericView(page, viewDTO, statusId);
    }

    @Override
    public ApiResult<?> editViewRowData(UUID viewId, UUID rowId, Map<String, Object> map) {
        return timesheetService.editViewRowData(viewId, rowId, map);
    }

    @Override
    public ApiResult<List<Map<String, Object>>> getViewDataByIdList(UUID viewId, List<String> idList) {
        return timesheetService.getViewDataByIdList(viewId, idList);
    }

}
