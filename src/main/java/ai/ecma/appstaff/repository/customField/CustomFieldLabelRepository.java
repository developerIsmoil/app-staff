package ai.ecma.appstaff.repository.customField;

import ai.ecma.appstaff.entity.customField.CustomFieldLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

public interface CustomFieldLabelRepository extends JpaRepository<CustomFieldLabel, UUID> {

    @Transactional
    @Modifying
    void deleteAllByCustomFieldIdAndIdNotIn(UUID customFieldId, Collection<UUID> id);

}
