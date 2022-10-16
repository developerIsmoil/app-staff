package ai.ecma.appstaff.repository.view;

import ai.ecma.appstaff.enums.PermissionUserThisViewEnum;
import ai.ecma.appstaff.entity.view.UserView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.*;

public interface UserViewRepository extends JpaRepository<UserView, Long> {

//    List<UserView> findAllByUserId(UUID userId);

    List<UserView> findAllByUserIdAndPermission(UUID userId, PermissionUserThisViewEnum permissionUserThisViewEnum);

//    PermissionUserThisViewEnum findByUserIdAndViewId(UUID userId, UUID viewId);

    List<UserView> findAllByUserIdAndTableName(UUID userId, String tableName);

    Optional<UserView> findFirstByViewId(UUID viewId);

    Optional<UserView> findByUserIdAndViewId(UUID userId, UUID view_id);

    @Query(value = "select cast(user_id as varchar) from user_view where view_id=:viewId and deleted=false", nativeQuery = true)
    List<UUID> getUserIdByViewId(UUID viewId);

    //
    List<UserView> findAllByViewIdAndUserIdIn(UUID viewId, Collection<UUID> userId);

    List<UserView> findAllByViewId(UUID viewId);


    @Transactional
    @Modifying
    @Query(value = "UPDATE user_view SET deleted=true WHERE view_id=:viewId", nativeQuery = true)
    void deleteAllByViewId(@Param("viewId") UUID viewId);


    //BU METHOD VIEW NI PUBLIC QILGANDA SHU VIEW DAGI BARCHA USER LAR NI VIEW DAN O'CHIRADI
    @Transactional
    @Modifying
    @Query(value = "update user_view set removed=true where view_id=:viewId and deleted=false", nativeQuery = true)
    void updateUserViewRemovedTrue(@Param("viewId") UUID viewId);


    //BU METHOD VIEW NI PUBLIC QILGANDA SHU VIEW DAGI BARCHA USER LAR NI VIEW DAN O'CHIRADI
    @Transactional
    @Modifying
    @Query(value = "update user_view set removed=true where view_id=:viewId and deleted=false and user_id not in(:adminIdList)", nativeQuery = true)
    void updateUserViewRemovedTrueWithoutAdmin(@Param("viewId") UUID viewId, @Param("adminIdList") Set<UUID> adminIdList);


    //BU METHOD VIEW NI PRIVATE QILGANDA SHU VIEW DA AVVAL BOR BO'LGAN USER LARNI QAYTA TIKLAYDI
    @Transactional
    @Modifying
    @Query(value = "update user_view set removed=false where view_id=:viewId and deleted=false", nativeQuery = true)
    void updateUserViewRemovedFalse(@Param("viewId") UUID viewId);


    @Transactional
    @Modifying
    void deleteAllByIdNot(UUID id);

    void deleteAllByViewIdAndUserIdIn(UUID viewId, Collection<UUID> userId);
}
