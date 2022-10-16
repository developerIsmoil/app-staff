package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.Department;
import ai.ecma.appstaff.entity.HistoryLog;
import ai.ecma.appstaff.entity.Position;
import ai.ecma.appstaff.enums.EntityFieldNameEnum;
import ai.ecma.appstaff.enums.EntityNameEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.DepartmentDTO;
import ai.ecma.appstaff.payload.OptionDTO;
import ai.ecma.appstaff.payload.PositionDTO;
import ai.ecma.appstaff.payload.feign.CompanyFeignDTO;
import ai.ecma.appstaff.repository.HistoryLogRepository;
import ai.ecma.appstaff.repository.PositionRepository;

import ai.ecma.appstaff.service.feign.FeignService;
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
public class PositionServiceImpl implements PositionService {

    private final DepartmentService departmentService;
    private final PositionRepository positionRepository;
    private final HistoryLogRepository historyLogRepository;
    private final EmployeeCategoryService employeeCategoryService;
    private final EmployeeService employeeService;
    private final TariffGridService tariffGridService;
    private final FeignService feignService;

    @Autowired
    public PositionServiceImpl(
            @Lazy DepartmentService departmentService,
            PositionRepository positionRepository,
            HistoryLogRepository historyLogRepository,
            @Lazy EmployeeCategoryService employeeCategoryService,
            @Lazy EmployeeService employeeService,
            @Lazy TariffGridService tariffGridService, FeignService feignService) {
        this.departmentService = departmentService;
        this.positionRepository = positionRepository;
        this.historyLogRepository = historyLogRepository;
        this.employeeCategoryService = employeeCategoryService;
        this.employeeService = employeeService;
        this.tariffGridService = tariffGridService;
        this.feignService = feignService;
    }

    /**
     * Lavozim qo'shish uchun
     *
     * @param positionDTO a
     * @return a
     */
    @Override
    public ApiResult<PositionDTO> addPosition(PositionDTO positionDTO) {
        // log.info("class PositionServiceImpl => addPosition => method entered => DTO : {}", positionDTO);

        checkPositionExist(positionDTO, Optional.empty());

        // KELGAN DTO ICHIDAGI MA'LUMOTLARDAN FOYDALANIB LAVOZIM YASAB OLDIK
        Position newPosition = makePosition(new Position(), positionDTO);

        try {
            // LAVOZIMNI DATABASEGA SAQLAYMIZ
            positionRepository.save(newPosition);
        } catch (Exception e) {
            // log.info("class PositionServiceImpl => addPosition => error saving position");
            throw RestException.restThrow(ResponseMessage.ERROR_POSITION_SAVING);
        }
        return ApiResult.successResponse(PositionDTO.fromPosition(newPosition), ResponseMessage.SUCCESS_POSITION_SAVED);

    }

    /**
     * Biron bir ID ga tegishli lavozimni tahrirlash uchun
     *
     * @param id          a
     * @param positionDTO a a
     * @return a
     */
    @Override
    public ApiResult<PositionDTO> editPosition(UUID id, PositionDTO positionDTO) {
        // log.info("class PositionServiceImpl => editPosition => method entered => ID : {} DTO : {}", id, positionDTO);

        Position positionFromDB = getPositionFromDB(id, false);

        checkPositionExist(positionDTO, Optional.of(id));

        // createHistory(positionFromDB, positionDTO);

        Position position = makePosition(positionFromDB, positionDTO);

        try {
            positionRepository.save(position);
        } catch (Exception e) {
            // log.info("class PositionServiceImpl => editPosition => error saving position");
            throw RestException.restThrow(ResponseMessage.ERROR_POSITION_EDITING);
        }
        return ApiResult.successResponse(PositionDTO.fromPosition(position), ResponseMessage.SUCCESS_POSITION_EDITED);

    }

