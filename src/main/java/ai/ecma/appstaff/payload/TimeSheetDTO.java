package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSheetDTO {
    private String id;
    private Double additionSalary;
    private Double advanceSalary;
    private Double bonus;
    private Double paidSalary;
    private Double premium;
    private Double retentionSalary;
    private Double salary;
    private Double taxAmount;
    private Double totalSalary;
    private Integer workDays;
    private Integer workedDays;
    private Integer employeeCount;
    private String TimeSheetStatus;
    private Long TimeSheetDate;
}
