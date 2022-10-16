package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.Skill;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.SkillDTO;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface SkillService {

    ApiResult<?> addSkill(SkillDTO skillDTO);

    ApiResult<?> editSkill(UUID id, SkillDTO skillDTO);

    ApiResult<?> getAllSkill();

    ApiResult<?> getOneSkill(UUID id);

    ApiResult<?> deleteSkill(UUID id);

    ApiResult<?> deleteSkillByIdList(List<UUID> id);

    Set<Skill> getSkillListFromDB(List<UUID> idList);

    List<SkillDTO> getAllSkillFromDB();

    List<SkillDTO> getSkillDTOList(Collection<Skill> skillList);

    ApiResult<?> crudSkill(SkillDTO skillDTO);
}
