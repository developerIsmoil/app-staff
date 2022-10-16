package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.*;
import ai.ecma.appstaff.enums.*;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.*;
import ai.ecma.appstaff.payload.DepartmentDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldValueDTO;
import ai.ecma.appstaff.payload.feign.*;
import ai.ecma.appstaff.projection.IEmployee;
import ai.ecma.appstaff.repository.*;

import ai.ecma.appstaff.service.customField.CustomFieldValueService;
import ai.ecma.appstaff.service.feign.FeignService;
import ai.ecma.appstaff.service.otherService.TurniketService;
import ai.ecma.appstaff.service.view.ViewService;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.ResponseMessage;
import ai.ecma.appstaff.utils.RestConstants;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final ViewService viewService;
    private final SkillService skillService;
    private final FeignService feignService;
    private final TimesheetService timeSheetService;
    private final AdditionalService additionalService;
    private final PositionService positionService;
    private final TariffGridService tariffGridService;
    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final HistoryLogRepository historyLogRepository;
    private final PhoneNumberTypeService phoneNumberTypeService;
    private final PrivilegeTypeService privilegeTypeService;
    private final EmploymentInfoRepository employmentInfoRepository;
    private final EmployeeCategoryService employeeCategoryService;
    private final EmployeeWorkDayRepository employeeWorkDayRepository;
    private final EmployeeAttachmentRepository employeeAttachmentRepository;
    private final EmployeePhoneNumberRepository employeePhoneNumberRepository;
    private final EmployeeEducationHistoryRepository employeeEducationHistoryRepository;
    private final EmployeeExperienceHistoryRepository employeeExperienceHistoryRepository;
    private final TimeSheetEmployeeRepository timeSheetEmployeeRepository;
    private final CustomFieldValueService customFieldValueService;
    private final TurniketService turniketService;

    @Autowired
    public EmployeeServiceImpl(
            ViewService viewService,
            SkillService skillService,
            FeignService feignService,
            TimesheetService timeSheetService,
            AdditionalService additionalService,
            @Lazy PositionService positionService,
            TariffGridService tariffGridService,
            EmployeeRepository employeeRepository,
            @Lazy DepartmentService departmentService,
            HistoryLogRepository historyLogRepository,
            @Lazy PhoneNumberTypeService phoneNumberTypeService,
            @Lazy PrivilegeTypeService privilegeTypeService,
            EmploymentInfoRepository employmentInfoRepository,
            @Lazy EmployeeCategoryService employeeCategoryService,
            EmployeeWorkDayRepository employeeWorkDayRepository,
            EmployeeAttachmentRepository employeeAttachmentRepository,
            EmployeePhoneNumberRepository employeePhoneNumberRepository,
            EmployeeEducationHistoryRepository employeeEducationHistoryRepository,
            EmployeeExperienceHistoryRepository employeeExperienceHistoryRepository,
            TimeSheetEmployeeRepository timeSheetEmployeeRepository,
            CustomFieldValueService customFieldValueService,
            TurniketService turniketService) {
        this.viewService = viewService;
        this.skillService = skillService;
        this.feignService = feignService;
        this.timeSheetService = timeSheetService;
        this.additionalService = additionalService;
        this.positionService = positionService;
        this.tariffGridService = tariffGridService;
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.historyLogRepository = historyLogRepository;
        this.phoneNumberTypeService = phoneNumberTypeService;
        this.privilegeTypeService = privilegeTypeService;
        this.employmentInfoRepository = employmentInfoRepository;
        this.employeeCategoryService = employeeCategoryService;
        this.employeeWorkDayRepository = employeeWorkDayRepository;
        this.employeeAttachmentRepository = employeeAttachmentRepository;
        this.employeePhoneNumberRepository = employeePhoneNumberRepository;
        this.employeeEducationHistoryRepository = employeeEducationHistoryRepository;
        this.employeeExperienceHistoryRepository = employeeExperienceHistoryRepository;
        this.timeSheetEmployeeRepository = timeSheetEmployeeRepository;
        this.customFieldValueService = customFieldValueService;
        this.turniketService = turniketService;
    }

    @Transactional
    @Override
    public ApiResult<?> addEmployee(EmployeeDTO employeeDTO) {

        // log.info("class EmployeeServiceImpl => addEmployee => method entered => DTO : {}", employeeDTO);

        checkTariffGridIsExist(employeeDTO.getEmployments());

        log.info("__addEmployee checkBranchList");
        checkBranchList(employeeDTO.getEmployments());

        log.info("__addEmployee checkCompanyList");
        checkCompanyList(employeeDTO.getEmployments());

        log.info("__addEmployee checkAttachmentIdListValid");
        checkAttachmentIdListValid(employeeDTO);

        Employee employee = makeEmployee(new Employee(), employeeDTO);

        saveEmployee(employee);

        makeEmployeeOtherDetails(employeeDTO, employee);

        log.info("__addEmployee saveUserAndGetUserIdFromAuthService");
        UUID userId = saveUserAndGetUserIdFromAuthService(employeeDTO);

        log.info("__addEmployee checkUserIsAlreadyExists");
        checkUserIsAlreadyExists(userId);

        employee.setUserId(userId);

        // faqat view uchun bu
        employee.setPhoneNumber(CommonUtils.makePhoneNumber(employeeDTO.getPhoneNumber()));

        saveEmployee(employee);

        //AGAR RASM HAM YUKLANGAN BO'LSA UNI TURNIKETGA QO'SHADI
        turniketService.addUserInTurniket(employee, employeeDTO.getEmployerInfo());

        return getEmployeeFormById(employee.getId());

    }


    @Transactional
    @Override
    public ApiResult<?> editEmployee(UUID id, EmployeeDTO employeeDTO) {
        // log.info("class EmployeeServiceImpl => editEmployee => method entered => ID : {} DTO : {}", id, employeeDTO);

        checkTariffGridIsExist(employeeDTO.getEmployments());

        Employee employeeFromDB = getEmployeeFromDB(id);

        //RASM O'ZGARGANMI TURNIKET UCHUN
        boolean photoChanged = !Objects.equals(employeeFromDB.getPhotoId(), employeeDTO.getEmployerInfo().getPhotoId()) && Objects.nonNull(employeeDTO.getEmployerInfo().getPhotoId());

        // LOGGA YOZISH UCHUN
        // createHistoryLog(employeeFromDB, employeeDTO);

        makeEmployee(employeeFromDB, employeeDTO);

        // DATABASEGA SAQLANGAN HODIM UCHUN EmployeeAttachment YARATIB DATABASEGA SAQLASH
        makeEmployeeAttachment(employeeFromDB, employeeDTO.getAttachments());

        // DATABASEGA SAQLANGAN HODIM UCHUN EmploymentInfo YARATIB DATABASEGA SAQLASH
        makeEmploymentInfo(employeeFromDB, employeeDTO.getEmployments());

        // DATABASEGA SAQLANGAN HODIM UCHUN EmployeeExperienceInfo YARATIB DATABASEGA SAQLASH
        makeEmployeeExperienceHistory(employeeFromDB, employeeDTO.getExperiences());

        // DATABASEGA SAQLANGAN HODIM UCHUN EmployeeEducationInfo YARATIB DATABASEGA SAQLASH
        makeEmployeeEducationHistory(employeeFromDB, employeeDTO.getEducations());

        makeEmployeePhoneNumber(employeeFromDB, employeeDTO.getEmployerInfo().getPhoneNumbers());

        makeEmployeeSkill(employeeFromDB, employeeDTO.getSkill());


        employeeRepository.save(employeeFromDB);

        employeeDTO.setId(employeeFromDB.getUserId());

//        UserFeignDTO userFeignDTO = editStaffBugFixPathAuthService(employeeDTO);
//
//        employeeFromDB.setUserId(userFeignDTO.getId());
//        employeeRepository.save(employeeFromDB);
//
        editUserFromAuthService(employeeDTO);

        //AGAR RASM O'ZGARGAN BO'LSA TURNIKETDAGI RASMNI O'ZGARTIRISH UCHUN
        if (photoChanged)
            turniketService.editUserInTurniket(employeeFromDB, employeeDTO.getEmployerInfo());

        return getEmployeeFormById(employeeFromDB.getId());
    }

    @Override
    public ApiResult<?> getAllEmployee(Integer page, Integer size) {
        // log.info("class EmployeeServiceImpl => getAllEmployee => method entered => PAGE : {} SIZE : {} ", page, size);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Employee> employeePage = employeeRepository.findAll(pageRequest);

        CustomPage<EmployeeDTO> employeeCustomPage = new CustomPage<>(
                getEmployeeDTOList(employeePage.getContent()),
                employeePage.hasNext()
        );

        return ApiResult.successResponse(employeeCustomPage);

    }

    @Override
    public ApiResult<?> getAllEmployee() {
        List<Employee> employeeList = employeeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<UserDTO> userDTOList = employeeList
                .stream()
                .map(employee ->
                        new UserDTO(
                                employee.getId(),
                                employee.getFirstName(),
                                employee.getLastName()
                        ))
                .collect(Collectors.toList());

        return ApiResult.successResponse(userDTOList);
    }

    @Override
    public ApiResult<EmployeeDTO> getOneEmployee(UUID id) {
        // log.info("class EmployeeServiceImpl => getOneEmployee => method entered => ID : {} ", id);

        Employee employeeFromDB = getEmployeeFromDB(id);

//        // log.info("class EmployeeServiceImpl => getOneEmployee => employee {} ", employeeFromDB);
        EmployeeDTO employeeDTO = getEmployeeDTO(employeeFromDB);

        return ApiResult.successResponse(employeeDTO);

    }

    @Override
    public ApiResult<?> deleteEmployee(UUID id) {
        // log.info("class EmployeeServiceImpl => deleteEmployee => method entered => ID : {} ", id);
        try {
            Employee employeeFromDB = getEmployeeFromDB(id);
            //TURNIKETDAN HAM O'CHIRAMIZ EMPLOYEE NI
            turniketService.deleteUserFromTurniket(employeeFromDB);

            employmentInfoRepository.deleteByEmployee_Id(id);
            employeeEducationHistoryRepository.deleteByEmployee_Id(id);
            employeeExperienceHistoryRepository.deleteByEmployee_Id(id);
            employeeRepository.deleteById(id);

        } catch (Exception e) {
            e.printStackTrace();
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_NOT_DELETED);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_EMPLOYEE_DELETED);
    }

    @Override
    public ApiResult<?> getEmployeeForm() {

        AdditionalDTO additionalDTO = getAdditionalDTO();

        EmployeeFormInfoDTO employeeFormInfoDTO = getEmployeeFormInfoDTO(additionalDTO);

        EmploymentFormInfoDTO employmentFormInfoDTO = getEmploymentFormInfoDTO(additionalDTO);

        EmployeeFormDTO employeeFormDTO = getEmployeeFormDTO(additionalDTO, employeeFormInfoDTO, employmentFormInfoDTO);

        return ApiResult.successResponse(employeeFormDTO);
    }


    @Override
    public ApiResult<?> getEmployeeFormById(UUID employeeId) {

        try {
            synchronizeEmployeeRoleInfo(employeeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AdditionalDTO additionalDTO = getAdditionalDTO();

        Employee employeeFromDB = getEmployeeFromDB(employeeId);

        EmployeeFormDTO employeeFormDTO = getEmployeeFormDTO(employeeId, additionalDTO, employeeFromDB);

        return ApiResult.successResponse(employeeFormDTO);
    }


    @Override
    public ApiResult<?> editViewRowData(UUID viewId, UUID rowId, Map<String, Object> map) {

        Employee employee = getEmployeeFromDB(rowId);

        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            String key = stringObjectEntry.getKey();
            Object value = stringObjectEntry.getValue();
            if (key.equals(ColumnKey.FIRST_NAME)) {
                employee.setFirstName(value.toString());
            } else if (key.equals(ColumnKey.LAST_NAME)) {
                employee.setLastName(value.toString());
            } else if (Pattern.matches(RestConstants.UUID_REGEX, key)) {

                //  custom field uchun bu
                customFieldValueService.addCustomFieldValue(
                        new CustomFieldValueDTO(
                                value,
                                UUID.fromString(key),
                                rowId.toString()
                        )
                );
            }
            //TODO BUNI DAVOM ETTORISH KERAK
        }

        employeeRepository.save(employee);

        List<Map<String, Object>> resultData = viewService.getRowData(viewId, Collections.singletonList(rowId.toString()));

        if (!resultData.isEmpty()) {
            return ApiResult.successResponse(resultData.get(0));
        } else {
            throw RestException.restThrow(ResponseMessage.ERROR_DATA_NOT_FOUND);
        }

    }

    @Override
    public List<BranchFeignDTO> getBranchesByUserId(UUID userId) {

        List<Long> branchIdListByUserId = employeeRepository.findAllBranchIdByUserId(userId);

        return feignService.getBranchByIdList(branchIdListByUserId);
    }

    @Override
    public void existsByDepartmentId(UUID departmentId) {
        boolean exists = employmentInfoRepository.existsByDepartmentId(departmentId);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_YOU_CAN_NOT_DELETE_DEPARTMENT_CONNECT_EMPLOYMENT_INFO);
        }
    }

    @Override
    public void existsByEmployeeCategoryId(UUID employeeCategoryId) {

        boolean exists = employmentInfoRepository.existsByEmployeeCategoryId(employeeCategoryId);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_EMPLOYEE_CATEGORY_CONNECT_EMPLOYMENT_INFO);
        }
    }

    @Override
    public void existsByEmployeeCategoryTypeId(UUID id) {
        boolean exists = employmentInfoRepository.existsByEmployeeCategoryTypeId(id);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_EMPLOYEE_CATEGORY_TYPE_CONNECT_EMPLOYMENT_INFO);
        }
    }

    @Override
    public void existsByPhoneNumberTypeId(UUID id) {

        boolean exists = employeePhoneNumberRepository.existsByTypeId(id);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_PHONE_NUMBER_TYPE_CONNECT_EMPLOYEE_PHONE_NUMBER);
        }

    }

    @Override
    public void existsByPositionId(UUID id) {
        boolean exists = employmentInfoRepository.existsByPositionId(id);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_POSITION_CONNECT_EMPLOYMENT_INFO);
        }
    }

    @Override
    public void existsByEmployeePrivilegeTypeId(PrivilegeType privilegeType) {

        boolean exists = employeeRepository.existsByPrivilegeTypesIn(Collections.singletonList(privilegeType));

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_PRIVILEGE_TYPE_CONNECT_EMPLOYEE);
        }
    }

    @Override
    public List<EmploymentInfoFeignDTO> getEmploymentInfoById(List<UUID> employmentInfoIdList) {

        List<EmploymentInfoFeignDTO> employmentInfoFeignDTOList = new ArrayList<>();

        List<EmploymentInfo> employmentInfoList = employmentInfoRepository.findAllById(employmentInfoIdList);

        Set<Long> branchIdList = new HashSet<>();

        for (EmploymentInfo employmentInfo : employmentInfoList) {

            EmploymentInfoFeignDTO employmentInfoFeignDTO = new EmploymentInfoFeignDTO(
                    employmentInfo.getId(),
                    CommonUtils.concatTwoStringWithSpace(employmentInfo.getEmployee().getFirstName(), employmentInfo.getEmployee().getLastName()),
                    employmentInfo.getBranchId(),
                    employmentInfo.getDepartment().getId(),
                    employmentInfo.getDepartment().getName(),
                    employmentInfo.getPosition().getId(),
                    employmentInfo.getPosition().getName(),
                    employmentInfo.getEmployeeCategory().getId(),
                    employmentInfo.getEmployeeCategory().getEmployeeCategoryType().getName()
            );

            branchIdList.add(employmentInfo.getBranchId());

            employmentInfoFeignDTOList.add(employmentInfoFeignDTO);
        }

        return getEmploymentInfoFeignDTOList(employmentInfoFeignDTOList, branchIdList);

    }

    @NotNull
    private List<EmploymentInfoFeignDTO> getEmploymentInfoFeignDTOList(List<EmploymentInfoFeignDTO> employmentInfoFeignDTOList,
                                                                       Set<Long> branchIdList) {
        List<BranchFeignDTO> branchFeignDTOList = feignService.getBranchByIdList(branchIdList);

        for (EmploymentInfoFeignDTO employmentInfoFeignDTO : employmentInfoFeignDTOList) {

            BranchFeignDTO branch = branchFeignDTOList
                    .stream()
                    .filter(branchFeignDTO -> Objects.equals(employmentInfoFeignDTO.getBranchId(), branchFeignDTO.getId()))
                    .findAny()
                    .orElse(new BranchFeignDTO(RestConstants.UNKNOWN));

            employmentInfoFeignDTO.setBranch(branch.getName());

        }

        return employmentInfoFeignDTOList;
    }

    @Override
    public List<EmploymentInfoFeignDTO> getEmploymentInfoFullInfoById(List<UUID> employmentInfoIdList) {

        List<EmploymentInfoFeignDTO> employmentInfoFeignDTOList = new ArrayList<>();

        List<EmploymentInfo> employmentInfoList = employmentInfoRepository.findAllById(employmentInfoIdList);

        List<EmployeeWorkDay> employeeWorkDays = employeeWorkDayRepository.findAllByEmploymentInfoIn(employmentInfoList);

        List<Employee> employeeList = employmentInfoList.stream().map(EmploymentInfo::getEmployee).collect(Collectors.toList());

        List<EmployeePhoneNumber> employeePhoneNumberList = employeePhoneNumberRepository.findAllByEmployeeInAndMainIsTrue(employeeList);

        Set<Long> branchIdList = new HashSet<>();

        for (EmploymentInfo employmentInfo : employmentInfoList) {

            StringBuilder weekDaysStringBuilder = new StringBuilder();

            for (EmployeeWorkDay employeeWorkDay : employeeWorkDays) {

                if (employmentInfo.getId().equals(employeeWorkDay.getEmploymentInfo().getId())) {
                    weekDaysStringBuilder.append(employeeWorkDay.getWeekDay().name());
                    weekDaysStringBuilder.append(" ");
                }
            }

            EmployeePhoneNumber phoneNumber = employeePhoneNumberList
                    .stream()
                    .filter(employeePhoneNumber -> Objects.equals(employeePhoneNumber.getEmployee(), employmentInfo.getEmployee()))
                    .findAny()
                    .orElse(new EmployeePhoneNumber(RestConstants.UNKNOWN));

            String weekDays = weekDaysStringBuilder.toString();

            EmploymentInfoFeignDTO employmentInfoFeignDTO = new EmploymentInfoFeignDTO(
                    employmentInfo.getId(),
                    CommonUtils.concatTwoStringWithSpace(employmentInfo.getEmployee().getFirstName(), employmentInfo.getEmployee().getLastName()),
                    employmentInfo.getBranchId(),
                    employmentInfo.getDepartment().getId(),
                    employmentInfo.getDepartment().getName(),
                    employmentInfo.getPosition().getId(),
                    employmentInfo.getPosition().getName(),
                    employmentInfo.getEmployeeCategory().getId(),
                    employmentInfo.getEmployeeCategory().getEmployeeCategoryType().getName(),
                    phoneNumber.getPhoneNumber(),
                    employmentInfo.getPaymentCriteriaType(),
                    employmentInfo.getContractForm(),
                    employmentInfo.getEmployeeMode(),
                    weekDays
            );

            branchIdList.add(employmentInfo.getBranchId());

            employmentInfoFeignDTOList.add(employmentInfoFeignDTO);
        }

        return getEmploymentInfoFeignDTOList(employmentInfoFeignDTOList, branchIdList);

    }

    @Override
    public List<UserDTO> getAllOperators() {
        List<UserDTO> operators = feignService.getAllOperators();

        return getAllEmployee(operators);

    }

    @Override
    public List<UserDTO> getAllMentors() {
        List<UserDTO> mentors = feignService.getAllMentors();

        return getAllEmployee(mentors);
    }

    @Override
    public List<IEmployee> getAllMentorsByBranch(Integer id) {

        List<UserDTO> mentors = feignService.safeGetAllMentors();

        List<UUID> mentorsId = mentors
                .stream()
                .map(UserDTO::getId)
                .collect(Collectors.toList());

        return employeeRepository.getEmployeeByBranchId(mentorsId, id);
    }

    @Override
    public ApiResult<?> resignationEmployee(EmployeeResignationDTO employeeResignationDTO) {

        Employee employeeFromDB = getEmployeeFromDB(employeeResignationDTO.getEmployeeId());

        Date resignationDate = new Date(employeeResignationDTO.getResignationDate());

        employeeFromDB.setResignation(Boolean.TRUE);
        employeeFromDB.setResignationDate(resignationDate);
        employeeFromDB.setResignationDescription(employeeResignationDTO.getDescription());

        List<EmploymentInfo> employmentInfoList = getEmploymentInfoByEmployee(employeeFromDB);

        for (EmploymentInfo employmentInfo : employmentInfoList) {

            Boolean isResignation = employmentInfo.getResignation();

            if (Objects.isNull(isResignation) || Objects.equals(Boolean.FALSE, isResignation)) {
                employmentInfo.setResignation(Boolean.TRUE);
                employmentInfo.setResignationDate(resignationDate);
                employmentInfo.setResignationDescription(employeeResignationDTO.getDescription());
            }
        }

        employeeRepository.save(employeeFromDB);
        employmentInfoRepository.saveAll(employmentInfoList);

        saveResignationEmployeeAttachments(employeeFromDB, employeeResignationDTO);
        return getEmployeeFormById(employeeFromDB.getId());

    }

    @Override
    public ApiResult<?> resignationEmployment(EmployeeResignationDTO employeeResignationDTO) {

        EmploymentInfo employmentInfoFromDB = getEmploymentInfoFromDB(employeeResignationDTO.getEmploymentId());
        Boolean isResignation = employmentInfoFromDB.getResignation();

        Date resignationDate = new Date(employeeResignationDTO.getResignationDate());

        if (Objects.isNull(isResignation) || Objects.equals(Boolean.FALSE, isResignation)) {

            employmentInfoFromDB.setResignation(Boolean.TRUE);
            employmentInfoFromDB.setResignationDate(resignationDate);
            employmentInfoFromDB.setResignationDescription(employeeResignationDTO.getDescription());
        }

        employmentInfoRepository.save(employmentInfoFromDB);

        saveResignationEmployeeAttachments(employmentInfoFromDB.getEmployee(), employeeResignationDTO);
        return getEmployeeFormById(employmentInfoFromDB.getEmployee().getId());
    }

    @Override
    public ApiResult<?> editViewRowDataForMentorView(UUID viewId, UUID rowId, Map<String, Object> map) {

        return ApiResult.successResponse("View da malumotlarni tahrirlab bo'lmaydi ");

    }

    private List<UserDTO> getAllEmployee(List<UserDTO> employees) {

        List<UUID> employeesId = employees
                .stream()
                .map(UserDTO::getId)
                .collect(Collectors.toList());

        List<Long> rolesId = employees
                .stream()
                .map(UserDTO::getRoleId)
                .collect(Collectors.toList());

        List<IEmployee> iEmployees = employeeRepository.findAllOperatorsByIdAndRoleId(employeesId, rolesId);

        return iEmployees
                .stream()
                .map(iEmployee -> new UserDTO(
                        UUID.fromString(iEmployee.getId()),
                        iEmployee.getFirstName(),
                        iEmployee.getLastName(),
                        employees
                                .stream()
                                .filter(userDTO -> Objects.equals(userDTO.getId().toString(), iEmployee.getId()))
                                .findAny()
                                .orElse(new UserDTO())
                                .getPhoneNumber()
                ))
                .collect(Collectors.toList());
    }

    private void makeEmployeePhoneNumber(Employee employee, List<PhoneNumberDTO> phoneNumberDTOList) {

        List<EmployeePhoneNumber> employeePhoneNumberList = new ArrayList<>();

        Set<UUID> idList = new HashSet<>();

        int countMain = 0;

        List<UUID> uuidList = phoneNumberDTOList
                .stream()
                .map(PhoneNumberDTO::getPhoneNumberTypeId)
                .collect(Collectors.toList());

        List<PhoneNumberType> phoneNumberTypeList = phoneNumberTypeService.getPhoneNumberTypeListFromDB(uuidList);

        for (PhoneNumberDTO phoneNumberDTO : phoneNumberDTOList) {

            checkPhoneNumberIsUseAlready(phoneNumberDTO, employee.getId());

            if (Objects.isNull(phoneNumberDTO.getId())) {
                employeePhoneNumberList.add(
                        new EmployeePhoneNumber(
                                employee,
                                phoneNumberDTO.getPhoneNumber(),
                                phoneNumberTypeList
                                        .stream()
                                        .filter(phoneNumberType -> Objects.equals(phoneNumberType.getId(), phoneNumberDTO.getPhoneNumberTypeId()))
                                        .findAny()
                                        .orElseThrow(() -> RestException.restThrow(ResponseMessage.ERROR_PHONE_NUMBER_TYPE_NOT_FOUND)),
//                                phoneNumberTypeService.getPhoneNumberTypeFromDB(phoneNumberDTO.getPhoneNumberTypeId()),
                                phoneNumberDTO.isMain())
                );
            } else {
                idList.add(phoneNumberDTO.getId());
            }
            if (phoneNumberDTO.isMain()) {
                countMain++;
            }
        }

        if (countMain != 1) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_MAIN_PHONE_NUMBER_SHOULD_BE_ONE);
        }

        if (!idList.isEmpty()) {
            List<EmployeePhoneNumber> repositoryAllById = employeePhoneNumberRepository.findAllById(idList);

            UUID mainPhoneId = null;

            for (EmployeePhoneNumber employeePhoneNumber : repositoryAllById) {

                for (PhoneNumberDTO numberDTO : phoneNumberDTOList) {

                    checkPhoneNumberIsUseAlready(numberDTO, employee.getId());

                    if (employeePhoneNumber.getId().equals(numberDTO.getId())) {
                        employeePhoneNumber.setPhoneNumber(numberDTO.getPhoneNumber());
                        employeePhoneNumber.setType(phoneNumberTypeService.getPhoneNumberTypeFromDB(numberDTO.getPhoneNumberTypeId()));
                        employeePhoneNumber.setMain(numberDTO.isMain());
                        if (numberDTO.isMain()) {
                            mainPhoneId = employeePhoneNumber.getId();
                        }

                        employeePhoneNumberList.add(employeePhoneNumber);
                    }
                }
            }
            if (Objects.nonNull(mainPhoneId)) {
                for (EmployeePhoneNumber employeePhoneNumber : repositoryAllById) {
                    if (!mainPhoneId.equals(employeePhoneNumber.getId())) {
                        employeePhoneNumber.setMain(false);
                    }
                }
            }
        }

        List<String> employeePhoneNumberListFromDB = employeePhoneNumberRepository.findAllIdByEmployeeId(employee.getId());

        if ((idList.size() != employeePhoneNumberListFromDB.size()) && !employeePhoneNumberListFromDB.isEmpty()) {

            List<UUID> deletedEmployeePhoneNumberIdList = new ArrayList<>();

            for (String uuid : employeePhoneNumberListFromDB) {

                if (idList.add(UUID.fromString(uuid))) {
                    deletedEmployeePhoneNumberIdList.add(UUID.fromString(uuid));
                }
            }
            employeePhoneNumberRepository.deleteAllById(deletedEmployeePhoneNumberIdList);

        }


        try {
            if (!employeePhoneNumberList.isEmpty()) {

                Optional<EmployeePhoneNumber> optionalPhoneNumber = employeePhoneNumberList
                        .stream()
                        .filter(EmployeePhoneNumber::isMain)
                        .findAny();
                if (optionalPhoneNumber.isPresent()) {
                    EmployeePhoneNumber employeePhoneNumber = optionalPhoneNumber.get();
                    employee.setPhoneNumber(CommonUtils.makePhoneNumber(employeePhoneNumber.getPhoneNumber()));
                }

                employeePhoneNumberRepository.saveAll(employeePhoneNumberList);
            }
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_PHONE_NUMBER);
        }

    }

    private void checkPhoneNumberIsUseAlready(PhoneNumberDTO phoneNumberDTO, UUID id) {
        boolean exists = employeePhoneNumberRepository.existsByPhoneNumberAndMainIsTrueAndEmployee_IdNot(phoneNumberDTO.getPhoneNumber(), id);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_THIS_PHONE_NUMBER_ALREADY_USE_OTHER_EMPLOYEE);
        }
    }

    /**
     * XODIM QO'SHILAYOTGANIDA TARIF SETKASI MAVJUDLIGI TEKSHIRIB KELINGAN
     *
     * @param employmentInfos {@link List<EmploymentInfoDTO>}
     */
    private void checkTariffGridIsExist(List<EmploymentInfoDTO> employmentInfos) {

        for (EmploymentInfoDTO employmentInfo : employmentInfos) {

            TariffGridDTO tariffGridDTO = TariffGridDTO.makeTariffGridDTO(employmentInfo);

            tariffGridService.existsTariffGrid(tariffGridDTO);
        }
    }

    /**
     * AUTH SERVICEGA USER YARATIB KELAMIZ
     *
     * @param employeeDTO
     * @return
     */
    private UUID saveUserAndGetUserIdFromAuthService(EmployeeDTO employeeDTO) {
        EmployeeInfoDTO employerInfo = employeeDTO.getEmployerInfo();

        UserFeignDTO userFeignDTO = new UserFeignDTO(
                CommonUtils.makePascalCase(employerInfo.getFirstName()),
                CommonUtils.makePascalCase(employerInfo.getLastName()),
                CommonUtils.makePascalCase(employerInfo.getMiddleName()),
                CommonUtils.makePhoneNumber(employeeDTO.getPhoneNumber()),
                employerInfo.getEmail(),
                employerInfo.getBirthDate(),
                employerInfo.getPhotoId(),
                !employeeDTO.getAccountInfo().isAccess() ? null : employeeDTO.getAccountInfo().getRoles(),
                employeeDTO.getAccountInfo().isAccess());

        return feignService.saveUserAndGetUserId(userFeignDTO);

    }

    //    @CacheEvict(value = "safeGetUserById", key = "employeeDTO.id")
    public void editUserFromAuthService(EmployeeDTO employeeDTO) {

        UserFeignDTO userFeignDTO = UserFeignDTO.makeUserFeignDTO(employeeDTO);
        log.info("userFeignDTO {} ", userFeignDTO);

        feignService.editUser(userFeignDTO);

    }

    private UserFeignDTO editStaffBugFixPathAuthService(EmployeeDTO employeeDTO) {

        UserFeignDTO userFeignDTO = UserFeignDTO.makeUserFeignDTO(employeeDTO);

        return feignService.editStaffBugFixPath(userFeignDTO);

    }


    /**
     * DTO DAN KELGAN MA'LUMOTLAR BO'YICHA HODIM YASAB OLYAPMIZ
     *
     * @param employee
     * @param employeeDTO
     * @return
     */
    private Employee makeEmployee(Employee employee, EmployeeDTO employeeDTO) {

        // DTO DAN KELGAN getEmployerInfo NULL BO'LMASA UNDAGI MA'LUMOTLARNI HODIMGA SET QILGANIMZ
        EmployeeInfoDTO employerInfo = employeeDTO.getEmployerInfo();
        if (Objects.nonNull(employerInfo)) {

            // TUG'ILGAN SANANI TEKSHIRAMIZ
//            checkDateIsNotFutureOrThrow(employerInfo.getBirthDate());

            employee.setFirstName(CommonUtils.makePascalCase(employerInfo.getFirstName()));
            employee.setLastName(CommonUtils.makePascalCase(employerInfo.getLastName()));
            employee.setMiddleName(CommonUtils.makePascalCase(employerInfo.getMiddleName()));
            employee.setPhotoId(employerInfo.getPhotoId());
            employee.setBirthDate(new Date(employerInfo.getBirthDate()));
            employee.setMaritalStatus(employerInfo.getMaritalStatus());
            employee.setGender(employerInfo.getGender());
            employee.setEmail(employerInfo.getEmail());

            //XODIM UCHUN IMTIYOZ TURLARI BO'LSA
            if (Objects.nonNull(employerInfo.getPrivilegeTypes()) && !employerInfo.getPrivilegeTypes().isEmpty()) {

                //IMTIYOZ TURLARINING ID LISTI ORQALI DB DAN PrivilegeType LISTNI OLISH
                List<PrivilegeType> privilegeTypeListFromDB = privilegeTypeService.getAllByIdList(employerInfo.getPrivilegeTypes());

                //XODIMGA IMTIYOZ PrivilegeType LISTINI BERYAPMIZ
                employee.setPrivilegeTypes(new HashSet<>(privilegeTypeListFromDB));
            }
        } else {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVING_EMPLOYEE_INFO);
        }

        if (Objects.nonNull(employeeDTO.getPassportInfo())) {

            // DTO DAN KELGAN getEmployeePassportInfo NULL BO'LMASA UNDAGI MA'LUMOTLARNI HODIMGA SET QILGANIMZ
            EmployeePassportInfoDTO employeePassportInfo = employeeDTO.getPassportInfo();
//        if (notNull(employeePassportInfo)) {

            Long passportGivenDate = employeePassportInfo.getPassportGivenDate();
            Long passportExpireDate = employeePassportInfo.getPassportExpireDate();

//            checkFirstDateParamIsGreatOrThrow(passportExpireDate, passportGivenDate);
//            checkDateIsNotFutureOrThrow(employeePassportInfo.getPassportGivenDate());
//            checkDateIsNotPastOrThrow(employeePassportInfo.getPassportExpireDate());

            employee.setPassportSerial(employeePassportInfo.getPassportSerial());
            employee.setPassportNumber(employeePassportInfo.getPassportNumber());
            employee.setPassportGivenOrganisation(employeePassportInfo.getPassportGivenOrganisation());
            if (Objects.nonNull(passportGivenDate)) {
                employee.setPassportGivenDate(new Date(passportGivenDate));
            }
            if (Objects.nonNull(passportExpireDate)) {
                employee.setPassportExpireDate(new Date(passportExpireDate));
            }
            employee.setPermanentAddress(employeePassportInfo.getPermanentAddress());
            employee.setCurrentAddress(employeePassportInfo.getCurrentAddress());
            employee.setPersonalNumber(employeePassportInfo.getPersonalNumber());

//        } else {
//            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVING_PASSPORT_INFO);
//        }

        }

        AccountInfoDTO accountInfo = employeeDTO.getAccountInfo();
        if (Objects.nonNull(accountInfo)) {

            if (accountInfo.isAccess()) {
                List<Long> roles = accountInfo.getRoles();

                long[] longs = roles
                        .stream()
                        .mapToLong(l -> l)
                        .toArray();

                Long[] longArray = new Long[longs.length];
                int i = 0;

                for (long temp : longs) {
                    longArray[i++] = temp;
                }

                employee.setAccess(true);
                employee.setRoles(longArray);

            } else {
                employee.setAccess(false);
                employee.setRoles(null);
            }

            employee.setAccess(accountInfo.isAccess());
        } else {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVING_ACCOUNT_INFO);
        }

        // TAYYOR BO'LGAN HODIMNI QAYTARGANMIZ
        return employee;

    }

    private void makeEmploymentInfo(Employee employee, List<EmploymentInfoDTO> employmentInfoDTOList) {

        List<EmploymentInfo> employmentInfoList = new ArrayList<>();

        List<EmployeeWorkDay> employeeWorkDayList = new ArrayList<>();
        Set<UUID> uuidHashSet = new HashSet<>();

        for (EmploymentInfoDTO employmentInfoDTO : employmentInfoDTOList) {

            if (Objects.isNull(employmentInfoDTO.getId())) {

                Position positionFromDB = positionService.getPositionFromDB(employmentInfoDTO.getPositionId(), true);

                EmploymentInfo employmentInfo = new EmploymentInfo(
                        employee,
                        employmentInfoDTO.getBranchId(),
                        employmentInfoDTO.getCompanyId(),
                        departmentService.getDepartmentFromDB(employmentInfoDTO.getDepartmentId(), true),
                        positionFromDB,
                        employeeCategoryService.getEmployeeCategoryFromDB(employmentInfoDTO.getEmployeeCategoryId(), true),
                        employeeCategoryService.getEmployeeCategoryFromDB(employmentInfoDTO.getEmployeeCategoryId(), true).getEmployeeCategoryType(),
                        employmentInfoDTO.getPaymentCriteriaType(), employmentInfoDTO.getContractForm(), employmentInfoDTO.getEmployeeMode(),
                        EmployerStatusEnum.WORKING,
                        new Date(employmentInfoDTO.getHireDate()),
                        Objects.isNull(positionFromDB.getManageTimesheet()) ? Boolean.FALSE : positionFromDB.getManageTimesheet()
                );
                employmentInfoList.add(employmentInfo);

                List<EmployeeWorkDay> createEmployeeWorkDayList = createEmployeeWorkDayList(employmentInfo, employmentInfoDTO.getEmployeeWorkDayList());
                employeeWorkDayList.addAll(createEmployeeWorkDayList);
            } else {
                uuidHashSet.add(employmentInfoDTO.getId());
            }
        }

        if (!uuidHashSet.isEmpty()) {
            List<EmploymentInfo> employmentInfos = employmentInfoRepository.findAllById(uuidHashSet);

            if (uuidHashSet.size() != employmentInfos.size()) {
                throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_EMPLOYMENT_INFO);
            }


            for (EmploymentInfo employmentInfo : employmentInfos) {

                for (EmploymentInfoDTO employmentInfoDTO : employmentInfoDTOList) {

                    if (employmentInfo.getId().equals(employmentInfoDTO.getId())) {
                        employmentInfo.setBranchId(employmentInfoDTO.getBranchId());
                        employmentInfo.setCompanyId(employmentInfoDTO.getCompanyId());
                        employmentInfo.setDepartment(departmentService.getDepartmentFromDB(employmentInfoDTO.getDepartmentId(), true));

                        Position positionFromDB = positionService.getPositionFromDB(employmentInfoDTO.getPositionId(), true);
                        employmentInfo.setPosition(positionFromDB);
                        employmentInfo.setEmployeeCategory(employeeCategoryService.getEmployeeCategoryFromDB(employmentInfoDTO.getEmployeeCategoryId(), true));
                        employmentInfo.setPaymentCriteriaType(employmentInfoDTO.getPaymentCriteriaType());
                        employmentInfo.setContractForm(employmentInfoDTO.getContractForm());
                        employmentInfo.setEmployeeMode(employmentInfoDTO.getEmployeeMode());
//                        employmentInfo.setEmployerStatus(EmployerStatusEnum.WORKING);
                        employmentInfo.setHireDate(new Date(employmentInfoDTO.getHireDate()));
                        employmentInfoList.add(employmentInfo);

                        employmentInfo.setManageTable(Objects.isNull(positionFromDB.getManageTimesheet()) ? Boolean.FALSE : positionFromDB.getManageTimesheet());

                        List<EmployeeWorkDay> createEmployeeWorkDayList = editEmployeeWorkDayList(employmentInfo, employmentInfoDTO.getEmployeeWorkDayList());
                        employeeWorkDayList.addAll(createEmployeeWorkDayList);
                    }
                }
            }
        }

        // FRONTENDDAN KELMAGANLARINI DELETE QILISH UCHUN
        List<String> allIdByEmployeeId = employmentInfoRepository.findAllIdByEmployeeId(employee.getId());

        // FRONTENDDAN KELMAGANLARINI DELETE QILISH UCHUN
        if ((uuidHashSet.size() != allIdByEmployeeId.size()) && !allIdByEmployeeId.isEmpty()) {

            List<UUID> uuidList = allIdByEmployeeId
                    .stream()
                    .filter(s -> {
                        // AGAR SET GA QO'SHILSA U FRONTDAN KELMAGAN VA UNI O'CHIRSA BO'LADI
                        return uuidHashSet.add(UUID.fromString(s));
                    })
                    .map(UUID::fromString)
                    .collect(Collectors.toList());

            if (!uuidList.isEmpty()) {
                timeSheetService.deleteTimeSheetEmployeeByEmploymentInfo(uuidList);
                employmentInfoRepository.deleteAllById(uuidList);
            }

        }


        if (!employmentInfoList.isEmpty()) {

            List<EmploymentInfo> newEmploymentInfoList = employmentInfoList
                    .stream()
                    .filter(employmentInfo -> Objects.isNull(employmentInfo.getId()))
                    .collect(Collectors.toList());

            List<EmploymentInfo> oldEmploymentInfoList = employmentInfoList
                    .stream()
                    .filter(employmentInfo -> Objects.nonNull(employmentInfo.getId()))
                    .collect(Collectors.toList());
            try {
                employmentInfoRepository.saveAll(employmentInfoList);
            } catch (Exception e) {
                throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_EMPLOYMENT_INFO);
            }
            if (!employeeWorkDayList.isEmpty()) {
                employeeWorkDayRepository.saveAll(employeeWorkDayList);
            }

            addNewEmploymentInfoCurrentTimeSheet(newEmploymentInfoList);

            updateEmployeeAttendance(oldEmploymentInfoList);

        }


    }

    private void makeEmployeeAttachment(Employee employee, List<AttachmentDTO> attachmentDTOList) {
        List<EmployeeAttachment> employeeAttachmentList = new ArrayList<>();

        Set<UUID> attachmentIdList = new HashSet<>();

        // FRONTDAN KELGAN DTOLARNI AYLANYAPMIZ
        for (AttachmentDTO attachmentDTO : attachmentDTOList) {

            if (Objects.isNull(attachmentDTO)) {
                continue;
            }

            // AGAR ID NULL BO'LSA BU ATTACHMENT QO'SHILMAGAN BO'LADI
            // FRONT HAR DOIM YANGI QO'SHILGANIDA ID NI NULL QILIB. EDIT HOLATIDA ID BILAN YUBORADI
            if (Objects.isNull(attachmentDTO.getId())) {

                // YANGI ATTACHMENT YARATIB EMPLOYEEGA BIRIKTIRIB QO'YDIK
                employeeAttachmentList.add(
                        new EmployeeAttachment(
                                employee,
                                attachmentDTO.getFileId(),
                                attachmentDTO.getDescription()
                        )
                );
            } else {

                // AGAR ID NULL KELMASA DEMAK MAVJUD ATTACHMENTNI TAHRIRLAYOTGAN BO'LADI
                // VA BU ATTACHMENT ID LARINI BIR JOYGA YIG'IB OLAMIZ
                attachmentIdList.add(attachmentDTO.getId());
            }

        }

        // AGAR attachmentIdList SIZE 0 DAN KATTA BO'LMASA EDIT QILINADIGAN OBJECT YO'Q DEGANI
        // VA IFNI ICHIGA KIRMAYDI. FAQAT KELGAN OBJECTLAR SAQLANADI
        if (!attachmentIdList.isEmpty()) {
            // YIG'IB OLINGAN ID LAR BO'YICHA BAZADAN MAVJUD ATTACHMENTLARNI OLAMIZ
            List<EmployeeAttachment> attachmentRepositoryAllById = employeeAttachmentRepository.findAllById(attachmentIdList);

            // FRONTDAN EDIT UCHUN KELGAN ATTACHMENTLAR ID SINING SIZE
            // DBDAN OLINGAN ATTACHMENTLAR SIZEGA TENG BO'LMASA XATOLIK BO'LADI
            if (attachmentIdList.size() != attachmentRepositoryAllById.size()) {
                throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_ATTACHMENT);
            }

            for (EmployeeAttachment employeeAttachment : attachmentRepositoryAllById) {

                for (AttachmentDTO attachmentDTO : attachmentDTOList) {

                    // FRONTDAN KELGAN MA'LUMOTNI AYNAN SHU ID LIK ATTACHMENTGA SET QILDIK
                    // VA UNI TEPADAGI employeeAttachmentList GA QO'SHIB QO'YDIK.
                    if (employeeAttachment.getId().equals(attachmentDTO.getId())) {

                        employeeAttachment.setFileId(attachmentDTO.getFileId());
                        employeeAttachment.setDescription(attachmentDTO.getDescription());

                        employeeAttachmentList.add(employeeAttachment);
                    }
                }
            }
        }

        List<String> allIdByEmployeeId = employeeAttachmentRepository.findAllIdByEmployeeId(employee.getId());

        if ((attachmentIdList.size() != allIdByEmployeeId.size() && !allIdByEmployeeId.isEmpty())) {

            List<UUID> deletedEmployeeAttachmentList = new ArrayList<>();

            for (String uuid : allIdByEmployeeId) {

                if (attachmentIdList.add(UUID.fromString(uuid))) {
                    deletedEmployeeAttachmentList.add(UUID.fromString(uuid));
                }

            }

            employeeAttachmentRepository.deleteAllById(deletedEmployeeAttachmentList);
        }

        try {
            // employeeAttachmentList SIZE 0 GA TENG BO'LSA HODIMGA ATTACHMENT QO'SHILMADI DEGANI
            // AGAR 0 DAN KATTA BO'LSA ATTACHMENT QO'SHILDI YOKI BOR ATTACHMENT TAHRIRLANDI DEGANI
            if (!employeeAttachmentList.isEmpty()) {
                employeeAttachmentRepository.saveAll(employeeAttachmentList);
            }
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_ATTACHMENT);
        }

    }

    private void makeEmployeeExperienceHistory(Employee employee, List<EmployeeExperienceInfoDTO> employeeExperienceInfoDTOList) {

        List<EmployeeExperienceHistory> employeeExperienceHistoryList = new ArrayList<>();
        Set<UUID> idList = new HashSet<>();

        for (EmployeeExperienceInfoDTO employeeExperienceInfoDTO : employeeExperienceInfoDTOList) {

            if (Objects.isNull(employeeExperienceInfoDTO)) {
                continue;
            }
            if (Objects.isNull(employeeExperienceInfoDTO.getId())) {

                Long startedWorkDate = employeeExperienceInfoDTO.getStartedWorkDate();
                Long finishedWorkDate = employeeExperienceInfoDTO.getFinishedWorkDate();

//                checkFirstDateParamIsGreatOrThrow(finishedWorkDate, startedWorkDate);


                EmployeeExperienceHistory employeeExperienceHistory = new EmployeeExperienceHistory(
                        employee,
                        employeeExperienceInfoDTO.getOrganisationName(),
                        employeeExperienceInfoDTO.getPosition(),
                        new Date(startedWorkDate),
                        new Date(finishedWorkDate),
                        employeeExperienceInfoDTO.isNotFinished()
                );

                employeeExperienceHistoryList.add(employeeExperienceHistory);
            } else {
                idList.add(employeeExperienceInfoDTO.getId());
            }

        }

        if (!idList.isEmpty()) {
            List<EmployeeExperienceHistory> employeeExperienceHistories = employeeExperienceHistoryRepository.findAllById(idList);

            if (idList.size() != employeeExperienceHistories.size()) {
                throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_HISTORY_EXPERIENCE);
            }

            for (EmployeeExperienceHistory employeeExperienceHistory : employeeExperienceHistories) {

                for (EmployeeExperienceInfoDTO employeeExperienceInfoDTO : employeeExperienceInfoDTOList) {

                    if (employeeExperienceHistory.getId().equals(employeeExperienceInfoDTO.getId())) {

                        employeeExperienceHistory.setOrganisationName(employeeExperienceHistory.getOrganisationName());
                        employeeExperienceHistory.setPosition(employeeExperienceHistory.getPosition());
                        employeeExperienceHistory.setStartedWorkDate(employeeExperienceHistory.getStartedWorkDate());
                        employeeExperienceHistory.setFinishedWorkDate(employeeExperienceHistory.getFinishedWorkDate());
                        employeeExperienceHistory.setNotFinished(employeeExperienceHistory.isNotFinished());

                        employeeExperienceHistoryList.add(employeeExperienceHistory);
                    }

                }

            }
        }

        List<String> allIdList = employeeExperienceHistoryRepository.findAllIdByEmployeeId(employee.getId());

        if ((idList.size() != allIdList.size()) && !allIdList.isEmpty()) {
            List<UUID> deletedEmployeeExperienceHistoryIdList = new ArrayList<>();

            for (String uuid : allIdList) {
                if (idList.add(UUID.fromString(uuid))) {
                    deletedEmployeeExperienceHistoryIdList.add(UUID.fromString(uuid));
                }
            }

            employeeExperienceHistoryRepository.deleteAllById(deletedEmployeeExperienceHistoryIdList);
        }

        try {

            if (!employeeExperienceHistoryList.isEmpty()) {
                employeeExperienceHistoryRepository.saveAll(employeeExperienceHistoryList);
            }

        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_HISTORY_EXPERIENCE);
        }

    }

    private void makeEmployeeSkill(Employee employee, EmployeeSkillInfoDTO employeeSkillInfoDTO) {

        if (Objects.isNull(employeeSkillInfoDTO)) {
            throw RestException.restThrow(ResponseMessage.ERROR_SKILL_NOT_NULL);
        }

        employee.setSkills(null);

        if (!employeeSkillInfoDTO.getSkillsIdList().isEmpty()) {
            employee.setSkills(skillService.getSkillListFromDB(employeeSkillInfoDTO.getSkillsIdList()));
        }
        try {
            employeeRepository.save(employee);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_SKILL_INFO);
        }
    }

    private void makeEmployeeEducationHistory(Employee employee, List<EmployeeEducationInfoDTO> employeeEducationInfoDTOList) {

        List<EmployeeEducationHistory> employeeEducationHistoryList = new ArrayList<>();

        Set<UUID> idList = new HashSet<>();

        for (EmployeeEducationInfoDTO employeeEducationInfoDTO : employeeEducationInfoDTOList) {

            if (Objects.isNull(employeeEducationInfoDTO)) {
                continue;
            }
            if (Objects.isNull(employeeEducationInfoDTO.getId())) {

                Long startedStudyDate = employeeEducationInfoDTO.getStartedStudyDate();
                Long finishedStudyDate = employeeEducationInfoDTO.getFinishedStudyDate();

//                checkFirstDateParamIsGreatOrThrow(finishedStudyDate, startedStudyDate);


                EmployeeEducationHistory employeeEducationHistory = new EmployeeEducationHistory(employee,
                        employeeEducationInfoDTO.getStudyDegree(),
                        employeeEducationInfoDTO.getOrganisationName(),
                        employeeEducationInfoDTO.getStudyType(),
                        new Date(startedStudyDate),
                        Objects.isNull(finishedStudyDate) ? null : new Date(finishedStudyDate),
                        employeeEducationInfoDTO.isNotFinished()
                );

                employeeEducationHistoryList.add(employeeEducationHistory);

            } else {
                idList.add(employeeEducationInfoDTO.getId());
            }

        }

        if (!idList.isEmpty()) {
            List<EmployeeEducationHistory> educationHistoryList = employeeEducationHistoryRepository.findAllById(idList);

            if (Objects.equals(idList.size(), educationHistoryList.size())) {
                throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_HISTORY_EDUCATION);
            }

            for (EmployeeEducationHistory employeeEducationHistory : educationHistoryList) {

                for (EmployeeEducationInfoDTO employeeEducationInfoDTO : employeeEducationInfoDTOList) {

                    if (employeeEducationHistory.getId().equals(employeeEducationInfoDTO.getId())) {

                        employeeEducationHistory.setStartedStudyDate(new Date(employeeEducationInfoDTO.getStartedStudyDate()));
                        employeeEducationHistory.setFinishedStudyDate(new Date(employeeEducationInfoDTO.getFinishedStudyDate()));
                        employeeEducationHistory.setNotFinished(employeeEducationInfoDTO.isNotFinished());
                        employeeEducationHistory.setStudyDegree(employeeEducationInfoDTO.getStudyDegree());
                        employeeEducationHistory.setStudyType(employeeEducationInfoDTO.getStudyType());
                        employeeEducationHistory.setOrganisationName(employeeEducationInfoDTO.getOrganisationName());
                    }
                    employeeEducationHistoryList.add(employeeEducationHistory);
                }
            }
        }

        List<String> allIdByEmployeeId = employeeEducationHistoryRepository.findAllIdByEmployeeId(employee.getId());

        if ((Objects.equals(idList.size(), allIdByEmployeeId.size()) && !allIdByEmployeeId.isEmpty())) {

            List<UUID> deletedEmployeeEducationHistoryIdList = new ArrayList<>();

            for (String uuid : allIdByEmployeeId) {
                if (idList.add(UUID.fromString(uuid))) {
                    deletedEmployeeEducationHistoryIdList.add(UUID.fromString(uuid));
                }
            }

            employeeEducationHistoryRepository.deleteAllById(deletedEmployeeEducationHistoryIdList);
        }

        try {
            if (!employeeEducationHistoryList.isEmpty()) {
                employeeEducationHistoryRepository.saveAll(employeeEducationHistoryList);
            }
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_HISTORY_EDUCATION);

        }

    }

    private Employee getEmployeeFromDB(UUID id) {
        if (Objects.isNull(id)) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_NOT_FOUND);
        }
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);

        if (optionalEmployee.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_NOT_FOUND);
        }
        return optionalEmployee.get();
    }

    private EmploymentInfo getEmploymentInfoFromDB(UUID id) {
        if (Objects.isNull(id)) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYMENT_INFO_NOT_FOUND);
        }
        Optional<EmploymentInfo> optionalEmploymentInfo = employmentInfoRepository.findById(id);

        if (optionalEmploymentInfo.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYMENT_INFO_NOT_FOUND);
        }
        return optionalEmploymentInfo.get();
    }

    private List<EmployeeDTO> getEmployeeDTOList(List<Employee> employeeList) {
        return employeeList
                .stream()
                .map(this::getEmployeeDTO)
                .collect(Collectors.toList());
    }

    private EmployeeDTO getEmployeeDTO(Employee employee) {

        EmployeeInfoDTO employerInfo = getEmployerInfo(employee);
        EmployeePassportInfoDTO employeePassportInfo = getEmployeePassportInfo(employee);
        List<EmploymentInfoDTO> employmentInfo = getEmploymentInfo(employee);
        List<EmployeeEducationInfoDTO> employeeEducationInfo = getEmployeeEducationInfo(employee);
        List<EmployeeExperienceInfoDTO> employeeExperienceInfo = getEmployeeExperienceInfo(employee);
        EmployeeSkillInfoDTO employeeSkillInfo = getEmployeeSkillInfo(employee);
        List<EmployeeAttachment> attachmentDtoLisByEmployee = getAttachmentDTOLisByEmployee(employee);

        return new EmployeeDTO(
                employee.getId(),
                employerInfo,
                null,
                employeePassportInfo,
                employmentInfo,
                employeeEducationInfo,
                employeeExperienceInfo,
                getAttachmentDTO(attachmentDtoLisByEmployee),
                employeeSkillInfo
        );

    }

    private EmployeeSkillInfoDTO getEmployeeSkillInfo(Employee employee) {

        return new EmployeeSkillInfoDTO(skillService.getSkillDTOList(employee.getSkills()));

    }

    private List<SkillDTO> getSkillDTOList(Collection<Skill> skillList) {
        if (Objects.isNull(skillList) || skillList.isEmpty()) {
            return new ArrayList<>();
        }
        return skillList
                .stream()
                .map(this::getSkillDTO)
                .collect(Collectors.toList());
    }

    private SkillDTO getSkillDTO(Skill skill) {
        return new SkillDTO(skill.getId(), skill.getName(), skill.getColor());
    }

    private List<EmployeeExperienceInfoDTO> getEmployeeExperienceInfo(Employee employee) {
        List<EmployeeExperienceInfoDTO> employeeExperienceInfoDTOList = new ArrayList<>();

        List<EmployeeExperienceHistory> employeeExperienceHistoryList = employeeExperienceHistoryRepository.findAllByEmployee(employee);

        for (EmployeeExperienceHistory employeeExperienceHistory : employeeExperienceHistoryList) {

            EmployeeExperienceInfoDTO employeeExperienceInfoDTO = new EmployeeExperienceInfoDTO(employeeExperienceHistory.getId(), employeeExperienceHistory.getOrganisationName(), employeeExperienceHistory.getPosition(), employeeExperienceHistory.getStartedWorkDate().getTime(), employeeExperienceHistory.getFinishedWorkDate().getTime(), employeeExperienceHistory.isNotFinished());

            employeeExperienceInfoDTOList.add(employeeExperienceInfoDTO);
        }

        return employeeExperienceInfoDTOList;
    }


    private List<EmployeeEducationInfoDTO> getEmployeeEducationInfo(Employee employee) {

        List<EmployeeEducationHistory> employeeEducationHistoryList = employeeEducationHistoryRepository.findAllByEmployee(employee);
        List<EmployeeEducationInfoDTO> employeeEducationInfoDTOList = new ArrayList<>();

        for (EmployeeEducationHistory employeeEducationHistory : employeeEducationHistoryList) {

            EmployeeEducationInfoDTO employeeEducationInfoDTO = new EmployeeEducationInfoDTO(employeeEducationHistory.getId(), employeeEducationHistory.getStudyDegree(), employeeEducationHistory.getOrganisationName(), employeeEducationHistory.getStudyType(), employeeEducationHistory.getStartedStudyDate().getTime(), employeeEducationHistory.getFinishedStudyDate().getTime(), employeeEducationHistory.isNotFinished());

            employeeEducationInfoDTOList.add(employeeEducationInfoDTO);
        }

        return employeeEducationInfoDTOList;
    }

    private List<EmployeeEducationFormInfoDTO> getEmployeeEducationFormInfoDTO(Employee employee, AdditionalDTO additionalDTO) {

        List<EmployeeEducationHistory> employeeEducationHistoryList = employeeEducationHistoryRepository.findAllByEmployee(employee);
        List<EmployeeEducationFormInfoDTO> employeeEducationFormInfoDTOList = new ArrayList<>();

        for (EmployeeEducationHistory employeeEducationHistory : employeeEducationHistoryList) {

            EmployeeEducationFormInfoDTO employeeEducationFormInfoDTO = new EmployeeEducationFormInfoDTO(employeeEducationHistory.getId(),
                    OptionDTO.makeOptionDTO(
                            additionalDTO.getStudyDegreeList(),
                            Collections.singletonList(employeeEducationHistory.getStudyDegree())),
                    employeeEducationHistory.getOrganisationName(),
                    employeeEducationHistory.getStudyType(),
                    employeeEducationHistory.getStartedStudyDate().getTime(),
                    employeeEducationHistory.getFinishedStudyDate().getTime(),
                    employeeEducationHistory.isNotFinished());

            employeeEducationFormInfoDTOList.add(employeeEducationFormInfoDTO);
        }

        return employeeEducationFormInfoDTOList;
    }

    private List<EmploymentInfoDTO> getEmploymentInfo(Employee employee) {

        List<EmploymentInfo> employmentInfoList = employmentInfoRepository.findAllByEmployee_Id(employee.getId());
        List<EmploymentInfoDTO> employmentInfoDTOList = new ArrayList<>();

        for (EmploymentInfo employmentInfo : employmentInfoList) {

            EmploymentInfoDTO employmentInfoDTO = new EmploymentInfoDTO(employmentInfo.getId(), employmentInfo.getBranchId(), employmentInfo.getCompanyId(), employmentInfo.getDepartment().getId(), employmentInfo.getDepartment().getName(), employmentInfo.getPosition().getId(), employmentInfo.getPosition().getName(), employmentInfo.getEmployeeCategory().getId(), employmentInfo.getEmployeeCategory().getEmployeeCategoryType().getName(), employmentInfo.getPaymentCriteriaType(), employmentInfo.getContractForm(), employmentInfo.getEmployeeMode(), employmentInfo.getHireDate().getTime(), employmentInfo.getManageTable(), null);

            employmentInfoDTOList.add(employmentInfoDTO);
        }

        return employmentInfoDTOList;
    }

    private List<EmploymentInfo> getEmploymentInfoByEmployee(Employee employee) {

        return employmentInfoRepository.findAllByEmployee_Id(employee.getId());

    }

    private List<EmploymentFormInfoDTO> getEmploymentFormInfoDTO(Employee employee, AdditionalDTO additionalDTO) {

        List<EmploymentInfo> employmentInfoList = employmentInfoRepository.findAllByEmployee_Id(employee.getId());
        List<EmploymentFormInfoDTO> employmentFormInfoDTOList = new ArrayList<>();

        for (EmploymentInfo employmentInfo : employmentInfoList) {

            List<EmployeeWorkDay> employeeWorkDayList = employeeWorkDayRepository.findAllByEmploymentInfo(employmentInfo);

            EmploymentFormInfoDTO employmentFormInfoDTO =
                    new EmploymentFormInfoDTO(
                            employmentInfo.getId(),
                            OptionDTO.makeOptionDTO(additionalDTO.getBranchList(), Collections.singletonList(employmentInfo.getBranchId())),
                            OptionDTO.makeOptionDTO(additionalDTO.getCompanyList(), Collections.singleton(employmentInfo.getCompanyId())),
                            OptionDTO.makeOptionDTO(additionalDTO.getDepartmentList(), Collections.singletonList(employmentInfo.getDepartment().getId())),
                            OptionDTO.makeOptionDTO(additionalDTO.getPositionList(), Collections.singletonList(employmentInfo.getPosition().getId())),
                            OptionDTO.makeOptionDTO(additionalDTO.getEmployeeCategoryList(), Collections.singletonList(employmentInfo.getEmployeeCategory().getId())),
                            OptionDTO.makeOptionDTO(additionalDTO.getPaymentCriteriaTypeList(), Collections.singletonList(employmentInfo.getPaymentCriteriaType())),
                            OptionDTO.makeOptionDTO(additionalDTO.getContractFormList(), Collections.singletonList(employmentInfo.getContractForm())),
                            OptionDTO.makeOptionDTO(additionalDTO.getEmployeeModeList(), Collections.singletonList(employmentInfo.getEmployeeMode())),
                            employmentInfo.getHireDate().getTime(), null,
                            getEmployeeWorkDayDTOList(employeeWorkDayList),
                            employmentInfo.getResignation(),
                            Objects.isNull(employmentInfo.getResignationDate()) ? null : employmentInfo.getResignationDate().getTime(),
                            employmentInfo.getResignationDescription()
                    );

            employmentFormInfoDTOList.add(employmentFormInfoDTO);
        }

        return employmentFormInfoDTOList;
    }

    private EmployeePassportInfoDTO getEmployeePassportInfo(Employee employee) {

        return new EmployeePassportInfoDTO(
                employee.getPassportSerial(),
                employee.getPassportNumber(),
                employee.getPassportGivenOrganisation(),
                Objects.isNull(employee.getPassportGivenDate()) ? null : employee.getPassportGivenDate().getTime(),
                Objects.isNull(employee.getPassportExpireDate()) ? null : employee.getPassportExpireDate().getTime(),
                employee.getPermanentAddress(),
                employee.getCurrentAddress(),
                employee.getPersonalNumber());

    }

    private List<EmployeeAttachment> getAttachmentDTOLisByEmployee(Employee employee) {
        return employeeAttachmentRepository.findAllByEmployee(employee);
    }

    private List<AttachmentFeignDTO> getAttachmentDTO(Employee employee) {
        List<EmployeeAttachment> employeeAttachmentList = employeeAttachmentRepository.findAllByEmployee(employee);
        List<String> employeeAttachmentIdList = employeeAttachmentList.stream().map(EmployeeAttachment::getFileId).collect(Collectors.toList());

        List<AttachmentFeignDTO> attachmentInfoList = feignService.getAttachmentList(employeeAttachmentIdList);

        for (AttachmentFeignDTO attachmentFeignDTO : attachmentInfoList) {

            List<EmployeeAttachment> employeeAttachments = employeeAttachmentList
                    .stream()
                    .filter(item -> item.getFileId().equals(attachmentFeignDTO.getId()))
                    .collect(Collectors.toList());

            if (!employeeAttachments.isEmpty()) {
                EmployeeAttachment employeeAttachment = employeeAttachments.get(0);

                attachmentFeignDTO.setDescription(employeeAttachment.getDescription());
                attachmentFeignDTO.setFileId(employeeAttachment.getFileId());
                attachmentFeignDTO.setId(employeeAttachment.getId().toString());
                attachmentFeignDTO.setAction(employeeAttachment.getAction());
            }

        }

        return attachmentInfoList;
    }

    private AttachmentDTO getAttachmentDTO(EmployeeAttachment employeeAttachment) {
        return new AttachmentDTO(
                employeeAttachment.getId(),
                employeeAttachment.getFileId(),
                employeeAttachment.getDescription(),
                employeeAttachment.getAction()
        );
    }

    private List<AttachmentDTO> getAttachmentDTO(List<EmployeeAttachment> employeeAttachmentList) {
        return employeeAttachmentList.stream().map(this::getAttachmentDTO).collect(Collectors.toList());
    }

    private EmployeeInfoDTO getEmployerInfo(Employee employee) {

        return new EmployeeInfoDTO(
                employee.getFirstName(),
                employee.getLastName(),
                employee.getMiddleName(),
                employee.getPhotoId(),
                employee.getBirthDate().getTime(),
                employee.getMaritalStatus(),
                employee.getGender(),
                employee.getEmail(),
                getPhoneNumberDTOList(getPhoneNumberDTOListByEmployee(employee)),
                PrivilegeTypeDTO.mapPrivilegeTypeToPrivilegeTypeDTO(employee.getPrivilegeTypes())
        );

    }

    private EmployeeFormInfoDTO getEmployerFormInfoDTO(Employee employee, AdditionalDTO additionalDTO) {

        Set<PrivilegeType> privilegeTypes = employee.getPrivilegeTypes();
        List<UUID> getPrivilegeTypeIdList = getPrivilegeTypeIdList(privilegeTypes);

        List<EmployeePhoneNumber> employeePhoneNumberList = employeePhoneNumberRepository.findAllByEmployee(employee);

        List<AttachmentFeignDTO> feignServiceAttachmentList = feignService.getAttachmentList(Collections.singletonList(employee.getPhotoId()));
        AttachmentFeignDTO attachmentFeignDTO = new AttachmentFeignDTO();

        if (!feignServiceAttachmentList.isEmpty()) {
            attachmentFeignDTO = feignServiceAttachmentList.get(0);
        }

        return new EmployeeFormInfoDTO(employee.getFirstName(),
                employee.getLastName(),
                employee.getMiddleName(),
                attachmentFeignDTO,
                employee.getBirthDate().getTime(),
                OptionDTO.makeOptionDTO(
                        additionalDTO.getMaritalStatusList(),
                        Collections.singletonList(employee.getMaritalStatus())
                ),
                OptionDTO.makeOptionDTO(
                        additionalDTO.getGenderList(),
                        Collections.singletonList(employee.getGender())
                ),
                employee.getEmail(),
                getPhoneNumberFormDTOList(
                        employeePhoneNumberList,
                        additionalDTO),
                OptionDTO.makeOptionDTO(
                        additionalDTO.getPrivilegeTypeList(),
                        getPrivilegeTypeIdList
                )
        );

    }

    private List<UUID> getPrivilegeTypeIdList(Collection<PrivilegeType> privilegeTypeList) {
        if (Objects.isNull(privilegeTypeList)) {
            return new ArrayList<>();
        }
        return privilegeTypeList
                .stream()
                .map(this::getPrivilegeTypeId)
                .collect(Collectors.toList());
    }

    private UUID getPrivilegeTypeId(PrivilegeType privilegeType) {
        return privilegeType.getId();
    }

    private List<EmployeePhoneNumber> getPhoneNumberDTOListByEmployee(Employee employee) {
        return employeePhoneNumberRepository.findAllByEmployee(employee);
    }

    private PhoneNumberFormDTO getPhoneNumberFormDTOList(List<EmployeePhoneNumber> employeePhoneNumberList, AdditionalDTO additionalDTO) {

        List<PhoneNumberDTO> phoneNumberDTOList =
                employeePhoneNumberList
                        .stream()
                        .map(employeePhoneNumber ->
                                new PhoneNumberDTO(
                                        employeePhoneNumber.getId(),
                                        CommonUtils.makePhoneNumber(employeePhoneNumber.getPhoneNumber()),
                                        employeePhoneNumber.getType().getId(),
                                        employeePhoneNumber.isMain()
                                ))
                        .collect(Collectors.toList());

        return new PhoneNumberFormDTO(
                additionalDTO.getPhoneNumberTypeDTOListFromDB(),
                new OptionActionDTO(RestConstants.PHONE_NUMBER_TYPE_CRUD),
                phoneNumberDTOList
        );

    }

    private List<PhoneNumberDTO> getPhoneNumberDTOList(List<EmployeePhoneNumber> employeePhoneNumberList) {
        return employeePhoneNumberList
                .stream()
                .map(this::getPhoneNumberDTO)
                .collect(Collectors.toList());
    }


    private PhoneNumberDTO getPhoneNumberDTO(EmployeePhoneNumber employeePhoneNumber) {
        return new PhoneNumberDTO(
                employeePhoneNumber.getId(),
                CommonUtils.makePhoneNumber(employeePhoneNumber.getPhoneNumber()),
                Objects.nonNull(employeePhoneNumber.getType()) ? employeePhoneNumber.getType().getId() : null,
                Objects.nonNull(employeePhoneNumber.getType()) ? employeePhoneNumber.getType().getName() : null,
                employeePhoneNumber.isMain());
    }


    /**
     * ASOSIY TELEFON RAQAMNI OLISH UCHUN
     *
     * @param phoneNumberDTOList
     * @return
     */
    private String getMainPhoneNumber(List<PhoneNumberDTO> phoneNumberDTOList) {
        return null;

    }

    private List<EmployeeWorkDayInfoDTO> getEmployeeWorkDayDTOList(List<EmployeeWorkDay> employeeWorkDayList) {

        List<EmployeeWorkDayInfoDTO> employeeWorkDayInfoDTOList = new ArrayList<>();
        for (EmployeeWorkDay employeeWorkDay : employeeWorkDayList) {
            employeeWorkDayInfoDTOList.add(getEmployeeWorkDayDTO(employeeWorkDay));
        }

        return employeeWorkDayInfoDTOList;

    }

    private List<EmployeeWorkDayInfoDTO> getEmployeeWorkDayInfoDTOList() {

        List<EmployeeWorkDayInfoDTO> employeeWorkDayInfoDTOList = new ArrayList<>();

        for (WeekDayEnum weekDay : RestConstants.WEEK_DAYS) {
            employeeWorkDayInfoDTOList.add(getEmployeeWorkDayInfoDTO(weekDay));
        }

        return employeeWorkDayInfoDTOList;

    }

    private EmployeeWorkDayInfoDTO getEmployeeWorkDayInfoDTO(WeekDayEnum weekDayEnum) {

        if (Objects.equals(weekDayEnum, WeekDayEnum.SATURDAY) || Objects.equals(weekDayEnum, WeekDayEnum.SUNDAY)) {
            return new EmployeeWorkDayInfoDTO(
                    weekDayEnum,
                    weekDayEnum.name(),
                    false,
                    getTimeDTO(),
                    getTimeDTO(),
                    false,
                    getTimeDTO(),
                    getTimeDTO()
            );
        } else {
            return new EmployeeWorkDayInfoDTO(
                    weekDayEnum,
                    weekDayEnum.name(),
                    true,
                    getTimeDTO("09", "00"),
                    getTimeDTO("18", "00"),
                    true,
                    getTimeDTO("12", "00"),
                    getTimeDTO("13", "00")
            );
        }
    }

    private TimeDTO getTimeDTO(String hour, String minute) {
        return new TimeDTO(
                OptionDTO.makeOptionDTO(Arrays.asList(RestConstants.hours), List.of(hour)),
                OptionDTO.makeOptionDTO(Arrays.asList(RestConstants.minutes), List.of(minute))
        );
    }

    private TimeDTO getTimeDTO() {
        return new TimeDTO(
                OptionDTO.makeOptionDTO(Arrays.asList(RestConstants.hours)),
                OptionDTO.makeOptionDTO(Arrays.asList(RestConstants.minutes))
        );
    }

    private EmployeeWorkDayInfoDTO getEmployeeWorkDayDTO(EmployeeWorkDay employeeWorkDay) {
        TimeDTO startWork;
        TimeDTO endWork;
        TimeDTO startLunch;
        TimeDTO endLunch;

        if (employeeWorkDay.isWorking()) {

            TimeDTO startTime = breakTimeStr(employeeWorkDay.getStartTime());
            TimeDTO endTime = breakTimeStr(employeeWorkDay.getEndTime());
            startWork = getTimeDTO(startTime);
            endWork = getTimeDTO(endTime);

            if (employeeWorkDay.isLunch()) {

                TimeDTO lunchStartTime = breakTimeStr(employeeWorkDay.getLunchStartTime());
                TimeDTO lunchEndTime = breakTimeStr(employeeWorkDay.getLunchEndTime());
                startLunch = getTimeDTO(lunchStartTime);
                endLunch = getTimeDTO(lunchEndTime);
            } else {
                startLunch = getEmptyTimeDTO();
                endLunch = getEmptyTimeDTO();
            }
        } else {
            startWork = getEmptyTimeDTO();
            endWork = getEmptyTimeDTO();

            startLunch = getEmptyTimeDTO();
            endLunch = getEmptyTimeDTO();
        }

        return new EmployeeWorkDayInfoDTO(
                employeeWorkDay.getId(),
                employeeWorkDay.getWeekDay(),
                employeeWorkDay.getWeekDay().name(),
                employeeWorkDay.isWorking(),
                startWork,
                endWork,
                employeeWorkDay.isLunch(),
                startLunch,
                endLunch);

    }

    private TimeDTO getTimeDTO(TimeDTO timeDTO) {
        return new TimeDTO(
                OptionDTO.makeOptionDTO(
                        Arrays.asList(RestConstants.hours),
                        Collections.singletonList(timeDTO.getHour())
                ),
                OptionDTO.makeOptionDTO(
                        Arrays.asList(RestConstants.minutes),
                        Collections.singletonList(timeDTO.getMinute())
                ));
    }

    private TimeDTO getEmptyTimeDTO() {
        return new TimeDTO(
                OptionDTO.makeOptionDTO(Arrays.asList(RestConstants.hours)),
                OptionDTO.makeOptionDTO(Arrays.asList(RestConstants.minutes)));
    }

    private List<EmployeeWorkDay> editEmployeeWorkDayList(EmploymentInfo employmentInfo, List<EmployeeWorkDayDTO> employeeWorkDayDTOList) {

        List<UUID> uuidList = employeeWorkDayDTOList
                .stream()
                .map(EmployeeWorkDayDTO::getId)
                .collect(Collectors.toList());

        List<EmployeeWorkDay> employeeWorkDayList = employeeWorkDayRepository.findAllById(uuidList);

        int countWeekDays = 0;


        for (EmployeeWorkDayDTO employeeWorkDayDTO : employeeWorkDayDTOList) {

            EmployeeWorkDay workDay = employeeWorkDayList
                    .stream()
                    .filter(employeeWorkDay -> employeeWorkDay.getId().equals(employeeWorkDayDTO.getId()))
                    .findAny()
                    .orElseThrow(() -> RestException.restThrow(ResponseMessage.ERROR_WORK_DAY_NOT_FOUND));

            createEmployeeWorkDay(employmentInfo, employeeWorkDayDTO, workDay);

            countWeekDays++;
        }

        // FRONTENDGA BERILGAN 7 TA HAVFTA KUNI QAYTIB KELMADI
        if (countWeekDays != 7) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_FORM_WORK_DAY_NOT_FOUND_REQUIRED_INFORMATION);
        }

        return employeeWorkDayList;
    }

    private List<EmployeeWorkDay> createEmployeeWorkDayList(EmploymentInfo employmentInfo, List<EmployeeWorkDayDTO> employeeWorkDayDTOList) {

        List<EmployeeWorkDay> employeeWorkDayList = new ArrayList<>();

        int countWeekDays = 0;

        for (EmployeeWorkDayDTO employeeWorkDayDTO : employeeWorkDayDTOList) {

            EmployeeWorkDay employeeWorkDay = new EmployeeWorkDay();

            createEmployeeWorkDay(employmentInfo, employeeWorkDayDTO, employeeWorkDay);

            employeeWorkDayList.add(employeeWorkDay);
            countWeekDays++;
        }

        // FRONTENDGA BERILGAN 7 TA HAVFTA KUNI QAYTIB KELMADI
        if (countWeekDays != 7) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVE_FORM_WORK_DAY_NOT_FOUND_REQUIRED_INFORMATION);
        }

        return employeeWorkDayList;
    }

    private void createEmployeeWorkDay(EmploymentInfo employmentInfo, EmployeeWorkDayDTO employeeWorkDayDTO, EmployeeWorkDay employeeWorkDay) {

        WeekDayEnum weekDay = employeeWorkDayDTO.getWeekDay();

        boolean working = employeeWorkDayDTO.isWorking();

        TimeDTO startTime = null;
        TimeDTO endTime = null;
        double workingHours = 0d;

        boolean lunch = employeeWorkDayDTO.isLunch();

        TimeDTO lunchStartTime = new TimeDTO();
        TimeDTO lunchEndTime = new TimeDTO();
        Double lunchHours = null;

        if (working) {
            startTime = employeeWorkDayDTO.getStartTime();
            endTime = employeeWorkDayDTO.getEndTime();

            workingHours = getIntervalHours(startTime, endTime);

            if (lunch) {

                lunchStartTime = employeeWorkDayDTO.getLunchStartTime();
                lunchEndTime = employeeWorkDayDTO.getLunchEndTime();
                lunchHours = getIntervalHours(lunchStartTime, lunchEndTime);

                if (workingHours > 0) {
                    workingHours = workingHours - lunchHours;
                }
            }
        }

        //
        employeeWorkDay.setEmploymentInfo(employmentInfo);
        employeeWorkDay.setWeekDay(weekDay);
        employeeWorkDay.setWorking(working);
        employeeWorkDay.setStartTime(working ? buildTimeStr(startTime) : null);
        employeeWorkDay.setEndTime(working ? buildTimeStr(endTime) : null);
        employeeWorkDay.setWorkingHours(workingHours);
        employeeWorkDay.setLunch(lunch);
        employeeWorkDay.setLunchStartTime(lunch ? buildTimeStr(lunchStartTime) : null);
        employeeWorkDay.setLunchEndTime(lunch ? buildTimeStr(lunchEndTime) : null);
        employeeWorkDay.setLunchHours(lunchHours);
    }

    private double getIntervalHours(TimeDTO startTime, TimeDTO endTime) {

        String start = buildTimeStr(startTime);
        String end = buildTimeStr(endTime);

        return CommonUtils.getFromAndToDateInterval(start, end);
    }

    private String buildTimeStr(TimeDTO time) {
        return time.getHour() + ":" + time.getMinute() + ":00";
    }

    private TimeDTO breakTimeStr(String time) {
        // 00:00:00
        String hour = time.substring(0, 2);
        String minute = time.substring(3, 5);

        return new TimeDTO(hour, minute);
    }


    /**
     * CHECK BRANCH EXIST OTHER SERVICE
     *
     * @param employmentInfoDTOList {@link List<EmploymentInfoDTO>}
     */
    private void checkBranchList(List<EmploymentInfoDTO> employmentInfoDTOList) {

        //ID LARNI YIG'IB OLDIK VA ORASIDA NULL BO'LMAGANLARINI FILTER QILIB OLDIK
        List<Long> longList = employmentInfoDTOList
                .stream()
                .map(EmploymentInfoDTO::getBranchId)
                .filter(Objects::nonNull).collect(Collectors.toList());

        //ID LAR LISTI BO'SH BO'LMASA FEIGN SERVICEGA YUBORGANMIZ
        if (!longList.isEmpty()) {
            feignService.checkBranchList(longList);
        }
    }

    /**
     * CHECK BRANCH EXIST OTHER SERVICE
     *
     * @param employmentInfoDTOList {@link List<EmploymentInfoDTO>}
     */
    private void checkCompanyList(List<EmploymentInfoDTO> employmentInfoDTOList) {

        //ID LARNI YIG'IB OLDIK VA ORASIDA NULL BO'LMAGANLARINI FILTER QILIB OLDIK
        List<Long> longList = employmentInfoDTOList
                .stream()
                .map(EmploymentInfoDTO::getCompanyId)
                .filter(Objects::nonNull).collect(Collectors.toList());

        //ID LAR LISTI BO'SH BO'LMASA FEIGN SERVICEGA YUBORGANMIZ
        if (!longList.isEmpty()) {
            feignService.checkCompanyList(longList);
        }
    }

    private void checkAttachmentList(List<AttachmentDTO> attachmentList) {

        /**
         * ID LARNI YIG'IB OLDIK VA ORASIDA NULL BO'LMAGANLARINI FILTER QILIB OLDIK
         */
        List<String> uuidList = attachmentList
                .stream()
                .map(AttachmentDTO::getFileId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        /**
         *  ID LAR LISTI BO'SH BO'LMASA FEIGN SERVICEGA YUBORGANMIZ
         */
        if (!uuidList.isEmpty()) {
            feignService.checkAttachmentList(uuidList);
        }

    }

    private void createHistoryLog(Employee employee, EmployeeDTO employeeDTO) {

        List<HistoryLog> historyLogList = new ArrayList<>();

        if (!employee.getFirstName().equals(employeeDTO.getEmployerInfo().getFirstName())) {
            historyLogList.add(new HistoryLog(EntityFieldNameEnum.EMPLOYEE_FIRST_NAME,
                    employee.getFirstName(),
                    employeeDTO.getEmployerInfo().getFirstName(),
                    EntityNameEnum.EMPLOYEE));
        }
        if (!employee.getLastName().equals(employeeDTO.getEmployerInfo().getLastName())) {
            historyLogList.add(new HistoryLog(EntityFieldNameEnum.EMPLOYEE_LAST_NAME,
                    employee.getLastName(),
                    employeeDTO.getEmployerInfo().getLastName(),
                    EntityNameEnum.EMPLOYEE));
        }
        if (!employee.getMiddleName().equals(employeeDTO.getEmployerInfo().getMiddleName())) {
            historyLogList.add(new HistoryLog(EntityFieldNameEnum.EMPLOYEE_MIDDLE_NAME,
                    employee.getMiddleName(),
                    employeeDTO.getEmployerInfo().getMiddleName(),
                    EntityNameEnum.EMPLOYEE));
        }
//        if (!employee.getPhotoId().equals(employeeDTO.getEmployerInfo().getPhotoId())) {
//            historyLogList.add(
//                    new HistoryLog(
//                            EntityFieldNameEnum.EMPLOYEE_PHOTO_URL,
//                            employee.getPhotoId().toString(),
//                            employeeDTO.getEmployerInfo().getPhotoId().toString(),
//                            EntityNameEnum.EMPLOYEE
//                    )
//            );
//        }
        if (!employee.getBirthDate().equals(new Date(employeeDTO.getEmployerInfo().getBirthDate()))) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_BIRTH_DATE,
                            CommonUtils.getDateFormat(employee.getBirthDate()),
                            CommonUtils.getDateFormat(new Date(employeeDTO.getEmployerInfo().getBirthDate())),
                            EntityNameEnum.EMPLOYEE
                    ));
        }
        if (!employee.getMaritalStatus().equals(employeeDTO.getEmployerInfo().getMaritalStatus())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_MARITAL_STATUS,
                            employee.getMaritalStatus().toString(),
                            employeeDTO.getEmployerInfo().getMaritalStatus().toString(),
                            EntityNameEnum.EMPLOYEE
                    ));
        }
        if (!employee.getGender().equals(employeeDTO.getEmployerInfo().getGender())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_GENDER,
                            employee.getGender().toString(),
                            employeeDTO.getEmployerInfo().getGender().toString(),
                            EntityNameEnum.EMPLOYEE
                    ));
        }
        if (!employee.getEmail().equals(employeeDTO.getEmployerInfo().getEmail())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_EMAIL,
                            employee.getEmail(),
                            employeeDTO.getEmployerInfo().getEmail(),
                            EntityNameEnum.EMPLOYEE
                    ));
        }
        if (!employee.getPassportSerial().equals(employeeDTO.getPassportInfo().getPassportSerial())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_PASSPORT_SERIAL,
                            employee.getPassportSerial(),
                            employeeDTO.getPassportInfo().getPassportSerial(),
                            EntityNameEnum.EMPLOYEE
                    ));
        }
        if (!employee.getPassportNumber().equals(employeeDTO.getPassportInfo().getPassportNumber())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_PASSPORT_NUMBER,
                            employee.getPassportNumber(),
                            employeeDTO.getPassportInfo().getPassportNumber(),
                            EntityNameEnum.EMPLOYEE
                    ));
        }
        if (!employee.getPassportGivenOrganisation().equals(employeeDTO.getPassportInfo().getPassportGivenOrganisation())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_PASSPORT_GIVEN_ORGANISATION,
                            employee.getPassportGivenOrganisation(),
                            employeeDTO.getPassportInfo().getPassportGivenOrganisation(),
                            EntityNameEnum.EMPLOYEE
                    ));
        }
        if (!employee.getPassportGivenDate().equals(new Date(employeeDTO.getPassportInfo().getPassportGivenDate()))) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_PASSPORT_GIVEN_DATE,
                            CommonUtils.getDateFormat(employee.getPassportGivenDate()),
                            CommonUtils.getDateFormat(new Date(employeeDTO.getPassportInfo().getPassportGivenDate())),
                            EntityNameEnum.EMPLOYEE
                    ));
        }

        if (!employee.getPassportExpireDate().equals(new Date(employeeDTO.getPassportInfo().getPassportExpireDate()))) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_PASSPORT_EXPIRE_DATE,
                            CommonUtils.getDateFormat(employee.getPassportExpireDate()),
                            CommonUtils.getDateFormat(new Date(employeeDTO.getPassportInfo().getPassportExpireDate())),
                            EntityNameEnum.EMPLOYEE
                    ));
        }
        if (!employee.getPermanentAddress().equals(employeeDTO.getPassportInfo().getPermanentAddress())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_PASSPORT_PERMANENT_ADDRESS,
                            employee.getPermanentAddress(),
                            employeeDTO.getPassportInfo().getPermanentAddress(),
                            EntityNameEnum.EMPLOYEE
                    ));
        }
        if (!employee.getCurrentAddress().equals(employeeDTO.getPassportInfo().getCurrentAddress())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_PASSPORT_CURRENT_ADDRESS,
                            employee.getCurrentAddress(),
                            employeeDTO.getPassportInfo().getCurrentAddress(),
                            EntityNameEnum.EMPLOYEE
                    ));
        }
        if (!employee.getPersonalNumber().equals(employeeDTO.getPassportInfo().getPersonalNumber())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.EMPLOYEE_PERSONAL_NUMBER,
                            employee.getPersonalNumber(),
                            employeeDTO.getPassportInfo().getPersonalNumber(),
                            EntityNameEnum.EMPLOYEE
                    ));
        }

        historyLogRepository.saveAll(historyLogList);
    }

    private void updateEmployeeAttendance(List<EmploymentInfo> employmentInfoList) {

        List<TimeSheet> timeSheetList = timeSheetService.getCurrentTimeSheetListByEmploymentInfo(employmentInfoList);

        List<TimeSheetEmployee> timeSheetEmployeeList = timeSheetEmployeeRepository.findAllByEmploymentInfoInAndTimeSheetIn(employmentInfoList, timeSheetList);

        List<EmployeeWorkDay> employeeWorkDayList = employeeWorkDayRepository.findAllByEmploymentInfoIn(employmentInfoList);

        timeSheetService.updateEmployeeAttendance(timeSheetEmployeeList, employeeWorkDayList);

    }

    private void addNewEmploymentInfoCurrentTimeSheet(List<EmploymentInfo> employmentInfoList) {
        timeSheetService.addNewEmploymentInfoCurrentTimeSheet(employmentInfoList);
    }

    /**
     * BLA
     *
     * @return
     */
    @NotNull
    private AdditionalDTO getAdditionalDTO() {

        boolean havePermission = CommonUtils.havePermission(new PermissionEnum[]{PermissionEnum.HRM_GET_ROLE_LIST});

        List<RoleFeignDTO> roleFeignDTOList = havePermission ? feignService.safeGetRoleList() : new ArrayList<>();
        List<BranchFeignDTO> branchFeignDTOList = feignService.safeGetBranchList();
        List<CompanyFeignDTO> companyFeignDTOList = feignService.safeGetCompanyList();
        List<EnumDTO> maritalStatusList = additionalService.getMaritalStatuses();
        List<EnumDTO> genderList = additionalService.getGenders();
        List<PrivilegeTypeDTO> privilegeTypeDTOList = privilegeTypeService.getAllActive();
        List<DepartmentDTO> departmentDTOList = departmentService.getAllActiveDepartmentFromDB();
        List<PositionDTO> positionDTOList = positionService.getAllActivePositionFromDB();
        List<EmployeeCategoryDTO> employeeCategoryDTOList = employeeCategoryService.getAllActiveEmployeeCategoryFromDB();
        List<EnumDTO> paymentCriteriaTypeList = additionalService.getPaymentCriteriaType();
        List<EnumDTO> weekDayList = additionalService.getWeekDays();
        List<EnumDTO> contractFormsList = additionalService.getContractForms();
        List<EnumDTO> studyDegreeList = additionalService.getStudyDegrees();
        List<SkillDTO> skillDTOList = skillService.getAllSkillFromDB();
        List<EnumDTO> employeeModesList = additionalService.getEmployeeModes();
        List<PhoneNumberTypeInfoDTO> phoneNumberTypeInfoDTOList = phoneNumberTypeService.getPhoneNumberTypeInfoDTOListFromDB();

        return new AdditionalDTO(
                roleFeignDTOList,
                branchFeignDTOList,
                companyFeignDTOList,
                maritalStatusList,
                genderList,
                privilegeTypeDTOList,
                departmentDTOList,
                positionDTOList,
                employeeCategoryDTOList,
                paymentCriteriaTypeList,
                weekDayList,
                contractFormsList,
                studyDegreeList,
                skillDTOList,
                employeeModesList,
                phoneNumberTypeInfoDTOList
        );
    }


    @NotNull
    private EmployeeFormInfoDTO getEmployeeFormInfoDTO(AdditionalDTO additionalDTO) {
        return new EmployeeFormInfoDTO(
                OptionDTO.makeOptionDTO(additionalDTO.getMaritalStatusList()),
                OptionDTO.makeOptionDTO(additionalDTO.getGenderList()),
                new PhoneNumberFormDTO(
                        additionalDTO.getPhoneNumberTypeDTOListFromDB(),
                        new OptionActionDTO(RestConstants.PHONE_NUMBER_TYPE_CRUD),
                        Collections.singletonList(new PhoneNumberDTO(true))
                ),
                OptionDTO.makeOptionDTO(additionalDTO.getPrivilegeTypeList())
        );
    }

    /**
     * battar todo
     *
     * @param additionalDTO {@link AdditionalDTO}
     * @return
     */
    @NotNull
    private EmploymentFormInfoDTO getEmploymentFormInfoDTO(AdditionalDTO additionalDTO) {
        return new EmploymentFormInfoDTO(
                OptionDTO.makeOptionDTO(additionalDTO.getBranchList()),
                OptionDTO.makeOptionDTO(additionalDTO.getCompanyList()),
                OptionDTO.makeOptionDTO(additionalDTO.getDepartmentList()),
                OptionDTO.makeOptionDTO(additionalDTO.getPositionList()),
                OptionDTO.makeOptionDTO(additionalDTO.getEmployeeCategoryList()),
                OptionDTO.makeOptionDTO(additionalDTO.getPaymentCriteriaTypeList()),
                OptionDTO.makeOptionDTO(additionalDTO.getContractFormList()),
                OptionDTO.makeOptionDTO(additionalDTO.getEmployeeModeList()),
                getEmployeeWorkDayInfoDTOList()
        );
    }

    /**
     * battar todo
     *
     * @param additionalDTO
     * @param employeeFormInfoDTO
     * @param employmentFormInfoDTO
     * @return
     */
    @NotNull
    private EmployeeFormDTO getEmployeeFormDTO(AdditionalDTO additionalDTO,
                                               EmployeeFormInfoDTO employeeFormInfoDTO,
                                               EmploymentFormInfoDTO employmentFormInfoDTO) {
        return new EmployeeFormDTO(
                employeeFormInfoDTO,
                new AccountFormInfoDTO(OptionDTO.makeOptionDTO(additionalDTO.getRoleList())),
                new EmployeePassportInfoDTO(),
                Collections.singletonList(employmentFormInfoDTO),
                Collections.singletonList(new EmployeeEducationFormInfoDTO(OptionDTO.makeOptionDTO(additionalDTO.getStudyDegreeList()))),
                Collections.singletonList(new EmployeeExperienceInfoDTO()),
                Collections.singletonList(new AttachmentFeignDTO()),
                OptionDTO.makeOptionDTO(
                        additionalDTO.getSkillList(),
                        new OptionActionDTO(RestConstants.SKILL_CRUD)
                )
        );
    }

    @NotNull
    private EmployeeFormDTO getEmployeeFormDTO(UUID employeeId, AdditionalDTO additionalDTO, Employee employeeFromDB) {

        AccountFormInfoDTO accountFormInfoDTO = getAccountFormInfoDTO(additionalDTO, employeeFromDB);

        List<SkillDTO> employeeSkillDTOList = getSkillDTOList(employeeFromDB.getSkills());

        OptionDTO<SkillDTO> skillDTOOptionDTO = getSkillDTOOptionDTO(additionalDTO, employeeSkillDTOList);


        return new EmployeeFormDTO(
                employeeId,
                getEmployerFormInfoDTO(employeeFromDB, additionalDTO),
                accountFormInfoDTO,
                getEmployeePassportInfo(employeeFromDB),
                getEmploymentFormInfoDTO(employeeFromDB, additionalDTO),
                getEmployeeEducationFormInfoDTO(employeeFromDB, additionalDTO),
                getEmployeeExperienceInfo(employeeFromDB),
                getAttachmentDTO(employeeFromDB),
                skillDTOOptionDTO,
                employeeFromDB.getResignation(),
                Objects.isNull(employeeFromDB.getResignationDate()) ? null : employeeFromDB.getResignationDate().getTime(),
                employeeFromDB.getResignationDescription()
        );
    }

    private void synchronizeEmployeeRoleInfo(UUID employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);

        if (optionalEmployee.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_NOT_FOUND);
        }

        Employee employee = optionalEmployee.get();
        log.info("employee {}", employee);

        UserDTO userDTO = feignService.safeGetUserById(employee.getUserId());

        log.info("userDTO {}", userDTO);

        List<Long> roleIdList = userDTO.getRoleIdList();

        if (roleIdList.isEmpty()) {
            employee.setAccess(Boolean.FALSE);
            employee.setRoles(null);
        } else {
            employee.setAccess(Boolean.TRUE);
            employee.setRoles(roleIdList.toArray(new Long[0]));
        }


        employeeRepository.save(employee);

        log.info("employee {}", employee);
    }

    @NotNull
    private AccountFormInfoDTO getAccountFormInfoDTO(AdditionalDTO additionalDTO, Employee employeeFromDB) {

        List<Long> longList = Objects.isNull(employeeFromDB.getRoles()) ? new ArrayList<>() : Arrays.asList(employeeFromDB.getRoles());

        return new AccountFormInfoDTO(
                OptionDTO.makeOptionDTO(
                        additionalDTO.getRoleList(),
                        longList
                ),
                employeeFromDB.isAccess());
    }

    @NotNull
    private OptionDTO<SkillDTO> getSkillDTOOptionDTO(AdditionalDTO additionalDTO, List<SkillDTO> employeeSkillDTOList) {
        List<UUID> skillUuidList = employeeSkillDTOList
                .stream()
                .map(SkillDTO::getId)
                .collect(Collectors.toList());

        return OptionDTO.makeOptionDTO(
                additionalDTO.getSkillList(),
                skillUuidList,
                new OptionActionDTO(RestConstants.SKILL_CRUD)
        );
    }

    /**
     * BATTARAA
     *
     * @param employeeDTO {@link EmployeeDTO}
     */
    private void checkAttachmentIdListValid(EmployeeDTO employeeDTO) {
        // CHECK ATTACHMENT EXIST OTHER SERVICE
        List<AttachmentDTO> attachmentDTOList = new ArrayList<>();
        List<AttachmentDTO> attachments = employeeDTO.getAttachments();

        String photoId = employeeDTO.getEmployerInfo().getPhotoId();
        if (Objects.nonNull(photoId) && !photoId.isEmpty()) {
            attachmentDTOList.add(new AttachmentDTO(photoId));
        }
        attachmentDTOList.addAll(attachments);

        checkAttachmentList(attachmentDTOList);
    }

    private void makeEmployeeOtherDetails(EmployeeDTO employeeDTO, Employee employee) {
        // DATABASEGA SAQLANGAN HODIM UCHUN EmploymentInfo YARATIB DATABASEGA SAQLASH
        makeEmploymentInfo(employee, employeeDTO.getEmployments());

        // DATABASEGA SAQLANGAN HODIM UCHUN EmployeeExperienceInfo YARATIB DATABASEGA SAQLASH
        makeEmployeeExperienceHistory(employee, employeeDTO.getExperiences());

        // DATABASEGA SAQLANGAN HODIM UCHUN EmployeeEducationInfo YARATIB DATABASEGA SAQLASH
        makeEmployeeEducationHistory(employee, employeeDTO.getEducations());

        // DATABASEGA SAQLANGAN HODIM UCHUN EmployeeAttachment YARATIB DATABASEGA SAQLASH
        makeEmployeeAttachment(employee, employeeDTO.getAttachments());

        makeEmployeePhoneNumber(employee, employeeDTO.getEmployerInfo().getPhoneNumbers());

        makeEmployeeSkill(employee, employeeDTO.getSkill());
    }

    private void saveEmployee(Employee employee) {
        try {
            employeeRepository.save(employee);
        } catch (Exception e) {
            // log.info("class EmployeeServiceImpl => addEmployee => error saving employee");
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_SAVING);
        }
    }

    private void saveResignationEmployeeAttachments(Employee employeeFromDB, EmployeeResignationDTO employeeResignationDTO) {
        List<EmployeeAttachment> employeeAttachmentList = new ArrayList<>();

        List<AttachmentDTO> attachments = employeeResignationDTO.getAttachments();

        for (AttachmentDTO attachment : attachments) {

            EmployeeAttachment employeeAttachment = new EmployeeAttachment(
                    employeeFromDB,
                    attachment.getFileId(),
                    attachment.getDescription(),
                    Boolean.FALSE
            );

            employeeAttachmentList.add(employeeAttachment);
        }

        employeeAttachmentRepository.saveAll(employeeAttachmentList);
    }


    private void checkUserIsAlreadyExists(UUID userId) {
        boolean exists = employeeRepository.existsByUserId(userId);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_THIS_USER_ALREADY_EXISTS);
        }
    }

}
