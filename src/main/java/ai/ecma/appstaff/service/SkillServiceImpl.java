package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.HistoryLog;
import ai.ecma.appstaff.entity.Skill;
import ai.ecma.appstaff.enums.EntityFieldNameEnum;
import ai.ecma.appstaff.enums.EntityNameEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.SkillDTO;
import ai.ecma.appstaff.repository.HistoryLogRepository;
import ai.ecma.appstaff.repository.SkillRepository;

import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillServiceImpl   implements SkillService {

    private final SkillRepository skillRepository;
    private final HistoryLogRepository historyLogRepository;


    /**
     * Skill qo'shish
     *
     * @param skillDTO a
     * @return a
     */
    @Override
    public ApiResult<?> addSkill(SkillDTO skillDTO) {
        // log.info("class SkillServiceImpl => addSkill => method entered => DTO : {}", skillDTO);

//        checkStrIsEmptyOrNull(skillDTO.getName(), ResponseMessage.REQUIRED_SKILL_NAME);
//        checkStrIsEmptyOrNull(skillDTO.getColorCode(), ResponseMessage.REQUIRED_COLOR_CODE);

        checkSkillExist(skillDTO, Optional.empty());

        // SKILL YARATIB OLISH
        Skill newSkill = makeSkill(new Skill(), skillDTO);

        try {
            // YANGI BO'LIMNI DATABASEGA SAQLAYAPMIZ
            skillRepository.save(newSkill);
        } catch (Exception e) {
            // log.info("class SkillServiceImpl => addSkill => error saving skill");
            throw RestException.restThrow(ResponseMessage.ERROR_SKILL_SAVING);
        }
        return ApiResult.successResponse(getAllSkillFromDB());

    }

    /**
     * Skillni ID bo'yicha tahrirlash
     *
     * @param id       a
     * @param skillDTO a
     * @return a
     */
    @Override
    public ApiResult<?> editSkill(UUID id, SkillDTO skillDTO) {
        // log.info("class SkillServiceImpl => editSkill => method entered => ID : {} DTO : {}", id, skillDTO);

//        checkStrIsEmptyOrNull(skillDTO.getName(), ResponseMessage.REQUIRED_SKILL_NAME);
//        checkStrIsEmptyOrNull(skillDTO.getColorCode(), ResponseMessage.REQUIRED_COLOR_CODE);

        checkSkillExist(skillDTO, Optional.of(id));

        // ID BO'YICHA DATABASEDAN SKILL OLAMIZ
        Skill skillFromDB = getSkillFromDB(id);

        // createHistory(skillFromDB, skillDTO);

        // DTO DA KELGAN MA'LUMOTLARNI DATABASEDAN OLGAN SKILLIMIZGA SET QILAMIZ
        Skill skill = makeSkill(skillFromDB, skillDTO);

        try {
            // SKILL DATABASEGA SAQLAYMIZ
            skillRepository.save(skill);
        } catch (Exception e) {
            // log.info("class SkillServiceImpl => editSkill => error saving skill");
            throw RestException.restThrow(ResponseMessage.ERROR_SKILL_EDITING);
        }

        return ApiResult.successResponse(getAllSkillFromDB());

    }

    /**
     * Tizimdagi barcha skilllarni olish uchun
     * <p>
     * //     * @param page a
     * //     * @param size a
     *
     * @return a
     */
    @Override
    public ApiResult<?> getAllSkill() {
        return ApiResult.successResponse(getAllSkillFromDB());
    }

    /**
     * ID bo'yicha bitta skillni olish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<?> getOneSkill(UUID id) {
        // log.info("class SkillServiceImpl => getOneSkill => method entered => ID : {} ", id);

        // DATABASEDAN BERILGAN ID GA TENG BO'LGAN SKILL OLADI.
        //AGAR TOPA OLMASA XATOLIKKA TUSHADI
        Skill skillFromDB = getSkillFromDB(id);

        return ApiResult.successResponse(SkillDTO.fromSkill(skillFromDB));

    }

    /**
     * ID bo'yicha skillni o'chirish
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<?> deleteSkill(UUID id) {
        // log.info("class SkillServiceImpl => deleteSkill => method entered => ID : {} ", id);

//        if (checkCanDeleteSkill(id)) {
        try {
            skillRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_SKILL_NOT_FOUND);
        }
        return ApiResult.successResponse(getAllSkillFromDB());
//        } else {
//            throw RestException.restThrow("ERROR.SKILL.CAN.NOT.DELETE");
//        }

    }

    @Override
    public ApiResult<?> deleteSkillByIdList(List<UUID> id) {
        // log.info("class SkillServiceImpl => deleteSkillByIdList => method entered => ID : {} ", id);
//        if (checkCanDeleteSkillList(id)) {
        try {
            skillRepository.deleteAllById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_SKILL_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_SKILL_DELETED);
//        } else {
//            throw RestException.restThrow("ERROR.SKILL.CAN.NOT.DELETE");
//        }

    }

    /**
     * Skilllar LISTidan SkillDto lar LISTini olish uchun
     *
     * @param skillList a
     * @return a
     */
    @Override
    public List<SkillDTO> getSkillDTOList(Collection<Skill> skillList) {
        return skillList
                .stream()
                .map(SkillDTO::fromSkill)
                .collect(Collectors.toList());
    }

    @Override
    public ApiResult<?> crudSkill(SkillDTO skillDTO) {

        switch (skillDTO.getMethod()) {

            case CREATE:
                return addSkill(skillDTO);

            case EDIT:
                return editSkill(skillDTO.getId(), skillDTO);

            case DELETE:
                return deleteSkill(skillDTO.getId());

            default:
                throw RestException.restThrow(ResponseMessage.ERROR_INVALID_ACTION_TYPE);
        }
    }

    @Override
    public List<SkillDTO> getAllSkillFromDB() {

        Sort sortByColumn = CommonUtils.sortByColumn("createdAt", Sort.Direction.DESC);
        return skillRepository.findAll(sortByColumn)
                .stream()
                .map(SkillDTO::fromSkill)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Skill> getSkillListFromDB(List<UUID> idList) {
        if (idList.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_SKILL_NOT_FOUND);
        }
        Set<Skill> skillSet = (skillRepository.findAllByIdIn(idList));

        if (!Objects.equals(idList.size(), skillSet.size()))
            throw RestException.restThrow(ResponseMessage.ERROR_SKILL_NOT_FOUND);

        return skillSet;
    }

    private void checkSkillExist(SkillDTO skillDTO, Optional<UUID> optionalId) {

        boolean exists;

        if (optionalId.isEmpty()) {
            //SHU NOMLI BO'LIM DATABASEDA BOR YOKI YO'QLIGI TEKSHIRILGAN
            exists = skillRepository.existsByName(skillDTO.getName());
        } else {
            //SHU NOMLI VA BERILGAN ID DAN BOSHQA BO'LIM DATABASEDA BOR YOKI YO'QLIGI TEKSHIRILGAN
            exists = skillRepository.existsByNameAndIdNot(skillDTO.getName(), optionalId.get());
        }

        if (exists) {
            throw RestException.restThrow(ResponseMessage.ERROR_SKILL_ALREADY_EXIST);
        }

    }

    /**
     * SkillDto orqali Skill yaratib olish uchun
     *
     * @param skill    a
     * @param skillDTO a
     * @return a
     */
    private Skill makeSkill(Skill skill, SkillDTO skillDTO) {

        skill.setName(skillDTO.getName());
        skill.setColor(skillDTO.getColorCode());

        return skill;
    }

    /**
     * Skillni ID orqali databsedan olish uchun.
     * Databasedan skillni ololmasa xatolikka tushadi
     *
     * @param id a
     * @return a
     */
    private Skill getSkillFromDB(UUID id) {
        // AGAR ID NULL BO'LSA XATOLIK QAYTARGANMIZ
        if (Objects.isNull(id)) {
            throw RestException.restThrow(ResponseMessage.ERROR_SKILL_NOT_FOUND);
        }
        Optional<Skill> optionalSkill = skillRepository.findById(id);

        if (optionalSkill.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_SKILL_NOT_FOUND);
        }
        return optionalSkill.get();

    }

    private void createHistory(Skill skill, SkillDTO skillDTO) {

        List<HistoryLog> historyLogList = new ArrayList<>();

        if (!Objects.equals(skill.getName(), skillDTO.getName())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.SKILL_NAME,
                            skill.getName(),
                            skillDTO.getName(),
                            EntityNameEnum.SKILL
                    )
            );
        }

        if (!Objects.equals(skill.getColor(), skillDTO.getColorCode())) {
            historyLogList.add(
                    new HistoryLog(
                            EntityFieldNameEnum.SKILL_COLOR,
                            skill.getColor(),
                            skillDTO.getColorCode(),
                            EntityNameEnum.SKILL
                    )
            );
        }

        historyLogRepository.saveAll(historyLogList);
    }

}
