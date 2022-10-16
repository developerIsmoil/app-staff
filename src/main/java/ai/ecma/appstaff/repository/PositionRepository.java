package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.Position;
import ai.ecma.appstaff.projection.PositionProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {
    /**
     * lavozim nomi va department bo'yicha databaseda bor yoki yo'qligi tekshirilgan
     *
     * @param name
     * @param department_id
     * @return
     */
    boolean existsByNameAndDepartmentId(String name, UUID department_id);

    /**
     * berilgan id dan boshqa lavozim nomi va department bo'yicha databaseda bor yoki yo'qligi tekshirilgan
     *
     * @param name
     * @param department_id
     * @param id
     * @return
     */
    boolean existsByNameAndDepartmentIdAndIdNot(String name, UUID department_id, UUID id);

    /**
     * BARCHA ACTIVE = TRUE BO'LGANLARNI TOPISH UCHUN
     *
     * @return
     */
    List<Position> findAllByActiveTrue();

    /**
     * LAVOZIMLAR ORASIDA SHU DEPARTMENTGA TEGISHLI LAVOZIM BOR YOKI YO'QLIGINI TEKSHIRISH UCHUN
     *
     * @param department_id
     * @return
     */
    boolean existsByDepartmentId(UUID department_id);

    /**
     * LAVOZIMLAR ORASIDA SHU DEPARTMENTLARGA TEGISHLI LAVOZIMLAR BOR YOKI YO'QLIGINI TEKSHIRISH UCHUN
     *
     * @param department_id
     * @return
     */
    boolean existsAllByDepartmentIdIn(Collection<UUID> department_id);

    List<Position> findAllByActiveTrue(Sort sort);

    /**
     *
     */
    @Query(value = "select array_agg(aa.*), aa.parent_id from position as aa group by aa.parent_id order by  aa.parent_id", nativeQuery = true)
    List<PositionProjection> findAllByParentIdGroupByParentId();


    @Query(
            value = "select distinct cast(parent_id as varchar) from aaa order by parent_id",
            nativeQuery = true
    )
    List<String> parentIdList();
}
