package ai.ecma.appstaff.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

//
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountInfoDTO {

    // TANLANGAN ROLLAR ID SI
    private List<Long> roles;

    // TIZIMDA ISHLASH YOKI ISHLAMASLIGI
    // AGAR access TRUE KELSA HARDOIM roleIdList BO'LADI,
    // AGAR access FALSE KELSA roleIdList EMPTY BO'LADI
    private boolean access;
}
