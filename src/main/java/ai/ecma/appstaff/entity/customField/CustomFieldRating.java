package ai.ecma.appstaff.entity.customField;

import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = TableNameConstant.CUSTOM_FIELD_RATING)
@SQLDelete(sql = "update " + TableNameConstant.CUSTOM_FIELD_RATING + " set deleted=true where id = ?")
@Where(clause = "deleted=false")
public class CustomFieldRating extends AbsUUIDUserAuditEntity {

    @Column(name = "code_point", nullable = false)
    private String codePoint;

    @Column(nullable = false)
    private Integer count;

    @JsonIgnore
    @JoinColumn(insertable = false,updatable = false)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private CustomField customField;

    @Column(name = "custom_field_id",nullable = false)
    private UUID customFieldId;


    //todo entityga qo'lda qanday field qo'shsang ham shu mapga put qili birodar
    public static Map<String, String> getTableFields() {
        return new LinkedHashMap<>() {{
            put("id", "id");
            put("count", "count");
            put("codePoint", "codePoint");
            put("customField", "customField");
        }};
    }


    public CustomFieldRating(String codePoint, Integer count, UUID customFieldId) {
        this.codePoint = codePoint;
        this.count = count;
        this.customFieldId = customFieldId;
    }
}
