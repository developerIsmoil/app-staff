package ai.ecma.appstaff.entity.customField;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Entity(name = TableNameConstant.CUSTOM_FIELD)
@SQLDelete(sql = "update " + TableNameConstant.CUSTOM_FIELD + " set deleted=true where id = ?")
@Where(clause = "deleted=false")
public class CustomField extends AbsUUIDUserAuditEntity {

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "name", nullable = false)
    private String name;

    private boolean required;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomFieldTypeEnum type;

    @OneToMany(mappedBy = "customField", cascade = CascadeType.ALL)
    private List<CustomFieldDropDown> dropDowns;

    @OneToMany(mappedBy = "customField", cascade = CascadeType.ALL)
    private List<CustomFieldLabel> labels;

    @OneToMany(mappedBy = "customField", cascade = CascadeType.ALL)
    private List<CustomFieldTree> trees;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "customField", cascade = CascadeType.ALL)
    private CustomFieldRating customFieldRating;

}
