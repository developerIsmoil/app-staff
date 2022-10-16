package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.PhoneNumberType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PhoneNumberTypeRepository extends JpaRepository<PhoneNumberType, UUID> {


    boolean existsByNameEqualsIgnoreCaseAndColorEqualsIgnoreCase(String name, String color);
    boolean existsByNameEqualsIgnoreCaseAndColorEqualsIgnoreCaseAndIdNot(String name, String color, UUID id);


}
