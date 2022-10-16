package ai.ecma.appstaff.repository.view;

import ai.ecma.appstaff.entity.view.ViewSorting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ViewSortingRepository extends JpaRepository<ViewSorting, UUID> {


    @Transactional
    @Modifying
    @Query(value = "delete from view_sorting where view_object_id=:viewObjectId",nativeQuery = true)
    void deleteAllByViewObjectId(UUID viewObjectId);

    List<ViewSorting> findAllByViewObjectIdOrderByOrderIndex(UUID viewObjectId);

}
