package ai.ecma.appstaff.payload.feign;

import ai.ecma.appstaff.enums.ContractFormEnum;
import ai.ecma.appstaff.enums.EmployeeModeEnum;
import ai.ecma.appstaff.enums.PaymentCriteriaTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author IkhtiyorDev  <br/>
 * Date 16/03/22
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentInfoFeignDTO implements Serializable {

    private UUID id;
    private String fullName;
    private Long branchId;
    private String branch;
    private UUID departmentId;
    private String department;
    private UUID positionId;
    private String position;
    private UUID employeeCategoryId;
    private String employeeCategory;

    private String phoneNumber;
    private PaymentCriteriaTypeEnum paymentCriteriaType;
    private ContractFormEnum contractForm;
    private EmployeeModeEnum employeeMode;
    private String workingDays;

    public EmploymentInfoFeignDTO(UUID id,
                                  String fullName,
                                  Long branchId,
                                  UUID departmentId,
                                  String department,
                                  UUID positionId,
                                  String position,
                                  UUID employeeCategoryId,
                                  String employeeCategory) {
        this.id = id;
        this.fullName = fullName;
        this.branchId = branchId;
        this.departmentId = departmentId;
        this.department = department;
        this.positionId = positionId;
        this.position = position;
        this.employeeCategoryId = employeeCategoryId;
        this.employeeCategory = employeeCategory;
    }

    public EmploymentInfoFeignDTO(UUID id,
                                  String fullName,
                                  Long branchId,
                                  UUID departmentId,
                                  String department,
                                  UUID positionId,
                                  String position,
                                  UUID employeeCategoryId,
                                  String employeeCategory,
                                  String phoneNumber,
                                  PaymentCriteriaTypeEnum paymentCriteriaType,
                                  ContractFormEnum contractForm,
                                  EmployeeModeEnum employeeMode,
                                  String workingDays
    ) {
        this.id = id;
        this.fullName = fullName;
        this.branchId = branchId;
        this.departmentId = departmentId;
        this.department = department;
        this.positionId = positionId;
        this.position = position;
        this.employeeCategoryId = employeeCategoryId;
        this.employeeCategory = employeeCategory;
        this.phoneNumber = phoneNumber;
        this.paymentCriteriaType = paymentCriteriaType;
        this.contractForm = contractForm;
        this.employeeMode = employeeMode;
        this.workingDays = workingDays;
    }
}
