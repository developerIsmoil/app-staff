package ai.ecma.appstaff.payload.feign;

import ai.ecma.appstaff.entity.TimeSheetEmployee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmTimeSheetEmployeeDTO implements Serializable {

    private UUID employmentInfoId;

    private Double salary;

    private Integer workedHours;

    private Integer workHours;

    private Integer workedDays;

    private Integer workDays;

    private Double bonus;

    private Double premium;

    private Double advanceSalary;

    private Double retentionSalary;

    private Double additionSalary;

    private Double totalSalary;

    private Double taxAmount;

    private Double paidSalary;

    public static ConfirmTimeSheetEmployeeDTO fromTimeSheetEmployee(TimeSheetEmployee TimeSheetEmployee) {
        return new ConfirmTimeSheetEmployeeDTO(
                TimeSheetEmployee.getEmploymentInfo().getId(),
                TimeSheetEmployee.getSalary(),
                TimeSheetEmployee.getWorkedHours(),
                TimeSheetEmployee.getWorkHours(),
                TimeSheetEmployee.getWorkedDays(),
                TimeSheetEmployee.getWorkDays(),
                TimeSheetEmployee.getBonus(),
                TimeSheetEmployee.getPremium(),
                TimeSheetEmployee.getAdvanceSalary(),
                TimeSheetEmployee.getRetentionSalary(),
                TimeSheetEmployee.getAdditionSalary(),
                TimeSheetEmployee.getTotalSalary(),
                TimeSheetEmployee.getTaxAmount(),
                TimeSheetEmployee.getPaidSalary()
        );
    }

}
