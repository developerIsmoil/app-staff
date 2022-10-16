package ai.ecma.appstaff.repository.customField;

import ai.ecma.appstaff.entity.customField.CustomField;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CustomFieldRepository extends JpaRepository<CustomField, UUID> {

    List<CustomField> findAllByTableName(String tableName);

    boolean existsByNameAndTableName(String name, String tableName);

    @Query(value = "select * from custom_field where id in :id",nativeQuery = true)
    List<CustomField> findAllByIdIn(@Param("id") Collection<UUID> id);

}
