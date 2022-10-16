package ai.ecma.appstaff.service.view;


import ai.ecma.appstaff.entity.EmployeeCategoryType;
import ai.ecma.appstaff.enums.CustomFieldTypeEnum;
import ai.ecma.appstaff.enums.PaymentCriteriaTypeEnum;
import ai.ecma.appstaff.entity.view.ViewColumn;
import ai.ecma.appstaff.enums.TimeSheetStatusEnum;
import ai.ecma.appstaff.payload.DepartmentDTO;
import ai.ecma.appstaff.payload.PositionDTO;
import ai.ecma.appstaff.payload.customField.CustomFieldOptionDTO;
import ai.ecma.appstaff.payload.customField.CustomFiledTypeConfigDTO;
import ai.ecma.appstaff.payload.customField.RatingConfigDTO;
import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.CompanyFeignDTO;
import ai.ecma.appstaff.payload.feign.RoleFeignDTO;
import ai.ecma.appstaff.repository.EmployeeCategoryTypeRepository;
import ai.ecma.appstaff.service.DepartmentService;
import ai.ecma.appstaff.service.PositionService;
import ai.ecma.appstaff.service.feign.FeignService;
import ai.ecma.appstaff.utils.ColumnKey;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ViewColumnOptionsServiceImpl implements ViewColumnOptionsService {
    private final FeignService feignService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final EmployeeCategoryTypeRepository employeeCategoryTypeRepository;

    @Override
    //CUSTOM FIELD BERILSA SHU CUSTOM FIELD NING TYPE_CONFIGINI QAYTARADI
    public CustomFiledTypeConfigDTO mapCustomFieldTypeConfigFromEntityColumn(ViewColumn viewColumn, String tableName) {
        // log.info("_________class CustomFieldServiceImpl => ViewColumnOptionsServiceImpl =>  viewColumn: {}, tableName : {}", viewColumn, tableName);
        String columnName = viewColumn.getName();
        CustomFiledTypeConfigDTO typeConfigDTO = new CustomFiledTypeConfigDTO();

        CustomFieldTypeEnum viewColumnType = viewColumn.getType();
        // log.info("_________class CustomFieldServiceImpl => ViewColumnOptionsServiceImpl =>  type: {}", viewColumnType);


        //DROPDOWN YOKI LABELS BO'LSA
        if (CustomFieldTypeEnum.DROPDOWN.equals(viewColumnType) || CustomFieldTypeEnum.LABELS.equals(viewColumnType) || CustomFieldTypeEnum.ENUM_DROPDOWN.equals(viewColumnType)) {

            List<CustomFieldOptionDTO> customFieldOptionDTOList = mapOptionDTOListForEntity(columnName, tableName);
            typeConfigDTO.setOptions(customFieldOptionDTOList);

            //RATING BO'LSA
        } else if (CustomFieldTypeEnum.RATING.equals(viewColumnType)) {

            //RATING UCHUN CONFIG YASAB QAYTARADI
            RatingConfigDTO ratingConfigDTO = mapRatingToConfigDTOForEntity(columnName, tableName);
            typeConfigDTO.setRatingConfig(ratingConfigDTO);

        }

        return typeConfigDTO;
    }

    @Override
    public List<CustomFieldOptionDTO> getOptionForTimeSheetEmployee(String columnName) {

        switch (columnName) {

            case ColumnKey.BRANCH_ID:
                return getBranchesOption();
            case ColumnKey.POSITION_ID:
                return getPositionsOption();

            case ColumnKey.DEPARTMENT_ID:
                return getDepartmentsOption();

            case ColumnKey.PAYMENT_CRITERIA_TYPE:
                return getPaymentCriteriaTypesOption();


            case ColumnKey.EMPLOYEE_CATEGORY_TYPE_ID:
                return getEmployeeCategoryTypesOption();
            case ColumnKey.TIMESHEET_STATUS:
                return getTimesheetStatusTypesOption();
        }
        return null;
    }

    @Override
    public List<CustomFieldOptionDTO> getOptionForTimeSheetEmployeeForFinance(String columnName) {
        switch (columnName) {

            case ColumnKey.BRANCH_ID:
                return getBranchesOption();

            case ColumnKey.DEPARTMENT_ID:
                return getDepartmentsOption();

            case ColumnKey.POSITION_ID:
                return getPositionsOption();

            case ColumnKey.PAYMENT_CRITERIA_TYPE:
                return getPaymentCriteriaTypesOption();

            case ColumnKey.EMPLOYEE_CATEGORY_TYPE_ID:
                return getEmployeeCategoryTypesOption();
        }
        return null;
    }

    private List<CustomFieldOptionDTO> getPaymentCriteriaTypesOption() {
        PaymentCriteriaTypeEnum[] paymentCriteriaTypes = PaymentCriteriaTypeEnum.values();

        return Arrays
                .stream(paymentCriteriaTypes)
                .map(paymentCriteriaTypeEnum -> CustomFieldOptionDTO
                        .builder()
                        .id(paymentCriteriaTypeEnum.name())
                        .name(paymentCriteriaTypeEnum.name())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomFieldOptionDTO> getOptionForEmployee(String columnName) {

        switch (columnName) {
            case ColumnKey.ROLES:
                return getRolesOption();

            case ColumnKey.BRANCH_ID:
                return getBranchesOption();

            case ColumnKey.DEPARTMENT_ID:
                return getDepartmentsOption();

            case ColumnKey.POSITION_ID:
                return getPositionsOption();

            case ColumnKey.EMPLOYEE_CATEGORY_TYPE_ID:
                return getEmployeeCategoryTypesOption();
        }
        return null;
    }

    private List<CustomFieldOptionDTO> getTimesheetStatusTypesOption() {

        List<TimeSheetStatusEnum> timeSheetStatusEnums = Arrays.stream(TimeSheetStatusEnum.values()).collect(Collectors.toList());

        return timeSheetStatusEnums
                .stream()
                .map(timeSheetStatusEnum ->  CustomFieldOptionDTO
                        .builder()
                        .id(timeSheetStatusEnum.name())
                        .name(timeSheetStatusEnum.name())
                        .build()
                )
                .collect(Collectors.toList());

    }

    private List<CustomFieldOptionDTO> getEmployeeCategoryTypesOption() {
        List<EmployeeCategoryType> categoryFromDB = employeeCategoryTypeRepository.findAllByActiveTrue();

        return categoryFromDB
                .stream()
                .map(employeeCategoryType -> CustomFieldOptionDTO
                        .builder()
                        .id(employeeCategoryType.getId().toString())
                        .name(employeeCategoryType.getName())
                        .build()
                )
                .collect(Collectors.toList());

    }

    private List<CustomFieldOptionDTO> getPositionsOption() {
        List<PositionDTO> positionDTOList = positionService.getAllActivePositionFromDB();
        return positionDTOList
                .stream()
                .map(position -> CustomFieldOptionDTO
                        .builder()
                        .name(position.getName())
                        .id(position.getId().toString())
                        .build()
                )
                .collect(Collectors.toList());

    }

    private List<CustomFieldOptionDTO> getDepartmentsOption() {
        List<DepartmentDTO> allActiveDepartmentFromDB = departmentService.getAllActiveDepartmentFromDB();

        return allActiveDepartmentFromDB
                .stream()
                .map(department -> CustomFieldOptionDTO
                        .builder()
                        .name(department.getName())
                        .id(department.getId().toString())
                        .build()
                )
                .collect(Collectors.toList());

    }

    private List<CustomFieldOptionDTO> getBranchesOption() {
        List<BranchFeignDTO> branchFeignDTOS = feignService.safeGetBranchList();

        return branchFeignDTOS
                .stream()
                .map(branch -> CustomFieldOptionDTO
                        .builder()
                        .name(branch.getName())
                        .id(branch.getId().toString())
                        .build()
                )
                .collect(Collectors.toList());
    }

    private List<CustomFieldOptionDTO> getRolesOption() {
        List<RoleFeignDTO> roleFeignDTOS = feignService.safeGetRoleList();

        return roleFeignDTOS
                .stream()
                .map(role -> CustomFieldOptionDTO
                        .builder()
                        .id(role.getId().toString())
                        .name(role.getName())
                        .build()
                ).collect(Collectors.toList());
    }


    private RatingConfigDTO mapRatingToConfigDTOForEntity(String columnName, String tableName) {

        if (tableName.equals(TableNameConstant.EMPLOYEE) ||
                tableName.equals(TableNameConstant.MENTOR)) {
            return getRatingConfigForEmployee(columnName);
        }
        if (tableName.equals(TableNameConstant.TIMESHEET_EMPLOYEE)) {
            return getRatingConfigForTimeSheetEmployee(columnName);

        }
        if (tableName.equals(TableNameConstant.TIMESHEET_EMPLOYEE_FOR_FINANCE)) {
            return getRatingConfigForTimeSheetEmployeeForFinance(columnName);
        }
        return null;
    }

    private RatingConfigDTO getRatingConfigForTimeSheetEmployeeForFinance(String columnName) {
        return null;
    }

    private RatingConfigDTO getRatingConfigForTimeSheetEmployee(String columnName) {
        return null;
    }

    private RatingConfigDTO getRatingConfigForEmployee(String columnName) {
        return null;
    }

    private List<CustomFieldOptionDTO> mapOptionDTOListForEntity(String columnName, String tableName) {

        if (tableName.equals(TableNameConstant.EMPLOYEE) ||
                tableName.equals(TableNameConstant.MENTOR)) {
            return getOptionForEmployee(columnName);
        }
        if (tableName.equals(TableNameConstant.TIMESHEET_EMPLOYEE)) {
            return getOptionForTimeSheetEmployee(columnName);

        }
        if (tableName.equals(TableNameConstant.TIMESHEET_EMPLOYEE_FOR_FINANCE)) {
            return getOptionForTimeSheetEmployeeForFinance(columnName);
        }
        if (tableName.equals(TableNameConstant.TARIFF_GRID)) {
            return getOptionForTariffGrid(columnName);
        }
        return new ArrayList<>();
    }

    private List<CustomFieldOptionDTO> getOptionForTariffGrid(String columnName) {
        switch (columnName) {

            case ColumnKey.BRANCH_ID:
                return getBranchesOption();

            case ColumnKey.DEPARTMENT_ID:
                return getDepartmentsOption();

            case ColumnKey.COMPANY_ID:
                return getCompanyOptions();

            case ColumnKey.POSITION_ID:
                return getPositionsOption();

            case ColumnKey.PAYMENT_CRITERIA_TYPE:
                return getPaymentCriteriaTypesOption();

        }
        return null;
    }

    private List<CustomFieldOptionDTO> getCompanyOptions() {

        List<CompanyFeignDTO> companyList = feignService.safeGetCompanyList();

        return companyList
                .stream()
                .map(branch -> CustomFieldOptionDTO
                        .builder()
                        .name(branch.getName())
                        .id(branch.getId().toString())
                        .build()
                )
                .collect(Collectors.toList());

    }


}
