package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.HistoryLog;
import ai.ecma.appstaff.entity.PrivilegeType;
import ai.ecma.appstaff.enums.EntityFieldNameEnum;
import ai.ecma.appstaff.enums.EntityNameEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.PrivilegeTypeDTO;
import ai.ecma.appstaff.repository.HistoryLogRepository;
import ai.ecma.appstaff.repository.PrivilegeTypeRepository;

import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PrivilegeTypeServiceImpl   implements PrivilegeTypeService {

    private final PrivilegeTypeRepository privilegeTypeRepository;
    private final HistoryLogRepository historyLogRepository;
    private final TemplateForSickService templateForSickService;
    private final EmployeeService employeeService;

    @Autowired
    public PrivilegeTypeServiceImpl(
            PrivilegeTypeRepository privilegeTypeRepository,
            HistoryLogRepository historyLogRepository,
            @Lazy TemplateForSickService templateForSickService,
            @Lazy EmployeeService employeeService) {
        this.privilegeTypeRepository = privilegeTypeRepository;
        this.historyLogRepository = historyLogRepository;
        this.templateForSickService = templateForSickService;
        this.employeeService = employeeService;
    }

    /**
     * Create new PrivilegeType and return created object PrivilegeTypeDTO
     *
     * @param privilegeTypeDTO PrivilegeTypeDTO
     * @return PrivilegeTypeDTO
     */
    @Override
    public PrivilegeTypeDTO create(PrivilegeTypeDTO privilegeTypeDTO) {
        log.info("method-entered : create, params : {} privilegeTypeDTO", privilegeTypeDTO);

        checkPrivilegeTypeExist(privilegeTypeDTO.getName(), Optional.empty());

        PrivilegeType privilegeType = makePrivilegeType(new PrivilegeType(), privilegeTypeDTO);

        try {
            privilegeTypeRepository.save(privilegeType);
        } catch (Exception e) {
            log.info("method-exit ERROR_PRIVILEGE_TYPE_SAVING");
            throw RestException.restThrow(ResponseMessage.ERROR_PRIVILEGE_TYPE_SAVING);
        }

        log.info("method-exit SUCCESS");
        return PrivilegeTypeDTO.mapPrivilegeTypeToPrivilegeTypeDTO(privilegeType);

    }

    @Override
    public PrivilegeTypeDTO edit(UUID id, PrivilegeTypeDTO privilegeTypeDTO) {
        log.info("method-entered : edit, params : {} id, {} privilegeTypeDTO", id, privilegeTypeDTO);

        checkPrivilegeTypeExist(privilegeTypeDTO.getName(), Optional.of(id));

        PrivilegeType privilegeTypeFromDB = getByIdAndActive(id, false);

        // createHistory(privilegeTypeFromDB, privilegeTypeDTO);

        PrivilegeType privilegeType = makePrivilegeType(privilegeTypeFromDB, privilegeTypeDTO);

        try {
            privilegeTypeRepository.save(privilegeType);
        } catch (Exception e) {
            log.info("method-exit ERROR_PRIVILEGE_TYPE_EDITING");
            throw RestException.restThrow(ResponseMessage.ERROR_PRIVILEGE_TYPE_EDITING);
        }

        log.info("method-exit SUCCESS");
        return PrivilegeTypeDTO.mapPrivilegeTypeToPrivilegeTypeDTO(privilegeType);

    }


    @Override
    public void delete(UUID id) {
        log.info("method-entered : delete, params : {} id", id);

        checkCanDeleteByEmployeePrivilegeTypeOrThrow(id);

        try {
            privilegeTypeRepository.deleteById(id);
        } catch (Exception e) {
            log.info("method-exit ERROR_PRIVILEGE_TYPE_NOT_FOUND");
            throw RestException.restThrow(ResponseMessage.ERROR_PRIVILEGE_TYPE_NOT_FOUND);
        }

        log.info("method-exit SUCCESS");
    }

    @Override
    public List<PrivilegeTypeDTO> getAll() {
        log.info("method-entered : getAll, params : ");
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<PrivilegeType> privilegeTypeList = privilegeTypeRepository.findAll(sortByColumn);

        log.info("method-exit SUCCESS");
        return privilegeTypeList
                .stream()
                .map(PrivilegeTypeDTO::mapPrivilegeTypeToPrivilegeTypeDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<PrivilegeTypeDTO> getAllActive() {
        log.info("method-entered : getAllActive, params : ");
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<PrivilegeType> privilegeTypeList = privilegeTypeRepository.findAllByActiveTrue(sortByColumn);

        log.info("method-exit SUCCESS");
        return privilegeTypeList
                .stream()
                .map(PrivilegeTypeDTO::fromPrivilegeTypeForSelect)
                .collect(Collectors.toList());

    }

    @Override
    public List<PrivilegeTypeDTO> getAllForSelect() {
        log.info("method-entered : getAllForSelect, params : ");
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<PrivilegeType> privilegeTypeList = privilegeTypeRepository.findAllByActiveTrue(sortByColumn);

        log.info("method-exit SUCCESS");
        return privilegeTypeList
                .stream()
                .map(PrivilegeTypeDTO::mapPrivilegeTypeToPrivilegeTypeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrivilegeType> getAllByIdList(List<UUID> privilegeTypeIdList) {
        log.info("method-entered : getAllByIdList, params : {} privilegeTypeIdList", privilegeTypeIdList);

        List<PrivilegeType> privilegeTypeList = privilegeTypeRepository.findAllByIdIn(privilegeTypeIdList);

        checkPrivilegeTypeIdAndResultIsSame(privilegeTypeIdList, privilegeTypeList);

        log.info("method-exit SUCCESS");
        return privilegeTypeList;
    }


    @Override
    public PrivilegeTypeDTO getById(UUID id) {
        log.info("method-entered : getById, params : {} id ", id);
        PrivilegeType privilegeTypeFromDB = getByIdAndActive(id, false);

        log.info("method-exit SUCCESS");
        return PrivilegeTypeDTO.mapPrivilegeTypeToPrivilegeTypeDTO(privilegeTypeFromDB);
    }


    @Override
    public PrivilegeType getByIdAndActive(UUID id, boolean onlyActive) {
        log.info("method-entered : getByIdAndActive, params : {} id, {} onlyActive", id, onlyActive);

        PrivilegeType privilegeType = getPrivilegeType(id);

        checkPrivilegeTypeIsActive(onlyActive, privilegeType);

        log.info("method-exit SUCCESS");
        return privilegeType;
    }

    /**
     * Check if onlyActive TRUE and privilegeType's active is FALSE then throw error to user
     *
     * @param onlyActive    boolean
     * @param privilegeType object entity
     */
    private void checkPrivilegeTypeIsActive(boolean onlyActive, PrivilegeType privilegeType) {
        if (onlyActive && !privilegeType.isActive()) {
            throw RestException.restThrow(ResponseMessage.ERROR_PRIVILEGE_TYPE_NOT_ACTIVE);
        }
    }

    /**
     * Get PrivilegeType from database by id. If not found throw error to user
     *
     * @param id UUID
     * @return founded entity PrivilegeType
     */
    private PrivilegeType getPrivilegeType(UUID id) {
        Optional<PrivilegeType> optionalPrivilegeType = privilegeTypeRepository.findById(id);

        if (optionalPrivilegeType.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_PRIVILEGE_TYPE_NOT_FOUND);
        }

        return optionalPrivilegeType.get();
    }


    /**
     * Check can delete this PrivilegeType by id
     *
     * @param id UUID
     */
    private void checkCanDeleteByEmployeePrivilegeTypeOrThrow(UUID id) {

        // Check PrivilegeType not use TemplateForSick entity. If used throw error to user
        templateForSickService.existsByEmployeePrivilegeTypeId(id);

        PrivilegeType privilegeType = getByIdAndActive(id, false);

        // Check PrivilegeType not use Employee entity. If used throw error to user
        employeeService.existsByEmployeePrivilegeTypeId(privilegeType);

    }


    /**
     * This method use create and edit actions.
     * If new create PrivilegeType check this object name not choose already.
     * If edit exist PrivilegeType check name not choose already but ignore this object
     *
     * @param name       PrivilegeType name
     * @param optionalId UUID
     */
    private void checkPrivilegeTypeExist(String name, Optional<UUID> optionalId) {

        boolean exists;

        // if optionalId is empty PrivilegeType is new, and it is creating
        if (optionalId.isEmpty()) {
            exists = privilegeTypeRepository.existsByName(name);

            // if optionalId is not empty PrivilegeType already have, and it is editing
        } else {
            exists = privilegeTypeRepository.existsByNameAndIdNot(name, optionalId.get());
        }

        // if this name already taken then throw error to user
        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_PRIVILEGE_TYPE_ALREADY_EXIST);
        }

    }

    /**
     * Make PrivilegeType by PrivilegeTypeDTO
     * This method work create and edit actions
     *
     * @param privilegeType    PrivilegeType
     * @param privilegeTypeDTO PrivilegeTypeDTO
     * @return PrivilegeType
     */
    private PrivilegeType makePrivilegeType(PrivilegeType privilegeType, PrivilegeTypeDTO privilegeTypeDTO) {

        privilegeType.setName(privilegeTypeDTO.getName());

        privilegeType.setActive(privilegeTypeDTO.isActive());

        return privilegeType;
    }


    private void checkPrivilegeTypeIdAndResultIsSame(List<UUID> privilegeTypeIdList, List<PrivilegeType> privilegeTypeList) {
        if (!Objects.equals(privilegeTypeIdList.size(), privilegeTypeList.size())) {
            throw RestException.restThrow(ResponseMessage.ERROR_PRIVILEGE_TYPE_NOT_FOUND);
        }
    }


    private void createHistory(PrivilegeType privilegeType, PrivilegeTypeDTO privilegeTypeDTO) {

        List<HistoryLog> historyLogList = new ArrayList<>();

        if (!Objects.equals(privilegeType.getName(), privilegeTypeDTO.getName())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.PRIVILEGE_TYPE_NAME,
                            privilegeType.getName(),
                            privilegeTypeDTO.getName(),
                            EntityNameEnum.PRIVILEGE_TYPE
                    )
            );
        }

        if (privilegeType.isActive() != privilegeTypeDTO.isActive()) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.PRIVILEGE_TYPE_ACTIVE,
                            privilegeType.isActive() + "",
                            privilegeTypeDTO.isActive() + "",
                            EntityNameEnum.EMPLOYEE_CATEGORY
                    )
            );
        }

        historyLogRepository.saveAll(historyLogList);
    }
}
