package ai.ecma.appstaff.entity;

import ai.ecma.appstaff.enums.ContractFormEnum;
import ai.ecma.appstaff.enums.EmployeeModeEnum;
import ai.ecma.appstaff.enums.EmployerStatusEnum;
import ai.ecma.appstaff.enums.PaymentCriteriaTypeEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

// HODIM HAQIDA MA'LUMOT (QAYSI BO'LIMDA ISHLASHI, QAYSI LAVOZIMDA)
@Entity
@Table(name = TableNameConstant.EMPLOYMENT_INFO)
@SQLDelete(sql = "update " + TableNameConstant.EMPLOYMENT_INFO + " SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentInfo extends AbsUUIDUserAuditEntity {

    // QAYSI HODIM EKANLIGI
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.EMPLOYEE_ID)
    private Employee employee;

    // HODIM ISHLAYDIGAN FILIAL
    @Column(name = ColumnKey.BRANCH_ID)
    private Long branchId;

    // HODIM ISHLAYDIGAN FILIAL
    @Column(name = ColumnKey.COMPANY_ID)
    private Long companyId;

    // HODIM ISHLAYDIGAN BO'LIM
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.DEPARTMENT_ID)
    private Department department;

    // HODIM ISHLAYDIGAN LAVOZIM
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.POSITION_ID)
    private Position position;

    // HODIM KATEGORIYASI
    @JoinColumn(name = ColumnKey.EMPLOYEE_CATEGORY_ID)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private EmployeeCategory employeeCategory;

    // QAYSI HODIM KATEGORIYA TURI EKANLIGI
    // MASALAN (A1)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = ColumnKey.EMPLOYEE_CATEGORY_TYPE_ID)
    private EmployeeCategoryType employeeCategoryType;

    // TO'LOV STAVKASI
    @Enumerated(EnumType.STRING)
    @Column(name = ColumnKey.PAYMENT_CRITERIA_TYPE, nullable = false)
    private PaymentCriteriaTypeEnum paymentCriteriaType;

    // SHARTNOMA SHAKLI
    @Enumerated(EnumType.STRING)
    @Column(name = ColumnKey.CONTRACT_FORM, nullable = false)
    private ContractFormEnum contractForm;

    // ISH STAFKASI (FULL_TIME, RART_TIME)
    @Enumerated(EnumType.STRING)
    @Column(name = ColumnKey.EMPLOYEE_MODE, nullable = false)
    private EmployeeModeEnum employeeMode;

    // HODIMNING TIZIMDAGI HOLATI
    @Column(name = ColumnKey.EMPLOYER_STATUS)
    @Enumerated(EnumType.STRING)
    private EmployerStatusEnum employerStatus;

    // HODIMNING ISHGA QABUL QILINGAN SANASI
    @Column(name = ColumnKey.HIRE_DATE, nullable = false)
    private Date hireDate;

    // HODIMNING ISHDAN BO'SHAGAN SANASI
    // QAYSIDIR BO'LIMDAN ISHDAN BO'SHASHI UCHUN
    @Column(name = ColumnKey.RESIGNATION_DATE)
    private Date resignationDate;

    // ISHDAN BO'LGAN BO'LSA TRUE BO'LADI
    // QAYSIDIR BO'LIMDAN ISHDAN BO'SHASHI UCHUN
    @Column(name = ColumnKey.RESIGNATION)
    private Boolean resignation;

    @Column(name = ColumnKey.RESIGNATION_DESCRIPTION,columnDefinition = "text")
    private String resignationDescription;

    // TimeSheetNI BOSHQARA OLADIMI
    @Column(name = ColumnKey.MANAGE_TABLE)
    private Boolean manageTable;

    public EmploymentInfo(Employee employee, Long branchId, Long companyId, Department department, Position position, EmployeeCategory employeeCategory, EmployeeCategoryType employeeCategoryType, PaymentCriteriaTypeEnum paymentCriteriaType, ContractFormEnum contractForm, EmployeeModeEnum employeeMode, EmployerStatusEnum employerStatus, Date hireDate, Boolean manageTable) {
        this.employee = employee;
        this.branchId = branchId;
        this.companyId = companyId;
        this.department = department;
        this.position = position;
        this.employeeCategory = employeeCategory;
        this.employeeCategoryType = employeeCategoryType;
        this.paymentCriteriaType = paymentCriteriaType;
        this.contractForm = contractForm;
        this.employeeMode = employeeMode;
        this.employerStatus = employerStatus;
        this.hireDate = hireDate;
        this.manageTable = manageTable;
    }
}
