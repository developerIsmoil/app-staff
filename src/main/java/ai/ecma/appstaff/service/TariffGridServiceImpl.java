package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.HistoryLog;
import ai.ecma.appstaff.entity.TariffGrid;
import ai.ecma.appstaff.enums.EntityFieldNameEnum;
import ai.ecma.appstaff.enums.EntityNameEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.OptionDTO;
import ai.ecma.appstaff.payload.TariffGridDTO;
import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.repository.HistoryLogRepository;
import ai.ecma.appstaff.repository.TariffGridRepository;

import ai.ecma.appstaff.service.feign.FeignService;
import ai.ecma.appstaff.service.view.ViewService;
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
public class TariffGridServiceImpl implements TariffGridService {

    private final PositionService positionService;
    private final AdditionalService additionalService;
    private final DepartmentService departmentService;
    private final HistoryLogRepository historyLogRepository;
    private final TariffGridRepository tariffGridRepository;
    private final EmployeeCategoryService employeeCategoryService;
    private final FeignService feignService;
    private final ViewService viewService;

    @Autowired
    public TariffGridServiceImpl(
            @Lazy PositionService positionService,
            AdditionalService additionalService,
            @Lazy DepartmentService departmentService,
            HistoryLogRepository historyLogRepository,
            TariffGridRepository tariffGridRepository,
            @Lazy EmployeeCategoryService employeeCategoryService,
            FeignService feignService, ViewService viewService) {
        this.positionService = positionService;
        this.additionalService = additionalService;
        this.departmentService = departmentService;
        this.historyLogRepository = historyLogRepository;
        this.tariffGridRepository = tariffGridRepository;
        this.employeeCategoryService = employeeCategoryService;
        this.feignService = feignService;
        this.viewService = viewService;
    }

    @Override
    public ApiResult<?> addTariffGrid(TariffGridDTO tariffGridDTO) {
        // log.info("class TariffGridServiceImpl => addTariffGrid => method entered => DTO : {}", tariffGridDTO);

        checkTariffGridExist(tariffGridDTO, Optional.empty());

        //tariffGridDTO DAGI MA'LUMOTLAR BILAN tariffGrid YASAB OLDIK
        TariffGrid tariffGrid = makeTariffGrid(new TariffGrid(), tariffGridDTO);

        try {
            // TARIF SETKASINI DATABASEGA SAQLAYAPMIZ
            tariffGridRepository.save(tariffGrid);
        } catch (Exception e) {
            // log.info("class TariffGridServiceImpl => addTariffGrid => error saving tariffGrid");
            throw RestException.restThrow(ResponseMessage.ERROR_SAVING_TARIFF_GRID);
        }
//        List<BranchFeignDTO> branchFeignDTOList = feignService.safeGetBranchList();

        return viewService.getViewDataByIdList(tariffGridDTO.getViewId(), List.of(tariffGrid.getId().toString()));

//        return ApiResult.successResponse(TariffGridDTO.fromTariffGrid(tariffGrid, branchFeignDTOList), ResponseMessage.SUCCESS_TARIFF_GRID_SAVED);
    }

    @Override
    public ApiResult<?> editTariffGrid(UUID id, TariffGridDTO tariffGridDTO) {
        // log.info("class TariffGridServiceImpl => editTariffGrid => method entered => DTO : {}, Id => {}", tariffGridDTO, id);

        checkTariffGridExist(tariffGridDTO, Optional.of(id));

        //DATABASEDAN SHU IDLI tariffGrid NI OLYAPMIN
        TariffGrid tariffGridFromDB = getTariffGridFromDB(id, false);

//        // createHistory(tariffGridFromDB, tariffGridDTO);

        //tariffGrid NI tariffGridDTO DAGI MA'LUMOTLAR BILAN O'ZGARTIRYAPMIZ
        TariffGrid tariffGrid = makeTariffGrid(tariffGridFromDB, tariffGridDTO);

        try {
            // TARIF SETKASINI O'ZGARTIRYAPMIZ
            tariffGridRepository.save(tariffGrid);
        } catch (Exception e) {
            // log.info("class TariffGridServiceImpl => editTariffGrid => error editing tariffGrid");
            throw RestException.restThrow(ResponseMessage.ERROR_EDITING_TARIFF_GRID);
        }
//        List<BranchFeignDTO> branchFeignDTOList = feignService.safeGetBranchList();

        return viewService.getViewDataByIdList(tariffGridDTO.getViewId(), List.of(tariffGrid.getId().toString()));

//        return ApiResult.successResponse(TariffGridDTO.fromTariffGrid(tariffGrid, branchFeignDTOList), ResponseMessage.SUCCESS_TARIFF_GRID_EDITED);
    }

