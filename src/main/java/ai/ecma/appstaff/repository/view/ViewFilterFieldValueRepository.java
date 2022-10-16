package ai.ecma.appstaff.repository.view;

import ai.ecma.appstaff.entity.view.ViewFilterFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ViewFilterFieldValueRepository extends JpaRepository<ViewFilterFieldValue, UUID> {

    @Transactional
    @Modifying
    @Query(value = "delete from view_filter_field_value where view_filter_field_id in :filterFieldIdList", nativeQuery = true)
    void deleteAllByViewFilterFieldIdIn(List<UUID> filterFieldIdList);

    List<ViewFilterFieldValue> findAllByViewFilterField_ViewFilterId(UUID viewFilterField_viewFilterId);

}
