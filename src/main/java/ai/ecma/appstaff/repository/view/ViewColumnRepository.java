package ai.ecma.appstaff.repository.view;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.enums.ViewTypeEnum;
import ai.ecma.appstaff.entity.view.ViewColumn;
import ai.ecma.appstaff.projection.OrderIndexAndViewIdProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ViewColumnRepository extends JpaRepository<ViewColumn, UUID> {

    @Transactional
    @Modifying
    @Query(value = "delete from view_column where view_object_id=:viewObjectId",nativeQuery = true)
    void deleteAllByViewObjectId(UUID viewObjectId);

    List<ViewColumn> findAllByViewObject_DefaultViewIsTrueAndViewObject_TableNameAndViewObject_TypeAndNameNotIn(String viewObject_tableName, ViewTypeEnum viewObject_type, Collection<String> name);


    @Query(value = "select max(order_index)\n" +
            "from view_column\n" +
            "where view_object_id = :viewObjectId\n" +
            "  and deleted = false\n" +
            "  and hidden = false", nativeQuery = true)
    Double getLastOrderIndex(@Param("viewObjectId") UUID viewObjectId);


    @Query(value = "select max(vc.order_index) as orderIndex, cast(vc.view_object_id as varchar) as viewId\n" +
            "from view_column vc\n" +
            "         join view_object vo on vc.view_object_id = vo.id\n" +
            "where vc.deleted = false\n" +
            "  and vo.deleted = false\n" +
            "  and (vo.default_view or vo.id=:viewObjectId)\n" +
            "group by view_object_id", nativeQuery = true)
    List<OrderIndexAndViewIdProjection> getOrderIndexDefaultOrViewId(@Param("viewObjectId") UUID viewObjectId);

    List<ViewColumn> findAllByNameAndType(String name, CustomFieldTypeEnum type);

    Optional<ViewColumn> findByViewObjectIdAndCustomFieldId(UUID viewObjectId, UUID customFieldId);
}
