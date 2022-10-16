package ai.ecma.appstaff.repository.view;

import ai.ecma.appstaff.entity.view.FavouriteView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.UUID;

public interface FavouriteViewRepository extends JpaRepository<FavouriteView, UUID> {

    boolean existsByViewIdAndUserId(UUID viewId, UUID userId);

    @Transactional
    @Modifying
    void deleteByViewIdAndUserId(UUID viewId, UUID userId);

}
