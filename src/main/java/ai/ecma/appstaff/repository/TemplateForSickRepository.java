package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.TemplateForSick;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TemplateForSickRepository extends JpaRepository<TemplateForSick, UUID> {

    List<TemplateForSick> findAllByActiveTrue(Sort sort);

    boolean existsByFromCountAndToCountAndPercentAndPrivilegeFalse(Integer fromCount, Integer toCount, Double percent);

    boolean existsByPrivilegeType_IdAndPercentAndPrivilegeTrue(UUID privilegeType_id, Double percent);

    boolean existsByPrivilegeTypeId(UUID privilegeType_id);
}
