package ai.ecma.appstaff.repository.customField;

import ai.ecma.appstaff.entity.customField.CustomFieldTree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomFieldTreeRepository extends JpaRepository<CustomFieldTree, UUID> {
}
