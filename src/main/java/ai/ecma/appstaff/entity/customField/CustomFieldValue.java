package ai.ecma.appstaff.entity.customField;

import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
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
@Entity(name = TableNameConstant.CUSTOM_FIELD_VALUE)
@SQLDelete(sql = "update " + TableNameConstant.CUSTOM_FIELD_VALUE + " set deleted=true where id = ?")
@Where(clause = "deleted=false")
public class CustomFieldValue extends AbsUUIDUserAuditEntity {

    @JoinColumn(insertable = false,updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private CustomField customField;

    @Column(name = "custom_field_id",nullable = false)
    private UUID customFieldId;

    @Column(columnDefinition = "text")
    private String value;

    private String ownerId;    // QAYSI HUMANGA TEGISHLI EKANLIGI

    //todo entityga qo'lda qanday field qo'shsang ham shu mapga put qili birodar
    public static Map<String, String> GET_ENTITY_FIELDS() {
        return new LinkedHashMap<>() {{
            put(GET_ID(), GET_ID());
            put(GET_OWNER_ID(), GET_OWNER_ID());
            put(GET_VALUE(), GET_VALUE());
            put(GET_CUSTOM_FIELD(), GET_CUSTOM_FIELD_ID());
        }};
    }

    public static String GET_ID() {
        return "id";
    }

    public static String GET_OWNER_ID() {
        return "ownerId";
    }

    public static String GET_VALUE() {
        return "value";
    }

    public static String GET_CUSTOM_FIELD_ID() {
        return "customFieldId";
    }

    public static String GET_CUSTOM_FIELD() {
        return "customField";
    }


}
