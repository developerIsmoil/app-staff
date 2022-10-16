package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.PrivilegeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PrivilegeTypeRepository extends JpaRepository<PrivilegeType, UUID> {
    /**
     * nomi bo'yicha databaseda bor yoki yo'qligi tekshirilgan
     *
     * @param name
     * @return
     */
    boolean existsByName(String name);

    /**
     * berilgan id dan boshqa nomi bo'yicha databaseda bor yoki yo'qligi tekshirilgan
     *
     * @param name
     * @param id
     * @return
     */
    boolean existsByNameAndIdNot(String name, UUID id);

    /**
     * berilgan id lar bo'yicha Imtiyoz turlarini olish uchun
     *
     * @param id
     * @return
     */
    List<PrivilegeType> findAllByIdIn(List<UUID> id);


    List<PrivilegeType> findAllByActiveTrue();
    List<PrivilegeType> findAllByActiveTrue(Sort sort);
}
