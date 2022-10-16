package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.PrivilegeType;
import ai.ecma.appstaff.entity.Skill;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    Set<Skill> findAllByIdIn(List<UUID> idList);
}
