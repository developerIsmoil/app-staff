package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.*;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.view.InitialViewTypesDTO;
import ai.ecma.appstaff.payload.view.ViewDTO;
import ai.ecma.appstaff.projection.ITimeSheet;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The interface Timesheet service.
 */
public interface TimesheetFinanceService {


    ApiResult<ViewDTO> getViewById(UUID viewId);


    ApiResult<InitialViewTypesDTO> getViewTypes(String tableName);


    ApiResult<?> genericView(int page,ViewDTO viewDTO, String statusId);


    ApiResult<?> editViewRowData(UUID viewId, UUID rowId, Map<String, Object> map);


    ApiResult<List<Map<String, Object>>> getViewDataByIdList(UUID viewId, List<String> idList);

}
