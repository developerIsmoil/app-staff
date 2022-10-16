package ai.ecma.appstaff.payload.feign;

import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.DepartmentFeignDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author IkhtiyorDev  <br/>
 * Date 02/04/22
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchAndDepartmentFeignDTO implements Serializable {
    private List<BranchFeignDTO> branches;
    private List<DepartmentFeignDTO> departments;
}
