package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.Department;
import ai.ecma.appstaff.entity.HistoryLog;
import ai.ecma.appstaff.enums.EntityFieldNameEnum;
import ai.ecma.appstaff.enums.EntityNameEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.DepartmentDTO;
import ai.ecma.appstaff.payload.feign.CompanyFeignDTO;
import ai.ecma.appstaff.payload.feign.DepartmentFeignDTO;
import ai.ecma.appstaff.repository.DepartmentRepository;
import ai.ecma.appstaff.repository.HistoryLogRepository;

import ai.ecma.appstaff.service.feign.FeignService;
import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    private final PositionService positionService;
    private final DepartmentRepository departmentRepository;
    private final HistoryLogRepository historyLogRepository;
    private final TimesheetService timeSheetService;
    private final EmployeeService employeeService;
    private final EmployeeCategoryService employeeCategoryService;
    private final TimesheetService timesheetService;
    private final TariffGridService tariffGridService;
    private final FeignService feignService;

    @Autowired
    public DepartmentServiceImpl(
            @Lazy PositionService positionService,
            DepartmentRepository departmentRepository,
            HistoryLogRepository historyLogRepository,
            TimesheetService timeSheetService,
            @Lazy EmployeeService employeeService,
            @Lazy EmployeeCategoryService employeeCategoryService,
            TimesheetService timesheetService,
            @Lazy TariffGridService tariffGridService, FeignService feignService) {
        this.positionService = positionService;
        this.departmentRepository = departmentRepository;
        this.historyLogRepository = historyLogRepository;
        this.timeSheetService = timeSheetService;
        this.employeeService = employeeService;
        this.employeeCategoryService = employeeCategoryService;
        this.timesheetService = timesheetService;
        this.tariffGridService = tariffGridService;
        this.feignService = feignService;
    }


    @Override
    public ApiResult<DepartmentDTO> addDepartment(DepartmentDTO departmentDTO) {
        // log.info("class DepartmentServiceImpl => addDepartment => method entered => DTO : {}", departmentDTO);

        // BUNDAY DEPARTMENT AVVAL MAVJUDLIGINI TEKSHIRADI.
        // AGAR AVVAL MAVJUD BO'LSA XATOLIK QAYTADI
        // NAME (UNIQUE)
        checkDepartmentExistOrThrow(departmentDTO, Optional.empty());

        // DEPARTMENTNI YARATIB OLISH
        Department newDepartment = makeDepartment(new Department(), departmentDTO);

        try {
            // YANGI BO'LIMNI DATABASEGA SAQLAYAPMIZ
            departmentRepository.save(newDepartment);
        } catch (Exception e) {
            // log.info("class DepartmentServiceImpl => addDepartment => error saving department");
            throw RestException.restThrow(ResponseMessage.ERROR_DEPARTMENT_SAVING);
        }

        // YANGI QO'SHILGAN DEPARTMENT UCHUN TIMESHEET YARATAMIZ
        createTimeSheetForDepartmentNewThread(newDepartment);

        return ApiResult.successResponse(
                DepartmentDTO.makeDepartmentDTOFromDepartment(newDepartment),
                ResponseMessage.SUCCESS_DEPARTMENT_SAVED
        );
    }

    @Override
    @Transactional
    public ApiResult<DepartmentDTO> editDepartment(UUID id, DepartmentDTO departmentDTO) {
        // log.info("class DepartmentServiceImpl => editDepartment => method entered => ID : {} DTO : {}", id, departmentDTO);

        // BUNDAY DEPARTMENT AVVAL MAVJUDLIGINI TEKSHIRADI.
        // AGAR AVVAL MAVJUD BO'LSA XATOLIK QAYTADI
        checkDepartmentExistOrThrow(departmentDTO, Optional.of(id));

        // ID BO'YICHA DATABASEDAN DEPARTMENTNI OLAMIZ
        Department departmentFromDB = getDepartmentFromDB(id, false);

        // HISTORY YIG'IB BORADI
        createHistoryLog(departmentFromDB, departmentDTO);

        // DTO DA KELGAN MA'LUMOTLARNI DATABASEDAN OLGAN DEPARTMENTIMIZGA SET QILAMIZ
        Department department = makeDepartment(departmentFromDB, departmentDTO);

        try {
            // DEPARTMENTNI DATABASEGA SAQLAYMIZ
            departmentRepository.save(department);
        } catch (Exception e) {
            // log.info("class DepartmentServiceImpl => editDepartment => error saving department");
            throw RestException.restThrow(ResponseMessage.ERROR_DEPARTMENT_EDITING);
        }
        return ApiResult.successResponse(
                DepartmentDTO.makeDepartmentDTOFromDepartment(department),
                ResponseMessage.SUCCESS_DEPARTMENT_EDITED
        );
    }

    @Override
    public ApiResult<List<DepartmentDTO>> getAllDepartment(Integer page, Integer size) {
        // log.info("class DepartmentServiceImpl => getAllDepartment => method entered => PAGE : {} SIZE : {} ", page, size);
        return ApiResult.successResponse(getAllDepartmentsFromDB());
    }

    @Override
    public ApiResult<List<DepartmentDTO>> getAllDepartmentForSelect(Integer page, Integer size) {
        // log.info("class DepartmentServiceImpl => getAllDepartmentForSelect => method entered => PAGE : {} SIZE : {} ", page, size);
        return ApiResult.successResponse(getAllDepartmentsFromDB());
    }

    @Override
    public ApiResult<DepartmentDTO> getOneDepartment(UUID id) {
        // log.info("class DepartmentServiceImpl => getOneDepartment => method entered => ID : {} ", id);

        // DATABASEDAN BERILGAN ID GA TENG BO'LGAN DEPARTMENTNI OLADI.
        //AGAR TOPA OLMASA XATOLIKKA TUSHADI
        Department departmentFromDB = getDepartmentFromDB(id, false);

        return ApiResult.successResponse(DepartmentDTO.makeDepartmentDTOFromDepartment(departmentFromDB));

    }

    @Override
    public ApiResult<?> deleteDepartment(UUID id) {
        // log.info("class DepartmentServiceImpl => deleteDepartment => method entered => ID : {} ", id);

        checkCanDeleteDepartmentOrThrow(id);

        try {
            departmentRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_DEPARTMENT_NOT_FOUND);
        }

        return ApiResult.successResponse(ResponseMessage.SUCCESS_DEPARTMENT_DELETED);
    }

    @Override
    public List<DepartmentDTO> getAllActiveDepartmentFromDB() {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        return departmentRepository.findAllByActiveTrue(sortByColumn)
                .stream()
                .map(DepartmentDTO::makeDepartmentDTOFromDepartment)
                .collect(Collectors.toList());

    }

    @Override
    public Department getDepartmentFromDB(UUID id, boolean onlyActive) {

        Optional<Department> optionalDepartment = departmentRepository.findById(id);

        if (optionalDepartment.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_DEPARTMENT_NOT_FOUND);
        }

        Department department = optionalDepartment.get();

        if (onlyActive) {
            if (!department.isActive()) {
                throw RestException.restThrow(ResponseMessage.ERROR_DEPARTMENT_NOT_ACTIVE);
            }
        }

        return department;
    }

    @Override
    public List<DepartmentFeignDTO> getAllActiveDepartmentFromDBForOtherMicroservice() {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        return departmentRepository.findAllByActiveTrue(sortByColumn)
                .stream()
                .map(DepartmentFeignDTO::makeDepartmentFeignDTOFromDepartment)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentFeignDTO> getDepartmentsByUserId(UUID userId) {

        List<Department> departmentList = departmentRepository.findAllDepartmentByUserId(userId);

        return departmentList
                .stream()
                .map(DepartmentFeignDTO::makeDepartmentFeignDTOFromDepartment)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentFeignDTO> getAllActiveDepartmentFromDBByIdList(List<UUID> idList) {

        List<Department> departmentList = departmentRepository.findAllById(idList);

        return departmentList
                .stream()
                .map(DepartmentFeignDTO::makeDepartmentFeignDTOFromDepartment)
                .collect(Collectors.toList());
    }


    private Department makeDepartment(Department department, DepartmentDTO departmentDTO) {

        department.setName(departmentDTO.getName());
        department.setActive(departmentDTO.isActive());
        department.setCompanyId(departmentDTO.getCompanyId());

        return department;
    }

    private void checkCanDeleteDepartmentOrThrow(UUID departmentId) {

        positionService.existsByDepartmentId(departmentId);

        employeeService.existsByDepartmentId(departmentId);

        employeeCategoryService.existsByDepartmentId(departmentId);

        timesheetService.existsByDepartmentId(departmentId);

        tariffGridService.existsByDepartmentId(departmentId);

    }

    private void checkDepartmentExistOrThrow(DepartmentDTO departmentDTO, Optional<UUID> optionalId) {

//        boolean exists;
//
//        if (optionalId.isEmpty()) {
//            exists = departmentRepository.existsByName(departmentDTO.getName());
//        } else {
//            exists = departmentRepository.existsByNameAndIdNot(departmentDTO.getName(), optionalId.get());
//        }
//
//        if (exists) {
//            throw RestException.restThrow(ResponseMessage.ERROR_DEPARTMENT_ALREADY_EXIST);
//        }
    }

    public void createTimeSheetForDepartmentNewThread(Department savedDepartment) {
        new Thread(
                () -> timeSheetService.createTimeSheetForDepartment(savedDepartment)
        ).start();
    }

    @Override
    public List<DepartmentDTO> getAllDepartmentsFromDB() {

        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<DepartmentDTO> departmentDTOList = departmentRepository.findAll(sortByColumn)
                .stream()
                .map(DepartmentDTO::makeDepartmentDTOFromDepartment)
                .collect(Collectors.toList());

        List<CompanyFeignDTO> companyList = feignService.getCompanyList();

        for (DepartmentDTO departmentDTO : departmentDTOList) {
            for (CompanyFeignDTO companyFeignDTO : companyList) {

                if (Objects.equals(departmentDTO.getCompanyId(), companyFeignDTO.getId())) {
                    departmentDTO.setCompanyName(companyFeignDTO.getName());
                }
            }
        }

        return departmentDTOList;
    }

    @Transactional
    public void createHistoryLog(Department department, DepartmentDTO departmentDTO) {

        List<HistoryLog> historyLogList = new ArrayList<>();

        if (Objects.equals(department.getName(), departmentDTO.getName())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.DEPARTMENT_NAME,
                            department.getName(),
                            departmentDTO.getName(),
                            EntityNameEnum.DEPARTMENT
                    )
            );
        }

        if (Objects.equals(department.isActive(), departmentDTO.isActive())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.DEPARTMENT_ACTIVE,
                            department.isActive() + "",
                            departmentDTO.getName() + "",
                            EntityNameEnum.DEPARTMENT
                    )
            );
        }

        historyLogRepository.saveAll(historyLogList);
    }
}
