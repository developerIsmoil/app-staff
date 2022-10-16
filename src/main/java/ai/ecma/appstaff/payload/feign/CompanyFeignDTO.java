package ai.ecma.appstaff.payload.feign;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author IkhtiyorDev  <br/>
 * Date 08/08/22
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class    CompanyFeignDTO implements Serializable {
    private Long id;
    private String name;


    public CompanyFeignDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private List<Long> values;

    //    FILIALNI TANLANG
    private List<CompanyFeignDTO> options;

    public CompanyFeignDTO(String name) {
        this.name = name;
    }
}
