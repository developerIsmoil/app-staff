package ai.ecma.appstaff.entity.view;

import ai.ecma.appstaff.enums.CompareOperatorTypeEnum;
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
import java.util.Objects;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = TableNameConstant.VIEW_FILTER_FIELD)
@DynamicInsert
@DynamicUpdate
public class ViewFilterField extends AbsUUIDNotUserAuditEntityWithoutUpdated {

    @JoinColumn(name = "view_filter_id",insertable = false,updatable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ViewFilter viewFilter;

    @Column(name = "view_filter_id",nullable = false)
    private UUID viewFilterId;

    @Column(name = "compare_operator_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CompareOperatorTypeEnum compareOperatorType;

    @Column(name = "order_index", nullable = false)
    private Double orderIndex;

    @Column(nullable = false)
    private String field; //dueDate { MAP NI ICHIDAGI KEY }

    @Column(name = "field_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomFieldTypeEnum fieldType;

    @OneToOne(mappedBy = "viewFilterField")
    private ViewFilterFieldValue viewFilterFieldValue;

    @Column(name = "custom_field")
    private Boolean customField;



    public ViewFilterField(UUID viewFilterId, CompareOperatorTypeEnum compareOperatorType, Double orderIndex, String field, CustomFieldTypeEnum fieldType, Boolean customField) {
        this.viewFilterId = viewFilterId;
        this.compareOperatorType = compareOperatorType;
        this.orderIndex = orderIndex;
        this.field = field;
        this.fieldType = fieldType;
        this.customField = customField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ViewFilterField that = (ViewFilterField) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}