package ai.ecma.appstaff.repository.view;

import ai.ecma.appstaff.entity.view.ViewFilterSearchingColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ViewFilterSearchingColumnRepository extends JpaRepository<ViewFilterSearchingColumn, UUID> {

    @Transactional
    @Modifying
    @Query(value = "delete from view_filter_searching_column where view_filter_id=:viewFilterId",nativeQuery = true)
    void deleteAllByViewFilterIdNative(UUID viewFilterId);


    List<ViewFilterSearchingColumn> findAllByViewFilterId(UUID viewFilter_id);

}
