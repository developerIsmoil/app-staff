package ai.ecma.appstaff.repository.view;

import ai.ecma.appstaff.entity.view.ViewFilter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ViewFilterRepository extends JpaRepository<ViewFilter, UUID> {


//    ViewFilter findByViewObjectId();

}
