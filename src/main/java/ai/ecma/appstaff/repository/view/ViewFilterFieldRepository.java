package ai.ecma.appstaff.repository.view;

import ai.ecma.appstaff.entity.view.ViewFilterField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ViewFilterFieldRepository extends JpaRepository<ViewFilterField, UUID> {

    @Transactional
    @Modifying
    @Query(value = "delete from view_filter_field where view_filter_id=:viewFilterId",nativeQuery = true)
    void deleteAllByViewFilterId(UUID viewFilterId);

    List<ViewFilterField> findAllByViewFilterId(UUID viewFilterId);
}
