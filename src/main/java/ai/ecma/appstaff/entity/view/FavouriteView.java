package ai.ecma.appstaff.entity.view;

import ai.ecma.appstaff.entity.template.AbsUUIDNotUserAuditEntity;
import ai.ecma.appstaff.entity.template.AbsUUIDNotUserAuditEntityWithoutUpdated;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

/**
 * USER NING FAVOURITE VIEW LARI
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = TableNameConstant.FAVOURITE_VIEW)
public class FavouriteView extends AbsUUIDNotUserAuditEntityWithoutUpdated {

    //QAYSI VIEW LIGI
    @JoinColumn(insertable = false,updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ViewObject view;

    @Column(name = "view_object_id",nullable = false)
    private UUID viewId;

    //QAYSI USER GA ULANGAN
    @Column(name = "user_id",nullable = false)
    private UUID userId;

    public FavouriteView(UUID viewObjectId, UUID userId) {
        this.viewId = viewObjectId;
        this.userId = userId;
    }
}
