package ai.ecma.appstaff.repository.customField;

import ai.ecma.appstaff.entity.customField.CustomField;
import ai.ecma.appstaff.entity.customField.CustomFieldRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CustomFieldRatingRepository extends JpaRepository<CustomFieldRating, UUID> {

    List<CustomFieldRating> findAllByCustomFieldIn(Collection<CustomField> customField);
}
