package ai.ecma.appstaff.entity.customField;

import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = TableNameConstant.CUSTOM_FIELD_TREE)
@SQLDelete(sql = "update " + TableNameConstant.CUSTOM_FIELD_TREE + " set deleted=true where id = ?")
@Where(clause = "deleted=false")
public class CustomFieldTree extends AbsUUIDUserAuditEntity {

    @Column(nullable = false)
    private String name;

    private String color;

    private Double orderIndex;

//    @JoinColumn(insertable = false,updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CustomFieldTree parent;

    @Column(name = "parent_id",insertable = false,updatable = false)
    private UUID parentId;

    @OneToMany(mappedBy = "parent",cascade = CascadeType.ALL)
    private List<CustomFieldTree> children;

    @JoinColumn(insertable = false,updatable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude
    private CustomField customField;

    @Column(name = "custom_field_id",nullable = false)
    private UUID customFieldId;

    public CustomFieldTree(String name) {
        this.name = name;
    }
}
