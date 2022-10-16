package ai.ecma.appstaff.entity.view;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDNotUserAuditEntityWithoutUpdated;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity(name = TableNameConstant.VIEW_COLUMN)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"view_object_id", "name"})})
@DynamicInsert
@DynamicUpdate
public class ViewColumn extends AbsUUIDNotUserAuditEntityWithoutUpdated {

    @JoinColumn(insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ViewObject viewObject;

    @Column(name = "view_object_id", nullable = false)
    private UUID viewObjectId;

    //AGAR COLUMN TURI CUSTOM FIELD BO'LSA NAME SHU CUSTOM FIELD NING ID SI BO'LADI
    @Column(nullable = false)
    private String name;

    private Double orderIndex;

    private boolean pinned;

    private boolean hidden;

    private Integer width;

    @Enumerated(EnumType.STRING)
    private CustomFieldTypeEnum type;

    private UUID customFieldId;

    private boolean enabled = Boolean.TRUE;//SHU COLUMNNI DATA LARINI O'ZGARTIRSA BO'LADIMI

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ViewColumn that = (ViewColumn) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    //BU CONSTRUCTOR DAN DEFAULT VIEW YARATISHDA FOYDALANDIM
    public ViewColumn(UUID viewObjectId, String name, Double orderIndex, boolean pinned, boolean hidden, CustomFieldTypeEnum type, UUID customFieldId, boolean enabled) {
        this.viewObjectId = viewObjectId;
        this.name = name;
        this.orderIndex = orderIndex;
        this.pinned = pinned;
        this.hidden = hidden;
        this.type = type;
        this.customFieldId = customFieldId;
        this.enabled = enabled;
    }

    public ViewColumn(UUID viewObjectId, String name, Double orderIndex, boolean pinned, boolean hidden, Integer width, CustomFieldTypeEnum type, UUID customFieldId) {
        this.viewObjectId = viewObjectId;
        this.name = name;
        this.orderIndex = orderIndex;
        this.pinned = pinned;
        this.hidden = hidden;
        this.width = width;
        this.type = type;
        this.customFieldId = customFieldId;
    }

    public ViewColumn(Double orderIndex, boolean pinned) {
        this.orderIndex = orderIndex;
        this.pinned = pinned;
    }
}