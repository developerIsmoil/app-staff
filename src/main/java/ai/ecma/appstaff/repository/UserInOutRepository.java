package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.UserInOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserInOutRepository extends JpaRepository<UserInOut, UUID> {

//    @Query(value = "",nativeQuery = true)


}
