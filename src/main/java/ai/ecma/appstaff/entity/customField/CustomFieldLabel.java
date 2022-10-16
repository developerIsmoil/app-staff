package ai.ecma.appstaff.entity.customField;

import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = TableNameConstant.CUSTOM_FIELD_LABEL)
@SQLDelete(sql = "update " + TableNameConstant.CUSTOM_FIELD_LABEL + " set deleted=true where id = ?")
@Where(clause = "deleted=false")
public class CustomFieldLabel extends AbsUUIDUserAuditEntity {

    @Column(nullable = false)
    private String label;

    private String colorCode;

    private Double orderIndex;

    @JsonIgnore
    @JoinColumn(insertable = false,updatable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude
    private CustomField customField;

    @Column(name = "custom_field_id",nullable = false)
    private UUID customFieldId;


    //todo entityga qo'lda qanday field qo'shsang ham shu mapga put qili birodar
    public static Map<String, String> GET_ENTITY_FIELDS() {
        return new LinkedHashMap<>() {{
            put(GET_ID(), GET_ID());
            put(GET_LABEL(), GET_LABEL());
            put(GET_COLOR(), GET_COLOR());
            put(GET_CUSTOM_FIELD_ID(), GET_CUSTOM_FIELD_ID());
        }};
    }

    public static String GET_LABEL() {
        return "label";
    }

    public static String GET_ID() {
        return "id";
    }

    public static String GET_COLOR() {
        return "colorCode";
    }

    public static String GET_CUSTOM_FIELD_ID() {
        return "customFieldId";
    }

    public CustomFieldLabel(String label, String colorCode, UUID customFieldId) {
        this.label = label;
        this.colorCode = colorCode;
        this.customFieldId = customFieldId;
    }
}
