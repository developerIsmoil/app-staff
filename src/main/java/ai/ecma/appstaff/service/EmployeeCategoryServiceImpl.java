package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.EmployeeCategory;
import ai.ecma.appstaff.entity.HistoryLog;
import ai.ecma.appstaff.enums.EntityFieldNameEnum;
import ai.ecma.appstaff.enums.EntityNameEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.*;
import ai.ecma.appstaff.repository.EmployeeCategoryRepository;
import ai.ecma.appstaff.repository.HistoryLogRepository;

import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeCategoryServiceImpl   implements EmployeeCategoryService {

    private final PositionService positionService;
    private final DepartmentService departmentService;
    private final HistoryLogRepository historyLogRepository;
    private final EmployeeCategoryRepository employeeCategoryRepository;
    private final EmployeeCategoryTypeService employeeCategoryTypeService;
    private final EmployeeService employeeService;
    private final TariffGridService tariffGridService;

    @Autowired
    public EmployeeCategoryServiceImpl(
            @Lazy PositionService positionService,
            @Lazy DepartmentService departmentService,
            HistoryLogRepository historyLogRepository,
            EmployeeCategoryRepository employeeCategoryRepository,
            @Lazy EmployeeCategoryTypeService employeeCategoryTypeService,
            @Lazy EmployeeService employeeService,
            @Lazy TariffGridService tariffGridService) {
        this.positionService = positionService;
        this.departmentService = departmentService;
        this.historyLogRepository = historyLogRepository;
        this.employeeCategoryRepository = employeeCategoryRepository;
        this.employeeCategoryTypeService = employeeCategoryTypeService;
        this.employeeService = employeeService;
        this.tariffGridService = tariffGridService;
    }

    @Override
    public ApiResult<EmployeeCategoryDTO> addEmployeeCategory(EmployeeCategoryDTO employeeCategoryDTO) {
        // log.info("class EmployeeCategoryServiceImpl => addEmployeeCategory => method entered => DTO : {}", employeeCategoryDTO);

        checkEmployeeCategoryExist(employeeCategoryDTO, null, Optional.empty());

        // HODIM KATEGORIYASINI YARATIB OLYABMIZ
        EmployeeCategory newEmployeeCategory = makeEmployeeCategory(new EmployeeCategory(), employeeCategoryDTO);

        try {
            // HODIM KATEGORIYASINI DATABASEGA SAQLAYABMIZ
            employeeCategoryRepository.save(newEmployeeCategory);
        } catch (Exception e) {
            // log.info("class EmployeeCategoryServiceImpl => addEmployeeCategory => error saving employeeCategory");
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_SAVING);
        }
        return ApiResult.successResponse(EmployeeCategoryDTO.fromEmployeeCategory(newEmployeeCategory), ResponseMessage.SUCCESS_EMPLOYEE_CATEGORY_SAVED);

    }

    @Override
    public ApiResult<EmployeeCategoryDTO> editEmployeeCategory(UUID id, EmployeeCategoryDTO employeeCategoryDTO) {
        // log.info("class EmployeeCategoryServiceImpl => editEmployeeCategory => method entered => ID : {} DTO : {}", id, employeeCategoryDTO);

        // KELGAN ID BO'YICHA HODIM KATEGORIYASINI DATABASEDAN OLAMIZ
        // AGAR TOPILMASA XATOLIKKA TUSHADI
        EmployeeCategory employeeCategoryFromDB = getEmployeeCategoryFromDB(id, false);

        checkEmployeeCategoryExist(employeeCategoryDTO, employeeCategoryFromDB, Optional.of(id));

        // createHistoryLog(employeeCategoryFromDB, employeeCategoryDTO);

        // HODIM KATEGORIYASINI KELGAN DTO BO'YICHA O'ZGARTIEIB OLYAPMIZ
        EmployeeCategory employeeCategory = makeEmployeeCategory(employeeCategoryFromDB, employeeCategoryDTO);

        try {
            // HODIM KATEGORIYASINI DATABASEGA SAQLAYABMIZ
            employeeCategoryRepository.save(employeeCategory);
        } catch (Exception e) {
            // log.info("class EmployeeCategoryServiceImpl => editEmployeeCategory => error saving employeeCategory");
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_EDITING);
        }
        return ApiResult.successResponse(EmployeeCategoryDTO.fromEmployeeCategory(employeeCategory), ResponseMessage.SUCCESS_EMPLOYEE_CATEGORY_EDITED);

    }

    @Override
    public ApiResult<List<EmployeeCategoryDTO>> getAllEmployeeCategory(Integer page, Integer size) {
        // log.info("class EmployeeCategoryServiceImpl => getAllEmployeeCategory => method entered => PAGE : {} SIZE : {} ", page, size);
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<EmployeeCategory> employeeCategoryList = employeeCategoryRepository.findAll(sortByColumn);
        return ApiResult.successResponse(getEmployeeCategoryDTOList(employeeCategoryList));

    }

    @Override
    public ApiResult<List<EmployeeCategoryDTO>> getAllEmployeeCategoryForSelect(UUID departmentId, UUID positionId, Integer page, Integer size) {
        // log.info("class EmployeeCategoryServiceImpl => getAllEmployeeCategoryForSelect => method entered => departmentId : {} positionId : {} ", departmentId, positionId);
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<EmployeeCategory> employeeCategoryList = employeeCategoryRepository.findAllByActiveTrueAndDepartment_IdAndPosition_Id(departmentId, positionId,sortByColumn);
        return ApiResult.successResponse(getEmployeeCategoryDTOList(employeeCategoryList));

    }

    @Override
    public ApiResult<EmployeeCategoryDTO> getOneEmployeeCategory(UUID id) {
        // log.info("class EmployeeCategoryServiceImpl => getOneEmployeeCategory => method entered => ID : {} ", id);
        EmployeeCategory employeeCategoryFromDB = getEmployeeCategoryFromDB(id, false);
        // log.info("class EmployeeCategoryServiceImpl => getOneEmployeeCategory => employeeCategory {} ", employeeCategoryFromDB);
        return ApiResult.successResponse(getEmployeeCategoryFormDTO(employeeCategoryFromDB));
    }

    @Override
    public ApiResult<?> deleteEmployeeCategory(UUID id) {
        // log.info("class EmployeeCategoryServiceImpl => deleteEmployeeCategory => method entered => ID : {} ", id);

        checkCanDeleteEmployeeCategoryOrThrow(id);

        try {
            employeeCategoryRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_EMPLOYEE_CATEGORY_DELETED);
    }

    private void checkCanDeleteEmployeeCategoryOrThrow(UUID employeeCategoryId) {

        employeeService.existsByEmployeeCategoryId(employeeCategoryId);

        tariffGridService.existsByEmployeeCategoryId(employeeCategoryId);
    }

    @Override
    public ApiResult<?> deleteEmployeeCategoryByIdList(List<UUID> id) {
        // log.info("class EmployeeCategoryServiceImpl => deleteEmployeeCategoryByIdList => method entered => ID : {} ", id);
        try {
            employeeCategoryRepository.deleteAllById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_EMPLOYEE_CATEGORY_DELETED);
    }

    @Override
    public ApiResult<?> changeActiveEmployeeCategory(UUID id) {
        // log.info("class EmployeeCategoryServiceImpl => changeActiveEmployeeCategory => method entered => ID : {} ", id);
        EmployeeCategory employeeCategoryFromDB = getEmployeeCategoryFromDB(id, false);

        boolean changedActive = !employeeCategoryFromDB.isActive();
        employeeCategoryFromDB.setActive(changedActive);

        // log.info("class EmployeeCategoryServiceImpl => changeActiveEmployeeCategory => changed active => ACTIVE : {} ", changedActive);
        employeeCategoryRepository.save(employeeCategoryFromDB);
        return ApiResult.successResponse(ResponseMessage.SUCCESS_EMPLOYEE_CATEGORY_STATUS_CHANGE);

    }

    @Override
    public List<EmployeeCategoryDTO> getAllActiveEmployeeCategoryFromDB() {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<EmployeeCategory> employeeCategoryList = employeeCategoryRepository.findAllByActiveTrue(sortByColumn);

        return employeeCategoryList
                .stream()
                .map(this::getEmployeeCategoryDTOForSelect)
                .collect(Collectors.toList());

    }

    @Override
    public ApiResult<EmployeeCategoryDTO> getFormEmployeeCategory() {

        List<DepartmentDTO> allActiveDepartmentFromDB = departmentService.getAllActiveDepartmentFromDB();
        List<PositionDTO> allActivePositionFromDB = positionService.getAllActivePositionFromDB();
        List<EmployeeCategoryTypeDTO> allActiveEmployeeCategoryTypeFromDB = employeeCategoryTypeService.getAllActiveEmployeeCategoryTypeFromDB();

        EmployeeCategoryDTO employeeCategoryDTO = new EmployeeCategoryDTO(
                OptionDTO.makeOptionDTO(
                        allActiveDepartmentFromDB
                ),
                OptionDTO.makeOptionDTO(
                        allActivePositionFromDB
                ),
                OptionDTO.makeOptionDTO(
                        allActiveEmployeeCategoryTypeFromDB
                )
        );

        return ApiResult.successResponse(employeeCategoryDTO);
    }

    @Override
    public EmployeeCategory getEmployeeCategoryFromDB(UUID id, boolean onlyActive) {
        if (Objects.isNull(id)) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_NOT_FOUND);
        }
        Optional<EmployeeCategory> optionalEmployeeCategory = employeeCategoryRepository.findById(id);

        if (optionalEmployeeCategory.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_NOT_FOUND);
        }
        EmployeeCategory employeeCategory = optionalEmployeeCategory.get();

        if (onlyActive) {
            if (!employeeCategory.isActive()) {
                throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_NOT_ACTIVE);
            }
        }

        return employeeCategory;
    }

    @Override
    public void existsByDepartmentId(UUID departmentId) {
        boolean exists = employeeCategoryRepository.existsByDepartmentId(departmentId);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_DEPARTMENT_CONNECT_EMPLOYEE_CATEGORY);
        }
    }

    @Override
    public void existsByEmployeeCategoryTypeId(UUID id) {
        boolean exists = employeeCategoryRepository.existsByEmployeeCategoryTypeId(id);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_EMPLOYEE_CATEGORY_TYPE_CONNECT_EMPLOYEE_CATEGORY);
        }
    }

    @Override
    public void existsByPositionId(UUID id) {

        boolean exists = employeeCategoryRepository.existsByPositionId(id);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_POSITION_CONNECT_EMPLOYEE_CATEGORY);
        }
    }

    private void createHistoryLog(EmployeeCategory employeeCategory, EmployeeCategoryDTO employeeCategoryDTO) {

        List<HistoryLog> historyLogList = new ArrayList<>();

        if (Objects.equals(employeeCategory.getDepartment().getId(), employeeCategoryDTO.getDepartmentId())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_CATEGORY_DEPARTMENT,
                            employeeCategory.getDepartment().getId().toString(),
                            employeeCategoryDTO.getId().toString(),
                            EntityNameEnum.EMPLOYEE_CATEGORY
                    )
            );
        }

        if (Objects.equals(employeeCategory.getPosition().getId(), employeeCategoryDTO.getPositionId())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_CATEGORY_TYPE,
                            employeeCategory.getPosition().getId() + "",
                            employeeCategoryDTO.getPositionId() + "",
                            EntityNameEnum.EMPLOYEE_CATEGORY
                    )
            );
        }

        if (Objects.equals(employeeCategory.getEmployeeCategoryType().getId(), employeeCategoryDTO.getEmployeeCategoryTypeId())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_CATEGORY_POSITION,
                            employeeCategory.getEmployeeCategoryType().getId() + "",
                            employeeCategoryDTO.getEmployeeCategoryTypeId() + "",
                            EntityNameEnum.EMPLOYEE_CATEGORY
                    )
            );
        }

        if (Objects.equals(employeeCategory.getRequirement(), employeeCategoryDTO.getRequirement())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_CATEGORY_REQUIREMENT,
                            employeeCategory.getRequirement() + "",
                            employeeCategoryDTO.getRequirement() + "",
                            EntityNameEnum.EMPLOYEE_CATEGORY
                    )
            );
        }

        if (Objects.equals(employeeCategory.getDescription(), employeeCategoryDTO.getDescription())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_CATEGORY_DESCRIPTION,
                            employeeCategory.getDescription() + "",
                            employeeCategoryDTO.getDescription() + "",
                            EntityNameEnum.EMPLOYEE_CATEGORY
                    )
            );
        }

        if (employeeCategory.isActive() != (employeeCategoryDTO.getActive())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_CATEGORY_ACTIVE,
                            employeeCategory.isActive() + "",
                            employeeCategoryDTO.getActive() + "",
                            EntityNameEnum.EMPLOYEE_CATEGORY
                    )
            );
        }


        historyLogRepository.saveAll(historyLogList);
    }

    private void checkEmployeeCategoryExist(EmployeeCategoryDTO employeeCategoryDTO, EmployeeCategory employeeCategoryFromDB, Optional<UUID> optionalId) {

        boolean exists;

        if (optionalId.isEmpty()) {

            // YANGI QO'SHILAYOTGAN HODIM KATEGORIYASI AVVAL DATABASEGA QO'SHILGAN YOKI YO'QLIGI TEKSHIRILGAN
            exists = employeeCategoryRepository.existsByDepartment_idAndEmployeeCategoryType_IdAndPosition_Id(
                    employeeCategoryDTO.getDepartmentId(),
                    employeeCategoryDTO.getEmployeeCategoryTypeId(),
                    employeeCategoryDTO.getPositionId()
            );
        } else {

            UUID departmentId = Objects.nonNull(employeeCategoryDTO.getDepartmentId()) ? employeeCategoryDTO.getDepartmentId() : employeeCategoryFromDB.getDepartment().getId();
            UUID employeeCategoryTypeId = Objects.nonNull(employeeCategoryDTO.getEmployeeCategoryTypeId()) ? employeeCategoryDTO.getEmployeeCategoryTypeId() : employeeCategoryFromDB.getEmployeeCategoryType().getId();
            UUID positionId = Objects.nonNull(employeeCategoryDTO.getPositionId()) ? employeeCategoryDTO.getPositionId() : employeeCategoryFromDB.getPosition().getId();

            // TAHRIRLANAYOTGANDA KIRITILGAN MA'LUMOTLARDAGI HODIM KATEGORIYASI
            // HOZIRDA DATABASEDA MAVJUDLIGINI TEKSHIRILGAN
            exists = employeeCategoryRepository.existsByDepartment_idAndEmployeeCategoryType_IdAndPosition_IdAndIdNot(
                    departmentId,
                    employeeCategoryTypeId,
                    positionId,
                    optionalId.get()
            );
        }

        // AGAR AVVAL BUNDAY HODIM KATEGORIYASI QO'SHILGAN BO'LSA XATOLIK QAYTARILGAN
        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_ALREADY_EXIST);
        }

    }

    private EmployeeCategory makeEmployeeCategory(EmployeeCategory employeeCategory, EmployeeCategoryDTO employeeCategoryDTO) {

        // FAQAT getEmployeeCategoryTypeId NULL BO'LMASA
        // DATABASEDAN KELGAN ID BO'YICHA OLIB employeeCategory GA SET QILIB QO'YILGAN
        if (Objects.nonNull(employeeCategoryDTO.getEmployeeCategoryTypeId()))
            employeeCategory.setEmployeeCategoryType(employeeCategoryTypeService.getEmployeeCategoryTypeFromDB(employeeCategoryDTO.getEmployeeCategoryTypeId(), true));

        // FAQAT getPositionId NULL BO'LMASA
        // DATABASEDAN KELGAN ID BO'YICHA OLIB employeeCategory GA SET QILIB QO'YILGAN
        if (Objects.nonNull(employeeCategoryDTO.getPositionId()))
            employeeCategory.setPosition(positionService.getPositionFromDB(employeeCategoryDTO.getPositionId(), true));

        // FAQAT getDepartmentId NULL BO'LMASA
        // DATABASEDAN KELGAN ID BO'YICHA OLIB employeeCategory GA SET QILIB QO'YILGAN
        if (Objects.nonNull(employeeCategoryDTO.getDepartmentId()))
            employeeCategory.setDepartment(departmentService.getDepartmentFromDB(employeeCategoryDTO.getDepartmentId(), true));

        //KELGAN MA'LUMOT NULL BO'LMASA employeeCategory GA SET QILINGAN
        if (Objects.nonNull(employeeCategoryDTO.getDescription()))
            employeeCategory.setDescription(employeeCategoryDTO.getDescription());

        //KELGAN MA'LUMOT NULL BO'LMASA employeeCategory GA SET QILINGAN
        if (Objects.nonNull(employeeCategoryDTO.getRequirement()))
            employeeCategory.setRequirement(employeeCategoryDTO.getRequirement());

        //KELGAN MA'LUMOT NULL BO'LMASA employeeCategory GA SET QILINGAN
        if (Objects.nonNull(employeeCategoryDTO.getActive()))
            employeeCategory.setActive(employeeCategoryDTO.getActive());

        employeeCategory.setName(employeeCategory.getPosition().getName() + " " + employeeCategory.getEmployeeCategoryType().getName());

        // TAYYOR BO'LGAN OBYEKTNI QAYTARGANMIZ
        return employeeCategory;
    }

    private EmployeeCategoryDTO getEmployeeCategoryFormDTO(EmployeeCategory employeeCategory) {

        List<DepartmentDTO> allActiveDepartmentFromDB = departmentService.getAllActiveDepartmentFromDB();
        List<PositionDTO> allActivePositionFromDB = positionService.getAllActivePositionFromDB();
        List<EmployeeCategoryTypeDTO> allActiveEmployeeCategoryTypeFromDB = employeeCategoryTypeService.getAllActiveEmployeeCategoryTypeFromDB();

        return new EmployeeCategoryDTO(
                employeeCategory.getId(),
                OptionDTO.makeOptionDTO(
                        allActiveDepartmentFromDB,
                        Collections.singletonList(employeeCategory.getDepartment().getId())
                ),
                OptionDTO.makeOptionDTO(
                        allActivePositionFromDB,
                        Collections.singletonList(employeeCategory.getPosition().getId())
                ),
                OptionDTO.makeOptionDTO(
                        allActiveEmployeeCategoryTypeFromDB,
                        Collections.singletonList(employeeCategory.getEmployeeCategoryType().getId())
                ),
                employeeCategory.getRequirement(),
                employeeCategory.getDescription(),
                employeeCategory.isActive()
        );
    }

    private EmployeeCategoryDTO getEmployeeCategoryDTOForSelect(EmployeeCategory employeeCategory) {
        String position = Objects.nonNull(employeeCategory.getPosition()) ? employeeCategory.getPosition().getName() : "";
        String categoryType = Objects.nonNull(employeeCategory.getEmployeeCategoryType()) ? employeeCategory.getEmployeeCategoryType().getName() : "";
        return new EmployeeCategoryDTO(
                employeeCategory.getId(),
                employeeCategory.getPosition().getId(),
                position + " | " + categoryType
        );
    }

    private EmployeeCategoryDTO getEmployeeCategoryDTOSelect(EmployeeCategory employeeCategory) {
        return new EmployeeCategoryDTO(
                employeeCategory.getId(),
                employeeCategory.isActive(),
                employeeCategory.getEmployeeCategoryType() != null ? employeeCategory.getEmployeeCategoryType().getName() : null
        );
    }

    private List<EmployeeCategoryDTO> getEmployeeCategoryDTOList(List<EmployeeCategory> employeeCategoryList) {
        return employeeCategoryList
                .stream()
                .map(EmployeeCategoryDTO::fromEmployeeCategory)
                .collect(Collectors.toList());
    }

    private List<EmployeeCategoryDTO> getEmployeeCategoryDTOListSelect(List<EmployeeCategory> employeeCategoryList) {
        return employeeCategoryList
                .stream()
                .map(this::getEmployeeCategoryDTOSelect)
                .collect(Collectors.toList());
    }

}
