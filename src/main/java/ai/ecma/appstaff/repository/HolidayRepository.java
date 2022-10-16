package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.Holiday;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, UUID> {
    boolean existsByNameAndIdNot(String name, UUID id);

    boolean existsByName(String name);

    List<Holiday> findAllByActiveTrue();

    List<Holiday> findAllByActiveTrue(Sort sort);

    @Query(
            value = "SELECT *\n" +
                    "FROM holiday AS hd\n" +
                    "WHERE hd.id IN (SELECT hdl.holiday_id\n" +
                    "                FROM holiday_date_list AS hdl\n" +
                    "                WHERE hdl.date_list >= (:fromDate)\n" +
                    "                  AND hdl.date_list <= (:toDate))\n" +
                    "  AND hd.active = true\n" +
                    "  AND hd.deleted = false;",
            nativeQuery = true
    )
    List<Holiday> findAllByFromAndToDate(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );
}
