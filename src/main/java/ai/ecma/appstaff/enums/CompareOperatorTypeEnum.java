package ai.ecma.appstaff.enums;

import lombok.Getter;

@Getter
public enum CompareOperatorTypeEnum {
    GT("Greater than"),
    LT("Less than"),
    GTE("Greater than or equal"),
    LTE("Less than or equal"),

    EQ("Equals"),//DROPDOWN, RATING, MONEY, NUMBER, CHECKBOX,DATE LARDA AYNAN TENGLIGI, SHORT_TEXT,LONG_TEXT, EMAIL, PHONE LARDA CONTAINS BO'LISHI UCHUN
    NOT("Is not"),//SHORT_TEXT,LONG_TEXT DA DOES NOT CONTAIN BO'LSA

    ANY("Any"),   //BERILAYOTGAN QIYMATLARDA BITTASI TOPILSA HAM TRUE BO'LADI
    ALL("All"),    //BERILAYOTGAN QIYMATLARDA BARCHASI TOPILSA TRUE BO'LADI

    NOT_ANY("Not any"),   //BERILAYOTGAN QIYMATLARDAN BITTASI TOPILMASA HAM TRUE BO'LADI. {10 TA KELSA HECH BO'LMASA BITTASI TOPILMAY QOLISHI KERAK SHUNDA TRUE}
    NOT_ALL("Not all"),    //BERILAYOTGAN QIYMATLARDAN BARCHASI TOPILMASA TRUE BO'LADI

    RA("Range"),//RATING, MONEY, NUMBER, DATE TYPE LARIDA ORALIQDAGI QIYMALARNI OLISH UCHUN

    IS_SET("O'rnatilgan"),//NULL BOLMASA {DB DA BOR}
    IS_NOT_SET("O'rnatilmagan"),//NULL BOLGANLAR {DB DA YO'Q}
    ;

    private final String title;

    CompareOperatorTypeEnum(String title) {
        this.title = title;
    }
}
