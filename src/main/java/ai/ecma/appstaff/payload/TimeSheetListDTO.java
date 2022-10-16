package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSheetListDTO {

    //Oy
    private String month;

    //Nachisleniya
    private Double accruals;

    //Bonus nacheslaniya
    private Double bonusAccruals;

    //Premya
    private Double premium;

    //Ushlanmalar
    private Double connections;

    //Qo’shib berish
    private Double additionSalary;

    //Umumiy
    private Double totalSalary;

    //Soliq summa
    private Double taxAmount;

    //Hodimga to‘lanadigan
    private Double paidSalary;

    //Hodimlar soni
    private Integer employeeCount;

    //Ishlanadigan kunlar
    private Integer workDays;

    //TIMESHEET holati
    private String TimeSheetStatus;

}
