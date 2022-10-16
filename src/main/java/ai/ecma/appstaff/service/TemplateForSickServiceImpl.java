package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.HistoryLog;
import ai.ecma.appstaff.entity.TemplateForSick;
import ai.ecma.appstaff.enums.EntityFieldNameEnum;
import ai.ecma.appstaff.enums.EntityNameEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.OptionDTO;
import ai.ecma.appstaff.payload.PrivilegeTypeDTO;
import ai.ecma.appstaff.payload.TemplateForSickDTO;
import ai.ecma.appstaff.repository.HistoryLogRepository;
import ai.ecma.appstaff.repository.TemplateForSickRepository;

import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TemplateForSickServiceImpl   implements TemplateForSickService {

    private final PrivilegeTypeService privilegeTypeService;
    private final TemplateForSickRepository templateForSickRepository;
    private final HistoryLogRepository historyLogRepository;

    @Autowired
    public TemplateForSickServiceImpl(
            @Lazy PrivilegeTypeService privilegeTypeService,
            TemplateForSickRepository templateForSickRepository,
            HistoryLogRepository historyLogRepository) {
        this.privilegeTypeService = privilegeTypeService;
        this.templateForSickRepository = templateForSickRepository;
        this.historyLogRepository = historyLogRepository;
    }


    /**
     * TemplateForSick qo'shish
     *
     * @param templateForSickDTO a
     * @return a
     */
    @Override
    public ApiResult<TemplateForSickDTO> addTemplateForSick(TemplateForSickDTO templateForSickDTO) {
        // log.info("class TemplateForSickServiceImpl => addTemplateForSick => method entered => DTO : {}", templateForSickDTO);

        TemplateForSick templateForSick = new TemplateForSick();

        TemplateForSick newTemplateForSick = makeTemplateForSick(templateForSick, templateForSickDTO);
        // AGAR privilege = TRUE BO'LSA UNDA FAQAT FOIZ BILAN IMTIYOZ TURI BO'LADI. ISH STAJI UCHUN FROM TO BO'LMAYDI
        try {
            // YANGI BO'LIMNI DATABASEGA SAQLAYAPMIZ
            templateForSickRepository.save(newTemplateForSick);
        } catch (Exception e) {
            // log.info("class TemplateForSickServiceImpl => addTemplateForSick => error saving templateForSick");
            throw RestException.restThrow(ResponseMessage.ERROR_TEMPLATE_FOR_SICK_SAVING);
        }
        return ApiResult.successResponse(
                TemplateForSickDTO.fromTemplateForSick(newTemplateForSick),
                ResponseMessage.SUCCESS_TEMPLATE_FOR_SICK_SAVED
        );

    }

    /**
     * TemplateForSickni ID bo'yicha tahrirlash
     *
     * @param id                 a
     * @param templateForSickDTO a a
     * @return a
     */
    @Override
    public ApiResult<TemplateForSickDTO> editTemplateForSick(UUID id, TemplateForSickDTO templateForSickDTO) {
        // log.info("class TemplateForSickServiceImpl => editTemplateForSick => method entered => ID : {} DTO : {}", id, templateForSickDTO);

        // ID BO'YICHA DATABASEDAN TEMPLATE.FOR.SICKNI OLAMIZ
        TemplateForSick templateForSickFromDB = getTemplateForSickFromDB(id, false);

        // todo NULL ga tushib ketobdi tekshirib chiqish kerak
        // createHistory(templateForSickFromDB, templateForSickDTO);

        // DTO DA KELGAN MA'LUMOTLARNI DATABASEDAN OLGAN OBJECTIMIZGA SET QILAMIZ
        TemplateForSick templateForSick = makeTemplateForSick(templateForSickFromDB, templateForSickDTO);

        try {
            // TEMPLATE.FOR.SICKNI DATABASEGA SAQLAYMIZ
            templateForSickRepository.save(templateForSick);
        } catch (Exception e) {
            // log.info("class TemplateForSickServiceImpl => editTemplateForSick => error saving templateForSick");
            throw RestException.restThrow(ResponseMessage.ERROR_TEMPLATE_FOR_SICK_EDITING);
        }
        return ApiResult.successResponse(TemplateForSickDTO.fromTemplateForSick(templateForSick), ResponseMessage.SUCCESS_TEMPLATE_FOR_SICK_EDITED);
    }

    /**
     * Tizimdagi barcha templateForSicklarni olish uchun
     *
     * @param page a
     * @param size a a
     * @return a
     */
    @Override
    public ApiResult<?> getAllTemplateForSick(Integer page, Integer size) {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        // log.info("class TemplateForSickServiceImpl => getAllTemplateForSick => method entered => PAGE : {} SIZE : {} ", page, size);
        List<TemplateForSick> templateForSickList = templateForSickRepository.findAll(sortByColumn);
        return ApiResult.successResponse(getTemplateForSickDTOList(templateForSickList));
    }

    @Override
    public ApiResult<?> getAllTemplateForSickForSelect(Integer page, Integer size) {
        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        // log.info("class TemplateForSickServiceImpl => getAllTemplateForSickForSelect => method entered => PAGE : {} SIZE : {} ", page, size);
        List<TemplateForSick> templateForSickList = templateForSickRepository.findAllByActiveTrue(sortByColumn);
        return ApiResult.successResponse(getTemplateForSickDTOList(templateForSickList));
    }

    /**
     * ID bo'yicha bitta templateForSickni olish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<TemplateForSickDTO> getOneTemplateForSick(UUID id) {
        // log.info("class TemplateForSickServiceImpl => getOneTemplateForSick => method entered => ID : {} ", id);

        // DATABASEDAN BERILGAN ID GA TENG BO'LGAN TEMPLATE.FOR.SICKNI OLADI.
        //AGAR TOPA OLMASA XATOLIKKA TUSHADI
        TemplateForSick templateForSickFromDB = getTemplateForSickFromDB(id, false);
        return ApiResult.successResponse(getFormTemplateForSickDTO(templateForSickFromDB));

    }

    /**
     * ID bo'yicha templateForSickni o'chirish
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<?> deleteTemplateForSick(UUID id) {
        // log.info("class TemplateForSickServiceImpl => deleteTemplateForSick => method entered => ID : {} ", id);
        try {
            templateForSickRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_TEMPLATE_FOR_SICK_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_TEMPLATE_FOR_SICK_DELETED);
    }

    @Override
    public ApiResult<?> deleteTemplateForSickByIdList(List<UUID> id) {
        // log.info("class TemplateForSickServiceImpl => deleteTemplateForSickByIdList => method entered => ID : {} ", id);
        try {
            templateForSickRepository.deleteAllById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_TEMPLATE_FOR_SICK_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_TEMPLATE_FOR_SICK_DELETED);
    }

    /**
     * TemplateForSick holatini o'zgartirish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<?> changeActiveTemplateForSick(UUID id) {
        // log.info("class TemplateForSickServiceImpl => changeActiveTemplateForSick => method entered => ID : {} ", id);

        // DATABASEDAN BERILGAN ID GA TENG BO'LGAN TEMPLATE.FOR.SICKNI OLADI.
        //AGAR TOPA OLMASA XATOLIKKA TUSHADI
        TemplateForSick templateForSickFromDB = getTemplateForSickFromDB(id, false);

        // DATABASEDAN OLINGAN TEMPLATE.FOR.SICKNI HOLATINI TESKARI KO'RINISHGA O'TKAZISH (TRUE => FALSE) (FALSE => TRUE)
        boolean changedActive = !templateForSickFromDB.isActive();
        templateForSickFromDB.setActive(changedActive);

        // HOLATI O'ZGARGAN TEMPLATE.FOR.SICKNI DATABASEGA SAQLASH
        // log.info("class TemplateForSickServiceImpl => changeActiveTemplateForSick => changed active => ACTIVE : {} ", changedActive);
        templateForSickRepository.save(templateForSickFromDB);
        return ApiResult.successResponse(ResponseMessage.SUCCESS_TEMPLATE_FOR_SICK_STATUS_CHANGE);

    }

    @Override
    public ApiResult<?> getFormTemplateForSick() {

        List<PrivilegeTypeDTO> allActivePrivilegeTypeFromDB = privilegeTypeService.getAllActive();

        TemplateForSickDTO templateForSickDTO = new TemplateForSickDTO(
                OptionDTO.makeOptionDTO(
                        allActivePrivilegeTypeFromDB
                )
        );

        return ApiResult.successResponse(templateForSickDTO);
    }

    @Override
    public void existsByEmployeePrivilegeTypeId(UUID id) {
        boolean exists = templateForSickRepository.existsByPrivilegeTypeId(id);

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_CAN_NOT_DELETE_PRIVILEGE_TYPE_CONNECT_TEMPLATE_FOR_SICK);
        }
    }

    /**
     * TemplateForSickDto orqali TemplateForSick yaratib olish uchun
     *
     * @param templateForSick    a
     * @param templateForSickDTO a a
     * @return a
     */
    private TemplateForSick makeTemplateForSick(TemplateForSick templateForSick, TemplateForSickDTO templateForSickDTO) {

        if (templateForSickDTO.isPrivilege()) {

            if (Boolean.FALSE.equals(templateForSick.isPrivilege())) {
                templateForSick.setFromCount(null);
                templateForSick.setToCount(null);
                templateForSick.setPercent(null);
            }

            UUID privilegeTypeId = templateForSickDTO.getPrivilegeTypeId();
            Assert.notNull(privilegeTypeId, ResponseMessage.ERROR_TEMPLATE_FOR_SICK_PRIVILEGE_TYPE_ID_DOES_NOT_NULL);

            Double percent = templateForSickDTO.getPercent();

            boolean exists = templateForSickRepository.existsByPrivilegeType_IdAndPercentAndPrivilegeTrue(
                    privilegeTypeId, percent
            );

            if (exists) {
                throw RestException.restThrow(ResponseMessage.ERROR_TEMPLATE_FOR_SICK_ALREADY_EXIST);
            }

            templateForSick.setPrivilegeType(privilegeTypeService.getByIdAndActive(privilegeTypeId, true));
            templateForSick.setPercent(percent);


            // AGAR privilege = FALSE BO'LSA UNDA FAQAT ISH STAJI UCHUN FROM, TO VA FOIZ BO'LADI
        } else {

            if (Boolean.TRUE.equals(templateForSick.isPrivilege())) {
                templateForSick.setPrivilegeType(null);
                templateForSick.setPercent(null);
            }

            Integer fromCount = templateForSickDTO.getFromCount();
            Integer toCount = templateForSickDTO.getToCount();
            Double percent = templateForSickDTO.getPercent();

            Assert.notNull(fromCount, ResponseMessage.ERROR_TEMPLATE_FOR_SICK_FROM_COUNT_DOES_NOT_NULL);
            Assert.notNull(toCount, ResponseMessage.ERROR_TEMPLATE_FOR_SICK_TO_COUNT_DOES_NOT_NULL);

            if (fromCount >= toCount) {
                throw RestException.restThrow(ResponseMessage.ERROR_TEMPLATE_FOR_SICK_INVALID_DURATION);
            }

            boolean exists = templateForSickRepository.existsByFromCountAndToCountAndPercentAndPrivilegeFalse(
                    fromCount, toCount, percent
            );

            if (exists) {
                throw RestException.restThrow(ResponseMessage.ERROR_TEMPLATE_FOR_SICK_ALREADY_EXIST);
            }

            templateForSick.setFromCount(fromCount);
            templateForSick.setToCount(toCount);
            templateForSick.setPercent(percent);

        }
        templateForSick.setPrivilege(templateForSickDTO.isPrivilege());
        templateForSick.setActive(templateForSickDTO.isActive());

        return templateForSick;
    }

    /**
     * TemplateForSickni ID orqali databsedan olish uchun.
     * Databasedan templateForSickni ololmasa xatolikka tushadi
     *
     * @param id a
     * @return a
     */
    private TemplateForSick getTemplateForSickFromDB(UUID id, boolean onlyActive) {
        // AGAR ID NULL BO'LSA XATOLIK QAYTARGANMIZ
        if (Objects.isNull(id)) {
            throw RestException.restThrow(ResponseMessage.ERROR_TEMPLATE_FOR_SICK_NOT_FOUND);
        }
        Optional<TemplateForSick> optionalTemplateForSick = templateForSickRepository.findById(id);

        // SHU ID LIK TEMPLATE.FOR.SICK MAVJUD BO'LSA UNI RETURN QILGANMIZ. AKS HOLDA XATOLIK QAYTARGANMIZ
        if (optionalTemplateForSick.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_TEMPLATE_FOR_SICK_NOT_FOUND);
        }

        TemplateForSick templateForSick = optionalTemplateForSick.get();

        if (onlyActive) {
            if (!templateForSick.isActive()) {
                throw RestException.restThrow(ResponseMessage.ERROR_TEMPLATE_FOR_SICK_NOT_ACTIVE);
            }
        }
        return templateForSick;
    }


    /**
     * TemplateForSick dan TemplateForSickDto olish uchun
     *
     * @param templateForSick a
     * @return a
     */
    private TemplateForSickDTO getFormTemplateForSickDTO(TemplateForSick templateForSick) {

        List<PrivilegeTypeDTO> allActivePrivilegeTypeFromDB = privilegeTypeService.getAllActive();

        OptionDTO<PrivilegeTypeDTO> optionDTO = null;

        if (templateForSick.isPrivilege()) {
            optionDTO = OptionDTO.makeOptionDTO(
                    allActivePrivilegeTypeFromDB,
                    Collections.singletonList(templateForSick.getPrivilegeType().getId())
            );
        }

        return new TemplateForSickDTO(
                templateForSick.getId(),
                templateForSick.isPrivilege(),
                optionDTO,
                templateForSick.getFromCount(),
                templateForSick.getToCount(),
                templateForSick.getPercent(),
                templateForSick.isActive()
        );
    }

    /**
     * TemplateForSicklar LISTidan TemplateForSickDto lar LISTini olish uchun
     *
     * @param templateForSickList a
     * @return a
     */
    private List<TemplateForSickDTO> getTemplateForSickDTOList(List<TemplateForSick> templateForSickList) {
        return templateForSickList
                .stream()
                .map(TemplateForSickDTO::fromTemplateForSick)
                .collect(Collectors.toList());
    }


    // todo NULL ga tushib ketobdi tekshirib chiqish kerak
    private void createHistory(TemplateForSick templateForSick, TemplateForSickDTO templateForSickDTO) {

        List<HistoryLog> historyLogList = new ArrayList<>();

        if (templateForSick.isPrivilege() != templateForSickDTO.isPrivilege()) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TEMPLATE_FOR_SICK_PRIVILEGE,
                            templateForSick.isPrivilege() + "",
                            templateForSickDTO.isPrivilege() + "",
                            EntityNameEnum.TEMPLATE_FOR_SICK
                    )
            );
        }

        if ((Objects.nonNull(templateForSick.getPrivilegeType()) &&
                Objects.nonNull(templateForSickDTO.getPrivilegeTypeId())) ||

                Objects.nonNull(templateForSick.getPrivilegeType()) &&
                        !templateForSick.getPrivilegeType().getId().equals(templateForSickDTO.getPrivilegeTypeId())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TEMPLATE_FOR_SICK_PRIVILEGE_TYPE,
                            templateForSick.getPrivilegeType().getId().toString(),
                            templateForSickDTO.getPrivilegeTypeId() != null ? templateForSickDTO.getPrivilegeTypeId().toString() : null,
                            EntityNameEnum.TEMPLATE_FOR_SICK
                    )
            );
        }
        // entity - null, dto - null
        if ((Objects.nonNull(templateForSick.getFromCount()) &&
                Objects.nonNull(templateForSickDTO.getFromCount())) ||

                (Objects.nonNull(templateForSick.getFromCount()) &&
                        !templateForSick.getFromCount().equals(templateForSickDTO.getFromCount()))) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TEMPLATE_FOR_SICK_FROM_COUNT,
                            templateForSick.getFromCount().toString(),
                            Objects.nonNull(templateForSickDTO.getFromCount()) ? templateForSickDTO.getFromCount().toString() : null,
                            EntityNameEnum.TEMPLATE_FOR_SICK
                    )
            );
        }

        if ((Objects.nonNull(templateForSick.getToCount()) &&
                Objects.nonNull(templateForSickDTO.getToCount())) ||

                Objects.nonNull(templateForSick.getToCount()) &&
                        !templateForSick.getToCount().equals(templateForSickDTO.getToCount())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TEMPLATE_FOR_SICK_TO_COUNT,
                            templateForSick.getToCount().toString(),
                            Objects.nonNull(templateForSickDTO.getToCount()) ? templateForSickDTO.getToCount().toString() : null,
                            EntityNameEnum.TEMPLATE_FOR_SICK
                    )
            );
        }

        if (Objects.nonNull(templateForSick.getPercent()) &&
                !templateForSick.getPercent().equals(templateForSickDTO.getPercent())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TEMPLATE_FOR_SICK_PERCENT,
                            templateForSick.getPercent().toString(),
                            Objects.nonNull(templateForSickDTO.getPercent()) ? templateForSickDTO.getPercent().toString() : null,
                            EntityNameEnum.TEMPLATE_FOR_SICK
                    )
            );
        }

        if (templateForSick.isActive() != templateForSickDTO.isActive()) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.TEMPLATE_FOR_SICK_ACTIVE,
                            templateForSick.isActive() + "",
                            templateForSickDTO.isActive() + "",
                            EntityNameEnum.TEMPLATE_FOR_SICK
                    )
            );
        }

        historyLogRepository.saveAll(historyLogList);
    }
}
