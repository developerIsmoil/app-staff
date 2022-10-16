package ai.ecma.appstaff.enums;

// TimeSheetNING STATUSLARI
public enum TimeSheetStatusEnum {
    // timesheet yaratilgan default holati
    OPENED,

    // bo'lim boshlig'i tomonidan tasdiqlangan
    CONFIRM_HEAD_OF_DEPARTMENT,

    // hr tomonidan tasdiqlangan
    CONFIRM_HR,

    // tasdiqlash vaqti o'tib ketib tasdiqlanmagan
    NOT_CONFIRMED,

    // tasdiqlangan (yopilgan)
    CONFIRMED,

}
