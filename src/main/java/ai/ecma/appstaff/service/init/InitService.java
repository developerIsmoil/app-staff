package ai.ecma.appstaff.service.init;

import ai.ecma.appstaff.enums.PageEnum;
import ai.ecma.appstaff.enums.PermissionEnum;
import ai.ecma.appstaff.payload.*;
import ai.ecma.appstaff.payload.feign.PageAddDTO;
import ai.ecma.appstaff.payload.feign.PermissionAddDTO;
import ai.ecma.appstaff.repository.EmployeeRepository;
import ai.ecma.appstaff.repository.view.ViewObjectRepository;
import ai.ecma.appstaff.service.*;
import ai.ecma.appstaff.service.feign.FeignService;
import ai.ecma.appstaff.utils.RestConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InitService {

    private final FeignService feignService;
    private final PositionService positionService;
    private final TariffGridService tariffGridService;
    private final DepartmentService departmentService;
    private final PrivilegeTypeService privilegeTypeService;
    private final PhoneNumberTypeService phoneNumberTypeService;
    private final TemplateForSickService templateForSickService;
    private final EmployeeCategoryService employeeCategoryService;
    private final EmployeeCategoryTypeService employeeCategoryTypeService;
    private final ViewObjectRepository viewObjectRepository;
    private final EmployeeRepository employeeRepository;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    public void createElements() {
        UUID departmentId = addDepartment();
        UUID positionId = addPosition(departmentId);
        UUID employeeCategoryTypeId = addEmployeeCategoryType();
        UUID employeeCategoryId = addEmployeeCategory(departmentId, positionId, employeeCategoryTypeId);
        UUID privilegeTypeId = addPrivilegeType();
        UUID templateForSick = addTemplateForSick(privilegeTypeId);
//        addTariffGrid(1L, departmentId, positionId, employeeCategoryId);
        addPhoneNumberType();
    }

    private UUID addDepartment() {
        ApiResult<DepartmentDTO> academic = departmentService.addDepartment(new DepartmentDTO("Academic", true));

        DepartmentDTO data = academic.getData();

        return data.getId();
    }

    private UUID addPosition(UUID departmentId) {

        ApiResult<PositionDTO> position = positionService.addPosition(new PositionDTO("Position", true, departmentId));

        PositionDTO data = position.getData();

        return data.getId();

    }

    private UUID addEmployeeCategoryType() {
        ApiResult<EmployeeCategoryTypeDTO> employeeCategoryType = employeeCategoryTypeService.addEmployeeCategoryType(new EmployeeCategoryTypeDTO("A1", true));

        EmployeeCategoryTypeDTO data = employeeCategoryType.getData();

        return data.getId();
    }

    private UUID addEmployeeCategory(UUID departmentId, UUID positionId, UUID employeeCategoryTypeId) {
        ApiResult<EmployeeCategoryDTO> employeeCategory = employeeCategoryService.addEmployeeCategory(new EmployeeCategoryDTO(departmentId, positionId, employeeCategoryTypeId, "requirement", "description", true, "Employee category"));

        EmployeeCategoryDTO data = employeeCategory.getData();

        return data.getId();
    }

    private UUID addPrivilegeType() {
        PrivilegeTypeDTO privilegeType = privilegeTypeService.create(new PrivilegeTypeDTO("Privilege type", true));
        return privilegeType.getId();
    }

    private UUID addTemplateForSick(UUID privilegeTypeId) {
        ApiResult<TemplateForSickDTO> templateForSick = templateForSickService.addTemplateForSick(new TemplateForSickDTO(true, privilegeTypeId, 12d, true));

        TemplateForSickDTO data = templateForSick.getData();

        return data.getId();
    }

//    private UUID addTariffGrid(Long branchId, UUID departmentId, UUID positionId, UUID employeeCategoryId) {
//        ApiResult<TariffGridDTO> tariffGrid = tariffGridService.addTariffGrid(new TariffGridDTO(branchId, departmentId, positionId, employeeCategoryId, PaymentCriteriaTypeEnum.WORK, 100d, true, 100d, true, 100d, BonusType.TRADE, 100d, true));
//
//        TariffGridDTO data = tariffGrid.getData();
//        return data.getId();
//    }

    private UUID addPhoneNumberType() {
        ApiResult<List<PhoneNumberTypeDTO>> phoneNumberTypeList = phoneNumberTypeService.addPhoneNumberType(new PhoneNumberTypeDTO("Akasi"));

        List<PhoneNumberTypeDTO> data = phoneNumberTypeList.getData();
        return data.get(0).getId();
    }


    /**
     * BU AUTH SERVICE GA BORIB PAGE VA PERMISSIONLARINI OLIB BORADI
     */
    public void savePageAndPermissionToAuthService() {
        List<PageEnum> pageEnumList = Arrays.asList(PageEnum.values());

        List<PageAddDTO> pageAddDTOList = pageEnumList.stream().map(this::mapPageAddDTO).collect(Collectors.toList());

        ApiResult<Boolean> addPageToAuthResult = feignService.addPageToAuth(pageAddDTOList);

        if (!addPageToAuthResult.getSuccess()) {
            System.exit(1);
        }

        List<PermissionEnum> permissionEnumList = Arrays.asList(PermissionEnum.values());
        List<PermissionAddDTO> permissionAddDTOList = permissionEnumList.stream().map(this::mapPermissionAddDTO).collect(Collectors.toList());
        ApiResult<Boolean> addPermissionToAuthResult = feignService.addPermissionToAuth(permissionAddDTOList);

        if (!addPermissionToAuthResult.getSuccess()) {
            System.exit(1);
        }

    }

    private PageAddDTO mapPageAddDTO(PageEnum pageEnum) {
        return new PageAddDTO(pageEnum.getTitleUz(),
                pageEnum.getTitleEn(),
                pageEnum.getTitleRu(),
                pageEnum,
                pageEnum.getDepartment().getModule().name(),
                pageEnum.getDepartment(),
                null,
                pageEnum.getBeforeDepartment(),
                pageEnum.getBefore(),
                pageEnum.isDeleted());
    }

    private PermissionAddDTO mapPermissionAddDTO(PermissionEnum permissionEnum) {
        return new PermissionAddDTO(permissionEnum.getTitle(),
                permissionEnum,
                permissionEnum.getPage().getDepartment().getModule().name(),
                permissionEnum.getPage().getDepartment(),
                permissionEnum.getPage(),
                null,
                permissionEnum.getPage().getBeforeDepartment(),
                permissionEnum.getBefore() != null ? permissionEnum.getBefore().getPage() : null,
                permissionEnum.getBefore(), permissionEnum.isDeleted());
    }


    public void createOrUpdateUniqueQuery() {
        viewObjectRepository.executeUniqueQuery();
    }


    //QUERY EXECUTE QILIB BERADIGAN FUNCTION NI O'CHIRIB QAYTA YARATADI
    public void createOrUpdateQueryExecutorFunction() {
        if (dbUserName.toLowerCase().equals("staff")) {
            viewObjectRepository.executeFunctionForOwnerStaff();
        } else if (dbUserName.toLowerCase().equals("postgres")) {
            viewObjectRepository.executeFunctionForOwnerPostgres();
        }
    }

}
