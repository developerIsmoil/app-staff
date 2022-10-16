package ai.ecma.appstaff.payload.customField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomFieldTreeOptionDTO {

    private String parentId;

    private String id;

    private Double orderIndex;

    private String name;

    private String color;

    private List<CustomFieldTreeOptionDTO> children;

}
