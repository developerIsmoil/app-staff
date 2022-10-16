package ai.ecma.appstaff.payload.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchingOtherServiceDTO {

    private String search;

    private Map<String, List<String>> tableNameAndColumnsName;

}
