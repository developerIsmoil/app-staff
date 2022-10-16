package ai.ecma.appstaff.payload.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchAndSortForRedisDTO {

    private String tableName;

    private String search;

    private List<String> columnName;

    private List<SortingForRedisDTO> sortingForRedis;
}