    /**
     * Tizimda mavjud barcha lavozimlarni olish uchun
     *
     * @param page a
     * @param size a a
     * @return a
     */
    @Override
    public ApiResult<List<PositionDTO>> getAllPosition(Integer page, Integer size) {

        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        // log.info("class PositionServiceImpl => getAllPosition => method entered => PAGE : {} SIZE : {} ", page, size);
        List<Position> positionList = positionRepository.findAll(sortByColumn);

        List<PositionDTO> positionDTOList = getPositionDTOList(positionList);

        List<CompanyFeignDTO> companyList = feignService.getCompanyList();

        for (CompanyFeignDTO companyFeignDTO : companyList) {
            for (PositionDTO positionDTO : positionDTOList) {

                if (Objects.equals(positionDTO.getCompanyId(), companyFeignDTO.getId())) {
                    positionDTO.setCompanyName(companyFeignDTO.getName());
                }
            }
        }

        return ApiResult.successResponse(positionDTOList);
    }

    @Override
    public ApiResult<List<PositionDTO>> getAllPositionForSelect(Integer page, Integer size) {
        // log.info("class PositionServiceImpl => getAllPositionForSelect => method entered => PAGE : {} SIZE : {} ", page, size);
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<Position> positionList = positionRepository.findAllByActiveTrue(sortByColumn);
        return ApiResult.successResponse(getPositionDTOListForSelect(positionList));
    }

    /**
     * ID bo'yicha bitta lavozimni olish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<PositionDTO> getOnePosition(UUID id) {
        // log.info("class PositionServiceImpl => getOnePosition => method entered => ID : {} ", id);
        Position positionFromDB = getPositionFromDB(id, false);
        return ApiResult.successResponse(getPositionFormDTO(positionFromDB));
    }

    /**
     * Id bo'yicha lavozimni o'chirish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<?> deletePosition(UUID id) {
        // log.info("class PositionServiceImpl => deletePosition => method entered => ID : {} ", id);

        checkCanDeletePositionOrThrow(id);

        try {
            positionRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_POSITION_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_POSITION_DELETED);
    }

    private void checkCanDeletePositionOrThrow(UUID id) {

        employeeCategoryService.existsByPositionId(id);

        employeeService.existsByPositionId(id);

        tariffGridService.existsByPositionId(id);
    }

    @Override
    public ApiResult<?> deletePositionByIdList(List<UUID> id) {
        // log.info("class PositionServiceImpl => deletePositionByIdList => method entered => ID : {} ", id);
        try {
            positionRepository.deleteAllById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_POSITION_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_POSITION_DELETED);
    }

    /**
     * Lavozim holatini o'zgartieish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<?> changeActivePosition(UUID id) {
        // log.info("class PositionServiceImpl => changeActivePosition => method entered => ID : {} ", id);

        Position positionFromDB = getPositionFromDB(id, false);

        boolean changedActive = !positionFromDB.isActive();
        positionFromDB.setActive(changedActive);

        // log.info("class PositionServiceImpl => changeActivePosition => changed active => ACTIVE : {} ", changedActive);
        positionRepository.save(positionFromDB);
        return ApiResult.successResponse(ResponseMessage.SUCCESS_POSITION_STATUS_CHANGE);
    }

    @Override
    public List<PositionDTO> getAllActivePositionFromDB() {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<Position> positionList = positionRepository.findAllByActiveTrue(sortByColumn);
        return positionList
                .stream()
                .map(PositionDTO::fromPositionForSelect)
                .collect(Collectors.toList());
    }

    @Override
    public ApiResult<PositionDTO> getFormPosition() {

        PositionDTO positionDTO = new PositionDTO(
                OptionDTO.makeOptionDTO(
                        departmentService.getAllActiveDepartmentFromDB()
                )
        );

        return ApiResult.successResponse(positionDTO);
    }

    /**
     * Databasedan ID bo'yicha lavozimni olish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public Position getPositionFromDB(UUID id, boolean onlyActive) {
        if (Objects.isNull(id)) {
            throw RestException.restThrow(ResponseMessage.ERROR_POSITION_NOT_FOUND);
        }
        Optional<Position> optionalPosition = positionRepository.findById(id);

        if (optionalPosition.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_POSITION_NOT_FOUND);
        }
        Position position = optionalPosition.get();

        if (onlyActive) {
            if (!position.isActive()) {
                throw RestException.restThrow(ResponseMessage.ERROR_POSITION_NOT_ACTIVE);
            }
        }
        return position;
    }

    @Override
    public void existsAllByDepartmentId(List<UUID> departmentIdList) {

        boolean exists = positionRepository.existsAllByDepartmentIdIn(departmentIdList);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_DEPARTMENT_CONNECT_POSITION);
        }

    }

    @Override
    public void existsByDepartmentId(UUID departmentId) {
        boolean exists = positionRepository.existsByDepartmentId(departmentId);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_DEPARTMENT_CONNECT_POSITION);
        }
    }

    private void checkPositionExist(PositionDTO positionDTO, Optional<UUID> optionalId) {

        boolean exists;

        if (optionalId.isEmpty()) {

            // MA'LUM BIR DEPARTMENTGA AVVAL SHU NOMLI LAVOZIM QO'SHILGAN YOKI YO'QLIGINI TEKSHIRISH
            exists = positionRepository.existsByNameAndDepartmentId(positionDTO.getName(), positionDTO.getDepartmentId());

        } else {

            exists = positionRepository.existsByNameAndDepartmentIdAndIdNot(positionDTO.getName(), positionDTO.getDepartmentId(), optionalId.get());

        }

        // AGAR AVVAL BU NOMI LAVOZIM AYNAN SHU DEPARTMENTGA QO'SHILGAN BO'LSA XATOLIK QAYTADI
        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_POSITION_ALREADY_EXIST);
        }

    }


    /**
     * DTO dan kelgan ma'lumotlar orqali lavozim yasab olish uchun
     *
     * @param position    a
     * @param positionDTO a a
     * @return a
     */
    private Position makePosition(Position position, PositionDTO positionDTO) {

        if (Objects.nonNull(positionDTO.getDepartmentId())) {
            Department department = departmentService.getDepartmentFromDB(positionDTO.getDepartmentId(), true);
            position.setDepartment(department);
        }


        position.setName(positionDTO.getName());

        position.setActive(positionDTO.getActive());
        position.setManageTimesheet(positionDTO.getManageTimesheet());

        return position;
    }

