package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.PrivilegeType;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.PrivilegeTypeDTO;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PrivilegeTypeService {

    PrivilegeTypeDTO create(PrivilegeTypeDTO privilegeTypeDTO);

    PrivilegeTypeDTO edit(UUID id, PrivilegeTypeDTO privilegeTypeDTO);

    void delete(UUID id);

    List<PrivilegeTypeDTO> getAll();

    List<PrivilegeTypeDTO> getAllActive();

    List<PrivilegeTypeDTO> getAllForSelect();

    List<PrivilegeType> getAllByIdList(List<UUID> idList);

    PrivilegeTypeDTO getById(UUID id);

    PrivilegeType getByIdAndActive(UUID id, boolean onlyActive);

}
