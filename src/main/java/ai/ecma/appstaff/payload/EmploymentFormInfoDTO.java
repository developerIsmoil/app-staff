package ai.ecma.appstaff.payload;

 
import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.CompanyFeignDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmploymentFormInfoDTO {

    // ID
    private UUID id;

    // HODIM ISHLAYDIGAN FILIAL
    private OptionDTO<BranchFeignDTO> branch;

    private OptionDTO<CompanyFeignDTO> company;

    // HODIM ISHLAYDIGAN BO'LIM
    private OptionDTO<DepartmentDTO> department;

    // HODIM ISHLAYDIGAN LAVOZIM
    private OptionDTO<PositionDTO> position;

    // HODIM KATEGORIYASI
    private OptionDTO<EmployeeCategoryDTO> employeeCategory;

    // TO'LOV STAVKASI
    private OptionDTO<EnumDTO> paymentCriteriaType;

    // HODIM ISHLAYDIGAN HAFTA KUNLARI
//    private OptionDTO<EnumDTO> weekDays;

    // HODIM BIR KUNDA ISHLAYDIGAN ISH SOATI
    // todo from to ga o'zgartirish kreak
//    private Integer workHours;

    // SHARTNOMA SHAKLI
    private OptionDTO<EnumDTO> contractForm;

    // ISH STAFKASI (FULL_TIME, RART_TIME)
    private OptionDTO<EnumDTO> employeeMode;

    // HODIMNING ISHGA QABUL QILINGAN SANASI
    private Long hireDate;

    // HODIMNING TIZIMDAGI HOLATI
    private OptionDTO<EnumDTO> employerStatus;

    private List<EmployeeWorkDayInfoDTO> employeeWorkDayList;

    private Boolean resignation;
    private Long resignationDate;
    private String resignationDescription;

    public EmploymentFormInfoDTO(OptionDTO<BranchFeignDTO> branch,
                                 OptionDTO<CompanyFeignDTO> company,
                                 OptionDTO<DepartmentDTO> department,
                                 OptionDTO<PositionDTO> position,
                                 OptionDTO<EmployeeCategoryDTO> employeeCategory,
                                 OptionDTO<EnumDTO> paymentCriteriaType,
                                 OptionDTO<EnumDTO> contractForm,
                                 OptionDTO<EnumDTO> employeeMode,
                                 List<EmployeeWorkDayInfoDTO> employeeWorkDayList,
                                 Boolean resignation,
                                 Long resignationDate,
                                 String resignationDescription) {
        this.company = company;
        this.branch = branch;
        this.department = department;
        this.position = position;
        this.employeeCategory = employeeCategory;
        this.paymentCriteriaType = paymentCriteriaType;
        this.contractForm = contractForm;
        this.employeeMode = employeeMode;
        this.employeeWorkDayList = employeeWorkDayList;
        this.resignation = resignation;
        this.resignationDate = resignationDate;
        this.resignationDescription = resignationDescription;

    }

    public EmploymentFormInfoDTO(OptionDTO<BranchFeignDTO> branch,
                                 OptionDTO<CompanyFeignDTO> company,
                                 OptionDTO<DepartmentDTO> department,
                                 OptionDTO<PositionDTO> position,
                                 OptionDTO<EmployeeCategoryDTO> employeeCategory,
                                 OptionDTO<EnumDTO> paymentCriteriaType,
                                 OptionDTO<EnumDTO> contractForm,
                                 OptionDTO<EnumDTO> employeeMode,
                                 List<EmployeeWorkDayInfoDTO> employeeWorkDayList) {
        this.company = company;
        this.branch = branch;
        this.department = department;
        this.position = position;
        this.employeeCategory = employeeCategory;
        this.paymentCriteriaType = paymentCriteriaType;
        this.contractForm = contractForm;
        this.employeeMode = employeeMode;
        this.employeeWorkDayList = employeeWorkDayList;

    }
}