package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.CompanyFeignDTO;
import ai.ecma.appstaff.payload.feign.RoleFeignDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalDTO {

    //

 private    List<RoleFeignDTO> roleList = new ArrayList<>();

    //

 private    List<BranchFeignDTO> branchList = new ArrayList<>();

    //

 private    List<CompanyFeignDTO> companyList = new ArrayList<>();

    //

 private    List<EnumDTO> maritalStatusList = new ArrayList<>();

    //

 private    List<EnumDTO> genderList = new ArrayList<>();

    //

 private    List<PrivilegeTypeDTO> privilegeTypeList = new ArrayList<>();

    //

 private    List<DepartmentDTO> departmentList = new ArrayList<>();

    //

 private    List<PositionDTO> positionList = new ArrayList<>();

    //

 private    List<EmployeeCategoryDTO> employeeCategoryList = new ArrayList<>();

    //

 private    List<EnumDTO> paymentCriteriaTypeList = new ArrayList<>();

    //

 private    List<EnumDTO> weekDayList = new ArrayList<>();

    //

 private    List<EnumDTO> contractFormList = new ArrayList<>();

    //

 private    List<EnumDTO> studyDegreeList = new ArrayList<>();

    //

 private    List<SkillDTO> skillList = new ArrayList<>();

    //

 private    List<EnumDTO> employeeModeList = new ArrayList<>();

    //

 private List<PhoneNumberTypeInfoDTO> phoneNumberTypeDTOListFromDB = new ArrayList<>();
}
