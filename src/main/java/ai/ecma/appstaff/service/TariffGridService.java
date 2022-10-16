package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.TariffGrid;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.TariffGridDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TariffGridService {
    ApiResult<?> addTariffGrid(TariffGridDTO tariffGridDTO);

    ApiResult<?> editTariffGrid(UUID id, TariffGridDTO tariffGridDTO);

    ApiResult<?> getAllTariffGrid(Integer page, Integer size);

    ApiResult<TariffGridDTO> getOneTariffGridById(UUID id);

    ApiResult<?> deleteTariffGridById(UUID id);

    ApiResult<?> changeActiveTariffGrid(UUID id);

    ApiResult<?> getAllTariffGridSelect(Integer page, Integer size);

    ApiResult<?> deleteTariffGridByIdList(List<UUID> id);

    ApiResult<?> getFormTariffGrid();

    void existsTariffGrid(TariffGridDTO tariffGridDTO);

    TariffGrid getOneTariffGrid(TariffGridDTO tariffGridDTO);

    void existsByDepartmentId(UUID departmentId);

    void existsByEmployeeCategoryId(UUID employeeCategoryId);

    void existsByPositionId(UUID id);

    ApiResult<?> editViewRowData(UUID viewId, UUID rowId, Map<String, Object> map);
}
