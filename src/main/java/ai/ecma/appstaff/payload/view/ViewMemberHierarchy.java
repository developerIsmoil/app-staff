package ai.ecma.appstaff.payload.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * USER SHARING PERMISSION TUGAMSINI BOSGANDA KELADIGAN SHU VIEW DAGI MEMBERLAR
 * VA VIEW DA YO'Q VA PUBLIC VIEW LARNI KO'RA OLADIGAN USER LAR teamList DA KELADI
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewMemberHierarchy {

    //VIEW A'ZOLARI
    private List<ViewMemberDTO> memberList;

    //AGAR VIEW PRIVATE BO'LSA SHU VIEW DA YO'Q USERLAR RO'YXATI
    private List<ViewMemberDTO> teamList;
}
