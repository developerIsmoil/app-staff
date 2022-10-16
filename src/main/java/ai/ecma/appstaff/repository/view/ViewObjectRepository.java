package ai.ecma.appstaff.repository.view;

import ai.ecma.appstaff.enums.ViewTypeEnum;
import ai.ecma.appstaff.entity.view.ViewObject;
import ai.ecma.appstaff.projection.ViewObjectAndPermission;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ViewObjectRepository extends JpaRepository<ViewObject, UUID> {

    List<ViewObject> findAllByTableNameAndPubliclyIsTrueAndDefaultViewIsTrueAndIdNot(String tableName, UUID id);

    Optional<ViewObject> findAllByPubliclyIsTrueAndDefaultViewIsTrueAndTableNameAndType(String tableName, ViewTypeEnum type);

    List<ViewObject> findAllByTableName(String tableName);

    // HUMAN TABLE GA TEGISHLI STANDART(DEFAULT=TRUE) VIEW LARNI TOPIB KELADI.(HAR BIR VIEW DA BITTA STANDART VIEW DEFAULT QO'SHILADI)
    List<ViewObject> findAllByTableNameAndDefaultViewIsTrue(String tableName);

    // HUMAN TABLEGA TEGISHLI STANDART BO'LMAGAN VA PUBLIC BO'LGAN VIEWLARNI QAYTARADI
    List<ViewObject> findAllByTableNameAndDefaultViewIsFalseAndPubliclyIsTrue(String tableName);


//    List<ViewObject> findAllById(Collection<UUID> id);


    @Query(value = "select vo.*\n" +
            "from view_object vo\n" +
            "         left join user_view uv on vo.id = uv.view_id\n" +
            "where vo.table_name = :tableName\n" +
            "  and vo.publicly\n" +
            "  and uv.user_id = :userId\n" +
            "  and vo.deleted = false\n" +
            "  and uv.deleted = false", nativeQuery = true)
    List<ViewObject> findAllPublicViewAndPrivateInUserId(@Param("userId") UUID userId,
                                                         @Param("tableName") String tableName);

    /**
     * @param userId
     * @param tableName
     * @return
     */
    @Query(value = "select cast(vo.id as varchar)             as id,\n" +
            "       vo.table_name                      as tableName,\n" +
            "       cast(vo.created_by_id as varchar)  as createdById,\n" +
            "       cast(:userId as varchar)           as userId,\n" +
            "       vo.name                            as name,\n" +
            "       vo.default_view                    as defaultView,\n" +
            "       vo.auto_save                       as autoSave,\n" +
            "       vo.publicly                        as publicly,\n" +
            "       vo.shared                          as shared,\n" +
            "       vo.row_size                        as rowSize,\n" +
            "       cast(vo.view_filter_id as varchar) as viewFilterId,\n" +
            "       vo.type                            as type,\n" +
            "       uv.permission                      as permission,\n" +
            "       fv.id is not null                  as favourite\n" +
            "from view_object vo\n" +
            "         left join user_view uv\n" +
            "                   on vo.id = uv.view_id and uv.user_id = :userId and uv.deleted = false\n" +
            "         left join favourite_view fv on vo.id = fv.view_object_id and fv.user_id = :userId\n" +
            "where (publicly or uv.id is not null)\n" +
            "  and vo.deleted = false\n" +
            "  and vo.table_name = :tableName\n" +
            "order by fv.created_at desc NULLS LAST", nativeQuery = true)
    List<ViewObjectAndPermission> findAllPublicViewAndPrivateInPermission(@Param("userId") UUID userId,
                                                                          @Param("tableName") String tableName);


    /**
     * ADMIN UCHUN
     * @param userId
     * @param tableName
     * @return
     */
    @Query(value = "select cast(vo.id as varchar)             as id,\n" +
            "       vo.table_name                      as tableName,\n" +
            "       cast(vo.created_by_id as varchar)  as createdById,\n" +
            "       cast(:userId as varchar)           as userId,\n" +
            "       vo.name                            as name,\n" +
            "       vo.default_view                    as defaultView,\n" +
            "       vo.auto_save                       as autoSave,\n" +
            "       vo.publicly                        as publicly,\n" +
            "       vo.shared                          as shared,\n" +
            "       vo.row_size                        as rowSize,\n" +
            "       cast(vo.view_filter_id as varchar) as viewFilterId,\n" +
            "       vo.type                            as type,\n" +
            "       :permission                        as permission,\n" +
            "       fv.id is not null                  as favourite\n" +
            "from view_object vo\n" +
            "         left join favourite_view fv on vo.id = fv.view_id and fv.user_id = :userId\n" +
            "where\n" +
            "      vo.deleted = false and vo.table_name=:tableName\n" +
            "order by fv.created_at desc NULLS LAST", nativeQuery = true)
    List<ViewObjectAndPermission> findAllPublicViewAndPrivateInPermissionForAdmin(@Param("userId") UUID userId,
                                                                                  @Param("tableName") String tableName,
                                                                                  @Param("permission") String permission);


    @Transactional
    @Modifying
    @Query(value = RestConstants.INITIAL_EXECUTING_QUERY, nativeQuery = true)
    void executeUniqueQuery();

    @Query(value = "select * from get_entity_id_list_for_generic_view(:myQuery)", nativeQuery = true)
    List<String> getEntityIdListForGenericView(@Param("myQuery") String myQuery);

    @Query(value = "select * from get_entity_id_list_for_generic_view(:myQuery)", nativeQuery = true)
    List<String> getEntityForGenericViewDataRow(@Param("myQuery") String myQuery);


    Optional<ViewObject> findFirstByTableNameOrderByCreatedAtDesc(String tableName);

    @Transactional
    @Modifying
    @Query(value = RestConstants.QUERY_FOR_OWNER_POSTGRES, nativeQuery = true)
    void executeFunctionForOwnerPostgres();

    @Transactional
    @Modifying
    @Query(value = RestConstants.QUERY_FOR_OWNER_STAFF, nativeQuery = true)
    void executeFunctionForOwnerStaff();

    boolean existsByDateAndTableNameAndType(Date date, String tableName, ViewTypeEnum type);
}
