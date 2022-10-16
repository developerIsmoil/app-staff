package ai.ecma.appstaff.payload.view;

import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewColumnDTO {

    private String id;

    private String name;

    private boolean pinned;

    private boolean hidden;

    private Double orderIndex;

    private Integer width;

    private CustomFieldTypeEnum type;//response uchun faqat

    private CustomFiledTypeConfigDTO typeConfig;

    private boolean customField;

    //KIMDIR SHU COLUMN GA TEGINA OLADIMI
    private boolean enabled;

    //SHU FIELD DA FILTER QILSA BO'LADIMI
    private boolean filterable;

    //SHU FIELD DA SEARCH QILSA BO'LADIMI
    private boolean searchable;

    //SHU FIELD DA SORT QILSA BO'LADIMI
    private boolean sortable;

    //SHU FIELD DA TEGIB  BO'LMAYDI. HAMMA COLUMN LARNI HIDE=TRUE QILMOQCHI BOLSA HAM ROOT=TRUE BOLGAN COLUMNLARGA TEGA OLMAYDI
    private boolean root;

    private Boolean showColumn=true;

    public ViewColumnDTO(String id, String name, boolean pinned, boolean hidden, Double orderIndex, Integer width) {
        this.id = id;
        this.name = name;
        this.pinned = pinned;
        this.hidden = hidden;
        this.orderIndex = orderIndex;
        this.width = width;
    }

    public ViewColumnDTO(String id, String name, Double orderIndex, CustomFieldTypeEnum type, CustomFiledTypeConfigDTO typeConfig) {
        this.id = id;
        this.name = name;
        this.orderIndex = orderIndex;
        this.type = type;
        this.typeConfig = typeConfig;
    }
    //


    public ViewColumnDTO(String id, String name, CustomFieldTypeEnum type, CustomFiledTypeConfigDTO typeConfig, boolean enabled) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.typeConfig = typeConfig;
        this.enabled = enabled;
    }
}
