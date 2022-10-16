package ai.ecma.appstaff.payload.feign;

import ai.ecma.appstaff.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author IkhtiyorDev  <br/>
 * Date 01/03/22
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentFeignDTO implements Serializable {

    private UUID id;
    private String name;

    public static DepartmentFeignDTO makeDepartmentFeignDTOFromDepartment(Department department) {
        return new DepartmentFeignDTO(
                department.getId(),
                department.getName()
        );
    }
}
