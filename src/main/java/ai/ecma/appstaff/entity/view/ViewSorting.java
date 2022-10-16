
package ai.ecma.appstaff.entity.view;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDNotUserAuditEntityWithoutUpdated;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = TableNameConstant.VIEW_SORTING)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"view_object_id","field"})})
@DynamicInsert
@DynamicUpdate
public class ViewSorting extends AbsUUIDNotUserAuditEntityWithoutUpdated {

    @JoinColumn(insertable = false,updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ViewObject viewObject;

    @Column(name = "view_object_id",nullable = false)
    private UUID viewObjectId;

    @Column(nullable = false)
    private String field;

    @Column(name = "order_index",nullable = false)
    private Double orderIndex;
    @Enumerated(EnumType.STRING)
    @Column(name = "field_type",nullable = false)
    private CustomFieldTypeEnum fieldType;

    @Min(value = -1)
    @Max(value = 1)
    @Column(nullable = false)
    private Integer direction;

    private Boolean customField=false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ViewSorting that = (ViewSorting) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public ViewSorting(UUID viewObjectId, String field,
                       Double orderIndex, Integer direction,
                       CustomFieldTypeEnum fieldType,boolean customField) {
        this.viewObjectId = viewObjectId;
        this.field = field;
        this.orderIndex = orderIndex;
        this.direction = direction;
        this.fieldType=fieldType;
        this.customField=customField;
    }
}
