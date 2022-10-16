package ai.ecma.appstaff.enums;

import lombok.Getter;

@Getter
public enum DateCompareOperatorTypeEnum {

    //BULAR START_DATE BILAN ISHLAYDI
    EQ("Equals"),//
    GT("Greater than"),
    LT("Less than"),

    //BUNDA START_DATE VA END_DATE KELIHI MAJBURIY
    RA("Range"),

    //BULAR DateFilterTypeEnum BILAN KOMBINATSIYADA ISHLAYDI
    LAST("Last"),
    NEXT("Next"),
    THIS("Next"),

    //bular o'zi alohida
    TODAY("Next"),
    TOMORROW("Next"),
    YESTERDAY("Next"),
    ;

    private final String title;

    DateCompareOperatorTypeEnum(String title) {
        this.title = title;
    }
}
