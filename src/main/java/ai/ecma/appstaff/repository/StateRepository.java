package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StateRepository extends JpaRepository<State, UUID> {
}
