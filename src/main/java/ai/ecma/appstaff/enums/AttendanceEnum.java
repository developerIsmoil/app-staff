package ai.ecma.appstaff.enums;

import lombok.Getter;

@Getter
// HODIMNI DAVOMATINI OLGANDA ISHLATILADI. TimeSheetDA BO'LIM BOSHLIG'I HODIMNI DAVOMATINI OLADI
public enum AttendanceEnum {

    //*MT - Mehnat ta’tili
    VACATION("#00b533"),

    //*O‘HT - O‘z hisobidan ta’til
    OWN_VACATION("#b78103"),

    //*K - Kasal
    PATIENT("#ffc107"),

    //*TT - Tug‘riq ta’tili
    MATERNITY_LEAVE("#1890ff"),

    // WORKING TODAY
    // BU HOLATDA HODIMGA ISHLAGAN KUNI UCHUN MA'LUM BIR SOAT YOZILASI MASALA 8 (8 SOAT ISHLADI DEGANI)
    // BU 8 HAM TIZIMGA KIRITIB QO'YILGAN BO'LADI. STATIK EMAS
    WORKING("#0c53b7"),

    NOT_WORKING("#e2f5e9"),

    HOLIDAY("#b72136");

    private String colorCode;

    AttendanceEnum(String colorCode) {
        this.colorCode = colorCode;
    }
}
