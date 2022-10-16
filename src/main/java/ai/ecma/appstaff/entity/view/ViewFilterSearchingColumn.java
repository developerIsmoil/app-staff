package ai.ecma.appstaff.entity.view;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDNotUserAuditEntityWithoutUpdated;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Objects;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = TableNameConstant.VIEW_FILTER_SEARCHING_COLUMN)
@DynamicInsert
@DynamicUpdate
public class ViewFilterSearchingColumn extends AbsUUIDNotUserAuditEntityWithoutUpdated {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomFieldTypeEnum columnType;

    @Column(nullable = false)
    private String columnName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude
    private ViewFilter viewFilter;

    private Boolean customField=false;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ViewFilterSearchingColumn that = (ViewFilterSearchingColumn) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