    @Override
    public ApiResult<?> getAllTariffGrid(Integer page, Integer size) {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        // log.info("class TariffGridServiceImpl => getAllTariffGrid => method entered => PAGE : {} SIZE : {} ", page, size);
        List<TariffGrid> tariffGridList = tariffGridRepository.findAll(sortByColumn);
        return ApiResult.successResponse(getTariffGridDTOList(tariffGridList));
    }

    @Override
    public ApiResult<?> getAllTariffGridSelect(Integer page, Integer size) {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        // log.info("class TariffGridServiceImpl => getAllTariffGrid => method entered => PAGE : {} SIZE : {} ", page, size);
        List<TariffGrid> tariffGridList = tariffGridRepository.findAllByActiveTrue(sortByColumn);
        return ApiResult.successResponse(getTariffGridDTOList(tariffGridList));
    }

    @Override
    public ApiResult<TariffGridDTO> getOneTariffGridById(UUID id) {
        // log.info("class TariffGridServiceImpl => getOneTariffGridById => method entered => ID : {} ", id);

        // DATABASEDAN BERILGAN ID GA TENG BO'LGAN TARIFFGRID OLADI.
        //AGAR TOPA OLMASA XATOLIKKA TUSHADI
        TariffGrid tariffGridFromDB = getTariffGridFromDB(id, false);
        return ApiResult.successResponse(getTariffGridFormDto(tariffGridFromDB));
    }

    @Override
    public ApiResult<?> deleteTariffGridById(UUID id) {
        // log.info("class TariffGridServiceImpl => deleteTariffGridById => method entered => ID : {} ", id);
        try {
            tariffGridRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_TARIFF_GRID_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_TARIFF_GRID_DELETED);
    }

    @Override
    public ApiResult<?> deleteTariffGridByIdList(List<UUID> id) {
        // log.info("class TariffGridServiceImpl => deleteTariffGridByIdList => method entered => ID : {} ", id);
        try {
            tariffGridRepository.deleteAllById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_TARIFF_GRID_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_TARIFF_GRID_DELETED);
    }

    @Override
    public ApiResult<?> getFormTariffGrid() {

        TariffGridDTO tariffGridDTO = TariffGridDTO.tariffGridDTOOptions(
                feignService.getCompanyList(),
                feignService.getBranchList(),
                departmentService.getAllActiveDepartmentFromDB(),
                positionService.getAllActivePositionFromDB(),
                employeeCategoryService.getAllActiveEmployeeCategoryFromDB(),
                additionalService.getPaymentCriteriaType(),
                additionalService.getBonusTypes()
        );

        return ApiResult.successResponse(tariffGridDTO);
    }

