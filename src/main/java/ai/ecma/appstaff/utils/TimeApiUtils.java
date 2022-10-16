package ai.ecma.appstaff.utils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class TimeApiUtils {
    public static final ZoneId timeZone = ZoneId.of("Asia/Tashkent");


    public static LocalDate nowLocalDate(){
        return LocalDate.now(timeZone);
    }

    public static LocalDateTime nowLocalDateTime(){
        return LocalDateTime.now(timeZone);
    }

    public static LocalTime nowLocalTime(){
        return LocalTime.now(timeZone);
    }

    public static Date nowSqlDate(){
        return Date.valueOf(nowLocalDate());
    }


}
