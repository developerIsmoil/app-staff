package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.EmployeeCategoryType;
import ai.ecma.appstaff.entity.Position;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeCategoryTypeRepository extends JpaRepository<EmployeeCategoryType, UUID> {

    /**
     * Bunda hodim kategoriya turi nomi avval qo'shilgan yoki yo'qligini tekshirilgan
     *
     * @param name Hodim kategoriyasi nomi
     * @return boolean
     */
    boolean existsByName(String name);

    /**
     * Bunda berilgan ID ga tegishli bo'lmagan hodim kategoriya turlari
     * nomi avval qo'shilgan yoki yo'qligini tekshirilgan
     *
     * @param name Hodim kategoriyasi nomi
     * @param id   Hodim kategoriyasi ID si
     * @return boolean
     */
    boolean existsByNameAndIdNot(String name, UUID id);

    List<EmployeeCategoryType> findAllByActiveTrue();

    List<EmployeeCategoryType> findAllByActiveTrue(Sort sort);
}