    @Override
    public ApiResult<?> changeActiveTariffGrid(UUID id) {
        // log.info("class TariffGridServiceImpl => changeActiveTariffGrid => method entered => ID : {} ", id);

        // DATABASEDAN BERILGAN ID GA TENG BO'LGAN TARIFFGRIDNI OLADI.
        //AGAR TOPA OLMASA XATOLIKKA TUSHADI
        TariffGrid tariffGridFromDB = getTariffGridFromDB(id, false);

        // DATABASEDAN OLINGAN TARIFFGRIDNI HOLATINI TESKARI KO'RINISHGA O'TKAZISH (TRUE => FALSE) (FALSE => TRUE)
        boolean changedActive = !tariffGridFromDB.isActive();
        tariffGridFromDB.setActive(changedActive);

        // HOLATI O'ZGARGAN DEPARTMENTNI DATABASEGA SAQLASH
        // log.info("class TariffGridServiceImpl => changeActiveTariffGrid => changed active => ACTIVE : {} ", changedActive);
        tariffGridRepository.save(tariffGridFromDB);
        return ApiResult.successResponse(ResponseMessage.SUCCESS_TARIFF_GRID_STATUS_CHANGE);
    }

    //TARIF SETKASI MAVJUDLIGINI TEKSHIRISH
    @Override
    public void existsTariffGrid(TariffGridDTO tariffGridDTO) {

        log.info("TariffGridDTO {}", tariffGridDTO);

        //branchId, departmentId, positionId, paymentCriteria, employeeCategoryId LI TARIF SETKASI BORLIGINI TEKSHIRYAPMIZ
        Boolean exists = tariffGridRepository.existsByDepartment_IdAndBranchIdAndCompanyIdAndPosition_IdAndPaymentCriteriaTypeAndEmployeeCategory_IdAndActiveIsTrue(
                tariffGridDTO.getDepartmentId(),
                tariffGridDTO.getBranchId(),
                tariffGridDTO.getCompanyId(),
                tariffGridDTO.getPositionId(),
                tariffGridDTO.getPaymentCriteriaType(),
                tariffGridDTO.getEmployeeCategoryId()
        );

        log.info("exists {}", exists);

        if (Objects.equals(Boolean.FALSE, exists)) {
            throw RestException.restThrow(ResponseMessage.ERROR_TARIFF_GRID_NOT_MATCH);
        }
    }

    @Override
    public TariffGrid getOneTariffGrid(TariffGridDTO tariffGridDTO) {
        log.info("TariffGridDTO {}", tariffGridDTO);

        //branchId, departmentId, positionId, paymentCriteria, employeeCategoryId LI TARIF SETKASI BORLIGINI TEKSHIRYAPMIZ
        Optional<TariffGrid> tariffGridOptional = tariffGridRepository.findByDepartment_IdAndBranchIdAndCompanyIdAndPosition_IdAndPaymentCriteriaTypeAndEmployeeCategory_IdAndActiveIsTrue(
                tariffGridDTO.getDepartmentId(),
                tariffGridDTO.getBranchId(),
                tariffGridDTO.getCompanyId(),
                tariffGridDTO.getPositionId(),
                tariffGridDTO.getPaymentCriteriaType(),
                tariffGridDTO.getEmployeeCategoryId()
        );

        return tariffGridOptional.orElseGet(TariffGrid::new);

    }

