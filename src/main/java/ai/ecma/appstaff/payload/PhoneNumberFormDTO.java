package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

// HODIMGA TEGISHLI TELEFON RAQAMLAR
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumberFormDTO {

    private List<PhoneNumberTypeInfoDTO> options;

    private OptionActionDTO action;

    private List<PhoneNumberDTO> phoneNumbers;

}
