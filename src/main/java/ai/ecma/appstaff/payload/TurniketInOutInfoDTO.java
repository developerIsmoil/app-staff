package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.payload.view.ViewColumnDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TurniketInOutInfoDTO {

    private Map<String, TurniketFilterDTO> filters;

    private Map<String, ViewColumnDTO> columns;

    private List<Map<String, TurniketColumnDataDTO>> columnData;

}