    @Override
    public void existsByDepartmentId(UUID departmentId) {
        boolean exists = tariffGridRepository.existsByDepartmentId(departmentId);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_DEPARTMENT_CONNECT_TARIFF_GRID);
        }
    }

    @Override
    public void existsByEmployeeCategoryId(UUID employeeCategoryId) {

        boolean exists = tariffGridRepository.existsByEmployeeCategoryId(employeeCategoryId);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_EMPLOYEE_CATEGORY_CONNECT_TARIFF_GRID);
        }

    }

    @Override
    public void existsByPositionId(UUID id) {

        boolean exists = tariffGridRepository.existsByPositionId(id);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_POSITION_CONNECT_TARIFF_GRID);
        }

    }

    @Override
    public ApiResult<?> editViewRowData(UUID viewId, UUID rowId, Map<String, Object> map) {
        return ApiResult.successResponse();
    }

    private void checkTariffGridExist(TariffGridDTO tariffGridDTO, Optional<UUID> optionalId) {
        log.info("TariffGridDTO {}", tariffGridDTO);

        Boolean exists;

        if (optionalId.isEmpty()) {
            //branchId, departmentId, positionId, paymentCriteria, employeeCategoryId LI TARIF SETKASI BORLIGINI TEKSHIRYAPMIZ
//            exists = tariffGridRepository.existsByDepartment_IdAndBranchIdAndCompanyIdAndPosition_IdAndPaymentCriteriaTypeAndEmployeeCategory_IdAndActiveIsTrue(
            exists = tariffGridRepository.existsByDepartment_IdAndBranchIdAndCompanyIdAndPosition_IdAndPaymentCriteriaTypeAndEmployeeCategory_Id(
                    tariffGridDTO.getDepartmentId(),
                    tariffGridDTO.getBranchId(),
                    tariffGridDTO.getCompanyId(),
                    tariffGridDTO.getPositionId(),
                    tariffGridDTO.getPaymentCriteriaType(),
                    tariffGridDTO.getEmployeeCategoryId()
            );
        } else {
            //id SHUNDAN BOSHQA va branchId, departmentId, positionId, paymentCriteria, employeeCategoryId LI TARIF SETKASI BORLIGINI TEKSHIRYAPMIZ
//            exists = tariffGridRepository.existsByDepartment_IdAndBranchIdAndCompanyIdAndPosition_IdAndPaymentCriteriaTypeAndEmployeeCategory_IdAndIdNotAndActiveIsTrue(
            exists = tariffGridRepository.existsByDepartment_IdAndBranchIdAndCompanyIdAndPosition_IdAndPaymentCriteriaTypeAndEmployeeCategory_IdAndIdNot(
                    tariffGridDTO.getDepartmentId(),
                    tariffGridDTO.getBranchId(),
                    tariffGridDTO.getCompanyId(),
                    tariffGridDTO.getPositionId(),
                    tariffGridDTO.getPaymentCriteriaType(),
                    tariffGridDTO.getEmployeeCategoryId(),
                    optionalId.get()
            );
        }

        log.info("exists {}", exists);

        if (Objects.equals(Boolean.TRUE, exists)) {
            throw RestException.restThrow(ResponseMessage.ERROR_TARIFF_GRID_SAVING_EXISTS_UNIQUE);
        }


    }


    //TARIFFGRIDDAN TARIFGRIDDTO YASAB BERADI
    private TariffGridDTO getTariffGridFormDto(TariffGrid tariffGrid) {

        return new TariffGridDTO(
                tariffGrid.getId(),
                OptionDTO.makeOptionDTO(
                        feignService.getBranchList(),
                        Objects.isNull(tariffGrid.getBranchId()) ? new ArrayList<>() : Collections.singletonList(tariffGrid.getBranchId())
                ),
                OptionDTO.makeOptionDTO(
                        feignService.getCompanyList(),
                        Objects.isNull(tariffGrid.getCompanyId()) ? new ArrayList<>() : Collections.singletonList(tariffGrid.getCompanyId())
                ),
                OptionDTO.makeOptionDTO(
                        departmentService.getAllActiveDepartmentFromDB(),
                        Collections.singletonList(tariffGrid.getDepartment().getId())
                ),
                OptionDTO.makeOptionDTO(
                        positionService.getAllActivePositionFromDB(),
                        Collections.singletonList(tariffGrid.getPosition().getId())
                ),
                OptionDTO.makeOptionDTO(
                        employeeCategoryService.getAllActiveEmployeeCategoryFromDB(),
                        Collections.singletonList(tariffGrid.getEmployeeCategory().getId())
                ),
                OptionDTO.makeOptionDTO(
                        additionalService.getPaymentCriteriaType(),
                        Collections.singletonList(tariffGrid.getPaymentCriteriaType())
                ),
                tariffGrid.getPaymentAmount(),
                tariffGrid.isHour(),
                tariffGrid.getHourPaymentAmount(),
                tariffGrid.isDay(),
                tariffGrid.getDayPaymentAmount(),
                OptionDTO.makeOptionDTO(
                        additionalService.getBonusTypes(),
                        Collections.singletonList(tariffGrid.getBonusType())
                ),
                tariffGrid.getBonusPercent(),
                tariffGrid.isActive()
        );
    }

    private List<TariffGridDTO> getTariffGridDTOList(List<TariffGrid> content) {
        List<BranchFeignDTO> branchFeignDTOList = feignService.safeGetBranchList();

        return content
                .stream()
                .map(
                        tariffGrid -> TariffGridDTO.fromTariffGrid(tariffGrid, branchFeignDTOList))
                .collect(Collectors.toList());
    }

    //TARIFFGRIDDTODAN TARIFFGRID YASAB BERADI
    private TariffGrid makeTariffGrid(TariffGrid tariffGrid, TariffGridDTO tariffGridDTO) {
        //TODO BRANCHNI BORIB TEKSHIRIB KELISH KERAK

//        if (notNull(tariffGridDTO.getCompanyId()))
        tariffGrid.setCompanyId(tariffGridDTO.getCompanyId());

//        if (notNull(tariffGridDTO.getBranchId()))
        tariffGrid.setBranchId(tariffGridDTO.getBranchId());

//        if (notNull(tariffGridDTO.getDepartmentId()))
        tariffGrid.setDepartment(departmentService.getDepartmentFromDB(tariffGridDTO.getDepartmentId(), true));

//        if (notNull(tariffGridDTO.getPositionId()))
        tariffGrid.setPosition(positionService.getPositionFromDB(tariffGridDTO.getPositionId(), true));

//        if (notNull(tariffGridDTO.getEmployeeCategoryId()))
        tariffGrid.setEmployeeCategory(employeeCategoryService.getEmployeeCategoryFromDB(tariffGridDTO.getEmployeeCategoryId(), true));

//        if (notNull(tariffGridDTO.getPaymentCriteriaType()))
        tariffGrid.setPaymentCriteriaType(tariffGridDTO.getPaymentCriteriaType());

        tariffGrid.setPaymentAmount(tariffGridDTO.getPaymentAmount());

        if (Boolean.TRUE.equals(tariffGridDTO.isHour()) && Objects.nonNull(tariffGridDTO.getHourPaymentAmount())) {
            tariffGrid.setHour(tariffGridDTO.isHour());
            tariffGrid.setHourPaymentAmount(tariffGridDTO.getHourPaymentAmount());
        } else {
            tariffGrid.setHour(false);
        }

        if (Boolean.TRUE.equals(tariffGridDTO.isDay()) && Objects.nonNull(tariffGridDTO.getDayPaymentAmount())) {
            tariffGrid.setDay(tariffGridDTO.isDay());
            tariffGrid.setDayPaymentAmount(tariffGridDTO.getDayPaymentAmount());
        } else {
            tariffGrid.setDay(false);
        }

        if (Objects.nonNull(tariffGridDTO.getBonusType()) && Objects.nonNull(tariffGridDTO.getBonusPercent())) {
            tariffGrid.setBonusType(tariffGridDTO.getBonusType());
            tariffGrid.setBonusPercent(tariffGridDTO.getBonusPercent());
        }


        if (Boolean.TRUE.equals(tariffGridDTO.isActive())) {
            tariffGrid.setActive(true);
        } else {
            tariffGrid.setDay(false);
        }
        return tariffGrid;
    }

    private TariffGrid getTariffGridFromDB(UUID id, boolean onlyActive) {
        if (Objects.isNull(id)) {
            throw RestException.restThrow(ResponseMessage.ERROR_TARIFF_GRID_NOT_FOUND);
        }

        Optional<TariffGrid> optionalTariffGrid = tariffGridRepository.findById(id);

        if (optionalTariffGrid.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_TARIFF_GRID_NOT_FOUND);
        }
        TariffGrid tariffGrid = optionalTariffGrid.get();

        if (onlyActive) {
            if (!tariffGrid.isActive()) {
                throw RestException.restThrow(ResponseMessage.ERROR_TARIFF_GRID_NOT_ACTIVE);
            }
        }
        return tariffGrid;
    }

    public void createHistory(TariffGrid tariffGrid, TariffGridDTO tariffGridDTO) {

        List<HistoryLog> historyLogList = new ArrayList<>();

        if (!Objects.equals(tariffGrid.getDepartment().getId(), tariffGridDTO.getDepartmentId())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_DEPARTMENT,
                            tariffGrid.getDepartment().getId().toString(),
                            tariffGridDTO.getDepartmentId().toString(),
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        if (!Objects.equals(tariffGrid.getPosition().getId(), tariffGridDTO.getPositionId())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_POSITION,
                            tariffGrid.getPosition().getId().toString(),
                            tariffGridDTO.getPositionId().toString(),
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        if (!Objects.equals(tariffGrid.getBranchId(), tariffGridDTO.getBranchId())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_BRANCH,
                            tariffGrid.getBranchId().toString(),
                            tariffGridDTO.getBranchId().toString(),
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        if (!Objects.equals(tariffGrid.getEmployeeCategory().getId(), tariffGridDTO.getEmployeeCategoryId())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_EMPLOYEE_CATEGORY,
                            tariffGrid.getEmployeeCategory().getId().toString(),
                            tariffGridDTO.getEmployeeCategoryId().toString(),
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }


        if (!Objects.equals(tariffGrid.getPaymentCriteriaType(), tariffGridDTO.getPaymentCriteriaType())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_PAYMENT_CRITERIA,
                            tariffGrid.getPaymentCriteriaType().toString(),
                            tariffGridDTO.getPaymentCriteriaType().toString(),
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        if (!Objects.equals(tariffGrid.getPaymentAmount(), tariffGridDTO.getPaymentAmount())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_PAYMENT_AMOUNT,
                            tariffGrid.getPaymentAmount().toString(),
                            tariffGridDTO.getPaymentAmount().toString(),
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        if (tariffGrid.isDay() != tariffGridDTO.isDay()) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_DAY,
                            tariffGrid.isDay() + "",
                            tariffGridDTO.isDay() + "",
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        if (tariffGrid.getDayPaymentAmount() != tariffGridDTO.getDayPaymentAmount()) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_DAY_AMOUNT,
                            tariffGrid.getDayPaymentAmount() + "",
                            tariffGridDTO.getDayPaymentAmount() + "",
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        if (tariffGrid.isHour() != tariffGridDTO.isHour()) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_HOUR,
                            tariffGrid.isHour() + "",
                            tariffGridDTO.isHour() + "",
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        if (tariffGrid.getHourPaymentAmount() != tariffGridDTO.getHourPaymentAmount()) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_HOUR_AMOUNT,
                            tariffGrid.getHourPaymentAmount() + "",
                            tariffGridDTO.getHourPaymentAmount() + "",
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        if (!Objects.equals(tariffGrid.getBonusType(), tariffGridDTO.getBonusType())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_BONUS_TYPE_AMOUNT,
                            tariffGrid.getBonusType().name(),
                            tariffGridDTO.getBonusType().name(),
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        if (!Objects.equals(tariffGrid.getBonusPercent(), tariffGridDTO.getBonusPercent())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_BONUS_PERCENT,
                            tariffGrid.getBonusPercent() + "",
                            tariffGridDTO.getBonusPercent() + "",
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        if (tariffGrid.isActive() != tariffGridDTO.isActive()) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TARIFF_GRID_ACTIVE,
                            tariffGrid.isActive() + "",
                            tariffGridDTO.isActive() + "",
                            EntityNameEnum.TARIFF_GRID
                    )
            );
        }

        historyLogRepository.saveAll(historyLogList);

    }
}
