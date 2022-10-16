package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.payload.feign.RoleFeignDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//
@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountFormInfoDTO {

    // TANLANGAN ROLLAR
    private OptionDTO<RoleFeignDTO> role;

    // TIZIMDA ISHLASH YOKI ISHLAMASLIGI
    // AGAR access TRUE KELSA HARDOIM roleIdList BO'LADI,
    // AGAR access FALSE KELSA roleIdList EMPTY BO'LADI
    private boolean access;

    public AccountFormInfoDTO(OptionDTO<RoleFeignDTO> role) {
        this.role = role;
    }
}
