package ai.ecma.appstaff.payload.feign;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.guieffect.qual.UI;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BranchFeignDTO implements Serializable {
    private Long id;
    private String name;
    private Long companyId;



    public BranchFeignDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private List<Long> values;

    //    FILIALNI TANLANG
    private List<BranchFeignDTO> options;

    public BranchFeignDTO(String name) {
        this.name = name;
    }
}
