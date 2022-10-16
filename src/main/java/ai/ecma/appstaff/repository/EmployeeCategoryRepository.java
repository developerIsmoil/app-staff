package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.EmployeeCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeCategoryRepository extends JpaRepository<EmployeeCategory, UUID> {

    /**
     * Bunda hodim kategoriyasini bo'lim, lavozim va hodim kategoriyasi turi bo'yicha avval qo'shilgan
     * yoki yo'qligini tekshirilgan
     *
     * @param department_id           Bo'limning ID si
     * @param employeeCategoryType_id Qaysi hodim kategoriya turi ekanligi
     * @param position_id             Qaysi lavozim ekanligi
     * @return boolean
     */
    boolean existsByDepartment_idAndEmployeeCategoryType_IdAndPosition_Id(
            UUID department_id,
            UUID employeeCategoryType_id,
            UUID position_id);

    /**
     * Bunda berilgan IDdan boshqa hodim kategoriyalarini
     * bo'lim, lavozim va hodim kategoriyasi turi bo'yicha avval qo'shilgan yoki yo'qligini tekshirilgan
     *
     * @param department_id           Bo'limning ID si
     * @param employeeCategoryType_id Qaysi hodim kategoriyasi ekanligi
     * @param position_id             Qaysi lavozim ekanligi
     * @param id                      Bo'limning idsi
     * @return boolean
     */
    boolean existsByDepartment_idAndEmployeeCategoryType_IdAndPosition_IdAndIdNot(
            UUID department_id,
            UUID employeeCategoryType_id,
            UUID position_id, UUID id);

    Slice<EmployeeCategory> findAllByActiveTrue(Pageable pageable);

    List<EmployeeCategory> findAllByActiveTrue(Sort sort);

    List<EmployeeCategory> findAllByActiveTrueAndDepartment_IdAndPosition_Id(UUID department_id, UUID position_id,Sort sort);


    boolean existsByDepartmentId(UUID department_id);
    boolean existsByPositionId(UUID position_id);

    boolean existsByEmployeeCategoryTypeId(UUID employeeCategoryType_id);
}
