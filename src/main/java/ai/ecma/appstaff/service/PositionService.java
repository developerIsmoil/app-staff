package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.Position;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.CustomPage;
import ai.ecma.appstaff.payload.PositionDTO;

import java.util.List;
import java.util.UUID;

public interface PositionService {

    ApiResult<PositionDTO> addPosition(PositionDTO positionDTO);

    ApiResult<PositionDTO> editPosition(UUID id, PositionDTO positionDTO);

    ApiResult<List<PositionDTO>> getAllPosition(Integer page, Integer size);

    ApiResult<List<PositionDTO>> getAllPositionForSelect(Integer page, Integer size);

    ApiResult<PositionDTO> getOnePosition(UUID id);

    ApiResult<?> deletePosition(UUID id);

    ApiResult<?> deletePositionByIdList(List<UUID> id);

    ApiResult<?> changeActivePosition(UUID id);

    List<PositionDTO> getAllActivePositionFromDB();

    ApiResult<PositionDTO> getFormPosition();

    Position getPositionFromDB(UUID id, boolean onlyActive);

    void existsAllByDepartmentId(List<UUID> departmentIdList);

    void existsByDepartmentId(UUID departmentId);

}
