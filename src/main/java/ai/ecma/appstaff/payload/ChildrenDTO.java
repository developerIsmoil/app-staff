package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildrenDTO {

    private UUID id;
    private List<EmployeeHrmDTO> employeeHrmList;
    private boolean hasChild;
    private String positionName;
    private List<ChildrenDTO> childrenList;
}
