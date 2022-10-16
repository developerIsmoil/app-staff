package ai.ecma.appstaff.payload.customField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * CUSTOM FIELD NI QIYMATINI QAYTARISH UCHUN
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomFieldValueDTO {

    /**
     * QIYMATI
     */
//    @NotBlank(message = "{CUSTOM_FIELD_VALUE_REQUIRED}")
    private Object value;

    /**
     * CUSTOM FIELD ID SI
     */
    @NotNull(message = "{CUSTOM_FIELD_REQUIRED}")
    private UUID customFieldId;

    /**
     * QAYSI LEAD GA TEGISHLI
     */
    private String ownerId;

    /**
     * BU QIYMAT TEKSHIRAYOTGANDA SHU QIYMATNI HISOBGA OLMAY KETISH KERAKMI SHUNING UCHUN. BU QIYMAT FRONT END DAN KELMAYDI
     */
    private boolean needless = false;

    public CustomFieldValueDTO(Object value, UUID customFieldId, String ownerId) {
        this.value = value;
        this.customFieldId = customFieldId;
        this.ownerId = ownerId;
    }
}
