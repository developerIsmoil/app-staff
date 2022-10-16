package ai.ecma.appstaff.repository;

import ai.ecma.appstaff.entity.HistoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HistoryLogRepository extends JpaRepository<HistoryLog, UUID> {

}
