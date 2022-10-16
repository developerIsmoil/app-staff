package ai.ecma.appstaff.projection;

import ai.ecma.appstaff.enums.TimeSheetStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ITimeSheet {

    Integer getWorkHours();

    Integer getWorkedHours();

    //  TimeSheetNING ID SI
    Long getId();

    //  TimeSheetNING qaysi oyga tegishli ekanligi
    String getMonth();

    //  QO’SHIB BERISH
    Double getAdditionSalary();

    //  BERILGAN AVANS
    Double getAdvanceSalary();

    //  BONUS
    Double getBonus();

    //  HODIMGA TO‘LANADIGAN
    Double getPaidSalary();

    //  PREMYA
    Double getPremium();

    //  USHLANMALAR
    Double getRetentionSalary();

    //  MAOSH
    Double getSalary();

    //  SOLIQ SUMMA
    Double getTaxAmount();

    //  UMUMIY
    Double getTotalSalary();

    //  UMUMIY ISH KUNLAR
    Integer getWorkDays();

    //  ISHLAGAN KUNLAR
    Integer getWorkedDays();

    //  HODIMLAR SONI
    Integer getEmployeeCount();

    //  TIMESHEET HOLATI
    TimeSheetStatusEnum getTimeSheetStatus();

    //  SANA
    Date getTimeSheetDate();
}