    /**
     * lavozimni DTO ko'rinishiga o'tkazish uchun
     *
     * @param position a
     * @return a
     */
    private PositionDTO getPositionFormDTO(Position position) {

        List<DepartmentDTO> departmentDTOList = departmentService.getAllActiveDepartmentFromDB();

        return new PositionDTO(
                position.getId(),
                position.getName(),
                position.isActive(),
                OptionDTO.makeOptionDTO(
                        departmentDTOList,
                        Collections.singletonList(position.getDepartment().getId())
                ),
                position.getManageTimesheet()
        );

    }


    /**
     * Lavozimlar ro'yxatini DTO lar ro'yxatiga o'girish uchun
     *
     * @param positionList a
     * @return a
     */
    private List<PositionDTO> getPositionDTOList(List<Position> positionList) {
        return positionList
                .stream()
                .map(PositionDTO::fromPosition)
                .collect(Collectors.toList());
    }

    /**
     * Lavozimlar ro'yxatini DTO lar ro'yxatiga o'girish uchun
     *
     * @param positionList a
     * @return a
     */
    private List<PositionDTO> getPositionDTOListForSelect(List<Position> positionList) {
        return positionList
                .stream()
                .map(PositionDTO::fromPositionForSelect)
                .collect(Collectors.toList());
    }

    private void createHistory(Position position, PositionDTO positionDTO) {
        List<HistoryLog> historyLogList = new ArrayList<>();

        if (!Objects.equals(position.getDepartment().getId(), positionDTO.getDepartmentId())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.POSITION_DEPARTMENT,
                            position.getDepartment().getId().toString(),
                            positionDTO.getDepartmentId().toString(),
                            EntityNameEnum.POSITION
                    )
            );
        }


        if (!Objects.equals(position.getName(), positionDTO.getName())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.POSITION_NAME,
                            position.getName(),
                            positionDTO.getName(),
                            EntityNameEnum.POSITION
                    )
            );
        }

        if (position.isActive() != positionDTO.getActive()) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.POSITION_ACTIVE,
                            position.isActive() + "",
                            positionDTO.getActive() + "",
                            EntityNameEnum.POSITION
                    )
            );
        }


        historyLogRepository.saveAll(historyLogList);
    }
}
