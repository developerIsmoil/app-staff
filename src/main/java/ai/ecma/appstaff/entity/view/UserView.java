package ai.ecma.appstaff.entity.view;

import ai.ecma.appstaff.enums.PermissionUserThisViewEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntityWithoutUpdated;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "update " + TableNameConstant.USER_VIEW + " set deleted=true where id = ?")
@Where(clause = "deleted=false and removed=false")
@Entity(name = TableNameConstant.USER_VIEW)
public class UserView extends AbsUUIDUserAuditEntityWithoutUpdated {

    private UUID userId;

    private String tableName;

    @JoinColumn(insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ViewObject view;

    @Column(name = "view_id", nullable = false)
    private UUID viewId;

    //userni viewni private holda korishga ruhsati bo'lsa removed false bo'ladi
    private boolean removed = false;
//    private boolean publicly;

    @Enumerated(EnumType.STRING)
    private PermissionUserThisViewEnum permission;

    public UserView(UUID userId, String tableName, UUID viewId, boolean removed, PermissionUserThisViewEnum permissions) {
        this.userId = userId;
        this.tableName = tableName;
        this.viewId = viewId;
        this.removed = removed;
        this.permission = permissions;
    }

    public UserView(UUID userId, UUID viewId) {
        this.userId = userId;
        this.viewId = viewId;
    }
}
