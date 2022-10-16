package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.enums.ContractFormEnum;
import ai.ecma.appstaff.enums.EmployeeModeEnum;
import ai.ecma.appstaff.enums.PaymentCriteriaTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmploymentInfoDTO {
    // ID
    private UUID id;

    // HODIM ISHLAYDIGAN FILIAL
//    @NotNull(message = "REQUIRED.BRANCH.ID")
    private Long branchId;

    // HODIM ISHLAYDIGAN FILIAL
//    @NotNull(message = "REQUIRED.BRANCH.ID")
    private Long companyId;

    // HODIM ISHLAYDIGAN BO'LIM
    @NotNull(message = "REQUIRED DEPARTMENT ID")
    private UUID departmentId;

    // HODIM ISHLAYDIGAN BO'LIM
    private String departmentName;

    // HODIM ISHLAYDIGAN LAVOZIM
    @NotNull(message = "REQUIRED POSITION ID")
    private UUID positionId;

    // HODIM ISHLAYDIGAN LAVOZIM
    private String positionName;

    // HODIM KATEGORIYASI
    @NotNull(message = "REQUIRED EMPLOYEE CATEGORY ID")
    private UUID employeeCategoryId;

    // HODIM KATEGORIYASI
    private String employeeCategoryName;

    // TO'LOV STAVKASI
    @NotNull(message = "REQUIRED PAYMENT CRITERIA TYPE")
    private PaymentCriteriaTypeEnum paymentCriteriaType;

    // SHARTNOMA SHAKLI
    @NotNull(message = "REQUIRED CONTRACT FORM")
    private ContractFormEnum contractForm;

    // ISH STAFKASI (FULL_TIME, RART_TIME)
    @NotNull(message = "REQUIRED EMPLOYEE MODE")
    private EmployeeModeEnum employeeMode;

    // HODIMNING ISHGA QABUL QILINGAN SANASI
    @NotNull(message = "REQUIRED EMPLOYEE HIRE DATE")
    private Long hireDate;

//    @NotNull(message = "REQUIRED.MANAGE.TIMESHEET")
    private Boolean manageTimeSheet;

    private List<EmployeeWorkDayDTO> employeeWorkDayList;

}