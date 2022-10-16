package ai.ecma.appstaff.payload.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SortFilterSearchableDTO {
    //SHU FIELD DA FILTER QILSA BO'LADIMI
    private boolean filterable;

    //SHU FIELD DA SEARCH QILSA BO'LADIMI
    private boolean searchable;

    //SHU FIELD DA SORT QILSA BO'LADIMI
    private boolean sortable;

    private boolean root;
    private Boolean showColumn = true;
}
