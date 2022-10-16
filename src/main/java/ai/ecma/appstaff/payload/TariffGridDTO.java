package ai.ecma.appstaff.payload;


import ai.ecma.appstaff.entity.TariffGrid;
import ai.ecma.appstaff.enums.BonusType;
import ai.ecma.appstaff.enums.PaymentCriteriaTypeEnum;
import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.CompanyFeignDTO;
import ai.ecma.appstaff.utils.RestConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TariffGridDTO {
    private UUID id;
//
//    @NotNull(message = "${ERROR.BRANCH.ID.REQUIRED}")
//    private Long branchId;

    private String branchName;
    private Long branchId;

    private Long companyName;
    private Long companyId;

    private OptionDTO<BranchFeignDTO> branch;
    private OptionDTO<CompanyFeignDTO> company;

    @NotNull(message = "${ERROR.DEPARTMENT.REQUIRED}")
    private UUID departmentId;

    private String departmentName;

    private OptionDTO<DepartmentDTO> department;

    @NotNull(message = "${ERROR.POSITION.REQUIRED}")
    private UUID positionId;

    private String positionName;

    private OptionDTO<PositionDTO> position;

    @NotNull(message = "${ERROR.EMPLOYEE.CATEGORY.REQUIRED}")
    private UUID employeeCategoryId;

    private String employeeCategoryName;

    private OptionDTO<EmployeeCategoryDTO> employeeCategory;

    @NotNull(message = "${ERROR.PAYMENT.CRITERIA_TYPE.REQUIRED}")
    private PaymentCriteriaTypeEnum paymentCriteriaType;

    private OptionDTO<EnumDTO> paymentCriteriaTypes;

    @NotNull(message = "${ERROR.PAYMENT.AMOUNT.REQUIRED}")
    private Double paymentAmount;

    private boolean hour;
    private Double hourPaymentAmount;

    private boolean day;
    private Double dayPaymentAmount;

    private BonusType bonusType;

    private OptionDTO<EnumDTO> bonusTypes;
    private Double bonusPercent;

    private boolean active;

    private UUID viewId;

    public static TariffGridDTO fromTariffGrid(TariffGrid tariffGrid, List<BranchFeignDTO> branchList) {

        BranchFeignDTO branch = branchList
                .stream()
                .filter(branchFeignDTO -> Objects.equals(tariffGrid.getBranchId(), (branchFeignDTO.getId())))
                .findAny()
                .orElse(new BranchFeignDTO(RestConstants.UNKNOWN));

        return new TariffGridDTO(
                tariffGrid.getId(),
                tariffGrid.getBranchId(),
                branch.getName(),
                Objects.nonNull(tariffGrid.getDepartment()) ? tariffGrid.getDepartment().getId() : null,
                Objects.nonNull(tariffGrid.getDepartment()) ? tariffGrid.getDepartment().getName() : null,
                Objects.nonNull(tariffGrid.getPosition()) ? tariffGrid.getPosition().getId() : null,
                Objects.nonNull(tariffGrid.getPosition()) ? tariffGrid.getPosition().getName() : null,
                Objects.nonNull(tariffGrid.getEmployeeCategory()) ? tariffGrid.getEmployeeCategory().getId() : null,
                Objects.nonNull(tariffGrid.getEmployeeCategory()) ? tariffGrid.getEmployeeCategory().getEmployeeCategoryType().getName() : null,
                tariffGrid.getPaymentCriteriaType(),
                tariffGrid.getPaymentAmount(),
                tariffGrid.isHour(),
                tariffGrid.isHour() ? tariffGrid.getHourPaymentAmount() : 0,
                tariffGrid.isDay(),
                tariffGrid.isDay() ? tariffGrid.getDayPaymentAmount() : 0,
                tariffGrid.getBonusType(),
                tariffGrid.getBonusPercent(),
                tariffGrid.isActive()
        );
    }

    public TariffGridDTO(UUID id, OptionDTO<BranchFeignDTO> branch, OptionDTO<CompanyFeignDTO> company, OptionDTO<DepartmentDTO> department, OptionDTO<PositionDTO> position, OptionDTO<EmployeeCategoryDTO> employeeCategory, OptionDTO<EnumDTO> paymentCriteriaTypes, Double paymentAmount, boolean hour, Double hourPaymentAmount, boolean day, Double dayPaymentAmount, OptionDTO<EnumDTO> bonusTypes, Double bonusPercent, boolean active) {
        this.id = id;
        this.branch = branch;
        this.company = company;
        this.department = department;
        this.position = position;
        this.employeeCategory = employeeCategory;
        this.paymentCriteriaTypes = paymentCriteriaTypes;
        this.paymentAmount = paymentAmount;
        this.hour = hour;
        this.hourPaymentAmount = hourPaymentAmount;
        this.day = day;
        this.dayPaymentAmount = dayPaymentAmount;
        this.bonusTypes = bonusTypes;
        this.bonusPercent = bonusPercent;
        this.active = active;
    }

    public TariffGridDTO(OptionDTO<CompanyFeignDTO> company, OptionDTO<BranchFeignDTO> branch, OptionDTO<DepartmentDTO> department, OptionDTO<PositionDTO> position, OptionDTO<EmployeeCategoryDTO> employeeCategory, OptionDTO<EnumDTO> paymentCriteriaTypes, OptionDTO<EnumDTO> bonusTypes) {
        this.company = company;
        this.branch = branch;
        this.department = department;
        this.position = position;
        this.employeeCategory = employeeCategory;
        this.paymentCriteriaTypes = paymentCriteriaTypes;
        this.bonusTypes = bonusTypes;
    }

    public TariffGridDTO(UUID id, Long branchId, String branchName, UUID departmentId, String departmentName, UUID positionId, String positionName, UUID employeeCategoryId, String employeeCategoryName, PaymentCriteriaTypeEnum paymentCriteriaType, Double paymentAmount, boolean hour, Double hourPaymentAmount, boolean day, Double dayPaymentAmount, BonusType bonusType, Double bonusPercent, boolean active) {
        this.id = id;
        this.branchId = branchId;
        this.branchName = branchName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.positionId = positionId;
        this.positionName = positionName;
        this.employeeCategoryId = employeeCategoryId;
        this.employeeCategoryName = employeeCategoryName;
        this.paymentCriteriaType = paymentCriteriaType;
        this.paymentAmount = paymentAmount;
        this.hour = hour;
        this.hourPaymentAmount = hourPaymentAmount;
        this.day = day;
        this.dayPaymentAmount = dayPaymentAmount;
        this.bonusType = bonusType;
        this.bonusPercent = bonusPercent;
        this.active = active;
    }

    public TariffGridDTO(UUID id, OptionDTO<BranchFeignDTO> branch, OptionDTO<DepartmentDTO> department, OptionDTO<PositionDTO> position, OptionDTO<EmployeeCategoryDTO> employeeCategory, OptionDTO<EnumDTO> paymentCriteriaTypes, OptionDTO<EnumDTO> bonusTypes, Double paymentAmount, boolean hour, Double hourPaymentAmount, boolean day, Double dayPaymentAmount, BonusType bonusType, Double bonusPercent, boolean active) {
        this.id = id;
        this.branch = branch;
        this.department = department;
        this.position = position;
        this.employeeCategory = employeeCategory;
        this.paymentCriteriaTypes = paymentCriteriaTypes;
        this.bonusTypes = bonusTypes;
        this.paymentAmount = paymentAmount;
        this.hour = hour;
        this.hourPaymentAmount = hourPaymentAmount;
        this.day = day;
        this.dayPaymentAmount = dayPaymentAmount;
        this.bonusType = bonusType;
        this.bonusPercent = bonusPercent;
        this.active = active;
    }

    public static TariffGridDTO makeTariffGridDTO(EmploymentInfoDTO employmentInfo) {
        return new TariffGridDTO(
                employmentInfo.getBranchId(),
                employmentInfo.getCompanyId(),
                employmentInfo.getDepartmentId(),
                employmentInfo.getPositionId(),
                employmentInfo.getEmployeeCategoryId(),
                employmentInfo.getPaymentCriteriaType()
        );
    }

    public TariffGridDTO(Long branchId, Long companyId, UUID departmentId, UUID positionId, UUID employeeCategoryId, PaymentCriteriaTypeEnum paymentCriteriaType) {
        this.branchId = branchId;
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.employeeCategoryId = employeeCategoryId;
        this.paymentCriteriaType = paymentCriteriaType;
    }

    public TariffGridDTO(UUID departmentId, UUID positionId, UUID employeeCategoryId, PaymentCriteriaTypeEnum paymentCriteriaType, Double paymentAmount, boolean hour, Double hourPaymentAmount, boolean day, Double dayPaymentAmount, BonusType bonusType, Double bonusPercent, boolean active) {

        this.departmentId = departmentId;
        this.positionId = positionId;
        this.employeeCategoryId = employeeCategoryId;
        this.paymentCriteriaType = paymentCriteriaType;
        this.paymentAmount = paymentAmount;
        this.hour = hour;
        this.hourPaymentAmount = hourPaymentAmount;
        this.day = day;
        this.dayPaymentAmount = dayPaymentAmount;
        this.bonusType = bonusType;
        this.bonusPercent = bonusPercent;
        this.active = active;
    }

    public static TariffGridDTO tariffGridDTOOptions(List<CompanyFeignDTO> companyFeignDTOList,
                                                     List<BranchFeignDTO> branchFeignDTOList,
                                                     List<DepartmentDTO> departmentDTOList,
                                                     List<PositionDTO> positionDTOList,
                                                     List<EmployeeCategoryDTO> employeeCategoryTypeDTOList,
                                                     List<EnumDTO> paymentCriteriaTypeList,
                                                     List<EnumDTO> bonusTypeList) {
        return new TariffGridDTO(
                OptionDTO.makeOptionDTO(companyFeignDTOList),
                OptionDTO.makeOptionDTO(branchFeignDTOList),
                OptionDTO.makeOptionDTO(departmentDTOList),
                OptionDTO.makeOptionDTO(positionDTOList),
                OptionDTO.makeOptionDTO(employeeCategoryTypeDTOList),
                OptionDTO.makeOptionDTO(paymentCriteriaTypeList),
                OptionDTO.makeOptionDTO(bonusTypeList)
        );
    }
}
