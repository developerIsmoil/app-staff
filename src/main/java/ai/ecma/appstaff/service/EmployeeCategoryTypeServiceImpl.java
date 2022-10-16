package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.EmployeeCategoryType;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EmployeeCategoryTypeDTO;
import ai.ecma.appstaff.repository.EmployeeCategoryTypeRepository;

import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeCategoryTypeServiceImpl   implements EmployeeCategoryTypeService {


    private final EmployeeCategoryTypeRepository employeeCategoryTypeRepository;
    private final EmployeeService employeeService;
    private final EmployeeCategoryService employeeCategoryService;

    @Autowired
    public EmployeeCategoryTypeServiceImpl(
            EmployeeCategoryTypeRepository employeeCategoryTypeRepository,
            EmployeeService employeeService,
            @Lazy EmployeeCategoryService employeeCategoryService) {
        this.employeeCategoryTypeRepository = employeeCategoryTypeRepository;
        this.employeeService = employeeService;
        this.employeeCategoryService = employeeCategoryService;
    }

    /**
     * Hodim kategoriyasi turini qo'shish uchun
     *
     * @param employeeCategoryTypeDTO a
     * @return a
     */
    @Override
    public ApiResult<EmployeeCategoryTypeDTO> addEmployeeCategoryType(EmployeeCategoryTypeDTO employeeCategoryTypeDTO) {
        // log.info("class EmployeeCategoryTypeServiceImpl => addEmployeeCategoryType => method entered => DTO : {}", employeeCategoryTypeDTO);

        checkEmployeeCategoryTypeExist(employeeCategoryTypeDTO, Optional.empty());

        // HODIM KATEGORIYASI TURINI YASAB OLYABMIZ
        EmployeeCategoryType newEmployeeCategoryType = makeEmployeeCategoryType(new EmployeeCategoryType(), employeeCategoryTypeDTO);

        try {
            // HODIM KATEGORIYASI TURINI DATABASEGA SAQLAYAPMIZ
            employeeCategoryTypeRepository.save(newEmployeeCategoryType);
        } catch (Exception e) {
            // log.info("class EmployeeCategoryTypeServiceImpl => addEmployeeCategoryType => error saving employeeCategoryType");
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_TYPE_SAVING);
        }
        return ApiResult.successResponse(getEmployeeCategoryTypeDTO(newEmployeeCategoryType), ResponseMessage.SUCCESS_EMPLOYEE_CATEGORY_TYPE_SAVED);

    }

    /**
     * ID bo'yicha hodim kategoriyasi turini tahrirlash uchun
     *
     * @param id                      a
     * @param employeeCategoryTypeDTO a
     * @return a
     */
    @Override
    public ApiResult<EmployeeCategoryTypeDTO> editEmployeeCategoryType(UUID id, EmployeeCategoryTypeDTO employeeCategoryTypeDTO) {
        // log.info("class EmployeeCategoryTypeServiceImpl => editEmployeeCategoryType => method entered => ID : {} DTO : {}", id, employeeCategoryTypeDTO);

        checkEmployeeCategoryTypeExist(employeeCategoryTypeDTO, Optional.of(id));

        //ID BO'YICHA HODIM KATEGORIYASI TURINI DATABASEDAN OLYAPMIZ
        EmployeeCategoryType employeeCategoryTypeFromDB = getEmployeeCategoryTypeFromDB(id, false);

        // DTO DA KELGAN MA'LUMOTLAR BILAN HODIM KATEGORIYASI TURINI TAHRIRLAYAPMIZ
        EmployeeCategoryType employeeCategoryType = makeEmployeeCategoryType(employeeCategoryTypeFromDB, employeeCategoryTypeDTO);

        try {
            // HODIM KATEGORIYASI TURINI DATABASEGA SAQLAYAPMIZ
            employeeCategoryTypeRepository.save(employeeCategoryType);
        } catch (Exception e) {
            // log.info("class EmployeeCategoryTypeServiceImpl => editEmployeeCategoryType => error saving employeeCategoryType");
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_TYPE_EDITING);
        }
        return ApiResult.successResponse(getEmployeeCategoryTypeDTO(employeeCategoryType), ResponseMessage.SUCCESS_EMPLOYEE_CATEGORY_TYPE_EDITED);
    }

    /**
     * Tizimdagi hamma hodim kategoriyasi turini olish uchun
     *
     * @param page a
     * @param size a    a
     * @return a
     */
    @Override
    public ApiResult<List<EmployeeCategoryTypeDTO>> getAllEmployeeCategoryType(Integer page, Integer size) {
        // log.info("class EmployeeCategoryTypeServiceImpl => getAllEmployeeCategoryType => method entered => PAGE : {} SIZE : {} ", page, size);
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<EmployeeCategoryType> employeeCategoryTypeList = employeeCategoryTypeRepository.findAll(sortByColumn);
        return ApiResult.successResponse(getEmployeeCategoryTypeDTOList(employeeCategoryTypeList));

    }

    @Override
    public ApiResult<List<EmployeeCategoryTypeDTO>> getAllEmployeeCategoryTypeForSelect(Integer page, Integer size) {
        // log.info("class EmployeeCategoryTypeServiceImpl => getAllEmployeeCategoryTypeForSelect => method entered => PAGE : {} SIZE : {} ", page, size);
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<EmployeeCategoryType> employeeCategoryTypeList = employeeCategoryTypeRepository.findAllByActiveTrue(sortByColumn);
        return ApiResult.successResponse(getEmployeeCategoryTypeDTOList(employeeCategoryTypeList));

    }

    /**
     * ID bo'yicha bitta hodim kategoriyasi turini olish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<EmployeeCategoryTypeDTO> getOneEmployeeCategoryType(UUID id) {
        // log.info("class EmployeeCategoryTypeServiceImpl => getOneEmployeeCategoryType => method entered => ID : {} ", id);
        EmployeeCategoryType employeeCategoryTypeFromDB = getEmployeeCategoryTypeFromDB(id, false);

        // log.info("class EmployeeCategoryTypeServiceImpl => getOneEmployeeCategoryType => employeeCategoryType {} ", employeeCategoryTypeFromDB);
        return ApiResult.successResponse(getEmployeeCategoryTypeDTO(employeeCategoryTypeFromDB));

    }

    /**
     * Hodim kategoriyasi turini o'chirish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<?> deleteEmployeeCategoryType(UUID id) {
        // log.info("class EmployeeCategoryTypeServiceImpl => deleteEmployeeCategoryType => method entered => ID : {} ", id);

        checkCanDeleteEmployeeCategoryTypeOrThrow(id);

        try {
            employeeCategoryTypeRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_TYPE_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_EMPLOYEE_CATEGORY_TYPE_DELETED);
    }

    private void checkCanDeleteEmployeeCategoryTypeOrThrow(UUID id) {

        employeeCategoryService.existsByEmployeeCategoryTypeId(id);

        employeeService.existsByEmployeeCategoryTypeId(id);
    }

    @Override
    public ApiResult<?> deleteEmployeeCategoryTypeByIdList(List<UUID> id) {
        // log.info("class EmployeeCategoryTypeServiceImpl => deleteEmployeeCategoryTypeByIdList => method entered => ID : {} ", id);
        try {
            employeeCategoryTypeRepository.deleteAllById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_TYPE_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_EMPLOYEE_CATEGORY_TYPE_DELETED);
    }

    /**
     * Hodim kategoriyasi turini holatini o'zgartirish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<?> changeActiveEmployeeCategoryType(UUID id) {
        // log.info("class EmployeeCategoryTypeServiceImpl => changeActiveEmployeeCategoryType => method entered => ID : {} ", id);
        EmployeeCategoryType employeeCategoryTypeFromDB = getEmployeeCategoryTypeFromDB(id, false);

        boolean changedActive = !employeeCategoryTypeFromDB.isActive();
        employeeCategoryTypeFromDB.setActive(changedActive);

        // log.info("class EmployeeCategoryTypeServiceImpl => changeActiveEmployeeCategoryType => changed active => ACTIVE : {} ", changedActive);
        employeeCategoryTypeRepository.save(employeeCategoryTypeFromDB);
        return ApiResult.successResponse(ResponseMessage.SUCCESS_EMPLOYEE_CATEGORY_TYPE_STATUS_CHANGE);

    }

    @Override
    public List<EmployeeCategoryTypeDTO> getAllActiveEmployeeCategoryTypeFromDB() {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        List<EmployeeCategoryType> positionList = employeeCategoryTypeRepository.findAllByActiveTrue(sortByColumn);

        return positionList.stream().map(this::getEmployeeCategoryTypeFormDTO).collect(Collectors.toList());

    }

    /**
     * Hodim kategoriyasi turini ID bo'yicha databasedan olish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public EmployeeCategoryType getEmployeeCategoryTypeFromDB(UUID id, boolean onlyActive) {
        if (Objects.isNull(id)) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_TYPE_NOT_FOUND);
        }
        Optional<EmployeeCategoryType> optionalEmployeeCategoryType = employeeCategoryTypeRepository.findById(id);

        if (optionalEmployeeCategoryType.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_TYPE_NOT_FOUND);
        }

        EmployeeCategoryType employeeCategoryType = optionalEmployeeCategoryType.get();

        if (onlyActive) {
            if (!employeeCategoryType.isActive()) {
                throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_TYPE_NOT_ACTIVE);
            }
        }
        return employeeCategoryType;
    }

    /**
     * Hodim kategoriyasi turini LISTini DTO LISTiga o'girib olish uchun
     *
     * @param employeeCategoryTypeList a
     * @return a
     */
    private List<EmployeeCategoryTypeDTO> getEmployeeCategoryTypeDTOList(List<EmployeeCategoryType> employeeCategoryTypeList) {
        return employeeCategoryTypeList
                .stream()
                .map(this::getEmployeeCategoryTypeDTO)
                .collect(Collectors.toList());
    }

    private void checkEmployeeCategoryTypeExist(EmployeeCategoryTypeDTO employeeCategoryTypeDTO, Optional<UUID> optionalId) {

        boolean exists;

        if (optionalId.isEmpty()) {

            // BUNDAY HODIM KATEGORIYASI TURI AVVAL DATABASEDA QO'SHILGAN YOKI YOQ'LIGI TEKSHIRILGAN
            exists = employeeCategoryTypeRepository.existsByName(employeeCategoryTypeDTO.getName());

        } else {

            // O'ZGARTIRILAYOTGAN HODIM KATEGORIYASI TURI AVVAL DATABASEDA MAVJUD YOKI YO'QLIGINI TEKSHIRILGAN
            exists = employeeCategoryTypeRepository.existsByNameAndIdNot(employeeCategoryTypeDTO.getName(), optionalId.get());

        }

        // AGAR AVVAL QO'SHILGAN BO'LSA XATOLIK QAYTARAMIZ
        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYEE_CATEGORY_TYPE_ALREADY_EXIST);
        }

    }

    /**
     * Hodim kategoriyasi turini kelgan DTO bo'yicha yasab olish
     *
     * @param employeeCategoryType    a
     * @param employeeCategoryTypeDTO a
     * @return a
     */
    private EmployeeCategoryType makeEmployeeCategoryType(EmployeeCategoryType employeeCategoryType, EmployeeCategoryTypeDTO employeeCategoryTypeDTO) {

        if (Objects.nonNull(employeeCategoryTypeDTO.getName()))
            employeeCategoryType.setName(employeeCategoryTypeDTO.getName());

        if (Objects.nonNull(employeeCategoryTypeDTO.getActive()))
            employeeCategoryType.setActive(employeeCategoryTypeDTO.getActive());

        return employeeCategoryType;
    }

    /**
     * Hodim kategoriyasi turini DTO ga o'girib olish uchun
     *
     * @param employeeCategoryType a
     * @return a
     */
    private EmployeeCategoryTypeDTO getEmployeeCategoryTypeDTO(EmployeeCategoryType employeeCategoryType) {
        return new EmployeeCategoryTypeDTO(
                employeeCategoryType.getId(),
                employeeCategoryType.getName(),
                employeeCategoryType.isActive()
        );
    }

    /**
     * Hodim kategoriyasi turini DTO ga o'girib olish uchun
     *
     * @param employeeCategoryType a
     * @return a
     */
    private EmployeeCategoryTypeDTO getEmployeeCategoryTypeFormDTO(EmployeeCategoryType employeeCategoryType) {
        return new EmployeeCategoryTypeDTO(
                employeeCategoryType.getId(),
                employeeCategoryType.getName()
        );
    }

}
