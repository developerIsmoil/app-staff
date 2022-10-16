package ai.ecma.appstaff.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CallStatusEnum {
    ANSWERED("#00ff00"),//JAVOB BERILDI yashil
    NO_ANSWERED("#ff0000"),//JAVOB BERILMADI qizil
    BUSY("#ff0000"),//BAND QILIB QO'YILDI qizil
    INVALID("#ff0000"),//TIZIMDA MAVJUD BO'LMAGAN TELEFON RAQAM qizil
    ;

    private final String colorCode;
}
